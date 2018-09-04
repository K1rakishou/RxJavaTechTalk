import io.reactivex.Observable
import org.junit.Test

class `01_rxjava_is_cold_by_default` {

    /**
     * Реактивные стримы по-умолчанию холодные. Это означает,
     * что стрим не будет ничего эмитить, если у него нет подписчиков
     * */
    @Test
    fun test_cold() {
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .doOnNext { value -> println("value = $value") }
    }
}