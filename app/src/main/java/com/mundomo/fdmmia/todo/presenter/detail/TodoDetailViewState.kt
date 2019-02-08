package com.mundomo.fdmmia.todo.presenter.detail

import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.presenter.base.MviViewState

data class TodoDetailViewState(
    var todo: Todo? = null,
    var error: Throwable? = null,
    var onDeleteSuccess: Boolean = false,
    var isLoading: Boolean = false
) : MviViewState