package com.heptabargames.a7isenough.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

public class Rectangle {

    private LatLng northWest;
    private LatLng northEast;
    private LatLng southEast;
    private LatLng southWest;

    public Rectangle(LatLng northWest, LatLng northEast, LatLng southEast, LatLng southWest) {
        this.northWest = northWest;
        this.northEast = northEast;
        this.southEast = southEast;
        this.southWest = southWest;
    }


    public LatLng getNorthWest() {
        return northWest;
    }

    public LatLng getNorthEast() {
        return northEast;
    }

    public LatLng getSouthEast() {
        return southEast;
    }

    public LatLng getSouthWest() {
        return southWest;
    }

    public List<LatLng> getAllPoints() {
        return Arrays.asList(northWest, northEast, southEast, southWest);
    }
}
