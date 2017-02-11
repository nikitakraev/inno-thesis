import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * @author kitttn
 */

class SoulTimer(val scheduler: Scheduler = Schedulers.io()) {
    val map = mutableMapOf<String, PublishSubject<Long>>()
    val subs = mutableMapOf<String, Subscription>()

    fun start(id: String, unit: TimeUnit, delay: Int): Observable<Long> {
        return init(id, unit, delay, { it + 1 })
    }

    fun countdown(id: String, unit: TimeUnit, delay: Int): Observable<Long> {
        return init(id, unit, delay, { delay - it })
    }

    fun stop(id: String) {
        println("Stopping subscription $id...")
        map[id]?.onCompleted()
        val subscription = subs[id]
        if (subscription != null && !subscription.isUnsubscribed) {
            println("Stopping failed, terminating subscription manually")
            subscription.unsubscribe()
        }
    }

    private fun init(id: String, unit: TimeUnit, delay: Int, mappingFunc: (Long) -> Long): Observable<Long> {
        val existing = map[id]
        if (existing != null && !existing.finished) return existing

        val timer = map[id] ?: PublishSubject.create()

        subs[id] = Observable.interval(1L, unit, scheduler)
                .map { mappingFunc(it) }
                .take(delay)
                .doOnCompleted {
                    println("Removing value with id=$id...")
                    map.remove(id)
                }
                .subscribe(timer)

        map[id] = timer
        println(">> Map[$id]: ${timer}")
        return timer
    }
}

val <T> PublishSubject<T>.finished: Boolean
    get() {
        val finished = this.hasThrowable() || this.hasCompleted()
        println(">> finished: $finished")
        return finished
    }