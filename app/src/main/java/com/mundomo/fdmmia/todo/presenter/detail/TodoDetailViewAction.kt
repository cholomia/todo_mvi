package com.mundomo.fdmmia.todo.presenter.detail

import com.mundomo.fdmmia.todo.presenter.base.MviViewAction

sealed class TodoDetailViewAction : MviViewAction {

    object GetTodo : TodoDetailViewAction()
}