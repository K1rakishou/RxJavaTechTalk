import io.reactivex.Observable
import org.junit.Test

class `03_rxjava_is_synchronous_by_default` {

    /**
     * Реактивные стримы по-умолчанию синхронны. Это означает, что все работает в том потоке на котором
     * был произведён сабскрайб (исключения составляют операторы по-умолчанию работающие на каком-то шедулере,
     * например timer или interval)
     * */
    @Test
    fun test_print_current_thread_name() {
        Observable.just(1)
                .doOnNext { println("Current thread = ${Thread.currentThread().name}") }
                .subscribe()
    }
}