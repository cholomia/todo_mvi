package com.mundomo.fdmmia.todo.domain.common

import com.mundomo.fdmmia.todo.domain.enums.DateTimeFormat
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Date.toReadableString(dateTimeFormat: DateTimeFormat): String? {
    val simpleDateFormat = SimpleDateFormat(dateTimeFormat.value, Locale.getDefault())
    return simpleDateFormat.format(this)
}

fun String.toDate(dateTimeFormat: DateTimeFormat): Date? = try {
    val simpleDateFormat = SimpleDateFormat(dateTimeFormat.value, Locale.getDefault())
    simpleDateFormat.parse(this)
} catch (e: ParseException) {
    Timber.e(e)
    null
}

fun Long.toCountdownDateString(): String = String.format(
    DateTimeFormat.COUNTDOWN_FORMAT.value,
    TimeUnit.MILLISECONDS.toDays(this),
    TimeUnit.MILLISECONDS.toHours(this) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(this)),
    TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
        TimeUnit.MILLISECONDS.toHours(
            this
        )
    ),
    TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
        TimeUnit.MILLISECONDS.toMinutes(
            this
        )
    )
)

fun Date.yearsOld(): Int {
    val calendar = this.toCalendar()
    val birthdayYear = calendar.get(Calendar.YEAR)
    val birthdayMonth = calendar.get(Calendar.MONTH)
    val birthdayDay = calendar.get(Calendar.DAY_OF_MONTH)
    return getYearsDifference(
        birthdayYear,
        birthdayMonth,
        birthdayDay
    )
}

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun getYearsDifference(year: Int, month: Int, day: Int): Int {
    val birthday = Calendar.getInstance()
    val today = Calendar.getInstance()
    birthday.set(year, month, day)
    var age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < birthday.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}

fun Date.truncatedDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.HOUR, 0)
    calendar.set(Calendar.AM_PM, Calendar.AM)
    return calendar.time
}

fun Long.getMinutesAndSecondsOnly(): String = String.format(
    Locale.getDefault(),
    "%02d:%02d:%02d",
    TimeUnit.MILLISECONDS.toHours(this),
    TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
        TimeUnit.MILLISECONDS.toHours(this)
    ),
    TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
        TimeUnit.MILLISECONDS.toMinutes(this)
    )
)