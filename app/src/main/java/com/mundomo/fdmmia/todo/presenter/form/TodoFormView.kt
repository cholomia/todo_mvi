package com.mundomo.fdmmia.todo.presenter.form

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.mundomo.fdmmia.todo.domain.model.Todo
import io.reactivex.Observable

interface TodoFormView : MvpView {

    fun getTodo(): Observable<Long>

    fun createTodo(): Observable<Todo>

    fun render(viewState: TodoFormViewState)

}