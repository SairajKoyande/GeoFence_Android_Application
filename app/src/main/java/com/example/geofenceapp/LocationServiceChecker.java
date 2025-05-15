package com.example.geofenceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

/**
 * Helper class to check if location services are enabled
 * and prompt the user to enable them if they are not.
 */
public class LocationServiceChecker {
    private static final String TAG = "LocationServiceChecker";

    /**
     * Checks if location services are enabled on the device
     *
     * @param context Application context
     * @return true if location services are enabled, false otherwise
     */
    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Check if GPS or network provider is enabled
        boolean isGpsEnabled = false;
        boolean isNetworkEnabled = false;

        try {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e(TAG, "Error checking GPS provider: " + ex.getMessage());
        }

        try {
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.e(TAG, "Error checking Network provider: " + ex.getMessage());
        }

        return isGpsEnabled || isNetworkEnabled;
    }

    /**
     * Shows a dialog prompting the user to enable location services
     *
     * @param context Application context
     */
    public static void showLocationPrompt(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Location Services Disabled")
                .setMessage("This app requires location services to function properly. Would you like to enable location services now?")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open location settings
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
