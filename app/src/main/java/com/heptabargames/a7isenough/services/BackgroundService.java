package com.heptabargames.a7isenough.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.heptabargames.a7isenough.R;
import com.heptabargames.a7isenough.daos.EventsDAO;
import com.heptabargames.a7isenough.listeners.OnManifestLoaded;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service
{
    private static final String TAG = "BackgroundService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BackgroundService";

    private class LocationListener implements android.location.LocationListener, OnManifestLoaded {
        Location mLastLocation;
        List<Event> events;
        Zone currentZone;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            EventService eventService = new EventService(getApplicationContext());
            eventService.getManifest(this);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        @Override
        public void onManifest(List<Event> events) {
            this.events = events;
            List<Zone> zones = new ArrayList<Zone>();
            for(Event event : events ){
                zones.addAll(event.getZones());
            }
            LocalizationManager localizationManager = new LocalizationManager();
            currentZone = localizationManager.isInZone(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), zones);
            if(currentZone != null){
                List<Beacon> beacons = currentZone.getBeacons();
                boolean allBeaconsFound = true;
                for(Beacon beacon : beacons){
                    allBeaconsFound = beacon.getFound() != null;
                    if(!allBeaconsFound) break;
                }
                if(!allBeaconsFound){
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Vous êtes entré dans une zone !")
                            .setContentText("Essayez de trouver toutes les balises qui y sont cachées !")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
            }
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "onError: " + e);
        }

        public Location getmLastLocation() {
            return mLastLocation;
        }

        public List<Event> getEvents() {
            return events;
        }

        public Zone getCurrentZone() {
            return currentZone;
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
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
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
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

    public Zone getCurrentZone(){
        if( this.mLocationListeners[0].getCurrentZone()!= null){
            return this.mLocationListeners[0].getCurrentZone();
        }
        return this.mLocationListeners[1].getCurrentZone();
    }


}