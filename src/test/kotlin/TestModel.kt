import classes.*
import org.graphstream.algorithm.generator.RandomGenerator
import org.graphstream.graph.Edge
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.SingleGraph
import org.junit.Test
import java.util.*

/**
 * @author kitttn
 */

class TestModel {
    @Test
    fun checkWitnessing() {
        val events = getEvents()
        val npc = Agent(1, enjoyment = getRandomCategories(4), hate = getRandomCategories(3), location = Location(0.0, 0.0))
        println("Created NPC")
        for (evt in events) {
            println("Adding another event...")
            npc.witness(evt)
            println("Reputation: ${npc.getReputation()}")
        }
        println("DONE")
    }

    @Test
    fun checkCommunication() {
        val events = getEvents()
        val enj = getRandomCategories(4)
        val hate = getRandomCategories(3)
        val npc = Agent(1, enjoyment = enj, hate = hate, location = Location(0.0, 0.0))
        npc.debug = false
        npc.witness(events[0])
        println("Created NPC")
        val other = Agent(2, enjoyment = enj, hate = hate, location = Location(0.0, 0.0))
        other.debug = false
        println("Communicating 50 times...")
        var successful = 0
        for (i in 1..50) {
            val res = npc.communicate(other)
            if (res) successful += 1
        }

        println("Total successful: ${successful * 2.0}%")
        println("DONE")
    }

    @Test
    fun testK5() {
        val events = getEvents().subList(0, 5)
        val agents = mutableListOf<Agent>()
        for (i in 0 until 5)
            agents += Agent(i, enjoyment = getRandomCategories(4), hate = getRandomCategories(3), location = Location(0.0, 0.0))

        agents[0].witness(events[0])
        for (i in 0 until 5) {
            for (j in i + 1 until 5)
                agents[i].communicate(agents[j])
            for (j in 0 until i)
                agents[i].communicate(agents[j])
        }

        for (i in 0 until 5)
            println("${agents[i].uid} reputation: ${agents[i].getReputation()}")

        println("DONE")
    }

    fun createC5Graph() {
        routes.clear()

        addEdge(1, 2)
        addEdge(1, 0)
        addEdge(2, 1)
        addEdge(2, 3)
        addEdge(3, 2)
        addEdge(3, 4)
        addEdge(4, 3)
        addEdge(4, 0)
        addEdge(0, 4)
        addEdge(0, 1)
    }

    fun createL5Graph() {
        routes.clear()

        addEdge(0, 1)
        addEdge(1, 0)
        addEdge(1, 2)
        addEdge(2, 1)
        addEdge(2, 3)
        addEdge(3, 2)
        addEdge(3, 4)
        addEdge(4, 3)
    }

    fun createK5Graph() {
        routes.clear()

        for (i in 0..4)
            for (j in 0..4)
                if (i != j)
                    addEdge(i, j)
    }

    fun createS5Graph() {
        routes.clear()

        for (j in 1..4) {
            addEdge(0, j)
            addEdge(j, 0)
        }
    }

    @Test
    fun checkEverybodyKnowsWorks() {
        val l = getRandomCategories(3)
        val h = getRandomCategories(3)
        val evt = Event("1", l, "1", 20)

        val npcs = mutableListOf<Agent>()
        for (i in 1..5) {
            val agent = Agent(i, l, h, Location(0.0, 0.0))
            agent.witness(evt)
            npcs += agent
        }

        println("everybody knows: ${everybodyKnows(npcs, evt)}")
    }

    fun testSmallWorld30Times(startFrom: Int, creator: () -> Unit, mood: Double) {
        val l = getRandomCategories(3)
        val h = getRandomCategories(3)

        val runs = mutableListOf<Int>()

        for (run in 1..1000) {
            creator()
            val npcs = mutableListOf<Agent>()
            for (i in 0 until routes.size) {
                npcs += Agent(i, l, h, Location(0.0, 0.0)).apply { debug = false; this.mood = mood }
            }

            val evt = Event("1", l, "1", 20)

            val g = AgentGraph("test")
            g.debug = false
            for ((k, v) in routes)
                g.addAgent(npcs[k], v)

            val totalSteps = g.spread(evt, startFrom = startFrom)
            // println("Total steps for spreading: $totalSteps")
            runs += totalSteps
        }

        val mean = runs.sum() / runs.size.toDouble()
        val variance = runs.map { (it - mean) * (it - mean) }.sum() / (runs.size - 1.0)
        // println("Mean: $mean")
        // println("Std deviation: ${Math.sqrt(variance)}")
        print("&$mean (${String.format("%.2f", Math.sqrt(variance))})")
    }

    fun createFallout() {
        routes.clear()

        val graph = SingleGraph("Test graph")
        val gen = RandomGenerator(6.0)
        gen.addNodeLabels(true)
        gen.addSink(graph)
        gen.setRandomSeed(1111) // 406 585
        gen.begin()

        for (i in 0..1500)
            gen.nextEvents()
        gen.end()

        for (i in 0 until graph.edgeCount) {
            val edge = graph.getEdge<Edge>(i)
            val from = edge.getSourceNode<Node>().index
            val to = edge.getTargetNode<Node>().index
            addEdge(from, to)
            addEdge(to, from)
        }

        addEdge(122, 445)
        addEdge(445, 122)
        graph.addEdge<Edge>("edge", "122", "445")

        // println(routes)
    }

    @Test
    fun testOblivion30Times() {
        val startFrom = 480
        testSmallWorld30Times(startFrom, { createOblivion() }, 1.0)
        testSmallWorld30Times(startFrom, { createOblivion() }, .75)
        testSmallWorld30Times(startFrom, { createOblivion() }, .5)
        testSmallWorld30Times(startFrom, { createOblivion() }, .25)
        println(routes)
    }

    @Test
    fun testFallout30Times() {
        val startFrom = 146
        val func = { createFallout() }
        testSmallWorld30Times(startFrom, { func() }, 1.0)
        testSmallWorld30Times(startFrom, { func() }, .75)
        testSmallWorld30Times(startFrom, { func() }, .5)
        testSmallWorld30Times(startFrom, { func() }, .25)
        println(routes)
    }

    fun createOblivion() {
        routes.clear()

        val graph = SingleGraph("Oblivion")
        val gen = RandomGenerator(6.0)
        gen.addNodeLabels(true)
        gen.addSink(graph)
        gen.setRandomSeed(102)
        gen.begin()

        for (i in 0..500)
            gen.nextEvents()
        gen.end()

        for (i in 0 until graph.edgeCount) {
            val edge = graph.getEdge<Edge>(i)
            val from = edge.getSourceNode<Node>().index
            val to = edge.getTargetNode<Node>().index
            addEdge(from, to)
            addEdge(to, from)
        }
        // println(routes)
    }

    @Test
    fun testC530Times() {
        val func = { createC5Graph() }
        for (i in 0..3)
            testSmallWorld30Times(0, func, 1 - 0.25 * i)
    }

    @Test
    fun testL530Times() {
        val func = { createL5Graph() }
        for (i in 0..3)
            testSmallWorld30Times(0, func, 1 - 0.25 * i)
        println()
        for (i in 0..3)
            testSmallWorld30Times(1, func, 1 - 0.25 * i)
        println()
        for (i in 0..3)
            testSmallWorld30Times(2, func, 1 - 0.25 * i)
    }

    @Test
    fun testK530Times() {
        val func = { createK5Graph() }
        for (i in 0..3)
            testSmallWorld30Times(0, func, 1 - 0.25 * i)
    }

    @Test
    fun testS530Times() {
        val func = { createS5Graph() }
        for (i in 0..3)
            testSmallWorld30Times(0, func, 1 - 0.25 * i)
        println()
        for (i in 0..3)
            testSmallWorld30Times(1, func, 1 - 0.25 * i)
    }


    fun everybodyKnows(npcs: List<Agent>, evt: Event): Boolean =
            npcs.map { it.knowsAbout(evt) }.reduce { one, two -> one && two }
}

val categories = listOf("dragon", "wolf", "elf", "dwarf", "cat", "home", "children", "love", "family",
        "town", "village", "tower", "artifact", "magic", "agility", "gambling", "coins", "gold")

fun getRandomCategories(total: Int): List<String> {
    val random = Random()
    val cats = mutableListOf<String>()
    for (i in 0..total) {
        var c = categories[(random.nextInt(categories.size))]
        while (c in cats)
            c = categories[(random.nextInt(categories.size))]
        cats += c
    }

    return cats
}

fun getEvents(): List<Event> {
    val list = mutableListOf<Event>()
    for (i in 1..10)
        list += Event(i.toString(), getRandomCategories(3), i.toString(), i * 5L)
    return list
}