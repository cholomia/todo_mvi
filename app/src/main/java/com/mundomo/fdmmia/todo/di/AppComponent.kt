package com.mundomo.fdmmia.todo.di

import com.mundomo.fdmmia.todo.MyApp
import dagger.Component
import org.jetbrains.annotations.NotNull
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RepositoryModule::class, NetworkModule::class])
interface AppComponent {

    operator fun plus(module: ActivityModule): ActivityComponent

    fun inject(app: MyApp)

}