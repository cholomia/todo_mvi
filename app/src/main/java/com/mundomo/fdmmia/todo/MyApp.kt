package com.mundomo.fdmmia.todo

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.mundomo.fdmmia.todo.di.AppComponent
import com.mundomo.fdmmia.todo.di.AppModule
import com.mundomo.fdmmia.todo.di.DaggerAppComponent
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber
import javax.inject.Inject

class MyApp : MultiDexApplication() {

    companion object {
        operator fun get(context: Context): MyApp = context.applicationContext as MyApp
    }

    @Inject
    lateinit var realmConfiguration: RealmConfiguration

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Realm.init(this)

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        appComponent.inject(this)

        Realm.setDefaultConfiguration(realmConfiguration)
    }

}