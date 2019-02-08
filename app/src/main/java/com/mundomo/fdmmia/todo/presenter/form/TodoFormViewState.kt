package com.mundomo.fdmmia.todo.presenter.form

import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.presenter.base.MviViewState

data class TodoFormViewState(
    var todo: Todo? = null
) : MviViewState {
}