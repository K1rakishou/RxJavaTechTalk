import io.reactivex.Observable
import org.junit.Test

class `00_rxjava_wont_emit_anything_without_subscriber` {

    /**
     * Реактивные стримы холодные по-умолчанию. Это означает,
     * что стрим не будет ничего эмитить, если у него нет подписчиков
     * */
    @Test
    fun test_cold() {
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .doOnNext { value -> println("value = $value") }
    }
















































    /**
     * Исключение составляет ConnectableObservable
     * */
    @Test
    fun test_hot() {
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .doOnNext { value -> println("value = $value") }
                .publish()
                .connect()
    }
}