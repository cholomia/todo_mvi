package com.mundomo.fdmmia.todo.di

import com.mundomo.fdmmia.todo.presenter.detail.TodoDetailActivity
import com.mundomo.fdmmia.todo.presenter.main.MainActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: TodoDetailActivity)
}