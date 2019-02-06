package com.mundomo.fdmmia.todo.presenter.main

import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.presenter.base.MviViewState
import timber.log.Timber

sealed class MainViewState : MviViewState {

    data class Todos(val todos: List<Todo>) : MainViewState()

    data class Error(val error: Throwable) : MainViewState() {
        init {
            Timber.e(error)
        }
    }

    object ShowLoading : MainViewState()

    object HideLoading : MainViewState()

    object ClearSingleEvent : MainViewState()

}