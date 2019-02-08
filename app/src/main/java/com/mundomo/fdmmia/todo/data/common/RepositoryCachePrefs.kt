package com.mundomo.fdmmia.todo.data.common

import android.app.Application
import com.mundomo.fdmmia.todo.data.common.PreferenceHelper.customPrefs
import com.mundomo.fdmmia.todo.data.common.PreferenceHelper.get
import com.mundomo.fdmmia.todo.data.common.PreferenceHelper.set
import com.mundomo.fdmmia.todo.domain.common.toDate
import com.mundomo.fdmmia.todo.domain.common.toReadableString
import com.mundomo.fdmmia.todo.domain.enums.CacheStatus
import com.mundomo.fdmmia.todo.domain.enums.DateTimeFormat
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RepositoryCachePrefs @Inject constructor(private val application: Application) {

    companion object {
        private const val CACHE_PREFS = "cache_prefs"
    }

    fun updateCacheUpdate(key: String) {
        customPrefs(application, CACHE_PREFS)[key] =
            Date().toReadableString(DateTimeFormat.DATE_TIME_SHORT)
    }

    fun getCacheUpdate(key: String, staleDate: Long, expiryDate: Long? = null): CacheStatus {
        val readableString: String? = customPrefs(application, CACHE_PREFS)[key]
        val lastUpdate = readableString?.toDate(DateTimeFormat.DATE_TIME_SHORT)

        val cacheStatus = when {
            (lastUpdate == null && expiryDate != null) || (lastUpdate != null && isElapsed(
                expiryDate,
                lastUpdate
            )) -> CacheStatus.EXPIRED
            (lastUpdate == null && expiryDate == null) || (lastUpdate != null && isElapsed(
                staleDate,
                lastUpdate
            )) -> CacheStatus.STALE
            else -> CacheStatus.FRESH
        }
        Timber.d("getCacheUpdate: key=$key, cacheStatus=${cacheStatus.name}")
        return cacheStatus
    }

    fun deleteCachePrefs() = customPrefs(application, CACHE_PREFS)
        .edit()
        .clear()
        .apply()

    private fun isElapsed(date: Long?, lastUpdate: Date) =
        date != null && Date().time - lastUpdate.time >= date

}