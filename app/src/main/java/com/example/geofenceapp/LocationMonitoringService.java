package com.example.geofenceapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * Service that monitors changes in location settings.
 * It registers a broadcast receiver to listen for location mode changes.
 */
public class LocationMonitoringService extends Service {
    private static final String TAG = "LocationMonitorService";

    // Static reference to LocationSettingsListener (can be set by activities)
    public static LocationSettingsListener locationSettingsListener;

    private LocationSettingsReceiver locationSettingsReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Location monitoring service created");

        // Register the receiver to listen for location mode changes
        locationSettingsReceiver = new LocationSettingsReceiver();
        IntentFilter filter = new IntentFilter();

        // Different intents for different Android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        } else {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
            filter.addAction(LocationManager.MODE_CHANGED_ACTION);
        }

        registerReceiver(locationSettingsReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Location monitoring service started");

        // If this is the first start (not a restart), check location immediately
        boolean locationEnabled = LocationServiceChecker.isLocationEnabled(this);
        Log.d(TAG, "Initial location status: " + (locationEnabled ? "enabled" : "disabled"));

        if (!locationEnabled && locationSettingsListener != null) {
            locationSettingsListener.onLocationSettingsChanged(false);
        }

        // Make this a foreground service if needed for reliability
        // This is optional, but helps the service stay alive
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            startForeground(9999, notificationHelper.createMonitoringNotification());
        }

        // Return START_STICKY to ensure the service restarts if killed
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Location monitoring service destroyed");

        // Unregister the receiver when service is destroyed
        if (locationSettingsReceiver != null) {
            try {
                unregisterReceiver(locationSettingsReceiver);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Set the listener to receive location settings change events
     *
     * @param listener The listener to set
     */
    public static void setLocationSettingsListener(LocationSettingsListener listener) {
        locationSettingsListener = listener;
    }

    /**
     * Start the location monitoring service
     *
     * @param context Application context
     */
    public static void startService(Context context) {
        Intent serviceIntent = new Intent(context, LocationMonitoringService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    /**
     * Stop the location monitoring service
     *
     * @param context Application context
     */
    public static void stopService(Context context) {
        Intent serviceIntent = new Intent(context, LocationMonitoringService.class);
        context.stopService(serviceIntent);
    }
}