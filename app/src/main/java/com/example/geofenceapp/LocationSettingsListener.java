package com.example.geofenceapp;

/**
 * Interface for listening to location settings changes.
 * Activities that need to respond to location settings changes should implement this.
 */
public interface LocationSettingsListener {
    /**
     * Called when location settings change.
     *
     * @param enabled True if location services are enabled, false otherwise
     */
    void onLocationSettingsChanged(boolean enabled);
}