package com.mundomo.fdmmia.todo.data.repository

import com.mundomo.fdmmia.todo.data.dao.TodoDao
import com.mundomo.fdmmia.todo.data.data_source.TodoDataSource
import com.mundomo.fdmmia.todo.data.data_source.TodoListDataSource
import com.mundomo.fdmmia.todo.data.network.ApiService
import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.domain.repository.TodoRepository
import io.reactivex.Observable

class TodoRepositoryImpl(
    private val apiService: ApiService,
    private val todoDataSource: TodoDataSource,
    private val todoListDataSource: TodoListDataSource,
    private val todoDao: TodoDao
) : TodoRepository {

    override fun getLiveTodos(): Observable<List<Todo>> = todoListDataSource.get(true)

    override fun refreshTodos(): Observable<Boolean> = todoListDataSource.refresh(true)
        .map { true }
        .toObservable()

    override fun getLiveTodo(todoId: Long): Observable<Todo> = todoDataSource.get(todoId)

    override fun deleteTodo(todoId: Long): Observable<Boolean> = apiService.deleteTodo(todoId)
        .flatMap { todoDao.delete(todoId) }
        .toObservable()

}