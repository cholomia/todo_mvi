package com.mundomo.fdmmia.todo.presenter.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface MainView : MvpView {

    fun getLiveTodos(): Observable<Boolean>

    fun refreshTodos(): Observable<Boolean>

    fun render(viewState: MainViewState)

}