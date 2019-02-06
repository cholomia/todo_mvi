package com.mundomo.fdmmia.todo.presenter.base

import android.os.Bundle
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.mundomo.fdmmia.todo.MyApp
import com.mundomo.fdmmia.todo.di.AppComponent
import io.reactivex.subjects.PublishSubject

abstract class BaseMviActivity<V : MvpView, VS : MviViewState, P : MviPresenter<V, VS>, A : MviViewAction> :
    MviActivity<V, P>() {

    val viewAction: PublishSubject<A> by lazy { PublishSubject.create<A>() }

    protected abstract fun injectDependencies(appComponent: AppComponent)

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(MyApp[applicationContext].appComponent)
        super.onCreate(savedInstanceState)
    }

    fun showError(error: Throwable) {
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

}