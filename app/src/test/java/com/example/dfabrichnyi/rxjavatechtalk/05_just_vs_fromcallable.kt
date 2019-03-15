import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertNotEquals
import org.junit.Test

class `05_just_vs_fromcallable` {

    /**
     * Предположим, что у нас есть какая-то синхронная функция, которую мы хотим завернуть в
     * реактивный стрим и эта функция кидает исключение, если её вызывают из main потока
     * */
    private fun longRunningOperation(): String {
        val currentThreadName = Thread.currentThread().name

        println("current thread name = $currentThreadName")
        assertNotEquals("Cannot be executed on the main thread!", "main", currentThreadName)

        //симуляция какой-либо долгой операции
        Thread.sleep(500)
        return "done"
    }
















































    /**
     * Есть много способов для попадания из императивного мира в реактивный. Один из них - оператор just.
     * Однако у него есть подводный камень - этот оператор ожидает на входе константу, т.е. готовый
     * результат, а не функцию. Это означает, что в данном случае сначала longRunningOperation
     * будет выполнена в основном потоке и только ПОСЛЕ ЭТОГО результат будет передан оператору just.
     * */
    @Test
    fun test1() {
        Observable.just(longRunningOperation())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    println(result)
                }, { error ->
                    error.printStackTrace()
                })

        Thread.sleep(1000)
    }










































 










    /**
     * Для того чтобы выполнить какую-либо синхронную функцию в реактивном стриме есть
     * оператор fromCallable
     * */
    @Test
    fun test2() {
        Observable.fromCallable { longRunningOperation() }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    println("result = $result")
                }, { error ->
                    error.printStackTrace()
                })

        Thread.sleep(1000)
    }
}