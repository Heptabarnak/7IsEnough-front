package com.heptabargames.a7isenough.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Zone {

    private static final int EARTH_RADIUS = 6378;

    private String name;
    private String description;
    private List<Beacon> beacons;
    private List<Rectangle> polygons;

    public Zone(String name, String description) {
        this.name = name;
        this.description = description;
        this.beacons = new ArrayList<>();
        this.polygons = new ArrayList<>();
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

    public List<Rectangle> getPolygons() {
        return polygons;
    }

    public void addPolygon(Rectangle rectangle) {
        this.polygons.add(rectangle);
    }

    public static Zone fromJSON(JSONObject zone, Sector sector) throws JSONException {
        Zone newZone = new Zone(
                zone.getString("name"),
                zone.getString("description")
        );

        JSONArray beacons = zone.getJSONArray("beacons");

        for (int i = 0; i < beacons.length(); i++) {
            newZone.addBeacon(Beacon.fromJSON(beacons.getJSONObject(i)));
        }

        loadSectors(newZone, zone.getJSONArray("sectors"), sector);

        return newZone;
    }

    private static void loadSectors(Zone zone, JSONArray sectors, Sector sector) throws JSONException {
        double dist = (sector.getSize() / EARTH_RADIUS) * (180 / Math.PI);

        for (int i = 0; i < sectors.length(); i++) {
            int sectorId = sectors.getInt(i);

            // Distance in meters from origin
            double latDistWest = (sectorId % sector.getNbPerLine()) * sector.getSize() * dist;
            double latDistEast = (sectorId % sector.getNbPerLine()) * (sector.getSize() + 1) * dist;

            double lngDistNorth = (sectorId / sector.getNbPerLine()) * sector.getSize() / Math.cos(sector.getLatOrigin() * Math.PI / 180);
            double lngDistSouth = sectorId / sector.getNbPerLine() * (sector.getSize() + 1) / Math.cos(sector.getLatOrigin() * Math.PI / 180);

            zone.addPolygon(new Rectangle(
                    new LatLng(latDistWest + sector.getLatOrigin(), lngDistNorth + sector.getLngOrigin()),
                    new LatLng(latDistEast + sector.getLatOrigin(), lngDistNorth + sector.getLngOrigin()),
                    new LatLng(latDistEast + sector.getLatOrigin(), lngDistSouth + sector.getLngOrigin()),
                    new LatLng(latDistWest + sector.getLatOrigin(), lngDistSouth + sector.getLngOrigin())
            ));
        }
    }

}
