package com.heptabargames.a7isenough.listeners;

import com.heptabargames.a7isenough.models.Zone;

import java.util.List;

public interface ZoneListener {
    void onZonesCheckedByGPS(List<Zone> zones);

    void onZonesCheckedByNetwork(List<Zone> zones);

    void onError(Exception e);
}
