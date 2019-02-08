package com.mundomo.fdmmia.todo.presenter.main

import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.presenter.base.MviViewState

data class MainViewState(
    var todos: List<Todo> = emptyList(),
    var error: Throwable? = null,
    var isLoading: Boolean = false,
    var clearSingleEvent: Boolean = false
) : MviViewState