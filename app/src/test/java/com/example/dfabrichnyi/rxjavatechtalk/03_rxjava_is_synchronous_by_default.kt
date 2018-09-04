import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import org.junit.Test
import java.util.concurrent.TimeUnit

class `03_rxjava_is_synchronous_by_default` {

    /**
     * Реактивные стримы по-умолчанию синхронны. Это означает, что все работает в том потоке
     * на котором был произведён сабскрайб
     * */
    @Test
    fun test_print_current_thread_name() {
        Observable.just(1)
                .doOnNext { println("Current thread = ${Thread.currentThread().name}") }
                .subscribe()
    }



























































    /**
     * Исключения составляют операторы по-умолчанию работающие на каком-то шедулере,
     * например timer или interval (они помечены аннотацией @SchedulerSupport). Почти все они
     * работают на Schedulers.computation()
     * */
    @Test
    fun test_zip_with_timer() {
        Observable.just(1)
                .zipWith(Observable.timer(50, TimeUnit.MILLISECONDS))
                .doOnNext { println("Current thread = ${Thread.currentThread().name}") }
                .subscribe()

        Thread.sleep(100)
    }
}