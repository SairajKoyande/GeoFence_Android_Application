/*
package com.example.geofenceapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;

*/
/**
 * A helper class that implements square/rectangular geofence functionality
 *//*

public class SquareGeofenceHelper {
    private static final String TAG = "SquareGeofenceHelper";
    private final Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean isMonitoring = false;
    private boolean isUserInside = false;

    // Square/rectangle geofence boundaries
    private double northLat; // Top boundary
    private double southLat; // Bottom boundary
    private double eastLng;  // Right boundary
    private double westLng;  // Left boundary

    public SquareGeofenceHelper(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationCallback();
        createLocationRequest();
    }

    */
/**
     * Create a square geofence centered at the given location with specified side length
     * @param center The center point of the square
     * @param sideLengthMeters The side length of the square in meters
     *//*

    public void createSquareGeofence(LatLng center, float sideLengthMeters) {
        // Calculate the half side length in degrees
        // This is an approximation - 1 degree of latitude is roughly 111,000 meters
        // Longitude degrees vary based on latitude, so we adjust accordingly
        double halfSideLatDegrees = (sideLengthMeters / 2) / 111000.0;
        double halfSideLngDegrees = (sideLengthMeters / 2) / (111000.0 * Math.cos(Math.toRadians(center.latitude)));

        // Calculate the boundaries of the square
        northLat = center.latitude + halfSideLatDegrees;
        southLat = center.latitude - halfSideLatDegrees;
        eastLng = center.longitude + halfSideLngDegrees;
        westLng = center.longitude - halfSideLngDegrees;

        Log.d(TAG, "Square geofence created with bounds: " +
                "N: " + northLat + ", S: " + southLat + ", E: " + eastLng + ", W: " + westLng);
    }

    */
/**
     * Create a rectangular geofence with specified corners
     * @param northEast The northeast corner
     * @param southWest The southwest corner
     *//*

    public void createRectangularGeofence(LatLng northEast, LatLng southWest) {
        northLat = northEast.latitude;
        eastLng = northEast.longitude;
        southLat = southWest.latitude;
        westLng = southWest.longitude;

        Log.d(TAG, "Rectangular geofence created with bounds: " +
                "N: " + northLat + ", S: " + southLat + ", E: " + eastLng + ", W: " + westLng);
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(5000) // Update every 5 seconds
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(2000) // Fastest update interval: 2 seconds
                .build();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    // Check if the user is inside the square/rectangle geofence
                    boolean wasInside = isUserInside;
                    isUserInside = isLocationInGeofence(location);

                    // If the user's state changed, broadcast it
                    if (wasInside != isUserInside) {
                        broadcastGeofenceTransition(location, isUserInside);
                    }
                }
            }
        };
    }

    */
/**
     * Check if the given location is inside the defined rectangular geofence
     *//*

    private boolean isLocationInGeofence(Location location) {
        // Simple boundary check for square/rectangle
        return location.getLatitude() <= northLat &&
                location.getLatitude() >= southLat &&
                location.getLongitude() <= eastLng &&
                location.getLongitude() >= westLng;
    }

    */
/**
     * Broadcast a geofence transition event
     *//*

    private void broadcastGeofenceTransition(Location location, boolean entering) {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        intent.putExtra("isEntering", entering);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());
        context.sendBroadcast(intent);

        Log.d(TAG, "Broadcasting geofence transition: " +
                (entering ? "ENTER" : "EXIT") +
                " at " + location.getLatitude() + ", " + location.getLongitude());
    }

    */
/**
     * Start monitoring for geofence transitions
     *//*

    public void startMonitoring() {
        if (isMonitoring) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted");
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        isMonitoring = true;
        Log.d(TAG, "Started monitoring square geofence");
    }

    */
/**
     * Stop monitoring for geofence transitions
     *//*

    public void stopMonitoring() {
        if (!isMonitoring) {
            return;
        }

        fusedLocationClient.removeLocationUpdates(locationCallback);
        isMonitoring = false;
        Log.d(TAG, "Stopped monitoring square geofence");
    }

    */
/**
     * Check if monitoring is active
     *//*

    public boolean isMonitoring() {
        return isMonitoring;
    }

    */
/**
     * Check if user is currently inside the geofence
     *//*

    public boolean isUserInside() {
        return isUserInside;
    }
}*/
