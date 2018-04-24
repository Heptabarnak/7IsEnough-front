package com.heptabargames.a7isenough.models;

import java.util.ArrayList;

/**
 * Created by Julien on 24/04/2018.
 */

public class Zone {

    private int id;
    private String name;
    private String description;
    private ArrayList<Beacon> beacons;
    private ArrayList<Sector> sectors;

    public Zone(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.beacons = new ArrayList<Beacon>();
        this.sectors = new ArrayList<Sector>();
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

    public ArrayList<Beacon> getBeacons() {
        return beacons;
    }

    public void addBeacon(Beacon beacon) {
        this.beacons.add(beacon);
    }

    public ArrayList<Sector> getSectors() {
        return sectors;
    }

    public void addSector(Sector sector) {
        this.sectors.add(sector);
    }
}
