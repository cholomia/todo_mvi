package com.mundomo.fdmmia.todo.domain.data_source

import com.mundomo.fdmmia.todo.domain.enums.CacheStatus
import com.mundomo.fdmmia.todo.domain.exception.NoRecordsFoundException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.functions.Function

abstract class BaseDataSource<Key, Raw, Parsed> : DataSource<Key, Raw, Parsed> {

    companion object {
        const val IGNORE_KEY = 0L
    }

    private val isInitialDataLoaded = mutableMapOf<Key, Boolean>()

    /**
     * query from local database,
     */
    override fun get(key: Key): Observable<Parsed> = when (getCacheStatus(key)) {
        CacheStatus.FRESH -> query(key)
            .onErrorResumeNext(errorResumeFunction(key))
            .switchMap { forceRefreshIfEmptyObservable(key, it) }
        CacheStatus.STALE -> query(key)
            .take(1)
            .onErrorResumeNext(errorResumeFunction(key))
            .switchMap { parsed ->
                refresh(key)
                    .toObservable()
                    .switchMap { response ->
                        query(key)
                            .startWith(response)
                    }
                    .startWith(parsed)
            }
        CacheStatus.EXPIRED -> refresh(key)
            .toObservable()
            .switchMap { parsed ->
                query(key)
                    .startWith(parsed)
            }
    }

    private fun errorResumeFunction(key: Key): Function<Throwable, ObservableSource<out Parsed>> =
        Function { error ->
            when {
                error is NoRecordsFoundException && isInitialDataLoaded[key] ?: true -> refresh(key)
                    .toObservable()
                    .flatMap { parsed ->
                        query(key)
                            .startWith(parsed)
                    }
                else -> throw error
            }
        }

    /**
     * Trigger fetch data from network and save result
     */
    override fun refresh(key: Key): Single<Parsed> = fetch(key)
        .map { parse(key, it) }
        .flatMap { save(key, it) }
        .map { parsed ->
            updateCacheStatus(key)
            parsed
        }
        .doFinally { isInitialDataLoaded[key] = false }

    private fun forceRefreshIfEmptyObservable(
        key: Key,
        parsed: Parsed
    ): Observable<Parsed> = when {
        isInitialDataLoaded[key] ?: true &&
                (parsed as? Collection<*>)?.isEmpty() == true -> refresh(key)
            .toObservable()
        else -> Observable.just(parsed)
    }

}

