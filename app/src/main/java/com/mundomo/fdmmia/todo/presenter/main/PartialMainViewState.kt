package com.mundomo.fdmmia.todo.presenter.main

import com.mundomo.fdmmia.todo.domain.model.Todo
import timber.log.Timber

sealed class PartialMainViewState {

    data class Todos(val todos: List<Todo>) : PartialMainViewState()

    data class Error(val error: Throwable) : PartialMainViewState() {
        init {
            Timber.e(error)
        }
    }

    object ShowLoading : PartialMainViewState()

    object HideLoading : PartialMainViewState()

    object ClearSingleEvent : PartialMainViewState()

}