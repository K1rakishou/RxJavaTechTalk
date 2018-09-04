import io.reactivex.Observable
import org.junit.Test

class `08_1_nested_rx_streams_error_handling` {

    /**
     * Зачем создавать один реактивный стрим внутри другого?
     * Представим, что у нас есть последовательность элементов, которая обрабатывается
     * некоторой функцией. Допустим, что некоторые элементы могут быть неверные.
     * Мы не хотим обрабатывать такие элементы и бросаем исключение. Реактивные стримы спроектированы
     * таким образом, что исключение - это терминальный ивент, т.е. ивент который завершает стрим.
     * После этого ивента не должно быть никаких других ивентов.
     *
     * (В реальности никто не запрещает этого, можно создать обзёрвабл через Observable.create
     * и передать в стрим несколько onError или onComplete, но это нарушение контракта).
     * */
    @Test
    fun testNestedObservables1() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .doOnNext { if (it % 2 == 0) throw IllegalStateException("error") }
                .subscribe({ value -> println(value) }, { error -> error.printStackTrace() })
    }


















































    /**
     * Допустим, нам надо в случае эксепшена не завершать стрим, а возвращать какой-то элемент
     * сигнализирующий о том, что произошла ошибка. Можно попытаться заюзать оператор
     * onErrorReturnItem (Или onErrorReturn/onErrorResumeNext), но это не совсем поможет,
     * потому что стрим всё-равно завершится, а мы хотим, чтобы он продолжал работу
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
     * При возникновении исключения внутренний стрим обработает его и завершится, тогда как
     * внешний стрим продолжит свою работу, по-скольку вместо ошибки получит нормальное значение.
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

        Thread.sleep(100)
    }






















































    /**
     * Другой вариант - заюзать один из операторов ***DelayError, которые запомнят все
     * выброшенные исключения в CompositeException и выбросят это исключение при
     * получении onComplete ивента
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