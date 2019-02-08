package com.mundomo.fdmmia.todo.data.data_source

import com.mundomo.fdmmia.todo.data.IO_SCHEDULER
import com.mundomo.fdmmia.todo.data.MAIN_SCHEDULER
import com.mundomo.fdmmia.todo.data.common.RealmInstance
import com.mundomo.fdmmia.todo.data.common.RepositoryCachePrefs
import com.mundomo.fdmmia.todo.data.dao.TodoDao
import com.mundomo.fdmmia.todo.data.network.ApiService
import com.mundomo.fdmmia.todo.domain.common.hours
import com.mundomo.fdmmia.todo.domain.common.minutes
import com.mundomo.fdmmia.todo.domain.data_source.BaseDataSource
import com.mundomo.fdmmia.todo.domain.enums.CacheStatus
import com.mundomo.fdmmia.todo.domain.model.Todo
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class TodoListDataSource @Inject constructor(
    private val apiService: ApiService,
    private val todoDao: TodoDao,
    private val realmInstance: RealmInstance,
    private val repositoryCachePrefs: RepositoryCachePrefs,
    @Named(IO_SCHEDULER) private val io: Scheduler,
    @Named(MAIN_SCHEDULER) private val main: Scheduler
) : BaseDataSource<Boolean, List<Todo>, List<Todo>>() {

    private val cacheKeyName: String = TodoListDataSource::class.java.name

    override fun query(key: Boolean): Observable<List<Todo>> = Observable.defer {
        todoDao.getLiveList(realmInstance.getRealm())
    }.subscribeOn(main)

    override fun fetch(key: Boolean): Single<List<Todo>> = apiService.getTodos()
        .subscribeOn(io)

    override fun parse(key: Boolean, raw: List<Todo>): List<Todo> = raw

    override fun save(key: Boolean, parsed: List<Todo>): Single<List<Todo>> = todoDao.save(parsed)
        .subscribeOn(io)


    override fun getCacheStatus(key: Boolean): CacheStatus = repositoryCachePrefs.getCacheUpdate(
        cacheKeyName,
        10.minutes.inMilliseconds.longValue,
        1.hours.inMilliseconds.longValue
    )

    override fun updateCacheStatus(key: Boolean) {
        repositoryCachePrefs.updateCacheUpdate(cacheKeyName)
    }

}