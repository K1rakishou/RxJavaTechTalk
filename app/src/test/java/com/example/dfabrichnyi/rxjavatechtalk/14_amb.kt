import io.reactivex.Observable
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class `14_amb` {

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

        fun getDelayObservable(debugName: String): Observable<Long> {
            return Observable.just(Unit)
                    .concatMap {
                        val delay = getDelay()
                        println("$debugName = $delay")

                        return@concatMap Observable.timer(delay, TimeUnit.MILLISECONDS)
                    }
        }

        val obs1 = getDelayObservable("host1")
                .map { "88.244.13.41" }

        val obs2 = getDelayObservable("host2")
                .map { "88.244.13.42" }

        val obs3 = getDelayObservable("host3")
                .map { "88.244.13.43" }

        Observable.just(Unit)
                .concatMap {
                    return@concatMap Observable.amb(listOf(obs1, obs2, obs3))
                }
                .doOnNext { println("lowest ping at current attempt = ${it}") }
                .repeat(15)
                .toList()
                .map { lowestPingHostList ->
                    return@map lowestPingHostList
                            .groupingBy { it }
                            .eachCount()
                            .maxBy { it.value }?.key
                }
                .subscribe({ println("Host with the lowest average ping = $it") }, {})

        Thread.sleep(4000)
    }
}