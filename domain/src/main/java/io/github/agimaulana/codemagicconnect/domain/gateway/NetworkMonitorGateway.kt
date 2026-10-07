package io.github.agimaulana.codemagicconnect.domain.gateway

interface NetworkMonitorGateway {

    suspend fun isWifiConnected(): Boolean
}
