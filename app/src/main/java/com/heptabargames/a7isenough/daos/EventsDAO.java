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
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.OnManifestLoaded;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Sector;
import com.heptabargames.a7isenough.models.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        FileInputStream inputStream;

        try {
            inputStream = context.openFileInput(EVENT_FILE_PREFIX + event.getId());

            JSONObject jsonObject = new JSONObject(new InputStreamReader(inputStream, "UTF-8").);

            parseEvent(jsonObject, event);
            inputStream.close();

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


    private void createEventRequest(final Event event, final OnEventLoaded callback) {
        // TODO Prevent two request
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.GET,
                SERVER_URL + event.getId() + ".json",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Event '" + event.getName() + "' loaded");
                        try {
                            FileOutputStream outputStream
                                    = context.openFileOutput(EVENT_FILE_PREFIX + event.getId(), Context.MODE_PRIVATE);
                            outputStream.write(response.toString(4).getBytes());
                        } catch (JSONException | IOException e1) {
                            e1.printStackTrace();
                        }

                        try {
                            parseEvent(response, event);
                            callback.onEvent(event);
                        } catch (JSONException e) {
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
                                        ev.isNull("startDate") ? null : new Date(ev.getInt("startDate")),
                                        ev.isNull("endDate") ? null : new Date(ev.getInt("endDate"))
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
