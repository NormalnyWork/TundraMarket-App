package com.normalnywork.tundramarket.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.annotation.Singleton

@Singleton
class NetworkStatusObserver(private val context: Context) {

    fun isConnected(): Boolean {
        return context.getSystemService(ConnectivityManager::class.java).isConnected()
    }

    fun observe(): Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

        fun sendCurrentStatus() {
            trySend(isConnected())
        }

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                sendCurrentStatus()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                sendCurrentStatus()
            }

            override fun onLost(network: Network) {
                sendCurrentStatus()
            }

            override fun onUnavailable() {
                sendCurrentStatus()
            }
        }

        sendCurrentStatus()
        connectivityManager.registerDefaultNetworkCallback(callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun ConnectivityManager.isConnected(): Boolean {
        val capabilities = getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
