package com.mundomo.fdmmia.todo.presenter.form

import com.mundomo.fdmmia.todo.domain.model.Todo
import timber.log.Timber

sealed class PartialTodoFormViewState {
    object ShowLoading : PartialTodoFormViewState()
    object OnCreateSuccess : PartialTodoFormViewState()
    object ClearSingleEvent : PartialTodoFormViewState()

    data class ShowTodo(val todo: Todo) : PartialTodoFormViewState()
    data class Error(val error: Throwable) : PartialTodoFormViewState() {
        init {
            Timber.e(error)
        }
    }
}