import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class `13_concatmap_flatmap_switchmap` {

    private val random = Random()

    /**
     * Меняем порядок элементов на рандомный
     * */
    fun longOperation(value: Int): Observable<Int> {
        return Observable.just(value)
                .subscribeOn(Schedulers.io())
                .zipWith(Observable.timer(Math.abs(random.nextInt(100)).toLong(), TimeUnit.MILLISECONDS))
                .map { it.first }
                .doOnNext { println(Thread.currentThread().name) }
    }

    /**
     * Оператор concatMap используется для комбинирования операций, которые будут выполнены
     * последовательно (новая операция не будет запущена до тех пор пока старая не завершится)
     * */
    @Test
    fun test1() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .concatMap { longOperation (it) }
                .subscribe({ println("value = $it") })

        Thread.sleep(1500)
    }

    /**
     * Оператор flatMap используется для асинхронного комбинирования операций,
     * результаты от которых могут прийти в рандомном порядке (все операции запускаются одновременно)
     * */
    @Test
    fun test2() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .flatMap { longOperation (it) }
                .subscribe({ println("value = $it") })

        Thread.sleep(1500)
    }

    /**
     * flatMap можно превратить в concatMap просто установив параметр maxConcurrency в 1
     * */
    @Test
    fun test2_5() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .flatMap({ longOperation (it) }, 1)
                .subscribe({ println("value = $it") })

        Thread.sleep(1500)
    }

    /**
     * Если нужно выполнять операции асинхронно, но при этом получить результаты в том порядке
     * в каком они были выполнены, то существует оператор concatMapEager
     * */
    @Test
    fun test3() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .concatMapEager { longOperation (it) }
                .subscribe({ println("value = $it") })

        Thread.sleep(1500)
    }

    /**
     * Оператор switchMap используется для переключения с одной операции на другую.
     * При переключении switchMap отписывается от старой операции, прежде чем подписаться на новую
     * */
    @Test
    fun test4() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .switchMap {
                    return@switchMap longOperation(it)
                            .doOnSubscribe { println("subscribe") }
                            .doOnDispose { println("dispose") }
                }
                .subscribe({ println("value = $it") })

        Thread.sleep(1500)
    }
}