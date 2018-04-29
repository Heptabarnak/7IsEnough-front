package com.heptabargames.a7isenough.models;

public class Sector {
    private int size;
    private int nbPerLine;
    private double latOrigin;
    private double lngOrigin;

    public Sector(int size, int nbPerLine, double latOrigin, double lngOrigin) {
        this.size = size;
        this.nbPerLine = nbPerLine;
        this.latOrigin = latOrigin;
        this.lngOrigin = lngOrigin;
    }

    public int getSize() {
        return size;
    }

    public int getNbPerLine() {
        return nbPerLine;
    }

    public double getLatOrigin() {
        return latOrigin;
    }

    public double getLngOrigin() {
        return lngOrigin;
    }
}
