package com.heptabargames.a7isenough.services;

import android.content.Context;

import com.heptabargames.a7isenough.daos.EventsDAO;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.OnManifestLoaded;
import com.heptabargames.a7isenough.models.Event;

public class EventService {

    private EventsDAO eventsDAO;


    public EventService(Context context) {
        eventsDAO = new EventsDAO(context);
    }

    public void loadEvent(Event event, OnEventLoaded callback) {
        eventsDAO.loadEvent(event, callback);

        // TODO Load scores
    }

    public void getManifest(OnManifestLoaded callback) {
        eventsDAO.getManifest(callback);
    }
}
