import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class `15_amb` {

    /**
     * Эмитит все элементы того обзервабла, который первым эмитит элемент
     * или первым посылает терминальный ивент
     *
     * Пример - есть несколько серверов, нужно определить до какого из них найменьшая средняя
     * задержка (за несколько попыток)
     * */
    @Test
    fun test() {
        val random = Random()

        //random delay 100..200 ms
        fun getDelay(): Long {
            return Math.abs(random.nextInt(100)).toLong() + 100
        }

        //simulate network request
        fun ping(hostname: String): Observable<String> {
            return Observable.just(Unit)
                    .concatMap {
                        val delay = getDelay()
                        println("ping to $hostname = ${delay}ms")

                        return@concatMap Observable.timer(delay, TimeUnit.MILLISECONDS)
                                .zipWith(Observable.just(hostname))
                                .map { it.second }
                    }
        }

        val obs1 = ping("88.244.13.41")
        val obs2 = ping("88.244.13.42")
        val obs3 = ping("88.244.13.43")

        Observable.just(Unit)
                .concatMap {
                    return@concatMap Observable.amb(listOf(obs1, obs2, obs3))
                }
                .doOnNext { println("lowest ping at current attempt = ${it}") }
                .doOnNext { println() }
                .repeat(15)
                .toList()
                .map { lowestPingHostList ->
                    return@map lowestPingHostList
                            .groupingBy { hostname -> hostname }
                            .eachCount()
                            .maxBy { it.value }?.key
                }
                .subscribe({ println("Host with the lowest average ping = $it") }, {})

        Thread.sleep(4000)
    }
}