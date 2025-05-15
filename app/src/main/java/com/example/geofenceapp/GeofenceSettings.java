package com.example.geofenceapp;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class GeofenceSettings {
    // Shared Preferences Keys
    public static final String KEY_LATITUDE = "geofence_latitude";
    public static final String KEY_LONGITUDE = "geofence_longitude";
    public static final String KEY_RADIUS = "geofence_radius";
    public static final String KEY_GEOFENCE_ACTIVE = "geofence_active";
    public static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    public static final String KEY_SOUND_ENABLED = "sound_enabled";
    public static final String KEY_VIBRATION_ENABLED = "vibration_enabled";

    // Default values
    public static final float DEFAULT_RADIUS = 100f; // meters
    public static final boolean DEFAULT_NOTIFICATION_ENABLED = true;
    public static final boolean DEFAULT_SOUND_ENABLED = true;
    public static final boolean DEFAULT_VIBRATION_ENABLED = true;

    /**
     * Check if a geofence is currently active
     *
     * @param context Application context
     * @return true if a geofence is active, false otherwise
     */
    public static boolean isGeofenceActive(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_GEOFENCE_ACTIVE, false);
    }

    /**
     * Save geofence settings
     *
     * @param context     Application context
     * @param latitude    Geofence center latitude
     * @param longitude   Geofence center longitude
     * @param radius      Geofence radius in meters
     * @param isActive    Whether the geofence is active or not
     */
    public static void saveGeofenceSettings(Context context, double latitude, double longitude, float radius, boolean isActive) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        editor.putFloat(KEY_LATITUDE, (float) latitude);
        editor.putFloat(KEY_LONGITUDE, (float) longitude);
        editor.putFloat(KEY_RADIUS, radius);
        editor.putBoolean(KEY_GEOFENCE_ACTIVE, isActive);
        
        editor.apply();
    }

    /**
     * Get the latitude of the geofence center
     *
     * @param context Application context
     * @return Latitude as double
     */
    public static double getLatitude(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getFloat(KEY_LATITUDE, 0);
    }

    /**
     * Get the longitude of the geofence center
     *
     * @param context Application context
     * @return Longitude as double
     */
    public static double getLongitude(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getFloat(KEY_LONGITUDE, 0);
    }

    /**
     * Get the radius of the geofence
     *
     * @param context Application context
     * @return Radius in meters
     */
    public static float getRadius(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getFloat(KEY_RADIUS, DEFAULT_RADIUS);
    }

    /**
     * Check if notifications are enabled
     *
     * @param context Application context
     * @return true if notifications are enabled, false otherwise
     */
    public static boolean isNotificationEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, DEFAULT_NOTIFICATION_ENABLED);
    }

    /**
     * Check if sound alerts are enabled
     *
     * @param context Application context
     * @return true if sound alerts are enabled, false otherwise
     */
    public static boolean isSoundEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, DEFAULT_SOUND_ENABLED);
    }

    /**
     * Check if vibration is enabled
     *
     * @param context Application context
     * @return true if vibration is enabled, false otherwise
     */
    public static boolean isVibrationEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, DEFAULT_VIBRATION_ENABLED);
    }

    /**
     * Save notification settings
     *
     * @param context              Application context
     * @param notificationEnabled  Whether notifications are enabled
     * @param soundEnabled         Whether sound alerts are enabled
     * @param vibrationEnabled     Whether vibration is enabled
     */
    public static void saveNotificationSettings(Context context, boolean notificationEnabled, boolean soundEnabled, boolean vibrationEnabled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, notificationEnabled);
        editor.putBoolean(KEY_SOUND_ENABLED, soundEnabled);
        editor.putBoolean(KEY_VIBRATION_ENABLED, vibrationEnabled);
        
        editor.apply();
    }
    public static boolean isGeofenceMonitoringFunctional(Context context) {
        // Check if geofence is active in settings
        boolean isGeofenceActive = isGeofenceActive(context);

        // Check if location services are enabled
        boolean isLocationEnabled = LocationServiceChecker.isLocationEnabled(context);

        // Check for location permissions
        boolean hasLocationPermission = hasRequiredPermissions(context);

        return isGeofenceActive && isLocationEnabled && hasLocationPermission;
    }
    public static boolean hasRequiredPermissions(Context context) {
        // Check for fine location permission
        boolean hasFineLocation = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

        // Check for background location on Android 10+
        boolean hasBackgroundLocation = true; // Default to true for Android 9 and below
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasBackgroundLocation = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }

        // Check for notification permission on Android 13+
        boolean hasNotificationPermission = true; // Default to true for Android 12 and below
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED;
        }

        return hasFineLocation && hasBackgroundLocation && hasNotificationPermission;
    }

}
