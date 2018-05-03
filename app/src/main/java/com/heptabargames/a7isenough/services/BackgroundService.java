package com.heptabargames.a7isenough.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
    private static final int LOCATION_INTERVAL = 60 * 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BackgroundService";
    private static BackgroundService backgroundService = null;
    private LocalizationManager localizationManager;
    private EventService eventService;
    private SettingsDAO settingsDAO;
    private Zone currentZone = null;


    private ZoneListener zoneListener = new ZoneListener() {

        @Override
        public void onZonesCheckedByGPS(List<Zone> zones) {
            Log.d(TAG, "onZoneCheckedByGPS(), Size = " + zones.size());
            if (zones.isEmpty()) return;
            Zone zone = zones.get(0);
            sendNotification(zone);
        }

        @Override
        public void onZonesCheckedByNetwork(List<Zone> zones) {
            Log.d(TAG, "onZoneCheckedByNetwork(), Size = " + zones.size());
            if (zones.isEmpty()) return;
            Zone zone = zones.get(0);
            sendNotification(zone);
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "An error occured : " + e.getMessage());
        }
    };

    private void sendNotification(Zone zone) {
        if (currentZone == zone) return;
        currentZone = zone;
        if (!zone.getNotFoundBeacons().isEmpty() && Boolean.parseBoolean(settingsDAO.getParameter("isChecked"))) {
            Log.d(TAG, "Trying to send notification");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.logo_complete)
                    .setContentTitle(getString(R.string.notification_in_zone_title, zone.getName()))
                    .setContentText(getString(R.string.notification_in_zone_desc))
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private class LocationListener implements android.location.LocationListener, OnManifestLoaded, OnEventsLoaded {

        Location mLastLocation;
        String provider;
        private List<Event> events;

        public LocationListener(String provider) {
            Log.d(TAG, "LocationListener " + provider);
            this.provider = provider;
            mLastLocation = new Location(provider);
            eventService.addOnEventsLoadedListener(this);
        }

        public void onEventChange(Event event) {
            Log.d(TAG, "onEvent " + event.getName() + " with " + provider);
            localizationManager.checkZone(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), event.getZones(), provider);
        }

        @Override
        public void onEvents(List<Event> listEvents) {
            events = listEvents;
            List<Zone> zones = new ArrayList<>();
            for (Event event : events) {
                zones.addAll(event.getZones());
            }
            localizationManager.checkZone(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), zones, provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
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
            eventService.loadAllEvent(listEvents);
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "onError: " + e);
        }
    }

    private LocationListener[] mLocationListeners;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        backgroundService = this;

        localizationManager = new LocalizationManager();
        localizationManager.addZoneListener(zoneListener);

        eventService = new EventService(getApplicationContext());

        settingsDAO = new SettingsDAO(getApplicationContext());

        mLocationListeners = new LocationListener[]{
                new LocationListener(LocationManager.GPS_PROVIDER),
                new LocationListener(LocationManager.NETWORK_PROVIDER)
        };
        initializeLocationManager();

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void initializeLocationManager() {
        Log.d(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public static BackgroundService getBackgroundService() {
        return backgroundService;
    }

    public void addZoneListener(ZoneListener zoneListener) {
        localizationManager.addZoneListener(zoneListener);
    }

    public void removeZoneListener(ZoneListener zoneListener) {
        localizationManager.removeZoneListener(zoneListener);
    }

    public void setCurrentEvent(final Event currentEvent) {
        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mLocationListeners[0].onEventChange(currentEvent);
                        mLocationListeners[1].onEventChange(currentEvent);
                    }
                },
                300);

    }
}