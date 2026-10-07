package io.github.agimaulana.codemagicconnect.domain.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WifiOnlyDownloadPolicyTest {

    private val policy = WifiOnlyDownloadPolicyImpl(WifiOnlyThreshold.Megabytes(50))

    @Test
    fun `given wifi only and large artifact when checking then wifi required`() {
        assertTrue(policy.requiresWifi(artifactSizeBytes = 60_000_000L, wifiOnlyEnabled = true))
    }

    @Test
    fun `given wifi only and small artifact when checking then wifi not required`() {
        assertFalse(policy.requiresWifi(artifactSizeBytes = 100L, wifiOnlyEnabled = true))
    }

    @Test
    fun `given wifi disabled and large artifact when checking then wifi not required`() {
        assertFalse(policy.requiresWifi(artifactSizeBytes = 60_000_000L, wifiOnlyEnabled = false))
    }

    @Test
    fun `given threshold at boundary when checking then strictly larger sizes require wifi`() {
        assertFalse(policy.requiresWifi(artifactSizeBytes = policy.threshold.bytes, wifiOnlyEnabled = true))
        assertTrue(policy.requiresWifi(artifactSizeBytes = policy.threshold.bytes + 1, wifiOnlyEnabled = true))
    }

    @Test
    fun `given megabyte threshold when reading bytes then converted to bytes`() {
        assertEquals(50L * 1024L * 1024L, WifiOnlyThreshold.Megabytes(50).bytes)
    }

    @Test
    fun `given default threshold when reading then fifty megabytes`() {
        assertEquals(WifiOnlyThreshold.Megabytes(50), WifiOnlyThreshold.Default)
    }
}
