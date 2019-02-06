package com.mundomo.fdmmia.todo.presenter.detail

import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.presenter.base.MviViewState
import timber.log.Timber

sealed class TodoDetailViewState : MviViewState {

    data class ShowTodo(val todo: Todo) : TodoDetailViewState()

    data class Error(val error: Throwable) : TodoDetailViewState() {
        init {
            Timber.e(error)
        }
    }

    object OnDeleteSuccess : TodoDetailViewState()

    object ShowLoading : TodoDetailViewState()

    object HideLoading : TodoDetailViewState()

    object ClearSingleEvent : TodoDetailViewState()

}