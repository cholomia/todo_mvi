package com.mundomo.fdmmia.todo.domain.data_source

import com.mundomo.fdmmia.todo.domain.enums.CacheStatus
import io.reactivex.Observable
import io.reactivex.Single

interface DataSource<Key, Raw, Parsed> {

    fun get(key: Key): Observable<Parsed>

    fun refresh(key: Key): Single<Parsed>

    fun query(key: Key): Observable<Parsed>

    fun fetch(key: Key): Single<Raw>

    fun parse(key: Key, raw: Raw): Parsed

    fun save(key: Key, parsed: Parsed): Single<Parsed>

    fun getCacheStatus(key: Key): CacheStatus

    fun updateCacheStatus(key: Key)

}