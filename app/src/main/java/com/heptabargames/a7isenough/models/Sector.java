package com.heptabargames.a7isenough.models;

import java.util.ArrayList;

/**
 * Created by Julien on 24/04/2018.
 */

public class Sector {
    private int id;
    private int size;
    private ArrayList<Point> points;

    public Sector(int id, int size) {
        this.id = id;
        this.size = size;
        this.points = new ArrayList<Point>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }
}
