package io.github.agimaulana.codemagicconnect.data.gateway

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.agimaulana.codemagicconnect.domain.gateway.NetworkMonitorGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitorGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkMonitorGateway {

    override suspend fun isWifiConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
}
