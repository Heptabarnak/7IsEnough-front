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
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.OnEventsLoaded;
import com.heptabargames.a7isenough.listeners.OnManifestLoaded;
import com.heptabargames.a7isenough.listeners.ZoneListener;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;
import com.heptabargames.a7isenough.MainActivity;

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
    private Zone currentZone = null;

    private ZoneListener zoneListener = new ZoneListener() {

        @Override
        public void onZonesCheckedByGPS(List<Zone> zones) {
            Log.d(TAG, "onZoneCheckedByGPS(), Size = "+zones.size());
            if(zones.isEmpty()) return;
            Zone zone = zones.get(0);
            sendNotification(zone);
        }

        @Override
        public void onZonesCheckedByNetwork(List<Zone> zones) {
            Log.d(TAG, "onZoneCheckedByNetwork(), Size = "+zones.size());
            if(zones.isEmpty()) return;
            Zone zone = zones.get(0);
            sendNotification(zone);
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "An error occured : " + e.getMessage());
        }
    };

    private void sendNotification(Zone zone){
        if(currentZone == zone) return;
        currentZone = zone;
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

    public class BackgroundBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    private class LocationListener implements android.location.LocationListener, OnManifestLoaded, OnEventsLoaded, OnEventLoaded {

        Location mLastLocation;
        String provider;
        private List<Event> events;

        public LocationListener(String provider) {
            Log.d(TAG, "LocationListener " + provider);
            this.provider = provider;
            mLastLocation = new Location(provider);
            MainActivity.applicationEventService.addOnEventsLoadedListener(this);
            MainActivity.applicationEventService.addOnEventLoadedListener(this);
        }

        @Override
        public void onEvent(Event event) {
            Log.e(TAG, "onEvent " + event.getName() + " with " + provider);
            MainActivity.applicationLocalizationManager.checkZone(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), event.getZones(), provider);
        }

        @Override
        public void onEvents(List<Event> listEvents) {
            events = listEvents;
            List<Zone> zones = new ArrayList<>();
            for (Event event : events) {
                zones.addAll(event.getZones());
            }
            MainActivity.applicationLocalizationManager.checkZone(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), zones, provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            MainActivity.applicationEventService.getManifest(this);
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
            MainActivity.applicationEventService.loadAllEvent(listEvents);
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "onError: " + e);
        }
    }

    private LocationListener[] mLocationListeners;

    @Override
    public IBinder onBind(Intent arg0) {
        return new BackgroundBinder();
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
        MainActivity.applicationLocalizationManager.addZoneListener(zoneListener);
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


    private void initializeLocationManager() {
        Log.d(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}