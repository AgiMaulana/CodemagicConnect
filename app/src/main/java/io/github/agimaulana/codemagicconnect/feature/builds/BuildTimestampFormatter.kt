package io.github.agimaulana.codemagicconnect.feature.builds

import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Formats a raw ISO-8601 timestamp from the Codemagic API (e.g. `2026-10-01T10:00:00Z`)
 * into a human-readable build timestamp (e.g. `Today, 14:20`).
 *
 * Unparseable input is returned unchanged so the UI never loses the timestamp.
 */
internal fun formatBuildTimestamp(
    isoTimestamp: String,
    nowMillis: Long = System.currentTimeMillis(),
    timeZone: TimeZone = TimeZone.getDefault(),
    locale: Locale = Locale.getDefault(),
    todayPattern: String = TODAY_PATTERN,
    yesterdayPattern: String = YESTERDAY_PATTERN,
    datePattern: String = DATE_PATTERN
): String {
    val buildMillis = parseIsoTimestamp(isoTimestamp) ?: return isoTimestamp
    val timeText = DateFormat.getTimeInstance(DateFormat.SHORT, locale).apply {
        this.timeZone = timeZone
    }.format(Date(buildMillis))
    if (isSameDay(buildMillis, nowMillis, timeZone)) {
        return todayPattern.format(timeText)
    }
    if (isSameDay(buildMillis, nowMillis - MILLIS_PER_DAY, timeZone)) {
        return yesterdayPattern.format(timeText)
    }
    val dateText = DateFormat.getDateInstance(DateFormat.MEDIUM, locale).apply {
        this.timeZone = timeZone
    }.format(Date(buildMillis))
    return datePattern.format(dateText, timeText)
}

private fun parseIsoTimestamp(isoTimestamp: String): Long? {
    val normalized = isoTimestamp.trim().replace(UTC_SUFFIX, UTC_OFFSET_SUFFIX)
    return ISO_PATTERNS.firstNotNullOfOrNull { pattern ->
        try {
            java.text.SimpleDateFormat(pattern, Locale.US).parse(normalized)?.time
        } catch (exception: Exception) {
            null
        }
    }
}

private fun isSameDay(firstMillis: Long, secondMillis: Long, timeZone: TimeZone): Boolean {
    val first = Calendar.getInstance(timeZone).apply { timeInMillis = firstMillis }
    val second = Calendar.getInstance(timeZone).apply { timeInMillis = secondMillis }
    return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
        first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
}

private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
private const val UTC_SUFFIX = "Z"
private const val UTC_OFFSET_SUFFIX = "+00:00"
private const val TODAY_PATTERN = "Today, %1\$s"
private const val YESTERDAY_PATTERN = "Yesterday, %1\$s"
private const val DATE_PATTERN = "%1\$s, %2\$s"

private val ISO_PATTERNS = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
    "yyyy-MM-dd'T'HH:mm:ssXXX"
)
