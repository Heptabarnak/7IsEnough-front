package com.heptabargames.a7isenough.services;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.ZoneListener;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Rectangle;
import com.heptabargames.a7isenough.models.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalizationManager implements OnEventLoaded {

    private List<ZoneListener> zoneListeners;
    private Zone currentZone;
    private Zone currentAllZone;
    private LatLng lastPos;

    public LocalizationManager() {
        this.zoneListeners = new ArrayList<ZoneListener>();
        this.currentAllZone = null;
        this.currentZone = null;
        lastPos = null;
    }

    @Override
    public void onEvent(Event event) {
        if(lastPos != null){
            checkZone(lastPos, event.getZones(), event);
        }
    }

    @Override
    public void onError(Exception e) {
        Log.e("LocalizationManager", e.getMessage());
    }

    public void checkZone(LatLng point, List<Zone> zones, Event current) {
        lastPos = point;
        List<Zone> newZones = new ArrayList<Zone>();
        for (Zone z : zones) {
            if (isPointInRectangle(point, z.getPolygons())) {
                Log.d("LocalizationManager", "Zone found");
                newZones.add(z);
            }
        }

        if(!newZones.isEmpty()){
            if(currentAllZone != newZones.get(0)){
                zoneAllChanged(newZones.get(0));
            }
            if(current != null){
                for (Zone newZone : newZones) {
                    if(currentZone != newZone && current.getZones().contains(newZone)){
                        zoneChanged(newZone);
                        break;
                    }
                }
            }
        }else{
            if(currentZone != null){
                zoneChanged(null);
            }
            if(currentAllZone != null){
                zoneAllChanged(null);
            }
        }
    }

    private boolean isPointInRectangle(LatLng tap, List<Rectangle> rectangles) {
        for (Rectangle rectangle : rectangles) {
            if (PolyUtil.containsLocation(tap,rectangle.getAllPoints(), false)) {
                return true;
            }
        }
        return false;
    }

    private void zoneChanged(Zone zone){
        currentZone = zone;
        for (ZoneListener zoneListener : zoneListeners) {
            zoneListener.onZoneChanged(currentZone);
        }
    }

    private void zoneAllChanged(Zone zone){
        currentAllZone = zone;

        for (ZoneListener zoneListener : zoneListeners) {
            zoneListener.onAllZoneChanged(currentAllZone);
        }
    }

    public void addZoneListener(ZoneListener zoneListener){
        zoneListeners.add(zoneListener);
        zoneListener.onZoneChanged(currentZone);
        zoneListener.onAllZoneChanged(currentAllZone);
    }

    public void removeZoneListener(ZoneListener zoneListener){
        zoneListeners.remove(zoneListener);
    }
}
