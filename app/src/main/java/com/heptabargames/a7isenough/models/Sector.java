package com.heptabargames.a7isenough.models;

import java.util.ArrayList;

/**
 * Created by Julien on 24/04/2018.
 */

public class Sector {
    private int size;
    private int nbPerLin;
    private ArrayList<Point> points;

    public Sector(int size, int nbPerLin) {
        this.size = size;
        this.nbPerLin = nbPerLin;
        this.points = new ArrayList<Point>();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNbPerLin() {
        return nbPerLin;
    }

    public void setNbPerLin(int nbPerLin) {
        this.nbPerLin = nbPerLin;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }
}
