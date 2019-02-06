package com.mundomo.fdmmia.todo.data.network

import com.mundomo.fdmmia.todo.domain.model.Todo
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("posts")
    fun getTodos(): Single<List<Todo>>

    @GET("posts/{todo_id}")
    fun getTodo(@Path("todo_id") todoId: Long): Single<Todo>

    @DELETE("posts/{todo_id}")
    fun deleteTodo(@Path("todo_id") todoId: Long): Single<EmptyResponse>

}