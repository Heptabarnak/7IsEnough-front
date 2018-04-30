package com.heptabargames.a7isenough.services;

import com.google.android.gms.maps.model.LatLng;
import com.heptabargames.a7isenough.models.Rectangle;
import com.heptabargames.a7isenough.models.Zone;

import java.util.List;

public class LocalizationManager {


    public Zone isInZone(LatLng point, List<Zone> zones) {
        for (Zone z : zones) {
            if (isPointInRectangle(point, z.getPolygons())) {
                return z;
            }
        }
        return null;
    }

    private boolean isPointInRectangle(LatLng tap, List<Rectangle> rectangles) {
        for (Rectangle rectangle : rectangles) {
            if (tap.latitude >= rectangle.getNorthWest().latitude
                    && tap.latitude <= rectangle.getNorthEast().latitude
                    && tap.longitude <= rectangle.getNorthEast().longitude
                    && tap.longitude >= rectangle.getSouthEast().longitude) {
                return true;
            }
        }
        return false;
    }
}
