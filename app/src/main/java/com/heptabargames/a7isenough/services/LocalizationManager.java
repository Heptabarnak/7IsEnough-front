package com.heptabargames.a7isenough.services;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.heptabargames.a7isenough.models.Rectangle;
import com.heptabargames.a7isenough.models.Zone;

import java.util.List;

public class LocalizationManager {


    public Zone isInZone(LatLng point, List<Zone> zones) {
        for (Zone z : zones) {
            if (isPointInRectangle(point, z.getPolygons())) {
                Log.d("LocalisationManager", "Zone found");
                return z;
            }
        }
        Log.d("LocalisationManager", "Zone not found");
        return null;
    }

    private boolean isPointInRectangle(LatLng tap, List<Rectangle> rectangles) {
        for (Rectangle rectangle : rectangles) {
            if (PolyUtil.containsLocation(tap,rectangle.getAllPoints(), false)) {
                return true;
            }
        }
        return false;
    }
}
