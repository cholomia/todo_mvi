package com.mundomo.fdmmia.todo.domain.repository

import com.mundomo.fdmmia.todo.domain.model.Todo
import io.reactivex.Observable

interface TodoRepository {

    fun getLiveTodos(): Observable<List<Todo>>

    fun refreshTodos(): Observable<Boolean>

    fun getLiveTodo(todoId: Long): Observable<Todo>

    fun deleteTodo(todoId: Long): Observable<Boolean>

}