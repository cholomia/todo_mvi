package com.mundomo.fdmmia.todo.presenter.detail

import com.mundomo.fdmmia.todo.domain.model.Todo
import timber.log.Timber

sealed class PartialTodoDetailViewState {

    data class ShowTodo(val todo: Todo) : PartialTodoDetailViewState()

    data class Error(val error: Throwable) : PartialTodoDetailViewState() {
        init {
            Timber.e(error)
        }
    }

    object NoTodoFound : PartialTodoDetailViewState()

    object OnDeleteSuccess : PartialTodoDetailViewState()

    object ShowLoading : PartialTodoDetailViewState()

    object HideLoading : PartialTodoDetailViewState()

    object ClearSingleEvent : PartialTodoDetailViewState()

}