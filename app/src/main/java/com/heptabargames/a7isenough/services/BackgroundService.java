package com.heptabargames.a7isenough.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.heptabargames.a7isenough.R;
import com.heptabargames.a7isenough.daos.SettingsDAO;
import com.heptabargames.a7isenough.listeners.OnEventsLoaded;
import com.heptabargames.a7isenough.listeners.OnManifestLoaded;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60 * 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BackgroundService";
    private SettingsDAO settingsDAO;
    private EventService eventService;

    private class LocationListener implements android.location.LocationListener, OnManifestLoaded, OnEventsLoaded {
        Location mLastLocation;
        List<Event> events;
        Zone currentZone;

        public LocationListener(String provider) {
            Log.d(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            EventService eventService = new EventService(getApplicationContext());
            eventService.getManifest(this);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: " + provider);
        }

        @Override
        public void onManifest(List<Event> listEvents) {
            Log.d(TAG, "onManifest");
            this.events = listEvents;
            eventService.addOnEventsLoadedListener(this);
            eventService.loadAllEvent(listEvents);
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "onError: " + e);
        }

        @Override
        public void onEvents(List<Event> events) {
            this.events = events;
            List<Zone> zones = new ArrayList<>();
            for (Event event : events) {
                zones.addAll(event.getZones());
            }

            LocalizationManager localizationManager = new LocalizationManager();

            currentZone = localizationManager.isInZone(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), zones);

            if (currentZone == null) return;

            List<Beacon> beacons = currentZone.getBeacons();

            boolean allBeaconsFound = true;
            for (Beacon beacon : beacons) {
                if (beacon.getFound() != null) {
                    allBeaconsFound = false;
                    break;
                }
            }
            if (!allBeaconsFound && Boolean.parseBoolean(settingsDAO.getParameter("isChecked"))) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.logo_complete)
                        .setContentTitle(getString(R.string.notification_in_zone_title, currentZone.getName()))
                        .setContentText(getString(R.string.notification_in_zone_desc))
                        .setPriority(NotificationCompat.PRIORITY_LOW);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }

    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        eventService = new EventService(getBaseContext());
        initializeLocationManager();
        settingsDAO = new SettingsDAO(getApplicationContext());
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }


    private void initializeLocationManager() {
        Log.d(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}