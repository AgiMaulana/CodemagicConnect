package io.github.agimaulana.codemagicconnect.feature.builds

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class BuildTimestampFormatterTest {

    private val formatter = BuildTimestampFormatter()
    private val utc = TimeZone.getTimeZone("UTC")
    private val defaultLocale = Locale.getDefault()

    private val nowMillis = isoToMillis("2026-10-07T12:00:00Z")

    @Before
    fun setUp() {
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `given timestamp earlier today when format then returns Today`() {
        val result = formatter.format("2026-10-07T08:15:00Z", nowMillis, utc, Locale.US)

        assertTrue(result is BuildsViewModel.BuildTimestamp.Today)
        assertEquals("8:15 AM", normalizeSpace((result as BuildsViewModel.BuildTimestamp.Today).timeText))
    }

    @Test
    fun `given timestamp yesterday when format then returns Yesterday`() {
        val result = formatter.format("2026-10-06T09:05:00Z", nowMillis, utc, Locale.US)

        assertTrue(result is BuildsViewModel.BuildTimestamp.Yesterday)
        assertEquals("9:05 AM", normalizeSpace((result as BuildsViewModel.BuildTimestamp.Yesterday).timeText))
    }

    @Test
    fun `given timestamp on older date when format then returns OnDate`() {
        val result = formatter.format("2020-01-15T10:00:00Z", nowMillis, utc, Locale.US)

        assertTrue(result is BuildsViewModel.BuildTimestamp.OnDate)
        val dated = result as BuildsViewModel.BuildTimestamp.OnDate
        assertEquals("Jan 15, 2020", dated.dateText)
        assertEquals("10:00 AM", normalizeSpace(dated.timeText))
    }

    @Test
    fun `given unparseable timestamp when format then returns Unknown with raw value`() {
        val result = formatter.format("not-a-timestamp", nowMillis, utc, Locale.US)

        assertEquals(BuildsViewModel.BuildTimestamp.Unknown("not-a-timestamp"), result)
    }

    private fun isoToMillis(iso: String): Long =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(iso)!!.time

    private fun normalizeSpace(value: String): String =
        value.replace(NARROW_NO_BREAK_SPACE, SPACE).replace(NO_BREAK_SPACE, SPACE)

    companion object {
        private const val SPACE = " "
        private const val NARROW_NO_BREAK_SPACE = "\u202F"
        private const val NO_BREAK_SPACE = "\u00A0"
    }
}
