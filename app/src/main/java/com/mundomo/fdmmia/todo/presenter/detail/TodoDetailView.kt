package com.mundomo.fdmmia.todo.presenter.detail

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface TodoDetailView : MvpView {

    fun getTodo(): Observable<Long>

    fun deleteTodo(): Observable<Long>

    fun render(viewState: TodoDetailViewState)

}