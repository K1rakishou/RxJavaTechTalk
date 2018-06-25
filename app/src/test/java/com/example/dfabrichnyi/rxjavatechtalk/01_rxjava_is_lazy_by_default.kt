import io.reactivex.Observable
import org.junit.Test

class `01_rxjava_is_lazy_by_default` {

    /**
     * Реактивные стримы ленивы. Это означает, что можно создавать бесконечные стримы.
     * */
    @Test
    fun test() {
        val infiniteStreamOfZeroesAndOnes = Observable.just(0, 1).repeat()

        infiniteStreamOfZeroesAndOnes
                .take(10)
                .subscribe({ value -> println("value = $value") })
    }
}