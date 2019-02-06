package com.mundomo.fdmmia.todo.data.common

import com.mundomo.fdmmia.todo.data.common.RealmHelper.rxTransaction
import com.mundomo.fdmmia.todo.domain.exception.NoRecordsFoundException
import io.reactivex.Observable
import io.reactivex.Single
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery

object RealmHelper {

    fun <T : Any> rxTransaction(transaction: (Realm) -> T): Single<T> = Single.fromCallable {
        var result: T? = null
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                result = transaction.invoke(it)
            }
        }
        result!!
    }

}

inline fun <reified T : RealmObject> List<T>.saveToRealm(): Single<List<T>> = rxTransaction {
    it.copyFromRealm(it.copyToRealmOrUpdate(this))
}

inline fun <reified T : RealmObject> T.saveToRealm(): Single<T> = rxTransaction {
    it.copyFromRealm(it.copyToRealmOrUpdate(this))
}

inline fun <reified T : RealmObject> RealmQuery<T>.findAllObservable(): Observable<List<T>> = when {
    realm.isAutoRefresh -> findAllAsync()
        .asFlowable()
        .filter { it.isLoaded && it.isValid }
        .map { realm.copyFromRealm(it) }
        .toObservable()
    else -> Observable.just(findAll().let { realm.copyFromRealm(it) })
}

inline fun <reified T : RealmObject> RealmQuery<T>.findObservable(): Observable<T> = when {
    realm.isAutoRefresh -> findAllAsync()
        .asFlowable()
        .filter { it.isLoaded && it.isValid }
        .map {
            realm.copyFromRealm(it).firstOrNull() ?: throw NoRecordsFoundException(
                "No object found"
            )
        }
        .toObservable()
    else -> Observable.just(findAll().let {
        realm.copyFromRealm(it).firstOrNull() ?: throw NoRecordsFoundException(
            "No object found"
        )
    })
}