package com.mundomo.fdmmia.todo.di

import android.app.Application
import com.mundomo.fdmmia.todo.data.IO_SCHEDULER
import com.mundomo.fdmmia.todo.data.MAIN_SCHEDULER
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.RealmConfiguration
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Singleton
    @Provides
    fun application(): Application = application

    @Singleton
    @Provides
    @Named(IO_SCHEDULER)
    fun ioScheduler(): Scheduler = Schedulers.io()

    @Singleton
    @Provides
    @Named(MAIN_SCHEDULER)
    fun mainScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Singleton
    @Provides
    fun realmConfiguration(): RealmConfiguration = RealmConfiguration.Builder()
        .deleteRealmIfMigrationNeeded()
        .build()

}