/*
package com.example.geofenceapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "geofence_channel";
    private static final String CHANNEL_NAME = "Geofence Notifications";
    private static final int NOTIFICATION_ID_ENTER = 1001;
    private static final int NOTIFICATION_ID_EXIT = 1002;


    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android Oreo and above
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Configure the notification channel
            channel.setDescription("Notifications for geofence transitions");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

            // Set the channel to play sound
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");

            try {
                // Try to get the custom sound resource ID
                int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

                // Set the sound for the notification channel
                if (soundResourceId != 0) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                            .build();
                    channel.setSound(soundUri, audioAttributes);
                    Log.d(TAG, "Set custom alert sound for notification channel");
                } else {
                    // Set default sound if custom sound is not found
                    channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null);
                    Log.d(TAG, "Set default sound for notification channel");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting sound for notification channel: " + e.getMessage());
                // Use default sound in case of error
                channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null);
            }

            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
        }
    }

    public void showGeofenceExitNotification(String message) {
        showGeofenceNotification("Geofence Exit Alert", message, NOTIFICATION_ID_EXIT);
    }

    public void showGeofenceEnterNotification(String message) {
        showGeofenceNotification("Geofence Enter Alert", message, NOTIFICATION_ID_ENTER);
    }

    private void showGeofenceNotification(String title, String message, int notificationId) {
        // Create an intent to open the app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, pendingIntentFlags);

        // Get the sound URI
        Uri soundUri = null;
        try {
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());
            if (soundResourceId != 0) {
                soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
                Log.d(TAG, "Using custom alert sound for notification");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting sound URI: " + e.getMessage());
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000, 500, 1000});

        // Set sound if we have a custom one, otherwise use default
        if (soundUri != null) {
            builder.setSound(soundUri);
        } else {
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
        }

        // Show the notification
        try {
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Notification posted successfully with ID: " + notificationId);
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage());
        }
    }
}*/
/*
package com.example.geofenceapp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "geofence_channel";
    private static final String CHANNEL_NAME = "Geofence Notifications";
    private static final int NOTIFICATION_ID_ENTER = 1001;
    private static final int NOTIFICATION_ID_EXIT = 1002;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android Oreo and above
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Configure the notification channel
            channel.setDescription("Notifications for geofence transitions");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

            // Set the channel to bypass DND and play sound even in silent mode
            channel.setBypassDnd(true);

            // Set audio attributes to USAGE_ALARM which can play in silent mode
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM) // Changed from USAGE_NOTIFICATION_EVENT
                    .build();

            try {
                // Try to get the custom sound resource ID
                int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

                // Set the sound for the notification channel
                if (soundResourceId != 0) {
                    channel.setSound(soundUri, audioAttributes);
                    Log.d(TAG, "Set custom alert sound for notification channel with alarm attributes");
                } else {
                    // Set default sound if custom sound is not found
                    channel.setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, audioAttributes);
                    Log.d(TAG, "Set default alarm sound for notification channel");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting sound for notification channel: " + e.getMessage());
                // Use default alarm sound in case of error
                channel.setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, audioAttributes);
            }

            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created with alarm attributes");
        }
    }

    public void showGeofenceExitNotification(String message) {
        showGeofenceNotification("Geofence Exit Alert", message, NOTIFICATION_ID_EXIT);
    }

    public void showGeofenceEnterNotification(String message) {
        showGeofenceNotification("Geofence Enter Alert", message, NOTIFICATION_ID_ENTER);
    }

    private void showGeofenceNotification(String title, String message, int notificationId) {
        // Create an intent to open the app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, pendingIntentFlags);

        // Get the sound URI
        Uri soundUri = null;
        try {
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());
            if (soundResourceId != 0) {
                soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
                Log.d(TAG, "Using custom alert sound for notification");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting sound URI: " + e.getMessage());
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM) // Changed from CATEGORY_ALARM
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000, 500, 1000});

        // Set sound if we have a custom one, otherwise use default alarm sound
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Oreo and above, sound is set via the channel
        } else {
            // For pre-Oreo devices
            if (soundUri != null) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                builder.setSound(soundUri, audioAttributes.getVolumeControlStream());
            } else {
                builder.setSound(Settings.System.DEFAULT_ALARM_ALERT_URI);
            }
        }

        // Show the notification
        try {
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Notification posted successfully with ID: " + notificationId);
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage());
        }
    }
}*/
/*
package com.example.geofenceapp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "geofence_channel";
    private static final String CHANNEL_NAME = "Geofence Notifications";
    private static final int NOTIFICATION_ID_ENTER = 1001;
    private static final int NOTIFICATION_ID_EXIT = 1002;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android Oreo and above
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Configure the notification channel
            channel.setDescription("Notifications for geofence transitions");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

            // Set the channel to bypass DND and play sound even in silent mode
            channel.setBypassDnd(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Set audio attributes to USAGE_ALARM which can play in silent mode
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Add this flag to enforce playing in silent mode
                    .build();

            try {
                // Try to get the custom sound resource ID
                int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

                // Set the sound for the notification channel
                if (soundResourceId != 0) {
                    channel.setSound(soundUri, audioAttributes);
                    Log.d(TAG, "Set custom alert sound for notification channel with alarm attributes");
                } else {
                    // Set default sound if custom sound is not found
                    channel.setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, audioAttributes);
                    Log.d(TAG, "Set default alarm sound for notification channel");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting sound for notification channel: " + e.getMessage());
                // Use default alarm sound in case of error
                channel.setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, audioAttributes);
            }

            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created with alarm attributes");
        }
    }

    public void showGeofenceExitNotification(String message) {
        showGeofenceNotification("Geofence Exit Alert", message, NOTIFICATION_ID_EXIT);
    }

    public void showGeofenceEnterNotification(String message) {
        showGeofenceNotification("Geofence Enter Alert", message, NOTIFICATION_ID_ENTER);
    }

    private void showGeofenceNotification(String title, String message, int notificationId) {
        // Create an intent to open the app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, pendingIntentFlags);

        // Get the sound URI
        Uri soundUri = null;
        try {
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());
            if (soundResourceId != 0) {
                soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
                Log.d(TAG, "Using custom alert sound for notification");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting sound URI: " + e.getMessage());
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX) // Changed from HIGH to MAX
                .setCategory(NotificationCompat.CATEGORY_ALARM) // Consistent use of CATEGORY_ALARM
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen
                .setVibrate(new long[]{0, 1000, 500, 1000});

        // Set sound if we have a custom one, otherwise use default alarm sound
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // For pre-Oreo devices
            if (soundUri != null) {
                // For pre-Lollipop devices
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    builder.setSound(soundUri, AudioManager.STREAM_ALARM);
                } else {
                    // For Lollipop to Nougat
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                            .build();

                    builder.setSound(soundUri, audioAttributes.getVolumeControlStream());
                }
            } else {
                builder.setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, AudioManager.STREAM_ALARM);
            }
        }
        // For Oreo and above, sound is handled by the notification channel

        // Show the notification
        try {
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_INSISTENT; // Makes the sound repeat until notification is acknowledged

            notificationManager.notify(notificationId, notification);
            Log.d(TAG, "Notification posted successfully with ID: " + notificationId);
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage());
        }
    }
}*/
/*
package com.example.geofenceapp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "geofence_channel";
    private static final String CHANNEL_NAME = "Geofence Notifications";
    private static final int NOTIFICATION_ID_ENTER = 1001;
    private static final int NOTIFICATION_ID_EXIT = 1002;

    private final Context context;
    private final NotificationManager notificationManager;
    private static final String MONITORING_CHANNEL_ID = "location_monitoring_channel";
    private static final String MONITORING_CHANNEL_NAME = "Location Monitoring";
    private static final int NOTIFICATION_ID_LOCATION_DISABLED = 1003;
    private static final int NOTIFICATION_ID_MONITORING_SERVICE = 9999;


  */
/*  public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android Oreo and above
        createNotificationChannel();
    }*//*

  // Update your constructor to call the new method
  public NotificationHelper(Context context) {
      this.context = context;
      this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      // Create notification channels for Android Oreo and above
      createNotificationChannels();
  }
    */
/**
     * Shows a notification when location services are disabled
     *//*

    public void showLocationDisabledNotification() {
        // Create an intent to open location settings
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 1, settingsIntent, pendingIntentFlags);

        // Create an intent to open the app
        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent appPendingIntent = PendingIntent.getActivity(
                context, 2, appIntent, pendingIntentFlags);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Location Services Disabled")
                .setContentText("Geofence monitoring requires location services. Tap to enable.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{0, 500, 200, 500})
                .addAction(0, "Open Settings", pendingIntent)
                .addAction(0, "Open App", appPendingIntent);

        // Show the notification
        try {
            Notification notification = builder.build();
            notificationManager.notify(NOTIFICATION_ID_LOCATION_DISABLED, notification);
            Log.d(TAG, "Location disabled notification posted");
        } catch (Exception e) {
            Log.e(TAG, "Error showing location disabled notification: " + e.getMessage());
        }
    }

    */
/**
     * Creates a silent notification for the foreground service
     *//*

    public Notification createMonitoringNotification() {
        // Intent to open the app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, pendingIntentFlags);

        // Build a silent notification
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, MONITORING_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setSound(null)
                    .setVibrate(null);
        }

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Geofence Monitoring Active")
                .setContentText("Monitoring for location changes")
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }
    // Call this method when creating the notification channels
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel geofenceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Configure the notification channel
            geofenceChannel.setDescription("Notifications for geofence transitions");
            geofenceChannel.enableLights(true);
            geofenceChannel.setLightColor(Color.RED);
            geofenceChannel.enableVibration(true);
            geofenceChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000, 500, 1000});

            // Critical settings to bypass DND and silent mode
            geofenceChannel.setBypassDnd(true);
            geofenceChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            geofenceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Set sound with proper audio attributes
            // ... (existing code for setting sound)

            notificationManager.createNotificationChannel(geofenceChannel);

            // Create the monitoring service channel (low importance)
            NotificationChannel monitoringChannel = new NotificationChannel(
                    MONITORING_CHANNEL_ID,
                    MONITORING_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );

            // Configure the monitoring channel
            monitoringChannel.setDescription("Background location monitoring service");
            monitoringChannel.setSound(null, null);
            monitoringChannel.enableVibration(false);
            monitoringChannel.setShowBadge(false);

            notificationManager.createNotificationChannel(monitoringChannel);
            Log.d(TAG, "Notification channels created");
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Configure the notification channel
            channel.setDescription("Notifications for geofence transitions");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000, 500, 1000});

            // Critical settings to bypass DND and silent mode
            channel.setBypassDnd(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Set sound behavior to guarantee it plays even in silent mode
            Uri soundUri = null;

            try {
                // Try to get the custom sound resource ID
                int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

                // Create Uri for the sound
                if (soundResourceId != 0) {
                    soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
                    Log.d(TAG, "Using custom alert sound for notification channel");
                } else {
                    // Use default alarm sound if custom sound is not found
                    soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                    Log.d(TAG, "Using default alarm sound for notification channel");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting sound URI: " + e.getMessage());
                // Use default alarm sound in case of error
                soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
            }

            // Create AudioAttributes with USAGE_ALARM to bypass silent mode
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Add this flag to enforce playing in silent mode
                    .build();

            // Set the sound for the notification channel with alarm attributes
            channel.setSound(soundUri, audioAttributes);

            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created with bypass DND and alarm attributes");
        }
    }

    public void showGeofenceExitNotification(String message) {
        showGeofenceNotification("Geofence Exit Alert", message, NOTIFICATION_ID_EXIT);
    }

    public void showGeofenceEnterNotification(String message) {
        showGeofenceNotification("Geofence Enter Alert", message, NOTIFICATION_ID_ENTER);
    }

    private void showGeofenceNotification(String title, String message, int notificationId) {
        // Create an intent to open the app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, pendingIntentFlags);

        // Get the sound URI (will be used for pre-Oreo devices only)
        Uri soundUri = null;
        try {
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());
            if (soundResourceId != 0) {
                soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
                Log.d(TAG, "Using custom alert sound for notification");
            } else {
                soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                Log.d(TAG, "Using default alarm sound for notification");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting sound URI: " + e.getMessage());
            soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX) // Maximum priority
                .setCategory(NotificationCompat.CATEGORY_ALARM) // Set as ALARM category
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen
                .setVibrate(new long[]{0, 1000, 500, 1000, 500, 1000})
                .setOnlyAlertOnce(false) // Alert every time
                .setDefaults(NotificationCompat.DEFAULT_ALL | Notification.FLAG_INSISTENT); // Make notification insistent

        // Set AudioAttributes based on Android version
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // For pre-Oreo devices, we need to configure the sound manually
            // Pre-Lollipop devices
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                builder.setSound(soundUri, AudioManager.STREAM_ALARM);
            } else {
                // For Lollipop to Nougat
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build();

                builder.setSound(soundUri, audioAttributes.getVolumeControlStream());
            }
        }
        // For Oreo and above, sound is handled by the notification channel

        // Force maximum volume for alarm stream (pre-Oreo)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                // Set max volume, but save and restore it after a delay
                final int originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

                // Force audio to play even in silent mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                            AudioManager.ADJUST_UNMUTE, 0);
                }

                // Restore volume after a delay
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(() -> {
                    if (audioManager != null) {
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
                    }
                }, 10000); // Restore after 10 seconds
            }
        }

        // Show the notification
        try {
            Notification notification = builder.build();

            // Make the sound repeat until notification is acknowledged
            notification.flags |= Notification.FLAG_INSISTENT;

            // Show notification
            notificationManager.notify(notificationId, notification);

            Log.d(TAG, "Notification posted successfully with ID: " + notificationId);
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage());
        }
    }
}*/
package com.example.geofenceapp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "geofence_channel";
    private static final String CHANNEL_NAME = "Geofence Notifications";
    private static final int NOTIFICATION_ID_ENTER = 1001;
    private static final int NOTIFICATION_ID_EXIT = 1002;

    private static final String MONITORING_CHANNEL_ID = "location_monitoring_channel";
    private static final String MONITORING_CHANNEL_NAME = "Location Monitoring";
    private static final int NOTIFICATION_ID_LOCATION_DISABLED = 1003;
    private static final int NOTIFICATION_ID_MONITORING_SERVICE = 9999;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channels for Android Oreo and above
        createNotificationChannels();
    }

    /**
     * Creates notification channels for Android Oreo and above
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create and configure the geofence alert channel
            NotificationChannel geofenceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Configure the geofence notification channel
            geofenceChannel.setDescription("Critical alerts for geofence transitions");
            geofenceChannel.enableLights(true);
            geofenceChannel.setLightColor(Color.RED);
            geofenceChannel.enableVibration(true);
            geofenceChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000, 500, 1000});

            // Critical settings to bypass DND and silent mode
            geofenceChannel.setBypassDnd(true);
            geofenceChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            geofenceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Set sound with proper audio attributes for alarm sound
            Uri soundUri = null;
            try {
                // Try to get the custom sound resource ID
                int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

                // Create Uri for the sound
                if (soundResourceId != 0) {
                    soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
                    Log.d(TAG, "Using custom alert sound for geofence channel");
                } else {
                    // Use default alarm sound if custom sound is not found
                    soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                    Log.d(TAG, "Using default alarm sound for geofence channel");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting sound URI: " + e.getMessage());
                // Use default alarm sound in case of error
                soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
            }

            // Create AudioAttributes with USAGE_ALARM to bypass silent mode
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM) // This is key for bypassing silent mode
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build();

            // Set the sound for the notification channel with alarm attributes
            geofenceChannel.setSound(soundUri, audioAttributes);

            // Create the monitoring service channel (low importance)
            NotificationChannel monitoringChannel = new NotificationChannel(
                    MONITORING_CHANNEL_ID,
                    MONITORING_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );

            // Configure the monitoring channel (silent)
            monitoringChannel.setDescription("Background location monitoring service");
            monitoringChannel.setSound(null, null);
            monitoringChannel.enableVibration(false);
            monitoringChannel.setShowBadge(false);

            // Register both channels
            notificationManager.createNotificationChannel(geofenceChannel);
            notificationManager.createNotificationChannel(monitoringChannel);

            Log.d(TAG, "All notification channels created successfully");
        }
    }

    /**
     * Shows a notification when location services are disabled
     */
    public void showLocationDisabledNotification() {
        // Create an intent to open location settings
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 1, settingsIntent, pendingIntentFlags);

        // Create an intent to open the app
        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent appPendingIntent = PendingIntent.getActivity(
                context, 2, appIntent, pendingIntentFlags);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Location Services Disabled")
                .setContentText("Geofence monitoring requires location services. Tap to enable.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{0, 500, 200, 500})
                .addAction(0, "Open Settings", pendingIntent)
                .addAction(0, "Open App", appPendingIntent);

        // Show the notification
        try {
            Notification notification = builder.build();
            notificationManager.notify(NOTIFICATION_ID_LOCATION_DISABLED, notification);
            Log.d(TAG, "Location disabled notification posted");
        } catch (Exception e) {
            Log.e(TAG, "Error showing location disabled notification: " + e.getMessage());
        }
    }

    /**
     * Creates a silent notification for the foreground service
     */
    public Notification createMonitoringNotification() {
        // Intent to open the app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, pendingIntentFlags);

        // Build a silent notification
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, MONITORING_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setSound(null)
                    .setVibrate(null);
        }

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Geofence Monitoring Active")
                .setContentText("Monitoring for location changes")
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    /**
     * Shows a notification when exiting a geofence
     */
    public void showGeofenceExitNotification(String message) {
        showGeofenceNotification("Geofence Exit Alert", message, NOTIFICATION_ID_EXIT);
    }

    /**
     * Shows a notification when entering a geofence
     */
    public void showGeofenceEnterNotification(String message) {
        showGeofenceNotification("Geofence Enter Alert", message, NOTIFICATION_ID_ENTER);
    }

    /**
     * Creates and shows a geofence notification with the specified title and message
     */
    private void showGeofenceNotification(String title, String message, int notificationId) {
        // Create an intent to open the app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, pendingIntentFlags);

        // Get the sound URI (will be used for pre-Oreo devices only)
        Uri soundUri = null;
        try {
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());
            if (soundResourceId != 0) {
                soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_sound");
                Log.d(TAG, "Using custom alert sound for notification");
            } else {
                soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                Log.d(TAG, "Using default alarm sound for notification");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting sound URI: " + e.getMessage());
            soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX) // Maximum priority
                .setCategory(NotificationCompat.CATEGORY_ALARM) // Set as ALARM category
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen
                .setVibrate(new long[]{0, 1000, 500, 1000, 500, 1000})
                .setOnlyAlertOnce(false) // Alert every time
                .setDefaults(NotificationCompat.DEFAULT_ALL | Notification.FLAG_INSISTENT); // Make notification insistent

        // For pre-Oreo devices, we need to configure the sound manually
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Force maximum volume for alarm stream (pre-Oreo)
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                // Save original volume to restore later
                final int originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

                // Set to max volume temporarily
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

                // Force audio to play even in silent mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                            AudioManager.ADJUST_UNMUTE, 0);
                }

                // Pre-Lollipop devices
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    builder.setSound(soundUri, AudioManager.STREAM_ALARM);
                } else {
                    // For Lollipop to Nougat
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                            .build();

                    builder.setSound(soundUri, audioAttributes.getVolumeControlStream());
                }

                // Restore volume after a delay
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(() -> {
                    if (audioManager != null) {
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
                    }
                }, 10000); // Restore after 10 seconds
            }
        }
        // For Oreo and above, sound settings are handled by the notification channel

        // Show the notification
        try {
            Notification notification = builder.build();

            // Make the sound repeat until notification is acknowledged
            notification.flags |= Notification.FLAG_INSISTENT;

            // Show notification
            notificationManager.notify(notificationId, notification);
            Log.d(TAG, "Geofence notification posted successfully with ID: " + notificationId);
        } catch (Exception e) {
            Log.e(TAG, "Error showing geofence notification: " + e.getMessage());
        }
    }
}