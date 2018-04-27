package com.heptabargames.a7isenough.listeners;

import com.heptabargames.a7isenough.models.Event;

import java.util.List;

public interface OnManifestLoaded {

    void onManifest(List<Event> events);

    void onError(Exception e);
}
