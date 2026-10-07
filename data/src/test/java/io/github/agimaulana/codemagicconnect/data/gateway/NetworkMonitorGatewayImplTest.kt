package io.github.agimaulana.codemagicconnect.data.gateway

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkMonitorGatewayImplTest {

    private val context = mockk<Context>()
    private val connectivityManager = mockk<ConnectivityManager>()
    private val network = mockk<Network>()
    private val capabilities = mockk<NetworkCapabilities>()

    private fun gateway() = NetworkMonitorGatewayImpl(context)

    private fun stubManager(active: Network? = network, caps: NetworkCapabilities? = capabilities) {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns active
        every { connectivityManager.getNetworkCapabilities(network) } returns caps
    }

    @Test
    fun `given wifi transport when checking then true`() = runTest {
        stubManager()
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true

        assertTrue(gateway().isWifiConnected())
    }

    @Test
    fun `given cellular transport when checking then false`() = runTest {
        stubManager()
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false

        assertFalse(gateway().isWifiConnected())
    }

    @Test
    fun `given no active network when checking then false`() = runTest {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns null

        assertFalse(gateway().isWifiConnected())
    }

    @Test
    fun `given no capabilities when checking then false`() = runTest {
        stubManager(caps = null)

        assertFalse(gateway().isWifiConnected())
    }
}
