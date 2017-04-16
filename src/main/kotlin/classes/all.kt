package classes

/**
 * @author kitttn
 */

data class Location(val lat: Double, val lng: Double)

class Event(val eid: String, val categories: List<String>, val qid: String, val weight: Long) {
    override fun toString(): String = "{eid: $eid, weight: $weight}"
}

class UserEvent(val id: String, val event: Event, var time: Long, val loc: Location) {
    override fun toString(): String = "{id: $id, evt: $event, time: $time}"
}