import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class `03_rxjava_is_synchronous_by_default` {

    /**
     * Реактивные стримы синхронны по-умолчанию. Это означает, что все вычисления происходят в том потоке,
     * из которого стрим был вызван (исключения составляют операторы по-умолчанию работающие на каком-то шедулере,
     * например timer или interval)
     * */
    @Test
    fun test_print_current_thread_name() {
        Observable.just(1)
                .doOnNext { println("Current thread = ${Thread.currentThread().name}") }
                .subscribe()
    }
}