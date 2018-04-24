package com.heptabargames.a7isenough.models;

import java.util.Date;

/**
 * Created by Julien on 24/04/2018.
 */

public class Beacon {
    private int id;
    private String hash;
    private int difficulty;
    private String theme;
    private String name;
    private String description;
    private String monumentDescription;
    private Date found;

    public Beacon(int id, String hash, int difficulty, String theme, String name, String description, String monumentDescription, Date found) {
        this.id = id;
        this.hash = hash;
        this.difficulty = difficulty;
        this.theme = theme;
        this.name = name;
        this.description = description;
        this.monumentDescription = monumentDescription;
        this.found = found;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    public String getMonumentDescription() {
        return monumentDescription;
    }

    public void setMonumentDescription(String monumentDescription) {
        this.monumentDescription = monumentDescription;
    }

    public Date getFound() {
        return found;
    }

    public void setFound(Date found) {
        this.found = found;
    }
}
