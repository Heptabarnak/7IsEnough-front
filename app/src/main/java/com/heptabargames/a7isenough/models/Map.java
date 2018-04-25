package com.heptabargames.a7isenough.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Julien on 25/04/2018.
 */

public class Map {

    private int id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ArrayList<Zone> zones;
    private ArrayList<Sector> sectors;

    public Map(int id, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.zones = new ArrayList<Zone>();
        this.sectors = new ArrayList<Sector>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public ArrayList<Sector> getSectors() {
        return sectors;
    }

    public void addSectors(Sector sector) {
        this.sectors.add(sector);
    }
}
