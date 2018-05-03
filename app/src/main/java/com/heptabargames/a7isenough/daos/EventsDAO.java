package com.heptabargames.a7isenough.daos;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.IOUtils;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.OnEventsLoaded;
import com.heptabargames.a7isenough.listeners.OnManifestLoaded;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Sector;
import com.heptabargames.a7isenough.models.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EventsDAO {

    public static final String TAG = "EventsDAO";

    public static final String SERVER_URL = "https://7isenough.insa.finch4.xyz/";
    public static final String MANIFEST_URL = "manifest.json";

    private static final String EVENT_FILE_PREFIX = "event_";

    private RequestQueue requestQueue;
    private Context context;

    public EventsDAO(Context context) {
        this.context = context;

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(context);
    }

    public void loadEvent(final Event event, final OnEventLoaded callback) {
        if (event.isLoaded()) {
            callback.onEvent(event);
            return;
        }

        try (FileInputStream inputStream = context.openFileInput(
                EVENT_FILE_PREFIX + event.getId() + "_" + event.getVersion() + ".json"
        )) {

            String result = new String(IOUtils.toByteArray(inputStream), "UTF-8");
            JSONObject jsonObject = new JSONObject(result);

            parseEvent(jsonObject, event);
            callback.onEvent(event);
        } catch (JSONException | FileNotFoundException e) {
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return !request.getUrl().equals(SERVER_URL + MANIFEST_URL)
                            && !request.getUrl().equals(SERVER_URL + event.getId() + ".json");
                }
            });
            createEventRequest(event, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllEvent(final List<Event> events, final OnEventsLoaded callback) {
        for (final Event event : events) {
            if (!event.isLoaded()) {
                try (FileInputStream inputStream = context.openFileInput(
                        EVENT_FILE_PREFIX + event.getId() + "_" + event.getVersion() + ".json"
                )) {

                    String result = new String(IOUtils.toByteArray(inputStream), "UTF-8");
                    JSONObject jsonObject = new JSONObject(result);

                    parseEvent(jsonObject, event);
                } catch (JSONException | FileNotFoundException e) {
                    requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                        @Override
                        public boolean apply(Request<?> request) {
                            return !request.getUrl().equals(SERVER_URL + MANIFEST_URL)
                                    && !request.getUrl().equals(SERVER_URL + event.getId() + ".json");
                        }
                    });
                    createEventRequest(event, callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        callback.onEvents(events);
    }


    private void createEventRequest(final Event event, final OnEventLoaded callback) {
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.GET,
                SERVER_URL + event.getId() + ".json",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Event '" + event.getName() + "' loaded");
                        try (FileOutputStream outputStream = context.openFileOutput(
                                EVENT_FILE_PREFIX + event.getId() + "_" + event.getVersion() + ".json",
                                Context.MODE_PRIVATE
                        )) {

                            outputStream.write(response.toString(4).getBytes());

                            parseEvent(response, event);
                            callback.onEvent(event);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            callback.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );

        requestQueue.add(stringRequest);

        // Remove any old version of the event

        File[] files = context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals(EVENT_FILE_PREFIX + event.getId() + "_" + event.getVersion() + ".json")
                        && name.startsWith(EVENT_FILE_PREFIX + event.getId() + "_");
            }
        });

        for (File oldV : files) {
            //noinspection ResultOfMethodCallIgnored
            oldV.delete();
        }
    }

    private void createEventRequest(final Event event, final OnEventsLoaded callback) {
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.GET,
                SERVER_URL + event.getId() + ".json",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Event '" + event.getName() + "' loaded");
                        try (FileOutputStream outputStream = context.openFileOutput(
                                EVENT_FILE_PREFIX + event.getId() + "_" + event.getVersion() + ".json",
                                Context.MODE_PRIVATE
                        )) {

                            outputStream.write(response.toString(4).getBytes());

                            parseEvent(response, event);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            callback.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );

        requestQueue.add(stringRequest);

        // Remove any old version of the event

        File[] files = context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals(EVENT_FILE_PREFIX + event.getId() + "_" + event.getVersion() + ".json")
                        && name.startsWith(EVENT_FILE_PREFIX + event.getId() + "_");
            }
        });

        for (File oldV : files) {
            //noinspection ResultOfMethodCallIgnored
            oldV.delete();
        }
    }

    private void parseEvent(JSONObject response, Event event) throws JSONException {

        JSONObject sectorObj = response.getJSONObject("sector");
        Sector sector = new Sector(
                sectorObj.getInt("size"),
                sectorObj.getInt("nbPerLine"),
                sectorObj.getJSONObject("origin").getDouble("lat"),
                sectorObj.getJSONObject("origin").getDouble("lng")
        );

        JSONArray zoneArray = response.getJSONArray("zones");

        for (int i = 0; i < zoneArray.length(); i++) {
            JSONObject ev = zoneArray.getJSONObject(i);
            event.addZone(Zone.fromJSON(ev, sector));
        }
    }

    public void getManifest(final OnManifestLoaded callback) {
        JsonArrayRequest stringRequest = new JsonArrayRequest(
                Request.Method.GET,
                SERVER_URL + MANIFEST_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Manifest loaded");
                        List<Event> events = new ArrayList<>(response.length());

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject ev = response.getJSONObject(i);

                                events.add(new Event(
                                        ev.getString("id"),
                                        ev.getString("name"),
                                        ev.getString("description"),
                                        ev.isNull("startDate") ? null : new Date(ev.getLong("startDate")),
                                        ev.isNull("endDate") ? null : new Date(ev.getLong("endDate")),
                                        ev.isNull("scoreboardId") ? null : ev.getString("scoreboardId"),
                                        ev.getInt("version")
                                ));
                            }
                        } catch (JSONException e) {
                            callback.onError(e);
                            return;
                        }
                        callback.onManifest(events);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );

        requestQueue.add(stringRequest);
    }
}
