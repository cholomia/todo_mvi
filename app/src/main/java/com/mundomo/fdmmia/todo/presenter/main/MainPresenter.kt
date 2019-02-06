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
        ).onErrorReturn { MainViewState.Error(it) }
            .switchMap { viewState ->
                when (viewState) {
                    is MainViewState.Error -> Observable.just<MainViewState>(MainViewState.ClearSingleEvent)
                        .startWith(viewState)
                    else -> Observable.just(viewState)
                }
            }
            .observeOn(main)

        subscribeViewState(allObservable, MainView::render)

    }

    private fun getLiveTodosObservable(): Observable<MainViewState> = todoRepository.getLiveTodos()
        .map<MainViewState> { MainViewState.Todos(it) }
        .startWith(MainViewState.ShowLoading)
        .onErrorReturn { MainViewState.Error(it) }
        .switchMap { viewState ->
            when (viewState) {
                MainViewState.ShowLoading -> Observable.just(viewState)
                else -> Observable.just<MainViewState>(MainViewState.HideLoading)
                    .startWith(viewState)
            }
        }
        .subscribeOn(main)

    private fun refreshTodos(): Observable<MainViewState> = todoRepository.refreshTodos()
        .map<MainViewState> { MainViewState.HideLoading }
        .startWith(MainViewState.ShowLoading)
        .onErrorReturn { MainViewState.Error(it) }
        .subscribeOn(io)

}