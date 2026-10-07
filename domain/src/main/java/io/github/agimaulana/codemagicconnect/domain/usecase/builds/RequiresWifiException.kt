package io.github.agimaulana.codemagicconnect.domain.usecase.builds

class RequiresWifiException(
    thresholdBytes: Long
) : IllegalStateException(
    "Connect to Wi-Fi to download files larger than ${thresholdBytes / BYTES_PER_MEGABYTE} MB"
) {

    companion object {
        private const val BYTES_PER_MEGABYTE = 1024L * 1024L
    }
}
