import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import org.junit.Test

class `03_rxjava_is_lazy_by_default` {

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

}