package com.heptabargames.a7isenough.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Zone {

    private static final double EARTH_RADIUS = 6378_000;

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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Zone){
            Zone b = (Zone) obj;
            if(b.getName().equals(name) && b.getDescription().equals(description)){
                return true;
            }
        }
        return false;
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

    public List<Beacon> getFoundBeacons(){
        List<Beacon> foundBeacon = new ArrayList<>();
        for (Beacon beacon : beacons) {
            if(beacon.getFound() != null){
                foundBeacon.add(beacon);
            }
        }
        return foundBeacon;
    }
    public List<Beacon> getNotFoundBeacons(){
        List<Beacon> notFoundBeacon = beacons;
        notFoundBeacon.removeAll(getFoundBeacons());
        return notFoundBeacon;
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
            double latDistWest = (sectorId / sector.getNbPerLine()) * dist;
            double latDistEast = latDistWest + dist;

            double lngDistNorth = (sectorId % sector.getNbPerLine()) * dist / Math.cos(sector.getLatOrigin() * Math.PI / 180);
            double lngDistSouth = lngDistNorth + (dist / Math.cos(sector.getLatOrigin() * Math.PI / 180));

            zone.addPolygon(new Rectangle(
                    new LatLng(sector.getLatOrigin() - latDistWest, sector.getLngOrigin() + lngDistNorth),
                    new LatLng(sector.getLatOrigin() - latDistEast, sector.getLngOrigin() + lngDistNorth),
                    new LatLng(sector.getLatOrigin() - latDistEast, sector.getLngOrigin() + lngDistSouth),
                    new LatLng(sector.getLatOrigin() - latDistWest, sector.getLngOrigin() + lngDistSouth)
            ));
        }
    }

}
