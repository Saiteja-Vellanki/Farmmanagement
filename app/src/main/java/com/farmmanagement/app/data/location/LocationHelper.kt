package com.farmmanagement.app.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * One-shot "current location" fetch using the platform LocationManager —
 * deliberately no Google Play Services / FusedLocationProvider dependency,
 * and deliberately no continuous tracking or background location, per
 * Phase 1 spec section 10. Caller is responsible for the runtime permission
 * check/request before calling this.
 */
class LocationHelper(private val context: Context) {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationOrNull(timeoutMs: Long = 8000L): Location? {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        // Prefer a recent last-known fix — instant, no waiting.
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        for (provider in providers) {
            if (manager.isProviderEnabled(provider)) {
                manager.getLastKnownLocation(provider)?.let { return it }
            }
        }

        // Fall back to a single fresh update with a timeout, then stop listening immediately.
        val enabledProvider = providers.firstOrNull { manager.isProviderEnabled(it) } ?: return null

        return suspendCancellableCoroutine { cont ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    manager.removeUpdates(this)
                    if (cont.isActive) cont.resume(location)
                }
            }
            cont.invokeOnCancellation { manager.removeUpdates(listener) }
            try {
                manager.requestLocationUpdates(enabledProvider, 0L, 0f, listener, Looper.getMainLooper())
            } catch (e: SecurityException) {
                if (cont.isActive) cont.resume(null)
                return@suspendCancellableCoroutine
            }
            // Manual timeout since requestLocationUpdates has no built-in one here.
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                manager.removeUpdates(listener)
                if (cont.isActive) cont.resume(null)
            }, timeoutMs)
        }
    }
}
