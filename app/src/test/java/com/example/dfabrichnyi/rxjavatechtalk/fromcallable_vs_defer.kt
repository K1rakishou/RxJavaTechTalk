package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import org.junit.Test

class fromcallable_vs_defer {

    /**
     * Помимо fromCallable существует оператор defer.
     *
     * Он очень похож на fromCallable с той разницей, что он ожидает получить на выходе из лямбы
     * другой реактивный стрим, который будет выполнен только после того как кто-то подпишется на этот стрим.
     * Это даёт нам возможность использовать его для создания реактивного стрима содержащего несколько элементов.
     * Но разве того же самого нельзя добиться с потощью оператора just? (Observable.just(1, 2, 3, 4).
     * Нет, нельзя. Как мы уже знаем, мы не можем передать в just функцию, поскольку он ожидает константу а не лямбду.
     *
     * TLDR: Defer объединяет в себя лучшее из just и fromCallable.
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