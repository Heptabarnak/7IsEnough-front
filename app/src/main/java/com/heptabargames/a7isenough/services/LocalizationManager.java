package com.heptabargames.a7isenough.services;

import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.heptabargames.a7isenough.MainActivity;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.ZoneListener;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Rectangle;
import com.heptabargames.a7isenough.models.Zone;

import java.util.List;

public class LocalizationManager{

    private List<ZoneListener> zoneListeners;
    private List<Zone> currentZoneCheckedByGPS;
    private List<Zone> currentZoneCheckedByNet;

    public LocalizationManager() {
        this.zoneListeners = new ArrayList<ZoneListener>();
        this.currentZoneCheckedByGPS = new ArrayList<Zone>();
        this.currentZoneCheckedByNet = new ArrayList<Zone>();
    }

    public void checkZone(LatLng point, List<Zone> zones, String provider) {
        List<Zone> newZones = new ArrayList<Zone>();
        for (Zone z : zones) {
            if (isPointInRectangle(point, z.getPolygons())) {
                Log.d("LocalizationManager", "Zone found, zone " + z.getName());
                newZones.add(z);
            }
        }

        zonesChecked(newZones, provider);
    }

    private boolean isPointInRectangle(LatLng tap, List<Rectangle> rectangles) {
        for (Rectangle rectangle : rectangles) {
            if (PolyUtil.containsLocation(tap,rectangle.getAllPoints(), false)) {
                return true;
            }
        }
        return false;
    }

    private void zonesChecked(List<Zone> zones, String provider){
        switch(provider){
            case LocationManager.GPS_PROVIDER :
                currentZoneCheckedByGPS = zones;
                for (ZoneListener zoneListener : zoneListeners) {
                    zoneListener.onZonesCheckedByGPS(zones);
                }
            case LocationManager.NETWORK_PROVIDER :
                currentZoneCheckedByNet = zones;
                for (ZoneListener zoneListener : zoneListeners) {
                    zoneListener.onZonesCheckedByNetwork(zones);
                }
        }
    }


    public void addZoneListener(ZoneListener zoneListener){
        zoneListeners.add(zoneListener);
        zoneListener.onZonesCheckedByGPS(currentZoneCheckedByGPS);
        zoneListener.onZonesCheckedByNetwork(currentZoneCheckedByNet);
    }

    public void removeZoneListener(ZoneListener zoneListener){
        zoneListeners.remove(zoneListener);
    }
}
