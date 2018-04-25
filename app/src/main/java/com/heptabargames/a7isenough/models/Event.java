package com.heptabargames.a7isenough.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Julien on 25/04/2018.
 */

public class Event {

    private int id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ArrayList<Zone> zones;
    private ArrayList<Sector> sectors;

    public Event(int id, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public boolean isPermanent() {
        return this.endDate == null;
    }
}
