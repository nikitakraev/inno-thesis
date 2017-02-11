import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rx.observers.TestSubscriber
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

/**
 * @author kitttn
 */

class SoulTimerTest {
    private var timer = SoulTimer()
    private lateinit var listener: TestSubscriber<Long>
    private val scheduler = TestScheduler()
    @Before
    fun setUp() {
        timer = SoulTimer(scheduler)
        listener = TestSubscriber()
    }

    @Test
    fun itRunsTenSecondsAndCompletes() {
        timer.start("1", TimeUnit.SECONDS, 10)
                .subscribe(listener)

        scheduler.time()
        scheduler.advanceTimeTo(4, TimeUnit.SECONDS)

        scheduler.time()
        listener.assertReceivedOnNext(listOf(1, 2, 3, 4))

        scheduler.advanceTimeTo(10001, TimeUnit.MILLISECONDS)
        scheduler.time()

        listener.awaitTerminalEvent()
        listener.assertCompleted()
        listener.assertNoErrors()
        val evts = listener.onNextEvents
        assertEquals(10, evts.size)
        listener.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }

    @Test
    fun itTakesSameSubjectWhileWorking() {
        scheduler.time()
        timer.start("2", TimeUnit.SECONDS, 10)
                .subscribe(listener)

        scheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        scheduler.time()

        val otherSub = TestSubscriber<Long>()
        timer.start("2", TimeUnit.SECONDS, 4)
                .subscribe(otherSub)

        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        scheduler.time()

        otherSub.assertReceivedOnNext(listOf(4, 5))
    }

    @Test
    fun itWorksSimultaneouslyForDistinctIds() {
        scheduler.time()
        timer.start("1", TimeUnit.SECONDS, 10)
                .subscribe(listener)

        val otherSub = TestSubscriber<Long>()
        timer.start("2", TimeUnit.SECONDS, 5)
                .subscribe(otherSub)

        scheduler.advanceTimeTo(6, TimeUnit.SECONDS)
        scheduler.time()
        otherSub.assertCompleted()
        assertEquals(5, otherSub.onNextEvents.size)
        otherSub.assertReceivedOnNext(listOf(1, 2, 3, 4, 5))

        listener.assertNotCompleted()
        listener.assertReceivedOnNext(listOf(1, 2, 3, 4, 5, 6))
    }

    @Test
    fun itDisconnectsWhenStoppingWhileRunning() {
        scheduler.time()
        timer.start("1", TimeUnit.SECONDS, 10)
                .subscribe(listener)

        scheduler.advanceTimeBy(4, TimeUnit.SECONDS)
        scheduler.time()
        listener.assertReceivedOnNext(listOf(1, 2, 3, 4))
        timer.stop("1")

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        scheduler.time()

        listener.assertCompleted()
        listener.assertReceivedOnNext(LongRange(1, 4).toList())
    }

    @Test
    fun itDoesNothingWhenStoppingAfterFinish() {
        scheduler.time()
        timer.start("1", TimeUnit.SECONDS, 10)
                .subscribe(listener)

        scheduler.advanceTimeTo(11, TimeUnit.SECONDS)
        scheduler.time()

        timer.stop("1")
        listener.assertCompleted()
        listener.assertNoErrors()
        listener.assertReceivedOnNext(LongRange(1, 10).toList())
    }

    @Test
    fun itDoesNothingWhenStoppingNonExistingId() {
        scheduler.time()
        timer.start("1", TimeUnit.SECONDS, 10)
                .subscribe(listener)

        scheduler.advanceTimeBy(4, TimeUnit.SECONDS)
        scheduler.time()
        listener.assertReceivedOnNext(listOf(1, 2, 3, 4))
        timer.stop("2")

        scheduler.advanceTimeBy(6, TimeUnit.SECONDS)
        scheduler.time()
        listener.assertCompleted()
        listener.assertNoErrors()
        assertEquals(10, listener.onNextEvents.size)
    }

    @Test
    fun itReversesWhenCountdown() {
        timer.countdown("1", TimeUnit.SECONDS, 3)
                .subscribe(listener)

        scheduler.time()
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        scheduler.time()

        listener.assertCompleted()
        listener.assertReceivedOnNext(listOf(3, 2, 1))
    }

    @Test
    fun twoCountDownsNotCollide() {
        timer.countdown("1", TimeUnit.SECONDS, 3)
                .subscribe(listener)

        val sub = TestSubscriber<Long>()
        timer.countdown("2", TimeUnit.SECONDS, 6)
                .subscribe(sub)

        scheduler.time()
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        scheduler.time()

        listener.assertCompleted()
        listener.assertReceivedOnNext(listOf(3, 2, 1))

        scheduler.advanceTimeTo(6, TimeUnit.SECONDS)
        sub.assertCompleted()
        sub.assertReceivedOnNext(listOf(6, 5, 4, 3, 2, 1))
    }

    fun TestScheduler.time() {
        println("Time: ${this.now()}")
    }
}