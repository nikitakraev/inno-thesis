import org.graphstream.algorithm.generator.RandomGenerator
import org.graphstream.graph.Edge
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.SingleGraph
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.*

/**
 * @author kitttn
 */

fun main(args: Array<String>) {
    val f = File("cities")
    val buff = BufferedReader(FileReader(f))
    val graph = SingleGraph("Test graph")
    val rand = Random(42)

    for (line in buff.lines())
        graph.addNode<Node>(line)

    graph.display()
}

val routes = mutableMapOf<Int, MutableList<Int>>()

fun addEdge(from: Int, to: Int) {
    if (routes[from] == null)
        routes[from] = mutableListOf()
    routes[from]?.add(to)
}