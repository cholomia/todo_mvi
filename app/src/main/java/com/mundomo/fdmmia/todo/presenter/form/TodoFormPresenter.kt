package com.mundomo.fdmmia.todo.presenter.form

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

class TodoFormPresenter @Inject constructor(
    private val todoRepository: TodoRepository,
    @Named(IO_SCHEDULER) private val io: Scheduler,
    @Named(MAIN_SCHEDULER) private val main: Scheduler
) : MviBasePresenter<TodoFormView, TodoFormViewState>() {

    override fun bindIntents() {
        Timber.d("bindIntents")

        val getTodoIntent = intent(TodoFormView::getTodo)
            .flatMap(this::getTodoObservable)
            .doOnNext { Timber.d("render getTodoIntent: $it") }

        val createTodoIntent = intent(TodoFormView::createTodo)
            .flatMap(this::createTodoObservable)
            .doOnNext { Timber.d("render createTodoIntent: $it") }

        val initialState = TodoFormViewState()

        val stateObservable = Observable.mergeArray(
            getTodoIntent,
            createTodoIntent
        ).onErrorReturn { PartialTodoFormViewState.Error(it) }
            .switchMap { viewState ->
                when (viewState) {
                    is PartialTodoFormViewState.Error -> Observable.just<PartialTodoFormViewState>(
                        PartialTodoFormViewState.ClearSingleEvent
                    )
                        .startWith(viewState)
                    else -> Observable.just(viewState)
                }
            }
            .scan(initialState, this::viewStateReducer)
            .observeOn(main)

    }

    private fun getTodoObservable(todoId: Long): Observable<PartialTodoFormViewState> =
        todoRepository.getLiveTodo(todoId)
            .take(1)
            .map<PartialTodoFormViewState> { PartialTodoFormViewState.ShowTodo(it) }
            .startWith(PartialTodoFormViewState.ShowLoading)
            .onErrorReturn { PartialTodoFormViewState.Error(it) }
            .observeOn(main)

    private fun createTodoObservable(todo: Todo): Observable<PartialTodoFormViewState> =
        todoRepository.createTodo(todo)
            .toObservable()
            .map<PartialTodoFormViewState> { PartialTodoFormViewState.OnCreateSuccess }
            .startWith(PartialTodoFormViewState.ShowLoading)
            .onErrorReturn { PartialTodoFormViewState.Error(it) }
            .subscribeOn(io)

    private fun viewStateReducer(
        previousState: TodoFormViewState,
        changes: PartialTodoFormViewState
    ): TodoFormViewState = when (changes) {
        PartialTodoFormViewState.ShowLoading -> TODO()
        PartialTodoFormViewState.OnCreateSuccess -> TODO()
        PartialTodoFormViewState.ClearSingleEvent -> TODO()
        is PartialTodoFormViewState.ShowTodo -> TODO()
        is PartialTodoFormViewState.Error -> TODO()
    }
}