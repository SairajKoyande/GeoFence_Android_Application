/*package com.example.geofenceapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.LocationServices;

public class    MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1003;

    private Button btnOpenMap;
    private Button btnSettings;
    private TextView txtGeofenceStatus;
    private TextView txtCurrentSettings;

    private GeofenceHelper geofenceHelper;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        btnOpenMap = findViewById(R.id.btn_open_map);
        btnSettings = findViewById(R.id.btn_settings);
        txtGeofenceStatus = findViewById(R.id.txt_geofence_status);
        txtCurrentSettings = findViewById(R.id.txt_current_settings);

        // Initialize GeofenceHelper
        geofenceHelper = new GeofenceHelper(this);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set click listeners
        btnOpenMap.setOnClickListener(v -> openMapActivity());
        btnSettings.setOnClickListener(v -> openSettingsActivity());

        // Check location permissions
        checkAndRequestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGeofenceStatus();
        updateCurrentSettings();
    }

    private void openMapActivity() {
        if (checkLocationPermission()) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            checkAndRequestPermissions();
        }
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Background location permission not needed for Android 9 and below
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permission not needed for Android 12 and below
    }

    private void checkAndRequestPermissions() {
        if (!checkLocationPermission()) {
            requestLocationPermission();
        } else if (!checkBackgroundLocationPermission()) {
            requestBackgroundLocationPermission();
        } else if (!checkNotificationPermission()) {
            requestNotificationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs location permissions to work properly.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE
                        );
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Background Location Permission Needed")
                        .setMessage("This app needs background location permissions to monitor geofences even when the app is not in use.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                                );
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission Needed")
                        .setMessage("This app needs notification permission to alert you about geofence transitions.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    NOTIFICATION_PERMISSION_REQUEST_CODE
                            );
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, now request background location if needed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !checkBackgroundLocationPermission()) {
                    requestBackgroundLocationPermission();
                } else if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Location permission is required for this app", Toast.LENGTH_LONG).show();
                // Show option to open app settings
                showSettingsDialog();
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Background location permission granted", Toast.LENGTH_SHORT).show();
                if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Background location permission is required for geofence monitoring", Toast.LENGTH_LONG).show();
                // Show option to open app settings
                showSettingsDialog();
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications will not work without permission", Toast.LENGTH_LONG).show();
                showSettingsDialog();
            }
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app needs permissions to work properly. Go to settings to grant permissions.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateGeofenceStatus() {
        boolean isGeofenceActive = GeofenceSettings.isGeofenceActive(this);

        if (isGeofenceActive) {
            txtGeofenceStatus.setText(getString(R.string.geofence_status_active));
            txtGeofenceStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            txtGeofenceStatus.setText(getString(R.string.geofence_status_inactive));
            txtGeofenceStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void updateCurrentSettings() {
        double latitude = sharedPreferences.getFloat(GeofenceSettings.KEY_LATITUDE, 0);
        double longitude = sharedPreferences.getFloat(GeofenceSettings.KEY_LONGITUDE, 0);
        float radius = sharedPreferences.getFloat(GeofenceSettings.KEY_RADIUS, 100);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.current_location))
                .append(": ")
                .append(String.format("%.6f, %.6f", latitude, longitude))
                .append("\n")
                .append(getString(R.string.radius))
                .append(": ")
                .append(String.format("%.0f meters", radius));

        txtCurrentSettings.setText(sb.toString());
    }
}*/
/*
package com.example.geofenceapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1003;
    private static final int REQUEST_LOCATION_SETTINGS = 1004;

    private Button btnOpenMap;
    private Button btnSettings;
    private TextView txtGeofenceStatus;
    private TextView txtCurrentSettings;

    private GeofenceHelper geofenceHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        btnOpenMap = findViewById(R.id.btn_open_map);
        btnSettings = findViewById(R.id.btn_settings);
        txtGeofenceStatus = findViewById(R.id.txt_geofence_status);
        txtCurrentSettings = findViewById(R.id.txt_current_settings);

        // Initialize GeofenceHelper
        geofenceHelper = new GeofenceHelper(this);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set click listeners
        btnOpenMap.setOnClickListener(v -> openMapActivity());
        btnSettings.setOnClickListener(v -> openSettingsActivity());

        // Check if location is enabled first
        checkLocationEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGeofenceStatus();
        updateCurrentSettings();

        // Check location status when app resumes
        checkLocationEnabled();
    }

    */
/**
     * Check if location services are enabled on the device
     * If not, prompt the user to enable them
     *//*

    private void checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            // Location services are disabled
            showEnableLocationDialog();
        } else {
            // Location services are enabled, check for permissions
            checkAndRequestPermissions();
        }
    }

    */
/**
     * Show dialog prompting user to enable location services
     *//*

    private void showEnableLocationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Location Services Disabled")
                .setMessage("This app requires location services to function properly. Would you like to enable location services now?")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    // Open location settings
                    Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(locationSettingsIntent, REQUEST_LOCATION_SETTINGS);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(this, "This app requires location services to work properly", Toast.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_SETTINGS) {
            // Check if location is enabled after returning from settings
            checkLocationEnabled();
        }
    }

    private void openMapActivity() {
        if (checkLocationPermission()) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            checkAndRequestPermissions();
        }
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Background location permission not needed for Android 9 and below
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permission not needed for Android 12 and below
    }

    private void checkAndRequestPermissions() {
        if (!checkLocationPermission()) {
            requestLocationPermission();
        } else if (!checkBackgroundLocationPermission()) {
            requestBackgroundLocationPermission();
        } else if (!checkNotificationPermission()) {
            requestNotificationPermission();
        }
    }

    // Rest of your existing methods remain the same...
    // requestLocationPermission, requestBackgroundLocationPermission, etc.

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs location permissions to work properly.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE
                        );
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Background Location Permission Needed")
                        .setMessage("This app needs background location permissions to monitor geofences even when the app is not in use.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                                );
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission Needed")
                        .setMessage("This app needs notification permission to alert you about geofence transitions.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    NOTIFICATION_PERMISSION_REQUEST_CODE
                            );
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, now request background location if needed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !checkBackgroundLocationPermission()) {
                    requestBackgroundLocationPermission();
                } else if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Location permission is required for this app", Toast.LENGTH_LONG).show();
                // Show option to open app settings
                showSettingsDialog();
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Background location permission granted", Toast.LENGTH_SHORT).show();
                if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Background location permission is required for geofence monitoring", Toast.LENGTH_LONG).show();
                // Show option to open app settings
                showSettingsDialog();
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications will not work without permission", Toast.LENGTH_LONG).show();
                showSettingsDialog();
            }
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app needs permissions to work properly. Go to settings to grant permissions.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateGeofenceStatus() {
        boolean isGeofenceActive = GeofenceSettings.isGeofenceActive(this);

        if (isGeofenceActive) {
            txtGeofenceStatus.setText(getString(R.string.geofence_status_active));
            txtGeofenceStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            txtGeofenceStatus.setText(getString(R.string.geofence_status_inactive));
            txtGeofenceStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void updateCurrentSettings() {
        double latitude = sharedPreferences.getFloat(GeofenceSettings.KEY_LATITUDE, 0);
        double longitude = sharedPreferences.getFloat(GeofenceSettings.KEY_LONGITUDE, 0);
        float radius = sharedPreferences.getFloat(GeofenceSettings.KEY_RADIUS, 100);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.current_location))
                .append(": ")
                .append(String.format("%.6f, %.6f", latitude, longitude))
                .append("\n")
                .append(getString(R.string.radius))
                .append(": ")
                .append(String.format("%.0f meters", radius));

        txtCurrentSettings.setText(sb.toString());
    }
}*/
/*
package com.example.geofenceapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1003;

    private Button btnOpenMap;
    private Button btnSettings;
    private TextView txtGeofenceStatus;
    private TextView txtCurrentSettings;

    private GeofenceHelper geofenceHelper;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        btnOpenMap = findViewById(R.id.btn_open_map);
        btnSettings = findViewById(R.id.btn_settings);
        txtGeofenceStatus = findViewById(R.id.txt_geofence_status);
        txtCurrentSettings = findViewById(R.id.txt_current_settings);

        // Initialize GeofenceHelper
        geofenceHelper = new GeofenceHelper(this);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set click listeners
        btnOpenMap.setOnClickListener(v -> openMapActivity());
        btnSettings.setOnClickListener(v -> openSettingsActivity());

        // Check if location services are enabled
        checkLocationServices();

        // Check location permissions
        checkAndRequestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if location services are enabled when app resumes
        checkLocationServices();
        updateGeofenceStatus();
        updateCurrentSettings();
    }

    */
/**
     * Check if location services are enabled and prompt the user to enable them if they are not
     *//*

    private void checkLocationServices() {
        if (!LocationServiceChecker.isLocationEnabled(this)) {
            Toast.makeText(this, "Location services are disabled. Please enable them to use this app.", Toast.LENGTH_LONG).show();
            LocationServiceChecker.showLocationPrompt(this);
        }
    }

    private void openMapActivity() {
        if (!LocationServiceChecker.isLocationEnabled(this)) {
            Toast.makeText(this, "Location services are disabled. Please enable them to use the map.", Toast.LENGTH_LONG).show();
            LocationServiceChecker.showLocationPrompt(this);
            return;
        }

        if (checkLocationPermission()) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            checkAndRequestPermissions();
        }
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Background location permission not needed for Android 9 and below
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permission not needed for Android 12 and below
    }

    private void checkAndRequestPermissions() {
        if (!checkLocationPermission()) {
            requestLocationPermission();
        } else if (!checkBackgroundLocationPermission()) {
            requestBackgroundLocationPermission();
        } else if (!checkNotificationPermission()) {
            requestNotificationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs location permissions to work properly.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE
                        );
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Background Location Permission Needed")
                        .setMessage("This app needs background location permissions to monitor geofences even when the app is not in use.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                                );
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission Needed")
                        .setMessage("This app needs notification permission to alert you about geofence transitions.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    NOTIFICATION_PERMISSION_REQUEST_CODE
                            );
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, now request background location if needed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !checkBackgroundLocationPermission()) {
                    requestBackgroundLocationPermission();
                } else if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Location permission is required for this app", Toast.LENGTH_LONG).show();
                // Show option to open app settings
                showSettingsDialog();
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Background location permission granted", Toast.LENGTH_SHORT).show();
                if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Background location permission is required for geofence monitoring", Toast.LENGTH_LONG).show();
                // Show option to open app settings
                showSettingsDialog();
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications will not work without permission", Toast.LENGTH_LONG).show();
                showSettingsDialog();
            }
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app needs permissions to work properly. Go to settings to grant permissions.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateGeofenceStatus() {
        boolean isGeofenceActive = GeofenceSettings.isGeofenceActive(this);

        if (isGeofenceActive) {
            txtGeofenceStatus.setText(getString(R.string.geofence_status_active));
            txtGeofenceStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            txtGeofenceStatus.setText(getString(R.string.geofence_status_inactive));
            txtGeofenceStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void updateCurrentSettings() {
        double latitude = sharedPreferences.getFloat(GeofenceSettings.KEY_LATITUDE, 0);
        double longitude = sharedPreferences.getFloat(GeofenceSettings.KEY_LONGITUDE, 0);
        float radius = sharedPreferences.getFloat(GeofenceSettings.KEY_RADIUS, 100);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.current_location))
                .append(": ")
                .append(String.format("%.6f, %.6f", latitude, longitude))
                .append("\n")
                .append(getString(R.string.radius))
                .append(": ")
                .append(String.format("%.0f meters", radius));

        txtCurrentSettings.setText(sb.toString());
    }
}*/
package com.example.geofenceapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity implements LocationSettingsListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1003;

    private Button btnOpenMap;
    private Button btnSettings;
    private TextView txtGeofenceStatus;
    private TextView txtCurrentSettings;

    private GeofenceHelper geofenceHelper;
    private SharedPreferences sharedPreferences;

    // Add a field to track the dialog state to prevent multiple dialogs
    private AlertDialog locationAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        btnOpenMap = findViewById(R.id.btn_open_map);
        btnSettings = findViewById(R.id.btn_settings);
        txtGeofenceStatus = findViewById(R.id.txt_geofence_status);
        txtCurrentSettings = findViewById(R.id.txt_current_settings);

        // Initialize GeofenceHelper
        geofenceHelper = new GeofenceHelper(this);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set click listeners
        btnOpenMap.setOnClickListener(v -> openMapActivity());
        btnSettings.setOnClickListener(v -> openSettingsActivity());

        // Start the location monitoring service
        LocationMonitoringService.startService(this);

        // Register this activity as the listener for location settings changes
        LocationMonitoringService.setLocationSettingsListener(this);

        // Check if location services are enabled
        checkLocationServices();

        // Check location permissions
        checkAndRequestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register this activity as the listener when it comes to foreground
        LocationMonitoringService.setLocationSettingsListener(this);

        // Check if location services are enabled when app resumes
        checkLocationServices();
        updateGeofenceStatus();
        updateCurrentSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Dismiss any showing dialog when activity goes to background
        dismissLocationDialog();
    }

    @Override
    protected void onDestroy() {
        // Dismiss any showing dialog
        dismissLocationDialog();

        // Clear the listener reference when activity is destroyed
        if (LocationMonitoringService.locationSettingsListener == this) {
            LocationMonitoringService.setLocationSettingsListener(null);
        }

        super.onDestroy();
    }

    /**
     * Implementation of LocationSettingsListener interface.
     * Called when location settings change.
     */
    @Override
    public void onLocationSettingsChanged(boolean enabled) {
        if (!enabled) {
            // Show alert dialog immediately when location is turned off
            showLocationAlertDialog();
        } else {
            // Location is now enabled, dismiss dialog if showing
            dismissLocationDialog();

            // Optionally show a toast or update UI
            Toast.makeText(this, "Location services are now enabled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if location services are enabled and prompt the user to enable them if they are not
     */
    private void checkLocationServices() {
        if (!LocationServiceChecker.isLocationEnabled(this)) {
            showLocationAlertDialog();
        }
    }

    /**
     * Shows an alert dialog for location services being disabled
     */
    private void showLocationAlertDialog() {
        // Run on UI thread since this might be called from a background thread
        runOnUiThread(() -> {
            // Dismiss any existing dialog first
            dismissLocationDialog();

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Location Services Required")
                    .setMessage("Location services have been turned off. Geofence monitoring requires location services to be enabled.")
                    .setCancelable(false)
                    .setPositiveButton("Enable Now", (dialog, which) -> {
                        // Open location settings
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });

            // Add cancel button only if we want to allow user to dismiss
            builder.setNegativeButton("Later", (dialog, which) -> {
                dialog.dismiss();
                // Optionally show a toast explaining consequences
                Toast.makeText(this,
                        "Geofence monitoring will not work until location is enabled",
                        Toast.LENGTH_LONG).show();
            });

            // Show the dialog
            locationAlertDialog = builder.create();
            locationAlertDialog.show();
        });
    }

    /**
     * Dismisses the location alert dialog if it's showing
     */
    private void dismissLocationDialog() {
        if (locationAlertDialog != null && locationAlertDialog.isShowing()) {
            try {
                locationAlertDialog.dismiss();
            } catch (Exception e) {
                Log.e("MainActivity", "Error dismissing dialog: " + e.getMessage());
            }
            locationAlertDialog = null;
        }
    }

    private void openMapActivity() {
        if (!LocationServiceChecker.isLocationEnabled(this)) {
            showLocationAlertDialog();
            return;
        }

        if (checkLocationPermission()) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            checkAndRequestPermissions();
        }
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Background location permission not needed for Android 9 and below
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permission not needed for Android 12 and below
    }

    private void checkAndRequestPermissions() {
        if (!checkLocationPermission()) {
            requestLocationPermission();
        } else if (!checkBackgroundLocationPermission()) {
            requestBackgroundLocationPermission();
        } else if (!checkNotificationPermission()) {
            requestNotificationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs location permissions to work properly.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE
                        );
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Background Location Permission Needed")
                        .setMessage("This app needs background location permissions to monitor geofences even when the app is not in use.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                                );
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission Needed")
                        .setMessage("This app needs notification permission to alert you about geofence transitions.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    NOTIFICATION_PERMISSION_REQUEST_CODE
                            );
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, now request background location if needed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !checkBackgroundLocationPermission()) {
                    requestBackgroundLocationPermission();
                } else if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Location permission is required for this app", Toast.LENGTH_LONG).show();
                // Show option to open app settings
                showSettingsDialog();
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Background location permission granted", Toast.LENGTH_SHORT).show();
                if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Background location permission is required for geofence monitoring", Toast.LENGTH_LONG).show();
                // Show option to open app settings
                showSettingsDialog();
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications will not work without permission", Toast.LENGTH_LONG).show();
                showSettingsDialog();
            }
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app needs permissions to work properly. Go to settings to grant permissions.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateGeofenceStatus() {
        boolean isGeofenceActive = GeofenceSettings.isGeofenceActive(this);

        if (isGeofenceActive) {
            txtGeofenceStatus.setText(getString(R.string.geofence_status_active));
            txtGeofenceStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            txtGeofenceStatus.setText(getString(R.string.geofence_status_inactive));
            txtGeofenceStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void updateCurrentSettings() {
        double latitude = sharedPreferences.getFloat(GeofenceSettings.KEY_LATITUDE, 0);
        double longitude = sharedPreferences.getFloat(GeofenceSettings.KEY_LONGITUDE, 0);
        float radius = sharedPreferences.getFloat(GeofenceSettings.KEY_RADIUS, 100);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.current_location))
                .append(": ")
                .append(String.format("%.6f, %.6f", latitude, longitude))
                .append("\n")
                .append(getString(R.string.radius))
                .append(": ")
                .append(String.format("%.0f meters", radius));

        txtCurrentSettings.setText(sb.toString());
    }
}