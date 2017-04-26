import org.graphstream.graph.Edge
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.SingleGraph
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import java.util.stream.Collectors

/**
 * @author kitttn
 */

val graph = SingleGraph("Test graph")
var num = 0
var connNum = 1
val percentageCommunicating = .01
val chanceToConnect = .01
val routes = mutableMapOf<Int, MutableList<Int>>()
var rand = Random(40)

fun main(args: Array<String>) {
    generateGraph(File("cities_falloutnv"))
    graph.display()
    println(routes.toSortedMap())

    // TODO: city to number correspondence
    // TODO: simulation with trust for thesis / journal
    // TODO:
}

fun getCommunicatorsFromCities(cities: List<List<GraphNode>>): List<List<GraphNode>> {
    val npcs = mutableListOf<List<GraphNode>>()
    for (town in cities) {
        // getting number of communicators
        val comms = Math.ceil(percentageCommunicating * town.size)
        // println("Selecting $comms from city with population=${town.size}...")
        val townCommunicators = mutableListOf<GraphNode>()
        val mutTowns = town.toMutableList()
        for (i in 1..comms.toInt()) {
            val elem = mutTowns.getRandomElement(rand)
            mutTowns -= elem
            townCommunicators += elem
        }

        npcs += townCommunicators
    }

    // println("List of npcs: $npcs")
    return npcs
}

fun connectCities(communicators: List<List<GraphNode>>) {
    for (i in 0 until communicators.size) {
        // making a guaranteed link from this town to another
        val linkFrom = i
        val townFrom = communicators[linkFrom]
        var connected = 0
        val others = communicators.filter { it != townFrom }.reduce { l1, l2 -> l1 + l2 }.toMutableList()
        // println("Connecting town #$linkFrom to random other ${others.size} elements...")

        // iterating over other towns and connecting random element from them to $linkFrom
        for (j in 0 until others.size) {
            val from = townFrom.getRandomElement(rand)
            val to = others.getRandomElement(rand)
            val result = if (connected == 0)
                connect(from, to)
            else probablyConnect(from, to)
            connected += if (result) 1 else 0
            others -= to
        }

        // println("Connected ${connected}")
    }
}

fun connect(from: GraphNode, to: GraphNode): Boolean {
    return try {
        graph.addEdge<Edge>("edge${num++}", from.name, to.name)
        addEdge(from.id, to.id)
        addEdge(to.id, from.id)
        true
    } catch (e: Exception) {
        false
    }
}

fun probablyConnect(from: GraphNode, to: GraphNode): Boolean {
    val prob = rand.nextDouble()
    return if (prob < chanceToConnect)
        connect(from, to)
    else false
}

fun buildCityConnections(allCities: List<GraphNode>) {
    val connected = mutableListOf<GraphNode>()
    val disconnected = allCities.toMutableList()

    while (disconnected.isNotEmpty()) {
        val node = disconnected.getRandomElement(rand)

        val howManyToAttach = if (rand.nextDouble() + connNum > connNum + 0.5) connNum else connNum + 1
        if (connected.size >= howManyToAttach) {
            // println("Attaching $howManyToAttach nodes...")
            val copy = connected.toMutableList()
            for (counter in 1..howManyToAttach) {
                val attachTo = copy.getRandomElement(rand)
                copy -= attachTo
                connect(node, attachTo)
            }
        }

        connected += node
        disconnected -= node
    }
}

fun <T> List<T>.getRandomElement(rand: Random) = this[rand.nextInt(this.size)]

fun generateGraph(f: File) {
    graph.clear()
    connNum = 1
    rand = Random(40)
    val buff = BufferedReader(FileReader(f))
    val cities = mutableListOf<List<GraphNode>>()

    val allCities = buff.lines().collect(Collectors.toList<String>())
    allCities += ""

    allCities
            .filter { it.isNotEmpty() }
            .forEach { graph.addNode<Node>(it) }

    num = 0
    val allNodes = allCities
            .map {
                val res = GraphNode(num, it)
                num += if (it.isEmpty()) 0 else 1
                res
            }

    val oneTown = mutableListOf<GraphNode>()
    for (node in allNodes) {
        if (node.name.isEmpty()) {
            cities += oneTown.toList()
            buildCityConnections(oneTown)
            oneTown.clear()
        } else
            oneTown += node
    }

    val all = getCommunicatorsFromCities(cities)
    connNum = 0
    //buildCityConnections(all)
    connectCities(all)
    println(allNodes.map { "${it.id} = ${it.name}" })

    buff.close()
}

fun addEdge(from: Int, to: Int) {
    if (routes[from] == null)
        routes[from] = mutableListOf()
    routes[from]?.add(to)
}

data class GraphNode(val id: Int, val name: String) {
    override fun toString(): String = "{$id}"
}