package com.example.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

/**
 * BroadcastReceiver that listens for changes in location settings
 * and notifies the application when location services are disabled.
 */
public class LocationSettingsReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationSettingsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received location settings changed broadcast: " + intent.getAction());

        // Check if location is enabled
        if (!LocationServiceChecker.isLocationEnabled(context)) {
            Log.d(TAG, "Location services have been disabled");

            // If app is in foreground, show dialog directly
            // We'll handle this with a callback to avoid context issues
            if (LocationMonitoringService.locationSettingsListener != null) {
                LocationMonitoringService.locationSettingsListener.onLocationSettingsChanged(false);
            } else {
                // If app is in background, show notification
                NotificationHelper notificationHelper = new NotificationHelper(context);
                notificationHelper.showLocationDisabledNotification();
            }
        } else {
            Log.d(TAG, "Location services have been enabled");

            // Notify the app that location is enabled
            if (LocationMonitoringService.locationSettingsListener != null) {
                LocationMonitoringService.locationSettingsListener.onLocationSettingsChanged(true);
            }
        }
    }
}