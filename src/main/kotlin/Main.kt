import java.util.concurrent.TimeUnit

/**
 * @author kitttn
 */

fun main(args: Array<String>) {
    var running = true
    val t = SoulTimer()

    t.start("1", TimeUnit.SECONDS, 6)
            .subscribe({ println("1: $it") }, {}, { println("1 Completed"); running = false })

    while (running)
        Thread.sleep(1)
}