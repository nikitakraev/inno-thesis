package classes

/**
 * @author kitttn
 */

open class Graph(val id: String) {
    protected val vertices = mutableListOf<Vertex>()
    fun addVertex(id: Int, connections: List<Int>) {
        vertices += Vertex(id, connections)
    }

    override fun toString(): String {
        return "Graph $id:\n${vertices.joinToString("\n")}"
    }

    fun verify() {
        val ids = vertices.map { it.id }
        val missing = mutableSetOf<Int>()

        for (v in vertices) {
            v.connections
                    .map { Checker(it, it in ids) }
                    .filter { !it.inGraph }
                    .forEach {
                        missing.add(it.id)
                    }
        }

        if (missing.size != 0) throw Error("Graph is missing vertices: $missing")
    }
}

data class Checker(val id: Int, val inGraph: Boolean)

class Vertex(val id: Int, val connections: List<Int>) {
    override fun toString() = "$id -> $connections"
}

class AgentGraph(id: String) : Graph(id) {
    var debug = true
    private val agents = mutableListOf<Agent>()

    fun addAgent(npc: Agent, connections: List<Int>) {
        agents += npc
        addVertex(npc.uid, connections)
    }

    /**
     * This function spreads the desired event, starting from NPC with id startFrom.
     * Returns number of steps required to propagate the Event
     */
    fun spread(evt: Event, startFrom: Int): Int {
        verify()
        agents.find { it.uid == startFrom }?.witness(evt) ?: throw Error("NPC not found!")
        val queue = mutableListOf(startFrom)
        val addToQueue = mutableListOf<Int>()
        var numOfSteps = 0

        val disconnected = vertices.map { it.connections.size in 1..2 }.filter { it }.count()
        println("Disconnected nodes: $disconnected")

        while (queue.size < agents.size && numOfSteps < 10000) {
            println("Step ${++numOfSteps}\n====================")
            for (agentId in queue) {
                val currNpc = agents.find { it.uid == agentId } ?: throw Error("NPC with id=$agentId not found!")
                val vertex = vertices.find { it.id == agentId } ?: throw Error("Vertex with id=$agentId not found!")
                for (conn in vertex.connections) {
                    if (conn in queue) {
                        println("Already knows, skipping...")
                        continue
                    }

                    if (conn in addToQueue) {
                        println("He is told on this step :)")
                        continue
                    }

                    val otherNpc = agents.find { it.uid == conn } ?: throw Error("Can't find Agent with id $conn")
                    val successfullyTransferred = currNpc.communicate(otherNpc)
                    if (successfullyTransferred) {
                        println("NPC `${otherNpc.uid}` now knows!")
                        addToQueue += otherNpc.uid
                    }
                }
            }

            queue.addAll(addToQueue)
            addToQueue.clear()

            println("List of those who know: $queue")
        }

        println("============= completed ===============")
        return numOfSteps
    }
}

fun AgentGraph.println(s: Any) {
    if (this.debug)
        System.out.println(s)
}