import io.reactivex.Observable
import org.junit.Test

class `05_fromcallable_vs_defer` {

    /**
     * Помимо fromCallable существует оператор defer.
     *
     * Он очень похож на fromCallable с той разницей, что он ожидает получить на выходе из лямбы
     * другой реактивный стрим, который будет выполнен только после того как кто-то подпишется
     * на этот стрим. Это даёт нам возможность использовать его для создания реактивного стрима
     * содержащего несколько элементов.
     * */

    @Test
    fun test1() {
        Observable.fromCallable { 1 }
                .subscribe({ println("value = $it") })
    }

    @Test
    fun test2() {
        Observable.defer { Observable.just(1, 2, 3, 4) }
                .subscribe({ println("value = $it") })
    }
}