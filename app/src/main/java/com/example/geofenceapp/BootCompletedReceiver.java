package com.example.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * BroadcastReceiver that starts the LocationMonitoringService when the device boots up.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot completed received with action: " + intent.getAction());

        if (intent.getAction() != null &&
                (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                        intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") ||
                        intent.getAction().equals("android.intent.action.REBOOT"))) {

            Log.d(TAG, "Starting location monitoring service after boot");

            // Start the location monitoring service
            try {
                // Use a slight delay to ensure system is fully booted
                Thread.sleep(3000);

                LocationMonitoringService.startService(context);
            } catch (Exception e) {
                Log.e(TAG, "Error starting service after boot: " + e.getMessage());
            }
        }
    }
}