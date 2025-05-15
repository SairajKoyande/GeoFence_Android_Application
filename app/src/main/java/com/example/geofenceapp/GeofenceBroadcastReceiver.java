/*
package com.example.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "GeofenceBroadcastReceiver.onReceive() called");

        if (intent == null) {
            Log.e(TAG, "Intent is null in GeofenceBroadcastReceiver");
            return;
        }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null) {
            Log.e(TAG, "Geofencing Event is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, "Geofencing Error: " + errorMessage);
            return;
        }

        // Get and log the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "Geofence transition type: " + getTransitionString(geofenceTransition));

        // Check if the transition type is EXIT or ENTER
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the geofences that were triggered
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();

            if (triggeredGeofences != null && !triggeredGeofences.isEmpty()) {
                // Extract details about the triggered geofences
                String geofenceId = triggeredGeofences.get(0).getRequestId();

                // Get the transition type (ENTER or EXIT)
                String transitionType = getTransitionString(geofenceTransition);

                // Log the event
                Log.i(TAG, "Geofence event detected: " + transitionType + " for " + geofenceId);

                // Get the location that triggered the geofence
                Location triggeringLocation = geofencingEvent.getTriggeringLocation();
                String locationInfo = "";

                if (triggeringLocation != null) {
                    locationInfo = " at location " +
                            triggeringLocation.getLatitude() + ", " +
                            triggeringLocation.getLongitude();
                }

                // Check if notifications are enabled in settings
                if (GeofenceSettings.isNotificationEnabled(context)) {
                    // Create a notification message based on transition type
                    String notificationMessage;
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationMessage = "You have exited the geofence area" + locationInfo;
                    } else {
                        notificationMessage = "You have entered the geofence area" + locationInfo;
                    }

                    // Create and show notification
                    NotificationHelper notificationHelper = new NotificationHelper(context);

                    // Use the appropriate notification method based on transition type
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationHelper.showGeofenceExitNotification(notificationMessage);
                    } else {
                        notificationHelper.showGeofenceEnterNotification(notificationMessage);
                    }
                }

                // Check if sound alerts are enabled
                if (GeofenceSettings.isSoundEnabled(context)) {
                    // Play alert sound
                    playAlertSound(context);
                }

                // Check if vibration is enabled
                if (GeofenceSettings.isVibrationEnabled(context)) {
                    // Vibrate the device
                    vibrate(context);
                }
            }
        }
    }

    private void playAlertSound(Context context) {
        try {
            // Try playing the sound from raw resources first
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

            MediaPlayer mediaPlayer;
            if (soundResourceId != 0) {
                // Use the sound from raw resources
                mediaPlayer = MediaPlayer.create(context, soundResourceId);
                Log.d(TAG, "Using custom alert sound from resources");
            } else {
                // Fallback to system notification sound
                mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
                Log.d(TAG, "Using system default notification sound");
            }

            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
                Log.d(TAG, "Alert sound playing successfully");
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for alert sound");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alert sound: " + e.getMessage());

            // Fallback method for playing sound
            try {
                MediaPlayer fallbackPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
                if (fallbackPlayer != null) {
                    fallbackPlayer.setOnCompletionListener(MediaPlayer::release);
                    fallbackPlayer.start();
                    Log.d(TAG, "Fallback alert sound playing successfully");
                }
            } catch (Exception ex) {
                Log.e(TAG, "Fallback sound also failed: " + ex.getMessage());
            }
        }
    }

    private void vibrate(Context context) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                // Vibrate pattern: wait 0ms, vibrate 1000ms, pause 500ms, vibrate 1000ms
                long[] pattern = {0, 1000, 500, 1000};

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    // For older devices
                    vibrator.vibrate(pattern, -1);
                }
                Log.d(TAG, "Device vibration triggered");
            } else {
                Log.d(TAG, "Device does not support vibration");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error triggering vibration: " + e.getMessage());
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "GEOFENCE_TRANSITION_ENTER";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "GEOFENCE_TRANSITION_EXIT";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "GEOFENCE_TRANSITION_DWELL";
            default:
                return "UNKNOWN_TRANSITION_TYPE: " + transitionType;
        }
    }
}

class GeofenceStatusCodes {
    public static String getStatusCodeString(int statusCode) {
        switch (statusCode) {
            case 1:
                return "GEOFENCE_NOT_AVAILABLE";
            case 2:
                return "GEOFENCE_TOO_MANY_GEOFENCES";
            case 3:
                return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            default:
                return "UNKNOWN_ERROR_CODE: " + statusCode;
        }
    }
}*/
/*
package com.example.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "GeofenceBroadcastReceiver.onReceive() called");

        if (intent == null) {
            Log.e(TAG, "Intent is null in GeofenceBroadcastReceiver");
            return;
        }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null) {
            Log.e(TAG, "Geofencing Event is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, "Geofencing Error: " + errorMessage);
            return;
        }

        // Get and log the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "Geofence transition type: " + getTransitionString(geofenceTransition));

        // Check if the transition type is EXIT or ENTER
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the geofences that were triggered
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();

            if (triggeredGeofences != null && !triggeredGeofences.isEmpty()) {
                // Extract details about the triggered geofences
                String geofenceId = triggeredGeofences.get(0).getRequestId();

                // Get the transition type (ENTER or EXIT)
                String transitionType = getTransitionString(geofenceTransition);

                // Log the event
                Log.i(TAG, "Geofence event detected: " + transitionType + " for " + geofenceId);

                // Get the location that triggered the geofence
                Location triggeringLocation = geofencingEvent.getTriggeringLocation();
                String locationInfo = "";

                if (triggeringLocation != null) {
                    locationInfo = " at location " +
                            triggeringLocation.getLatitude() + ", " +
                            triggeringLocation.getLongitude();
                }

                // Check if notifications are enabled in settings
                if (GeofenceSettings.isNotificationEnabled(context)) {
                    // Create a notification message based on transition type
                    String notificationMessage;
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationMessage = "You have exited the geofence area" + locationInfo;
                    } else {
                        notificationMessage = "You have entered the geofence area" + locationInfo;
                    }

                    // Create and show notification
                    NotificationHelper notificationHelper = new NotificationHelper(context);

                    // Use the appropriate notification method based on transition type
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationHelper.showGeofenceExitNotification(notificationMessage);
                    } else {
                        notificationHelper.showGeofenceEnterNotification(notificationMessage);
                    }
                }

                // Check if sound alerts are enabled
                if (GeofenceSettings.isSoundEnabled(context)) {
                    // Play alert sound
                    playAlertSound(context);
                }

                // Check if vibration is enabled
                if (GeofenceSettings.isVibrationEnabled(context)) {
                    // Vibrate the device
                    vibrate(context);
                }
            }
        }
    }

    private void playAlertSound(Context context) {
        try {
            // Try playing the sound from raw resources first
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

            MediaPlayer mediaPlayer;
            if (soundResourceId != 0) {
                // Use the sound from raw resources with ALARM audio attributes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();

                    mediaPlayer = MediaPlayer.create(context, soundResourceId);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    mediaPlayer = MediaPlayer.create(context, soundResourceId);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_ALARM);
                    }
                }
                Log.d(TAG, "Using custom alert sound from resources with alarm stream");
            } else {
                // Fallback to system alarm sound
                Uri defaultAlarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();

                    mediaPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    mediaPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_ALARM);
                    }
                }
                Log.d(TAG, "Using system default alarm sound");
            }

            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
                Log.d(TAG, "Alert sound playing successfully on alarm stream");
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for alert sound");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alert sound: " + e.getMessage());

            // Fallback method for playing sound
            try {
                MediaPlayer fallbackPlayer;
                Uri defaultAlarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();

                    fallbackPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (fallbackPlayer != null) {
                        fallbackPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    fallbackPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (fallbackPlayer != null) {
                        fallbackPlayer.setAudioStreamType(android.media.AudioManager.STREAM_ALARM);
                    }
                }

                if (fallbackPlayer != null) {
                    fallbackPlayer.setOnCompletionListener(MediaPlayer::release);
                    fallbackPlayer.start();
                    Log.d(TAG, "Fallback alert sound playing successfully");
                }
            } catch (Exception ex) {
                Log.e(TAG, "Fallback sound also failed: " + ex.getMessage());
            }
        }
    }

    private void vibrate(Context context) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                // Vibrate pattern: wait 0ms, vibrate 1000ms, pause 500ms, vibrate 1000ms
                long[] pattern = {0, 1000, 500, 1000};

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For newer devices, use VibrationEffect and specify AUDIO_ATTRIBUTES_USAGE_ALARM
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build();

                    VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
                    vibrator.vibrate(vibrationEffect, audioAttributes);
                } else {
                    // For older devices
                    vibrator.vibrate(pattern, -1);
                }
                Log.d(TAG, "Device vibration triggered with alarm attributes");
            } else {
                Log.d(TAG, "Device does not support vibration");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error triggering vibration: " + e.getMessage());
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "GEOFENCE_TRANSITION_ENTER";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "GEOFENCE_TRANSITION_EXIT";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "GEOFENCE_TRANSITION_DWELL";
            default:
                return "UNKNOWN_TRANSITION_TYPE: " + transitionType;
        }
    }
}
class GeofenceStatusCodes {
    public static String getStatusCodeString(int statusCode) {
        switch (statusCode) {
            case 1:
                return "GEOFENCE_NOT_AVAILABLE";
            case 2:
                return "GEOFENCE_TOO_MANY_GEOFENCES";
            case 3:
                return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            default:
                return "UNKNOWN_ERROR_CODE: " + statusCode;
        }
    }
}*/
/*
package com.example.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "GeofenceBroadcastReceiver.onReceive() called");

        if (intent == null) {
            Log.e(TAG, "Intent is null in GeofenceBroadcastReceiver");
            return;
        }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null) {
            Log.e(TAG, "Geofencing Event is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, "Geofencing Error: " + errorMessage);
            return;
        }

        // Get and log the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "Geofence transition type: " + getTransitionString(geofenceTransition));

        // Check if the transition type is EXIT or ENTER
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the geofences that were triggered
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();

            if (triggeredGeofences != null && !triggeredGeofences.isEmpty()) {
                // Extract details about the triggered geofences
                String geofenceId = triggeredGeofences.get(0).getRequestId();

                // Get the transition type (ENTER or EXIT)
                String transitionType = getTransitionString(geofenceTransition);

                // Log the event
                Log.i(TAG, "Geofence event detected: " + transitionType + " for " + geofenceId);

                // Get the location that triggered the geofence
                Location triggeringLocation = geofencingEvent.getTriggeringLocation();
                String locationInfo = "";

                if (triggeringLocation != null) {
                    locationInfo = " at location " +
                            triggeringLocation.getLatitude() + ", " +
                            triggeringLocation.getLongitude();
                }

                // Check if notifications are enabled in settings
                if (GeofenceSettings.isNotificationEnabled(context)) {
                    // Create a notification message based on transition type
                    String notificationMessage;
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationMessage = "You have exited the geofence area" + locationInfo;
                    } else {
                        notificationMessage = "You have entered the geofence area" + locationInfo;
                    }

                    // Create and show notification
                    NotificationHelper notificationHelper = new NotificationHelper(context);

                    // Use the appropriate notification method based on transition type
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationHelper.showGeofenceExitNotification(notificationMessage);
                    } else {
                        notificationHelper.showGeofenceEnterNotification(notificationMessage);
                    }
                }

                // Check if sound alerts are enabled
                if (GeofenceSettings.isSoundEnabled(context)) {
                    // Play alert sound
                    playAlertSound(context);
                }

                // Check if vibration is enabled
                if (GeofenceSettings.isVibrationEnabled(context)) {
                    // Vibrate the device
                    vibrate(context);
                }
            }
        }
    }

    private void playAlertSound(Context context) {
        MediaPlayer mediaPlayer = null;
        try {
            // Try playing the sound from raw resources first
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

            if (soundResourceId != 0) {
                // Use the sound from raw resources with ALARM audio attributes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Add this flag to enforce playing in silent mode
                            .build();

                    mediaPlayer = MediaPlayer.create(context, soundResourceId);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    mediaPlayer = MediaPlayer.create(context, soundResourceId);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    }
                }
                Log.d(TAG, "Using custom alert sound from resources with alarm stream");
            } else {
                // Fallback to system alarm sound
                Uri defaultAlarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Add this flag to enforce playing in silent mode
                            .build();

                    mediaPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    mediaPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    }
                }
                Log.d(TAG, "Using system default alarm sound");
            }

            if (mediaPlayer != null) {
                // Add error handling for MediaPlayer
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                    mp.release();
                    return true;
                });

                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
                Log.d(TAG, "Alert sound playing successfully on alarm stream");
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for alert sound");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alert sound: " + e.getMessage());
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            // Fallback method for playing sound
            try {
                MediaPlayer fallbackPlayer = null;
                Uri defaultAlarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Add this flag to enforce playing in silent mode
                            .build();

                    fallbackPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (fallbackPlayer != null) {
                        fallbackPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    fallbackPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (fallbackPlayer != null) {
                        fallbackPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    }
                }

                if (fallbackPlayer != null) {
                    fallbackPlayer.setOnCompletionListener(MediaPlayer::release);
                    fallbackPlayer.start();
                    Log.d(TAG, "Fallback alert sound playing successfully");
                }
            } catch (Exception ex) {
                Log.e(TAG, "Fallback sound also failed: " + ex.getMessage());
            }
        }
    }

    private void vibrate(Context context) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                // Vibrate pattern: wait 0ms, vibrate 1000ms, pause 500ms, vibrate 1000ms
                long[] pattern = {0, 1000, 500, 1000};

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For newer devices, use VibrationEffect and specify AUDIO_ATTRIBUTES_USAGE_ALARM
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Add this flag to enforce vibration in silent mode
                            .build();

                    VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
                    vibrator.vibrate(vibrationEffect, audioAttributes);
                } else {
                    // For older devices
                    vibrator.vibrate(pattern, -1);
                }
                Log.d(TAG, "Device vibration triggered with alarm attributes");
            } else {
                Log.d(TAG, "Device does not support vibration");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error triggering vibration: " + e.getMessage());
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "GEOFENCE_TRANSITION_ENTER";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "GEOFENCE_TRANSITION_EXIT";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "GEOFENCE_TRANSITION_DWELL";
            default:
                return "UNKNOWN_TRANSITION_TYPE: " + transitionType;
        }
    }
}
class GeofenceStatusCodes {
    public static String getStatusCodeString(int statusCode) {
        switch (statusCode) {
            case 1:
                return "GEOFENCE_NOT_AVAILABLE";
            case 2:
                return "GEOFENCE_TOO_MANY_GEOFENCES";
            case 3:
                return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            default:
                return "UNKNOWN_ERROR_CODE: " + statusCode;
        }
    }
}*/
/*
package com.example.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "GeofenceBroadcastReceiver.onReceive() called");

        if (intent == null) {
            Log.e(TAG, "Intent is null in GeofenceBroadcastReceiver");
            return;
        }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null) {
            Log.e(TAG, "Geofencing Event is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, "Geofencing Error: " + errorMessage);
            return;
        }

        // Get and log the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "Geofence transition type: " + getTransitionString(geofenceTransition));

        // Check if the transition type is EXIT or ENTER
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the geofences that were triggered
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();

            if (triggeredGeofences != null && !triggeredGeofences.isEmpty()) {
                // Extract details about the triggered geofences
                String geofenceId = triggeredGeofences.get(0).getRequestId();

                // Get the transition type (ENTER or EXIT)
                String transitionType = getTransitionString(geofenceTransition);

                // Log the event
                Log.i(TAG, "Geofence event detected: " + transitionType + " for " + geofenceId);

                // Get the location that triggered the geofence
                Location triggeringLocation = geofencingEvent.getTriggeringLocation();
                String locationInfo = "";

                if (triggeringLocation != null) {
                    locationInfo = " at location " +
                            triggeringLocation.getLatitude() + ", " +
                            triggeringLocation.getLongitude();
                }

                // Check if notifications are enabled in settings
                if (GeofenceSettings.isNotificationEnabled(context)) {
                    // Create a notification message based on transition type
                    String notificationMessage;
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationMessage = "You have exited the geofence area" + locationInfo;
                    } else {
                        notificationMessage = "You have entered the geofence area" + locationInfo;
                    }

                    // Create and show notification
                    NotificationHelper notificationHelper = new NotificationHelper(context);

                    // Use the appropriate notification method based on transition type
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationHelper.showGeofenceExitNotification(notificationMessage);
                    } else {
                        notificationHelper.showGeofenceEnterNotification(notificationMessage);
                    }
                }

                // Check if sound alerts are enabled
                if (GeofenceSettings.isSoundEnabled(context)) {
                    // Play alert sound
                    playAlertSound(context);
                }

                // Check if vibration is enabled
                if (GeofenceSettings.isVibrationEnabled(context)) {
                    // Vibrate the device
                    vibrate(context);
                }
            }
        }
    }

    private void playAlertSound(Context context) {
        MediaPlayer mediaPlayer = null;
        AudioManager audioManager = null;
        int originalVolume = -1;

        try {
            // Get the AudioManager
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            // Save original volume setting
            if (audioManager != null) {
                originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

                // Set volume to maximum for alarm stream
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

                // Force audio to play even in silent mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                            AudioManager.ADJUST_UNMUTE, 0);
                }
            }

            // Try playing the sound from raw resources first
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());

            if (soundResourceId != 0) {
                // Use the sound from raw resources with ALARM audio attributes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Enforce playing in silent mode
                            .build();

                    mediaPlayer = MediaPlayer.create(context, soundResourceId);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    mediaPlayer = MediaPlayer.create(context, soundResourceId);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    }
                }
                Log.d(TAG, "Using custom alert sound from resources with alarm stream");
            } else {
                // Fallback to system alarm sound
                Uri defaultAlarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Enforce playing in silent mode
                            .build();

                    mediaPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    mediaPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    }
                }
                Log.d(TAG, "Using system default alarm sound");
            }

            if (mediaPlayer != null) {
                // Add error handling for MediaPlayer
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                    restoreVolume(audioManager, originalVolume);
                    mp.release();
                    return true;
                });

                // Ensure volume is restored after playback
                final AudioManager finalAudioManager = audioManager;
                final int finalOriginalVolume = originalVolume;

                mediaPlayer.setOnCompletionListener(mp -> {
                    restoreVolume(finalAudioManager, finalOriginalVolume);
                    mp.release();
                });

                // Set MediaPlayer to loop a few times for better alert effectiveness
                mediaPlayer.setLooping(true);

                // Play at maximum volume
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();

                // Stop looping after several seconds
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(() -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.setLooping(false);
                    }
                }, 10000); // Stop looping after 10 seconds

                Log.d(TAG, "Alert sound playing successfully on alarm stream");
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for alert sound");
                restoreVolume(audioManager, originalVolume);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alert sound: " + e.getMessage());
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            restoreVolume(audioManager, originalVolume);

            // Fallback method for playing sound
            fallbackSoundPlay(context);
        }
    }

    private void fallbackSoundPlay(Context context) {
        MediaPlayer fallbackPlayer = null;
        AudioManager audioManager = null;
        int originalVolume = -1;

        try {
            // Get the AudioManager
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            // Save original volume setting
            if (audioManager != null) {
                originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

                // Set volume to maximum for alarm stream
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

                // Force audio to play even in silent mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                            AudioManager.ADJUST_UNMUTE, 0);
                }
            }

            Uri defaultAlarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Enforce playing in silent mode
                        .build();

                fallbackPlayer = MediaPlayer.create(context, defaultAlarmUri);
                if (fallbackPlayer != null) {
                    fallbackPlayer.setAudioAttributes(audioAttributes);
                }
            } else {
                // For older devices
                fallbackPlayer = MediaPlayer.create(context, defaultAlarmUri);
                if (fallbackPlayer != null) {
                    fallbackPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                }
            }

            if (fallbackPlayer != null) {
                // Ensure volume is restored after playback
                final AudioManager finalAudioManager = audioManager;
                final int finalOriginalVolume = originalVolume;

                fallbackPlayer.setOnCompletionListener(mp -> {
                    restoreVolume(finalAudioManager, finalOriginalVolume);
                    mp.release();
                });

                // Play at maximum volume
                fallbackPlayer.setVolume(1.0f, 1.0f);
                fallbackPlayer.start();
                Log.d(TAG, "Fallback alert sound playing successfully");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Fallback sound also failed: " + ex.getMessage());
            if (fallbackPlayer != null) {
                fallbackPlayer.release();
            }
            restoreVolume(audioManager, originalVolume);
        }
    }

    private void restoreVolume(AudioManager audioManager, int originalVolume) {
        // Restore original volume if we changed it
        if (audioManager != null && originalVolume >= 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
        }
    }

    private void vibrate(Context context) {
        try {
            Vibrator vibrator = null;

            // Get the vibrator service based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                if (vibratorManager != null) {
                    vibrator = vibratorManager.getDefaultVibrator();
                }
            } else {
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                // Strong vibration pattern to ensure user notices
                // wait 0ms, vibrate 1000ms, pause 500ms, vibrate 1000ms, pause 500ms, vibrate 1000ms
                long[] pattern = {0, 1000, 500, 1000, 500, 1000};

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For newer devices, use VibrationEffect and specify AUDIO_ATTRIBUTES_USAGE_ALARM
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Add this flag to enforce vibration in silent mode
                            .build();

                    // Create effect with maximum strength
                    VibrationEffect vibrationEffect;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // For Android 10+, use createWaveform with amplitudes
                        int[] amplitudes = {0, 255, 0, 255, 0, 255}; // Max amplitude
                        vibrationEffect = VibrationEffect.createWaveform(pattern, amplitudes, -1);
                    } else {
                        // For Android 8-9
                        vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
                    }

                    vibrator.vibrate(vibrationEffect, audioAttributes);
                } else {
                    // For older devices
                    vibrator.vibrate(pattern, -1);
                }
                Log.d(TAG, "Device vibration triggered with alarm attributes");
            } else {
                Log.d(TAG, "Device does not support vibration");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error triggering vibration: " + e.getMessage());
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "GEOFENCE_TRANSITION_ENTER";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "GEOFENCE_TRANSITION_EXIT";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "GEOFENCE_TRANSITION_DWELL";
            default:
                return "UNKNOWN_TRANSITION_TYPE: " + transitionType;
        }
    }
}*/
package com.example.geofenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "GeofenceBroadcastReceiver.onReceive() called");

        if (intent == null) {
            Log.e(TAG, "Intent is null in GeofenceBroadcastReceiver");
            return;
        }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null) {
            Log.e(TAG, "Geofencing Event is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, "Geofencing Error: " + errorMessage);
            return;
        }

        // Get and log the transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "Geofence transition type: " + getTransitionString(geofenceTransition));

        // Check if the transition type is EXIT or ENTER
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the geofences that were triggered
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();

            if (triggeredGeofences != null && !triggeredGeofences.isEmpty()) {
                // Extract details about the triggered geofences
                String geofenceId = triggeredGeofences.get(0).getRequestId();

                // Get the transition type (ENTER or EXIT)
                String transitionType = getTransitionString(geofenceTransition);

                // Log the event
                Log.i(TAG, "Geofence event detected: " + transitionType + " for " + geofenceId);

                // Get the location that triggered the geofence
                Location triggeringLocation = geofencingEvent.getTriggeringLocation();
                String locationInfo = "";

                if (triggeringLocation != null) {
                    locationInfo = " at location " +
                            triggeringLocation.getLatitude() + ", " +
                            triggeringLocation.getLongitude();
                }

                // Check if notifications are enabled in settings
                if (GeofenceSettings.isNotificationEnabled(context)) {
                    // Create a notification message based on transition type
                    String notificationMessage;
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationMessage = "You have exited the geofence area" + locationInfo;
                    } else {
                        notificationMessage = "You have entered the geofence area" + locationInfo;
                    }

                    // Create and show notification
                    NotificationHelper notificationHelper = new NotificationHelper(context);

                    // Use the appropriate notification method based on transition type
                    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        notificationHelper.showGeofenceExitNotification(notificationMessage);
                    } else {
                        notificationHelper.showGeofenceEnterNotification(notificationMessage);
                    }
                }

                // Check if sound alerts are enabled
                if (GeofenceSettings.isSoundEnabled(context)) {
                    // Play alert sound
                    playAlertSound(context);
                }

                // Check if vibration is enabled
                if (GeofenceSettings.isVibrationEnabled(context)) {
                    // Vibrate the device
                    vibrate(context);
                }
            }
        }
    }

    private void playAlertSound(Context context) {
        final MediaPlayer[] mediaPlayerRef = new MediaPlayer[1];
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int originalVolume;

        // Save original volume setting
        if (audioManager != null) {
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

            // Set volume to maximum for alarm stream
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

            // Force audio to play even in silent mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                        AudioManager.ADJUST_UNMUTE, 0);
            }
        } else {
            originalVolume = -1;
        }

        try {
            // Try playing the sound from raw resources first
            int soundResourceId = context.getResources().getIdentifier("alert_sound", "raw", context.getPackageName());
            MediaPlayer mediaPlayer = null;

            if (soundResourceId != 0) {
                // Use the sound from raw resources with ALARM audio attributes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Enforce playing in silent mode
                            .build();

                    mediaPlayer = MediaPlayer.create(context, soundResourceId);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    mediaPlayer = MediaPlayer.create(context, soundResourceId);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    }
                }
                Log.d(TAG, "Using custom alert sound from resources with alarm stream");
            } else {
                // Fallback to system alarm sound
                Uri defaultAlarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Enforce playing in silent mode
                            .build();

                    mediaPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioAttributes(audioAttributes);
                    }
                } else {
                    // For older devices
                    mediaPlayer = MediaPlayer.create(context, defaultAlarmUri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    }
                }
                Log.d(TAG, "Using system default alarm sound");
            }

            if (mediaPlayer != null) {
                mediaPlayerRef[0] = mediaPlayer;

                // Add error handling for MediaPlayer
                final int finalOriginalVolume = originalVolume;
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                    restoreVolume(audioManager, finalOriginalVolume);
                    mp.release();
                    return true;
                });

                // Ensure volume is restored after playback
                mediaPlayer.setOnCompletionListener(mp -> {
                    restoreVolume(audioManager, finalOriginalVolume);
                    mp.release();
                });

                // Set MediaPlayer to loop a few times for better alert effectiveness
                mediaPlayer.setLooping(true);

                // Play at maximum volume
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();

                // Stop looping after several seconds
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(() -> {
                    MediaPlayer mp = mediaPlayerRef[0];
                    if (mp != null && mp.isPlaying()) {
                        mp.setLooping(false);
                    }
                }, 10000); // Stop looping after 10 seconds

                Log.d(TAG, "Alert sound playing successfully on alarm stream");
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for alert sound");
                restoreVolume(audioManager, originalVolume);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alert sound: " + e.getMessage());
            if (mediaPlayerRef[0] != null) {
                mediaPlayerRef[0].release();
            }
            restoreVolume(audioManager, originalVolume);

            // Fallback method for playing sound
            fallbackSoundPlay(context);
        }
    }

    private void fallbackSoundPlay(Context context) {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int originalVolume;

        // Save original volume setting
        if (audioManager != null) {
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

            // Set volume to maximum for alarm stream
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

            // Force audio to play even in silent mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                        AudioManager.ADJUST_UNMUTE, 0);
            }
        } else {
            originalVolume = -1;
        }

        try {
            Uri defaultAlarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
            MediaPlayer fallbackPlayer = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Enforce playing in silent mode
                        .build();

                fallbackPlayer = MediaPlayer.create(context, defaultAlarmUri);
                if (fallbackPlayer != null) {
                    fallbackPlayer.setAudioAttributes(audioAttributes);
                }
            } else {
                // For older devices
                fallbackPlayer = MediaPlayer.create(context, defaultAlarmUri);
                if (fallbackPlayer != null) {
                    fallbackPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                }
            }

            if (fallbackPlayer != null) {
                // Ensure volume is restored after playback
                final MediaPlayer finalFallbackPlayer = fallbackPlayer;
                fallbackPlayer.setOnCompletionListener(mp -> {
                    restoreVolume(audioManager, originalVolume);
                    finalFallbackPlayer.release();
                });

                // Play at maximum volume
                fallbackPlayer.setVolume(1.0f, 1.0f);
                fallbackPlayer.start();
                Log.d(TAG, "Fallback alert sound playing successfully");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Fallback sound also failed: " + ex.getMessage());
            restoreVolume(audioManager, originalVolume);
        }
    }

    private void restoreVolume(AudioManager audioManager, int originalVolume) {
        // Restore original volume if we changed it
        if (audioManager != null && originalVolume >= 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
        }
    }

    private void vibrate(Context context) {
        try {
            Vibrator vibrator = null;

            // Get the vibrator service based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                if (vibratorManager != null) {
                    vibrator = vibratorManager.getDefaultVibrator();
                }
            } else {
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                // Strong vibration pattern to ensure user notices
                // wait 0ms, vibrate 1000ms, pause 500ms, vibrate 1000ms, pause 500ms, vibrate 1000ms
                long[] pattern = {0, 1000, 500, 1000, 500, 1000};

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For newer devices, use VibrationEffect and specify AUDIO_ATTRIBUTES_USAGE_ALARM
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) // Add this flag to enforce vibration in silent mode
                            .build();

                    // Create effect with maximum strength
                    VibrationEffect vibrationEffect;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // For Android 10+, use createWaveform with amplitudes
                        int[] amplitudes = {0, 255, 0, 255, 0, 255}; // Max amplitude
                        vibrationEffect = VibrationEffect.createWaveform(pattern, amplitudes, -1);
                    } else {
                        // For Android 8-9
                        vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
                    }

                    vibrator.vibrate(vibrationEffect, audioAttributes);
                } else {
                    // For older devices
                    vibrator.vibrate(pattern, -1);
                }
                Log.d(TAG, "Device vibration triggered with alarm attributes");
            } else {
                Log.d(TAG, "Device does not support vibration");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error triggering vibration: " + e.getMessage());
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "GEOFENCE_TRANSITION_ENTER";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "GEOFENCE_TRANSITION_EXIT";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "GEOFENCE_TRANSITION_DWELL";
            default:
                return "UNKNOWN_TRANSITION_TYPE: " + transitionType;
        }
    }
}