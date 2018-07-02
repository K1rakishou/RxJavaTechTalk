package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import org.junit.Test

class rxjava_split_stream {

    sealed class CacheResult<V> {
        class Found<V>(val value: V) : CacheResult<V>()
        class NotFound<V>(val value: V) : CacheResult<V>()
    }

    fun getFromCache(id: Int): CacheResult<Int> {
        if (id % 2 == 0) {
            return CacheResult.NotFound(id)
        }

        return CacheResult.Found(id)
    }

    fun getFromRemoteServer(ids: List<Int>): Observable<Int> {
        return Observable.fromIterable(ids)
    }

    /**
     * Пример использования разделения стримов с последующим мержем.
     *
     * Допустим нам необходимо для каждого элемента в стриме (id) получить некоторое значение из кеша (БД например),
     * а если этого значения нет, то получить его от сервера. При этом необходимо отделить все найденные в кеше значения,
     * от не найденых (чтобы можно было сделать запрос к серверу одним батчем).
     * */
    @Test
    fun test() {
        val mapped = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .map(this::getFromCache)
                .publish()
                .autoConnect(2)

        val foundValues = mapped
                .filter { result -> result is CacheResult.Found }
                .cast(CacheResult.Found::class.java)
                .doOnNext { println("found in the cache = ${it.value}") }
                .map { it.value as Int }

        val notFoundValues = mapped
                .filter { result -> result is CacheResult.NotFound }
                .cast(CacheResult.NotFound::class.java)
                .doOnNext { println("Not found in the cache = ${it.value}. Fetching from the server") }
                .toList()
                .flatMapObservable { resultList ->
                    val idsToRequestFromServer = resultList.map { it.value as Int }
                    return@flatMapObservable getFromRemoteServer(idsToRequestFromServer)
                }
                .doOnNext { println("Got from the server $it") }

        Observable.merge(foundValues, notFoundValues)
                .subscribe()
    }












































    /**
     * Того же самого результата можно добиться и не объединяя стримы в один после их разделения.
     * Можно просто подписаться отдельно на каждый из полученных стримов.
     * */
    @Test
    fun test2() {
        val mapped = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .map(this::getFromCache)
                .publish()
                .autoConnect(2)

        mapped
                .filter { result -> result is CacheResult.Found }
                .cast(CacheResult.Found::class.java)
                .doOnNext { println("found in the cache = ${it.value}") }
                .map { it.value as Int }
                .subscribe()

        mapped
                .filter { result -> result is CacheResult.NotFound }
                .cast(CacheResult.NotFound::class.java)
                .doOnNext { println("Not found in the cache = ${it.value}. Fetching from the server") }
                .toList()
                .flatMapObservable { resultList ->
                    val idsToRequestFromServer = resultList.map { it.value as Int }
                    return@flatMapObservable getFromRemoteServer(idsToRequestFromServer)
                }
                .doOnNext { println("Got from the server $it") }
                .subscribe()
    }
}