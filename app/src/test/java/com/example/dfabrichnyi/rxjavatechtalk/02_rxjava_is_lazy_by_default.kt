import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import org.junit.Test

class `02_rxjava_is_lazy_by_default` {

    /**
     * Реактивные стримы по-умолчанию ленивы. Это означает, что мы можем создавать
     * повторяющиеся до бесконечности последовательности обзерваблов
     * */
    @Test
    fun test() {
        val infiniteStreamOfZeroesAndOnes = Observable.just(0, 1)
                .repeat()

        infiniteStreamOfZeroesAndOnes
                .take(10)
                .subscribe({ value -> println("value = $value") })
    }

























































    /**
     * Например пользователь тыкает кнопку и мы хотим с каждым кликом менять циклично некий стейт
     * */
    @Test
    fun test2() {
        val stateObservable = Observable.just(4, 3, 1, 2)
                .repeat()

        Observable.range(0, 16)
                .zipWith(stateObservable)
                .subscribe({ println("click #${it.first}, current state = ${it.second}") })
    }
}