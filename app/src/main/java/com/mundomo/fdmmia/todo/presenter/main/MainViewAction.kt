package com.mundomo.fdmmia.todo.presenter.main

import com.mundomo.fdmmia.todo.presenter.base.MviViewAction

sealed class MainViewAction : MviViewAction {

    object GetLiveTodos : MainViewAction()

}