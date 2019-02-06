package com.mundomo.fdmmia.todo.presenter.detail

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.mundomo.fdmmia.todo.data.IO_SCHEDULER
import com.mundomo.fdmmia.todo.data.MAIN_SCHEDULER
import com.mundomo.fdmmia.todo.domain.model.Todo
import com.mundomo.fdmmia.todo.domain.repository.TodoRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class TodoDetailPresenter @Inject constructor(
    private val todoRepository: TodoRepository,
    @Named(IO_SCHEDULER) private val io: Scheduler,
    @Named(MAIN_SCHEDULER) private val main: Scheduler
) : MviBasePresenter<TodoDetailView, TodoDetailViewState>() {

    override fun bindIntents() {
        Timber.d("bindIntents")

        val getTodoIntent = intent(TodoDetailView::getTodo)
            .flatMap(this::getTodoObservable)
            .doOnNext { Timber.d("render getTodoIntent: $it") }

        val deleteTodoIntent = intent(TodoDetailView::deleteTodo)
            .flatMap(this::deleteTodoObservable)
            .doOnNext { Timber.d("render deleteTodo: $it") }

        val allObservable = Observable.mergeArray(
            getTodoIntent,
            deleteTodoIntent
        ).onErrorReturn { TodoDetailViewState.Error(it) }
            .switchMap { viewState ->
                when (viewState) {
                    is TodoDetailViewState.Error -> Observable.just<TodoDetailViewState>(
                        TodoDetailViewState.ClearSingleEvent
                    ).startWith(viewState)
                    else -> Observable.just(viewState)
                }
            }
            .observeOn(main)

        subscribeViewState(allObservable, TodoDetailView::render)
    }

    private fun getTodoObservable(todoId: Long): Observable<TodoDetailViewState> =
        todoRepository.getLiveTodo(todoId)
            .map<TodoDetailViewState> { TodoDetailViewState.ShowTodo(it) }
            .startWith(TodoDetailViewState.ShowLoading)
            .onErrorReturn { TodoDetailViewState.Error(it) }
            .switchMap { viewState ->
                when (viewState) {
                    TodoDetailViewState.ShowLoading -> Observable.just(viewState)
                    else -> Observable.just<TodoDetailViewState>(TodoDetailViewState.HideLoading)
                        .startWith(viewState)
                }
            }
            .subscribeOn(main)


    private fun deleteTodoObservable(todoId: Long): Observable<TodoDetailViewState> =
        todoRepository.deleteTodo(todoId)
            .map<TodoDetailViewState> { TodoDetailViewState.OnDeleteSuccess }
            .startWith(TodoDetailViewState.ShowLoading)
            .onErrorReturn { TodoDetailViewState.Error(it) }
            .switchMap { viewState ->
                when (viewState) {
                    TodoDetailViewState.ShowLoading -> Observable.just(viewState)
                    else -> Observable.just<TodoDetailViewState>(TodoDetailViewState.HideLoading)
                        .startWith(viewState)
                }
            }
            .subscribeOn(io)

}