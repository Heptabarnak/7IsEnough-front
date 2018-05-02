package com.heptabargames.a7isenough.services;

import android.content.Context;

import com.heptabargames.a7isenough.daos.EventsDAO;
import com.heptabargames.a7isenough.daos.SettingsDAO;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.OnEventsLoaded;
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

    private List<OnEventsLoaded> onEventsLoadeds;

    private Event lastEvent;

    public EventService(Context context) {
        eventsDAO = new EventsDAO(context);
        settingsDAO = new SettingsDAO(context);
        onEventLoadeds = new ArrayList<>();
        onEventsLoadeds = new ArrayList<>();
        lastEvent = null;
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
                lastEvent = event;
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

    public void loadAllEvent(List<Event> events){
        eventsDAO.loadAllEvent(events, new OnEventsLoaded() {
            @Override
            public void onEvents(List<Event> events) {

                for (OnEventsLoaded l : onEventsLoadeds) {
                    l.onEvents(events);
                }
            }

            @Override
            public void onError(Exception e) {
                for (OnEventsLoaded l : onEventsLoadeds) {
                    l.onError(e);
                }
            }
        });
    }

    public void addOnEventLoadedListener(OnEventLoaded listener) {
        onEventLoadeds.add(listener);
        if(lastEvent != null){
            listener.onEvent(lastEvent);
        }

    }

    public void addOnEventsLoadedListener(OnEventsLoaded listener) {
        onEventsLoadeds.add(listener);
    }

    public void removeOnEventsLoadedListener(OnEventsLoaded listener){
        onEventsLoadeds.remove(listener);
    }

    public void removeOnEventLoadedListener(OnEventLoaded listener){
        onEventLoadeds.remove(listener);
    }

    public void getManifest(OnManifestLoaded callback) {
        eventsDAO.getManifest(callback);
    }
}
