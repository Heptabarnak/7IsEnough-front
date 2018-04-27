package com.heptabargames.a7isenough.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Julien on 25/04/2018.
 */

public class Event {

    private String id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<Zone> zones;

    public Event(String id, String name, String description, Date startDate, Date endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.zones = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public List<Zone> getZones() {
        return zones;
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isPermanent() {
        return this.endDate == null;
    }
}
