package com.heptabargames.a7isenough.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Zone {

    private String name;
    private String description;
    private List<Beacon> beacons;
    private List<LatLng> polygon;

    public Zone(String name, String description) {
        this.name = name;
        this.description = description;
        this.beacons = new ArrayList<>();
        this.polygon = new ArrayList<>();
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

    public static Zone fromJSON(JSONObject zone) throws JSONException {
        Zone newZone = new Zone(
                zone.getString("name"),
                zone.getString("description")
        );

        JSONArray beacons = zone.getJSONArray("beacons");

        for (int i = 0; i < beacons.length(); i++) {
            newZone.addBeacon(Beacon.fromJSON(beacons.getJSONObject(i)));
        }

        loadSectors(newZone, zone.getJSONArray("sectors"));

        return newZone;
    }

    private static void loadSectors(Zone zone, JSONArray sectors) {

    }

}
