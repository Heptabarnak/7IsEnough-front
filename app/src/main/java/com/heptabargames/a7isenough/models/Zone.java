package com.heptabargames.a7isenough.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class Zone {

    private int id;
    private String name;
    private String description;
    private List<Beacon> beacons;
    private List<LatLng> polygon;

    public Zone(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.beacons = new ArrayList<>();
        this.polygon = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void addBeacon(Beacon beacon) {
        this.beacons.add(beacon);
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }

    public List<LatLng> getPolygon() {
        return polygon;
    }

    public void setPolygon(List<LatLng> polygon) {
        this.polygon = polygon;
    }
}
