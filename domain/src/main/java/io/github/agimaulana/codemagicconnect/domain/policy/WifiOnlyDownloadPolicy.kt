package io.github.agimaulana.codemagicconnect.domain.policy

import javax.inject.Inject

interface WifiOnlyDownloadPolicy {

    val threshold: WifiOnlyThreshold

    fun requiresWifi(artifactSizeBytes: Long, wifiOnlyEnabled: Boolean): Boolean
}

internal class WifiOnlyDownloadPolicyImpl @Inject constructor(
    override val threshold: WifiOnlyThreshold
) : WifiOnlyDownloadPolicy {

    override fun requiresWifi(artifactSizeBytes: Long, wifiOnlyEnabled: Boolean): Boolean =
        wifiOnlyEnabled && artifactSizeBytes > threshold.bytes
}
