package com.heptabargames.a7isenough.listeners;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.heptabargames.a7isenough.models.Zone;
import com.heptabargames.a7isenough.services.LocalizationService;

import java.util.List;

/**
 * Created by Julien on 27/04/2018.
 */

public class LocationListener implements android.location.LocationListener{
    @Override
    public void onLocationChanged(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}