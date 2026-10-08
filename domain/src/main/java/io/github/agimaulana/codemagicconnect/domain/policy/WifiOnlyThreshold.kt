package io.github.agimaulana.codemagicconnect.domain.policy

sealed interface WifiOnlyThreshold {

    val bytes: Long

    data class Megabytes(val value: Int) : WifiOnlyThreshold {
        override val bytes: Long = value * BYTES_PER_MEGABYTE
    }

    companion object {
        val Default: WifiOnlyThreshold = Megabytes(MB_FIFTY)

        private const val MB_FIFTY = 50
        private const val BYTES_PER_MEGABYTE = 1024L * 1024L
    }
}
