package com.heptabargames.a7isenough.listeners;

import com.heptabargames.a7isenough.models.Zone;

public interface ZoneListener {
    void onZoneChanged(Zone zone);

    void onAllZoneChanged(Zone zone);

    void onError(Exception e);
}
