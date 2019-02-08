package com.mundomo.fdmmia.todo.data.dao

import com.mundomo.fdmmia.todo.data.common.RealmHelper.rxTransaction
import com.mundomo.fdmmia.todo.data.common.findAllObservable
import com.mundomo.fdmmia.todo.data.common.findObservable
import com.mundomo.fdmmia.todo.data.common.saveToRealm
import com.mundomo.fdmmia.todo.domain.model.Todo
import io.reactivex.Observable
import io.reactivex.Single
import io.realm.Realm
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import javax.inject.Inject

class TodoDao @Inject constructor() {

    fun save(todos: List<Todo>): Single<List<Todo>> = rxTransaction { realm ->
        realm.where<Todo>()
            .not()
            .oneOf(Todo::id.name, todos.map { it.id }.toTypedArray())
            .findAll()
            .deleteAllFromRealm()
        realm.copyFromRealm(realm.copyToRealmOrUpdate(todos))
    }

    fun save(todo: Todo): Single<Todo> = todo.saveToRealm()

    fun getLiveList(realm: Realm): Observable<List<Todo>> = realm.where<Todo>()
        .findAllObservable()

    fun getLive(realm: Realm, todoId: Long): Observable<Todo> = realm.where<Todo>()
        .equalTo(Todo::id.name, todoId)
        .findObservable()

    fun delete(todoId: Long): Single<Boolean> = rxTransaction { realm ->
        realm.where<Todo>()
            .equalTo(Todo::id.name, todoId)
            .sort(Todo::id.name)
            .findAll()
            .deleteAllFromRealm()
    }

}