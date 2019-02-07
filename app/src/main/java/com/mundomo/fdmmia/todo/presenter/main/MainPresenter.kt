package com.mundomo.fdmmia.todo.presenter.main

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.mundomo.fdmmia.todo.data.IO_SCHEDULER
import com.mundomo.fdmmia.todo.data.MAIN_SCHEDULER
import com.mundomo.fdmmia.todo.domain.repository.TodoRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class MainPresenter @Inject constructor(
    private val todoRepository: TodoRepository,
    @Named(IO_SCHEDULER) private val io: Scheduler,
    @Named(MAIN_SCHEDULER) private val main: Scheduler
) : MviBasePresenter<MainView, MainViewState>() {

    override fun bindIntents() {
        Timber.d("bindIntents")

        val getLiveTodosIntent = intent(MainView::getLiveTodos)
            .flatMap { getLiveTodosObservable() }
            .doOnNext { Timber.d("render getLiveTodos: $it") }

        val refreshTodosIntent = intent(MainView::refreshTodos)
            .flatMap { refreshTodos() }
            .doOnNext { Timber.d("render refreshTodos: $it") }

        val allObservable = Observable.mergeArray(
            getLiveTodosIntent,
            refreshTodosIntent
        ).onErrorReturn { PartialMainViewState.Error(it) }
            .switchMap { viewState ->
                when (viewState) {
                    is PartialMainViewState.Error -> Observable.just<PartialMainViewState>(
                        PartialMainViewState.ClearSingleEvent
                    )
                        .startWith(viewState)
                    else -> Observable.just(viewState)
                }
            }
            .doOnNext { Timber.d("allObservable render: $it") }
            .observeOn(main)
        val initialState = MainViewState()
        val stateObservable = allObservable.scan(initialState, this::viewStateReducer)
            .distinctUntilChanged()
        subscribeViewState(stateObservable, MainView::render)
    }

    private fun getLiveTodosObservable(): Observable<PartialMainViewState> =
        todoRepository.getLiveTodos()
            .map<PartialMainViewState> { PartialMainViewState.Todos(it) }
            .startWith(PartialMainViewState.ShowLoading)
            .onErrorReturn { PartialMainViewState.Error(it) }
            .switchMap { viewState ->
                when (viewState) {
                    PartialMainViewState.ShowLoading -> Observable.just(viewState)
                    else -> Observable.just<PartialMainViewState>(PartialMainViewState.HideLoading)
                        .startWith(viewState)
                }
            }
            .subscribeOn(main)

    private fun refreshTodos(): Observable<PartialMainViewState> = todoRepository.refreshTodos()
        .map<PartialMainViewState> { PartialMainViewState.HideLoading }
        .startWith(PartialMainViewState.ShowLoading)
        .onErrorReturn { PartialMainViewState.Error(it) }
        .subscribeOn(io)

    private fun viewStateReducer(
        previousState: MainViewState,
        changes: PartialMainViewState
    ): MainViewState = when (changes) {
        is PartialMainViewState.Todos -> MainViewState(todos = changes.todos)
        is PartialMainViewState.Error -> {
            previousState.error = changes.error
            previousState
        }
        PartialMainViewState.ShowLoading -> {
            previousState.isLoading = true
            previousState
        }
        PartialMainViewState.HideLoading -> {
            previousState.isLoading = false
            previousState
        }
        PartialMainViewState.ClearSingleEvent -> MainViewState(todos = previousState.todos)
    }
}