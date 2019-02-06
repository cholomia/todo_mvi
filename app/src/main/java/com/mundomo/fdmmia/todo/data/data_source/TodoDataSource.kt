package com.mundomo.fdmmia.todo.data.data_source

import com.mundomo.fdmmia.todo.data.IO_SCHEDULER
import com.mundomo.fdmmia.todo.data.MAIN_SCHEDULER
import com.mundomo.fdmmia.todo.data.common.RealmInstance
import com.mundomo.fdmmia.todo.data.dao.TodoDao
import com.mundomo.fdmmia.todo.data.network.ApiService
import com.mundomo.fdmmia.todo.domain.data_source.BaseDataSource
import com.mundomo.fdmmia.todo.domain.enums.CacheStatus
import com.mundomo.fdmmia.todo.domain.model.Todo
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class TodoDataSource @Inject constructor(
    private val apiService: ApiService,
    private val todoDao: TodoDao,
    private val realmInstance: RealmInstance,
    @Named(IO_SCHEDULER) private val io: Scheduler,
    @Named(MAIN_SCHEDULER) private val main: Scheduler
) : BaseDataSource<Long, Todo, Todo>() {

    override fun query(key: Long): Observable<Todo> = Observable.defer {
        todoDao.getLive(realmInstance.getRealm(), key)
    }.subscribeOn(main)

    override fun fetch(key: Long): Single<Todo> = apiService.getTodo(key)

    override fun parse(key: Long, raw: Todo): Todo = raw

    override fun save(key: Long, parsed: Todo): Single<Todo> = todoDao.save(parsed)

    override fun getCacheStatus(key: Long): CacheStatus = CacheStatus.STALE

    override fun updateCacheStatus(key: Long) {
        // todo
    }
}