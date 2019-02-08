package com.mundomo.fdmmia.todo.presenter.main

import android.os.Bundle
import android.transition.TransitionManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.mundomo.fdmmia.todo.R
import com.mundomo.fdmmia.todo.di.ActivityModule
import com.mundomo.fdmmia.todo.di.AppComponent
import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.presenter.base.BaseMviActivity
import com.mundomo.fdmmia.todo.presenter.detail.TodoDetailActivity
import com.mundomo.fdmmia.todo.presenter.main.adapter.TodoAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class MainActivity :
    BaseMviActivity<MainView, MainViewState, MainPresenter, MainViewAction>(),
    MainView {

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    lateinit var todoAdapter: TodoAdapter

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(ActivityModule(this))
            .inject(this)
    }

    override fun createPresenter(): MainPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoAdapter = TodoAdapter(this::onSelectTodo)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = todoAdapter
        }

        btn_add_todo.setOnClickListener {
            // todo: go to add todo screen
        }
    }

    override fun onStart() {
        super.onStart()
        viewAction.onNext(MainViewAction.GetLiveTodos)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Timber.d("onRestoreInstanceState")
    }

    override fun getLiveTodos(): Observable<Boolean> = viewAction
        .filter { it === MainViewAction.GetLiveTodos }
        .map { true }
        .doOnNext { Timber.d("start getLiveTodos: $it") }

    override fun refreshTodos(): Observable<Boolean> = swipe_refresh_layout.refreshes()
        .map { true }
        .doOnNext { Timber.d("start refreshTodos: $it") }

    override fun render(viewState: MainViewState) {
        TransitionManager.beginDelayedTransition(main_view)
        Timber.d("render: $viewState")
        setTodos(viewState.todos)
        showLoading(viewState.isLoading)
        viewState.error?.let(this::showError)
    }

    private fun setTodos(todos: List<Todo>) {
        Timber.d("render setTodos - size = ${todos.size}")
        todoAdapter.submitList(todos)
    }

    private fun showLoading(isLoading: Boolean) {
        swipe_refresh_layout.post {
            swipe_refresh_layout.isRefreshing = isLoading
        }
    }

    private fun onSelectTodo(todo: Todo) {
        TodoDetailActivity.start(this, todo)
    }

}
