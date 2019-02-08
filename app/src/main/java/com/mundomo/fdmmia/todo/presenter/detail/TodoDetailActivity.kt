package com.mundomo.fdmmia.todo.presenter.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.jakewharton.rxbinding3.view.clicks
import com.mundomo.fdmmia.todo.R
import com.mundomo.fdmmia.todo.di.ActivityModule
import com.mundomo.fdmmia.todo.di.AppComponent
import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.presenter.base.BaseMviActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_todo_detail.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class TodoDetailActivity :
    BaseMviActivity<TodoDetailView, TodoDetailViewState, TodoDetailPresenter, TodoDetailViewAction>(),
    TodoDetailView {

    companion object {
        private const val TODO_ID = "todo_id"
        fun start(context: Context, todo: Todo) {
            val intent = Intent(context, TodoDetailActivity::class.java)
            intent.putExtra(TODO_ID, todo.id)
            context.startActivity(intent)
        }
    }

    @Inject
    lateinit var presenterProvider: Provider<TodoDetailPresenter>

    private val todoId: Long by lazy { intent.getLongExtra(TODO_ID, -1L) }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.plus(ActivityModule(this))
            .inject(this)
    }

    override fun createPresenter(): TodoDetailPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_detail)
    }

    override fun getTodo(): Observable<Long> = Observable.just(todoId)
        .doOnNext { Timber.d("start getTodo: $it") }

    override fun deleteTodo(): Observable<Long> = btn_delete.clicks()
        .map { todoId }
        .doOnNext { Timber.d("start deleteTodo: $it") }

    override fun render(viewState: TodoDetailViewState) {
        TransitionManager.beginDelayedTransition(todo_detail_view)
        when (viewState) {
            is TodoDetailViewState.ShowTodo -> showTodo(viewState.todo)
            is TodoDetailViewState.Error -> showError(viewState.error)
            TodoDetailViewState.OnDeleteSuccess -> onDeleteSuccess()
            TodoDetailViewState.ShowLoading -> showLoading(true)
            TodoDetailViewState.HideLoading -> showLoading(false)
            TodoDetailViewState.ClearSingleEvent -> Timber.d("clear single event")
        }
    }

    private fun showTodo(todo: Todo) {
        txt_title.text = todo.title
        txt_body.text = todo.body
        btn_update.setOnClickListener {
            // todo
        }
    }

    private fun onDeleteSuccess() {
        AlertDialog.Builder(this)
            .setTitle("Delete Success")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }

    // todo: prevent user interaction when loading by removing detail views
    private fun showLoading(isLoading: Boolean) {
        progress_bar.visibility = when {
            isLoading -> View.VISIBLE
            else -> View.GONE
        }
    }

}