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
val rand = Random(40)
var num = 0
var connNum = 1
val percentageCommunicating = .15

fun main(args: Array<String>) {
    val f = File("cities")
    val buff = BufferedReader(FileReader(f))
    val cities = mutableListOf<List<String>>()

    val allCities = buff.lines().collect(Collectors.toList<String>())
    allCities += ""

    allCities.filter { it.isNotEmpty() }
            .forEach { graph.addNode<Node>(it) }

    val oneTown = mutableListOf<String>()
    for (line in allCities) {
        if (line.isEmpty()) {
            cities += oneTown.toList()
            buildCityConnections(oneTown)
            oneTown.clear()
        } else
            oneTown += line
    }

    val all = getCommunicatorsFromCities(cities)

    connNum = 0
    buildCityConnections(all)

    graph.display()
}

fun getCommunicatorsFromCities(cities: List<List<String>>): List<String> {
    val npcs = mutableListOf<String>()
    for (town in cities) {
        // getting number of communicators
        val comms = Math.ceil(percentageCommunicating * town.size)
        val mutTowns = town.toMutableList()
        for (i in 1..comms.toInt()) {
            val elem = mutTowns.getRandomElement(rand)
            mutTowns -= elem
            npcs += elem
        }
    }

    println("List of npcs: $npcs")
    return npcs
}

fun buildCityConnections(allCities: List<String>) {
    val connected = mutableListOf<String>()
    val disconnected = allCities.toMutableList()

    while (disconnected.isNotEmpty()) {
        val node = disconnected.getRandomElement(rand)

        val howManyToAttach = if (rand.nextDouble() + connNum > connNum + 0.5) connNum else connNum + 1
        if (connected.size >= howManyToAttach) {
            println("Attaching $howManyToAttach nodes...")
            val copy = connected.toMutableList()
            for (counter in 1..howManyToAttach) {
                val attachTo = copy.getRandomElement(rand)
                copy -= attachTo
                try {
                    graph.addEdge<Edge>("edge${num++}", node, attachTo)
                } catch (e: Exception) {

                }
            }
        }

        connected += node
        disconnected -= node
    }
}

fun <T> List<T>.getRandomElement(rand: Random) = this[rand.nextInt(this.size)]

val routes = mutableMapOf<Int, MutableList<Int>>()

fun addEdge(from: Int, to: Int) {
    if (routes[from] == null)
        routes[from] = mutableListOf()
    routes[from]?.add(to)
}