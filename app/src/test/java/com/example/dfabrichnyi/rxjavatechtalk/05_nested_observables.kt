import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class `05_nested_observables` {

    /**
     * Зачем нужны вложенные стримы?
     * Представим, что у нас есть последовательность элементов, которая обрабатывается некоторой функцией.
     * Допустим, что некоторые элементы могут быть косячные. В таком случае функция бросит исключение.
     * Реактивные стримы спроектированы таким образом, что исключение - это терминальный ивент,
     * т.е. ивент который завершает стрим. После этого ивента не может быть никаких других ивентов.
     * */
    @Test
    fun testNestedObservables1() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .doOnNext { if (it % 2 == 0) throw IllegalStateException("error") }
                .subscribe({ value -> println(value) }, { error -> error.printStackTrace() })
    }


















































    /**
     * Допустим, нам надо в случае ошибки не завершать стрим, а возвращать какой-то элемент
     * сигнализирующий о том, что произошла ошибка. Можно попытаться заюзать оператор
     * onErrorReturnItem (Или onErrorReturn/onErrorResumeNext), но это не совсем поможет,
     * потому что стрим всё-равно завершится
     * */
    @Test
    fun testNestedObservables2() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .doOnNext { if (it % 2 == 0) throw IllegalStateException("error") }
                .onErrorReturnItem(-1)
                .subscribe({ value -> println(value) })
    }


















































    /**
     * Один из вариантов решения этой проблемы - создать внутренний стрим.
     * При возникновении исключения внутренний стрим вернёт -1 и завершится, тогда как
     * внешний стрим продолжит свою работу
     * */
    @Test
    fun testNestedObservables3() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .concatMap { v ->
                    return@concatMap Observable.just(v)
                            .doOnNext { if (it % 2 == 0) throw IllegalStateException("error") }
                            .onErrorReturnItem(-1)
                }
                .subscribe({ value -> println(value) })
    }






















































    /**
     * Другой вариант - заюзать варинты операторов ***DelayError, которые запомнят все выброшенные исключения
     * в CompositeException и выбросят это исключение при получении onComplete ивента
     * */
    @Test
    fun testNestedObservables4() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .concatMapDelayError { v ->
                    return@concatMapDelayError Observable.just(v)
                            .doOnNext { if (it % 2 == 0) throw IllegalStateException("error") }
                }
                .subscribe({ value -> println(value) }, { error -> error.printStackTrace() })
    }
}