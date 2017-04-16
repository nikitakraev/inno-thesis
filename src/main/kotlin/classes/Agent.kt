package classes

import java.util.*

/**
 * @author kitttn
 */
class Agent(val uid: Int, val enjoyment: List<String>, val hate: List<String>, var location: Location) {

    private var reputation = 0.0
    private val forgettable = mutableListOf<UserEvent>()
    private val persistent = mutableListOf<UserEvent>()
//    private val FADEOUT_TIME = 1000.0 * 60 * 60 * 24 // 1 hour in milliseconds
    private val FADEOUT_TIME = 1000.0 // 1 second in milliseconds
    var mood = 1.0
    private val base = 0.5
    private val rand = Random()

    var debug = true

    fun witness(event: Event) {
        val time = System.currentTimeMillis()
        val newEvt = UserEvent(time.toString(), event, time, location)
        gainKnowledge(newEvt)
    }

    fun gainKnowledge(event: UserEvent) : Boolean {
        val existing = forgettable.firstOrNull { it.id == event.id } ?: event
        forgettable.remove(existing)
        existing.time = System.currentTimeMillis()
        forgettable += event
        recalculate()
        return true
    }

    fun communicate(other: Agent): Boolean {
        val attempt = rand.nextDouble()
        val attitude = getAttitude(other)
        println("$attempt ?>= $attitude: ${attempt >= attitude}")
        if (attempt < attitude) {
            println("Can't communicate, not your day!")
            return false
        }

        val topMine = forgettable.map { Pair(it, value(it)) }.maxBy { it.second }
        val topOther = other.forgettable.map { Pair(it, value(it)) }.maxBy { it.second }
        println("$uid top event: $topMine")
        println("${other.uid} top event: $topOther")

        val top = listOf(topMine, topOther).sortedBy { it?.second }.lastOrNull()
        if (top != null) {
            println("Sharing event ${top.first} across others...")
            gainKnowledge(top.first)
            other.gainKnowledge(top.first)
        }

        return true
    }

    fun getAttitude(other: Agent): Double {
        val l = enjoyment.filter { it in other.enjoyment }.count()
        val h = hate.filter { it in other.hate }.count()
        val common = (l + h) / 2.0 / (enjoyment.size + hate.size) + base
        return 1 - mood * common
    }

    fun knowsAbout(evt: Event): Boolean = evt.eid in forgettable.map { it.event.eid }

    fun getReputation() = reputation

    private fun recalculate() {
        println("------------------------\nPlayer reputation for $uid:")
        reputation = 0.0
        for (evt in forgettable) { // selecting all event from memory
            // println("Processing event: $evt")
            val v = value(evt)
            println("For event $evt value is: $v")
            reputation += v
        }
        println("Reputation: $reputation\n------------------------")
    }

    private fun fT(evt: UserEvent): Double {
        val res = 1 - (System.currentTimeMillis() - evt.time) / FADEOUT_TIME
        println("Time decay func result: $res")
        return if (res < 0) 0.0 else res
    }

    private fun value(evt: UserEvent): Double {
        println("======================\nValue of $evt: ")
        val l = enjoyment.filter { it in evt.event.categories }.count()
        val h = hate.filter { it in evt.event.categories }.count()
        println("Common likes: $l, common hates: $h")
        val res = fT(evt) * (l - h) * evt.event.weight
        println("Value func: $res\n=================")
        return res
    }
}

fun Agent.println(arg: Any) {
    if (debug)
        System.out.println(arg)
}