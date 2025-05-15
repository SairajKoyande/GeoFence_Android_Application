/*
package com.example.geofenceapp;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

*/
/**
 * Helper class to manage audio settings and DND mode
 *//*

public class AudioSettingsHelper {
    private static final String TAG = "AudioSettingsHelper";

    */
/**
     * Check if the app has DND access permission
     * @param context Application context
     * @return true if app has DND access permission
     *//*

    public static boolean hasDndPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null &&
                    notificationManager.isNotificationPolicyAccessGranted();
        }
        return true; // No DND permission needed for versions below Android M
    }

    */
/**
     * Open the DND access settings screen
     * @param context Application context
     *//*

    public static void openDndSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    */
/**
     * Temporarily disable DND mode to allow sounds to play
     * @param context Application context
     * @return Previous interruption filter to restore later, or -1 if not modified
     *//*

    public static int disableDndMode(Context context) {
        int originalInterruptionFilter = -1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted()) {
                try {
                    // Save current interruption filter
                    originalInterruptionFilter = notificationManager.getCurrentInterruptionFilter();

                    // Set to INTERRUPTION_FILTER_ALL to disable DND
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    Log.d(TAG, "DND mode temporarily disabled");
                } catch (SecurityException e) {
                    Log.e(TAG, "SecurityException when disabling DND mode: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Error disabling DND mode: " + e.getMessage());
                }
            } else {
                Log.d(TAG, "No DND policy access permission");
            }
        }

        return originalInterruptionFilter;
    }

    */
/**
     * Restore previous DND mode setting
     * @param context Application context
     * @param previousInterruptionFilter The previous interruption filter to restore
     *//*

    public static void restoreDndMode(Context context, int previousInterruptionFilter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && previousInterruptionFilter != -1) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted()) {
                try {
                    notificationManager.setInterruptionFilter(previousInterruptionFilter);
                    Log.d(TAG, "DND mode restored to previous setting");
                } catch (SecurityException e) {
                    Log.e(TAG, "SecurityException when restoring DND mode: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Error restoring DND mode: " + e.getMessage());
                }
            }
        }
    }

    */
/**
     * Check if device is in silent mode
     * @param context Application context
     * @return true if device is in silent mode
     *//*

    public static boolean isDeviceInSilentMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            return audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT ||
                    audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
        }
        return false;
    }

    */
/**
     * Temporarily set device to normal ringer mode
     * @param context Application context
     * @return Previous ringer mode to restore later
     *//*

    public static int setNormalRingerMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int originalRingerMode = -1;

        if (audioManager != null) {
            // Save current ringer mode
            originalRingerMode = audioManager.getRingerMode();

            // Check if we have permission to change DND mode
            boolean canChangeRinger = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
                    canChangeRinger = false;
                }
            }

            // Set to normal mode if we have permission
            if (canChangeRinger) {
                try {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    Log.d(TAG, "Set device to normal ringer mode");
                } catch (SecurityException e) {
                    Log.e(TAG, "SecurityException when setting ringer mode: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Error setting ringer mode: " + e.getMessage());
                }
            }
        }

        return originalRingerMode;
    }

    */
/**
     * Restore previous ringer mode
     * @param context Application context
     * @param previousRingerMode The previous ringer mode to restore
     *//*

    public static void restoreRingerMode(Context context, int previousRingerMode) {
        if (previousRingerMode != -1) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                boolean canChangeRinger = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
                        canChangeRinger = false;
                    }
                }

                if (canChangeRinger) {
                    try {
                        audioManager.setRingerMode(previousRingerMode);
                        Log.d(TAG, "Restored previous ringer mode");
                    } catch (SecurityException e) {
                        Log.e(TAG, "SecurityException when restoring ringer mode: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Error restoring ringer mode: " + e.getMessage());
                    }
                }
            }
        }
    }

    */
/**
     * Set maximum volume for alarm stream
     * @param context Application context
     * @return Original volume level to restore later
     *//*

    public static int setMaximumAlarmVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int originalVolume = -1;

        if (audioManager != null) {
            try {
                // Save original volume
                originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

                // Set volume to maximum for alarm stream
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

                // Force audio to play even in silent mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                            AudioManager.ADJUST_UNMUTE, 0);
                }

                Log.d(TAG, "Maximum alarm volume set");
            } catch (Exception e) {
                Log.e(TAG, "Error setting maximum volume: " + e.getMessage());
            }
        }

        return originalVolume;
    }

    */
/**
     * Restore original alarm volume
     * @param context Application context
     * @param originalVolume The original volume level to restore
     *//*

    public static void restoreAlarmVolume(Context context, int originalVolume) {
        if (originalVolume != -1) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                try {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
                    Log.d(TAG, "Original alarm volume restored");
                } catch (Exception e) {
                    Log.e(TAG, "Error restoring volume: " + e.getMessage());
                }
            }
        }
    }

    */
/**
     * Ensure all audio-related permissions are granted. If necessary,
     * show the user a dialog to go to settings for DND access.
     *
     * @param context Application context
     * @return true if all needed permissions are granted
     *//*

    public static boolean ensureAudioPermissions(Context context) {
        // For Android M and above, check DND access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
                return false;
            }
        }

        return true;
    }
}*/
