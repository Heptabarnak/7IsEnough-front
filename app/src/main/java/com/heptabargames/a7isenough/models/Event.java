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
    private String scoreboardId;
    private boolean loaded;

    private int version;

    public Event(String id, String name, String description, Date startDate, Date endDate, String scoreboardId, int version) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.scoreboardId = scoreboardId;
        this.version = version;
        this.zones = new ArrayList<>();
        this.loaded = false;
    }

    public String getScoreboardId() {
        return scoreboardId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Event) {
            Event b = (Event) obj;
            return b.getId().equals(id);
        }
        return false;
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
        this.loaded = true;
        this.zones.add(zone);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isPermanent() {
        return this.id.equals("permanent");
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public int getVersion() {
        return version;
    }
}
