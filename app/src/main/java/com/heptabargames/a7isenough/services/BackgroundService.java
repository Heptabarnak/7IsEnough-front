package com.heptabargames.a7isenough.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
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
import com.heptabargames.a7isenough.listeners.ZoneListener;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BackgroundService";
    private SettingsDAO settingsDAO;
    private LocalizationManager localizationManager;
    private EventService eventService;
    private Event currentEvent;

    private ZoneListener zoneListener = new ZoneListener() {
        @Override
        public void onZoneChanged(Zone zone) {

        }

        @Override
        public void onAllZoneChanged(Zone zone) {
            Log.d(TAG, "onAllZoneChanged()");

            if (zone == null) return;

            if (!zone.getNotFoundBeacons().isEmpty() && Boolean.parseBoolean(settingsDAO.getParameter("isChecked"))) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.logo_complete)
                        .setContentTitle(getString(R.string.notification_in_zone_title, zone.getName()))
                        .setContentText(getString(R.string.notification_in_zone_desc))
                        .setPriority(NotificationCompat.PRIORITY_LOW);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "An error occured : " + e);
        }
    };

    public class BackgroundBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    private class LocationListener implements android.location.LocationListener, OnManifestLoaded, OnEventsLoaded {

        Location mLastLocation;
        private List<Event> events;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            eventService.addOnEventsLoadedListener(this);
        }

        @Override
        public void onEvents(List<Event> listEvents) {
            events = listEvents;
            List<Zone> zones = new ArrayList<>();
            for (Event event : events) {
                zones.addAll(event.getZones());
            }
            localizationManager.checkZone(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), zones, currentEvent);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            eventService.getManifest(this);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        @Override
        public void onManifest(List<Event> listEvents) {
            Log.e(TAG, "onManifest");
            this.events = listEvents;
            eventService.addOnEventsLoadedListener(this);
            eventService.loadAllEvent(listEvents);
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "onError: " + e);
        }
    }

    private LocationListener[] mLocationListeners;

    @Override
    public IBinder onBind(Intent arg0) {
        return new BackgroundBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        localizationManager = new LocalizationManager();
        localizationManager.addZoneListener(zoneListener);
        currentEvent = null;
        eventService = new EventService(getBaseContext());
        mLocationListeners = new LocationListener[]{
                new LocationListener(LocationManager.GPS_PROVIDER),
                new LocationListener(LocationManager.NETWORK_PROVIDER)
        };
        initializeLocationManager();
        settingsDAO = new SettingsDAO(getApplicationContext());
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public LocalizationManager getLocalizationManager() {
        return localizationManager;
    }
}