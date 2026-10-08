package io.github.agimaulana.codemagicconnect.feature.builds

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * Classifies a raw ISO-8601 `started_at` timestamp from the Codemagic API
 * (e.g. `2026-10-01T10:00:00Z`) into a [BuildsViewModel.BuildTimestamp] carrying
 * locale-formatted parts. Compose resolves the final localized label.
 *
 * Unparseable input yields [BuildsViewModel.BuildTimestamp.Unknown] carrying the raw
 * value, so the UI never loses the timestamp.
 */
class BuildTimestampFormatter @Inject constructor() {

    fun format(
        isoTimestamp: String,
        nowMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault(),
        locale: Locale = Locale.getDefault()
    ): BuildsViewModel.BuildTimestamp {
        val buildMillis = parseIsoTimestamp(isoTimestamp)
            ?: return BuildsViewModel.BuildTimestamp.Unknown(isoTimestamp)
        val timeText = DateFormat.getTimeInstance(DateFormat.SHORT, locale).apply {
            this.timeZone = timeZone
        }.format(Date(buildMillis))
        if (isSameDay(buildMillis, nowMillis, timeZone)) {
            return BuildsViewModel.BuildTimestamp.Today(timeText)
        }
        if (isSameDay(buildMillis, nowMillis - MILLIS_PER_DAY, timeZone)) {
            return BuildsViewModel.BuildTimestamp.Yesterday(timeText)
        }
        val dateText = DateFormat.getDateInstance(DateFormat.MEDIUM, locale).apply {
            this.timeZone = timeZone
        }.format(Date(buildMillis))
        return BuildsViewModel.BuildTimestamp.OnDate(dateText, timeText)
    }

    private fun parseIsoTimestamp(isoTimestamp: String): Long? {
        val normalized = isoTimestamp.trim().replace(UTC_SUFFIX, UTC_OFFSET_SUFFIX)
        return ISO_PATTERNS.firstNotNullOfOrNull { pattern ->
            try {
                SimpleDateFormat(pattern, Locale.US).parse(normalized)?.time
            } catch (parseException: ParseException) {
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

    companion object {
        private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
        private const val UTC_SUFFIX = "Z"
        private const val UTC_OFFSET_SUFFIX = "+00:00"
        private val ISO_PATTERNS = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
        )
    }
}
