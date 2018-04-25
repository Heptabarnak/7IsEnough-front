package com.heptabargames.a7isenough.models;

import java.time.LocalDateTime;

/**
 * Created by Julien on 25/04/2018.
 */

public class Event {

    private int id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map map;

    public Event(int id, LocalDateTime startDate, LocalDateTime endDate, Map map) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.map = map;
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
}
