package com.mundomo.fdmmia.todo.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mundomo.fdmmia.todo.BuildConfig
import com.mundomo.fdmmia.todo.data.BASE_URL
import com.mundomo.fdmmia.todo.data.IO_SCHEDULER
import com.mundomo.fdmmia.todo.data.network.ApiService
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

/**
 * todo: add file cache, network interceptor for refreshing token (if uses oauth)
 */
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun callAdapterFactory(
        @Named(IO_SCHEDULER) io: Scheduler
    ): CallAdapter.Factory = RxJava2CallAdapterFactory.createWithScheduler(io)

    @Singleton
    @Provides
    fun gson(): Gson = GsonBuilder()
        .create()

    @Singleton
    @Provides
    fun converterFactory(gson: Gson): Converter.Factory = GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun level(): HttpLoggingInterceptor.Level = when {
        BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
        else -> HttpLoggingInterceptor.Level.NONE
    }

    @Singleton
    @Provides
    fun httpLoggingInterceptor(level: HttpLoggingInterceptor.Level): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Timber.d(it)
        })
        httpLoggingInterceptor.level = level
        return httpLoggingInterceptor
    }

    @Singleton
    @Provides
    fun okHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(httpLoggingInterceptor) //NOTE: should always be the last interceptor
        .build()

    @Singleton
    @Provides
    fun apiService(
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        converterFactory: Converter.Factory
    ): ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(callAdapterFactory)
        .addConverterFactory(converterFactory)
        .client(okHttpClient)
        .build()
        .create(ApiService::class.java)

}