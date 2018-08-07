import io.reactivex.Observable
import org.junit.Test

class `02_rxjava_is_lazy_by_default` {

    /**
     * Реактивные стримы по-умолчанию ленивы. Это означает, что при попытке создать бесконечный стрим,
     * мы не словим ООМ тут же. Элементы будут генерироваться в рантайме, постепенно, а не все за раз.
     * Похоже на курсор из андроида.
     * */
    @Test
    fun test() {
        val infiniteStreamOfZeroesAndOnes = Observable.just(0, 1).repeat()

        infiniteStreamOfZeroesAndOnes
                .take(10)
                .subscribe({ value -> println("value = $value") })
    }
}