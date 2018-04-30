package com.heptabargames.a7isenough.services;

import android.content.Context;

import com.heptabargames.a7isenough.daos.EventsDAO;
import com.heptabargames.a7isenough.daos.SettingsDAO;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.OnManifestLoaded;
import com.heptabargames.a7isenough.models.Event;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    private EventsDAO eventsDAO;
    private SettingsDAO settingsDAO;

    private List<OnEventLoaded> onEventLoadeds;

    public EventService(Context context) {
        eventsDAO = new EventsDAO(context);
        settingsDAO = new SettingsDAO(context);
        onEventLoadeds = new ArrayList<>();
    }

    public void loadEvent(Event event) {
        eventsDAO.loadEvent(event, new OnEventLoaded() {
            @Override
            public void onEvent(Event event) {
                try {
                    settingsDAO.loadBeacons(event);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    this.onError(e);
                    return;
                }

                for (OnEventLoaded l : onEventLoadeds) {
                    l.onEvent(event);
                }
            }

            @Override
            public void onError(Exception e) {
                for (OnEventLoaded l : onEventLoadeds) {
                    l.onError(e);
                }
            }
        });
    }

    public void addOnEventLoadedListener(OnEventLoaded listener) {
        onEventLoadeds.add(listener);
    }

    public void getManifest(OnManifestLoaded callback) {
        eventsDAO.getManifest(callback);
    }
}
