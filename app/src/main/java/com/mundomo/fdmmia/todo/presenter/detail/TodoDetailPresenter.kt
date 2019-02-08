package com.mundomo.fdmmia.todo.presenter.detail

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.mundomo.fdmmia.todo.data.IO_SCHEDULER
import com.mundomo.fdmmia.todo.data.MAIN_SCHEDULER
import com.mundomo.fdmmia.todo.domain.exception.NoRecordsFoundException
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

        val initialState = TodoDetailViewState()
        val allObservable = Observable.mergeArray(
            getTodoIntent,
            deleteTodoIntent
        ).onErrorReturn { PartialTodoDetailViewState.Error(it) }
            .switchMap { viewState ->
                when (viewState) {
                    is PartialTodoDetailViewState.Error -> Observable.just<PartialTodoDetailViewState>(
                        PartialTodoDetailViewState.ClearSingleEvent
                    ).startWith(viewState)
                    else -> Observable.just(viewState)
                }
            }
            .scan(initialState, this::viewStateReducer)
            .distinctUntilChanged()
            .doOnNext { Timber.d("allObservable render: $it") }
            .observeOn(main)

        subscribeViewState(allObservable, TodoDetailView::render)
    }

    private fun getTodoObservable(todoId: Long): Observable<PartialTodoDetailViewState> =
        todoRepository.getLiveTodo(todoId)
            .map<PartialTodoDetailViewState> { PartialTodoDetailViewState.ShowTodo(it) }
            .startWith(PartialTodoDetailViewState.ShowLoading)
            .onErrorReturn {
                when (it) {
                    is NoRecordsFoundException -> PartialTodoDetailViewState.NoTodoFound
                    else -> PartialTodoDetailViewState.Error(it)
                }
            }
            .subscribeOn(main)

    private fun deleteTodoObservable(todoId: Long): Observable<PartialTodoDetailViewState> =
        todoRepository.deleteTodo(todoId)
            .toObservable()
            .map<PartialTodoDetailViewState> { PartialTodoDetailViewState.OnDeleteSuccess }
            .startWith(PartialTodoDetailViewState.ShowLoading)
            .onErrorReturn { PartialTodoDetailViewState.Error(it) }
            .subscribeOn(io)

    private fun viewStateReducer(
        previousState: TodoDetailViewState,
        changes: PartialTodoDetailViewState
    ): TodoDetailViewState = when (changes) {
        is PartialTodoDetailViewState.ShowTodo -> TodoDetailViewState(todo = changes.todo)
        is PartialTodoDetailViewState.Error -> {
            previousState.error = changes.error
            previousState.isLoading = false
            previousState
        }
        PartialTodoDetailViewState.NoTodoFound -> {
            previousState.isLoading = false
            previousState
        }
        PartialTodoDetailViewState.OnDeleteSuccess -> TodoDetailViewState(onDeleteSuccess = true)
        PartialTodoDetailViewState.ShowLoading -> {
            previousState.isLoading = true
            previousState
        }
        PartialTodoDetailViewState.ClearSingleEvent -> {
            previousState.error = null
            previousState.onDeleteSuccess = false
            previousState
        }
    }

}