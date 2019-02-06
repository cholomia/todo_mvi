package com.mundomo.fdmmia.todo.di

import com.mundomo.fdmmia.todo.data.dao.TodoDao
import com.mundomo.fdmmia.todo.data.data_source.TodoDataSource
import com.mundomo.fdmmia.todo.data.data_source.TodoListDataSource
import com.mundomo.fdmmia.todo.data.network.ApiService
import com.mundomo.fdmmia.todo.data.repository.TodoRepositoryImpl
import com.mundomo.fdmmia.todo.domain.repository.TodoRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun todoRepository(
        apiService: ApiService,
        todoDataSource: TodoDataSource,
        todoListDataSource: TodoListDataSource,
        todoDao: TodoDao
    ): TodoRepository = TodoRepositoryImpl(apiService, todoDataSource, todoListDataSource, todoDao)

}