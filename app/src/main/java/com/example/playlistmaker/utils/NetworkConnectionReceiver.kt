package com.example.playlistmaker.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast

/**
 * BroadcastReceiver для отслеживания изменений состояния сети
 */
class NetworkConnectionReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NetworkReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive called with action: ${intent?.action}")

        // Проверяем action (используем строку напрямую для совместимости)
        if (intent?.action == "android.net.conn.CONNECTIVITY_CHANGE") {
            context?.let { ctx ->
                val isConnected = isNetworkAvailable(ctx)
                Log.d(TAG, "Network available: $isConnected")

                if (!isConnected) {
                    Log.d(TAG, "Showing toast - No internet connection")
                    Toast.makeText(
                        ctx.applicationContext, // Используем applicationContext
                        "Отсутствует подключение к интернету",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Проверка доступности сети
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Для Android M (API 23) и выше
            val network = connectivityManager.activeNetwork
            if (network != null) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                networkCapabilities != null && (
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                        )
            } else {
                false
            }
        } else {
            // Для более старых версий Android (deprecated, но всё ещё работает)
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo != null && networkInfo.isConnected
        }
    }
}