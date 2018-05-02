package com.heptabargames.a7isenough.daos;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.util.IOUtils;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class SettingsDAO {

    public static final String TAG = "SettingsDAO";

    private static final String GAME_FILE = "game_file.json";
    private Context context;
    private boolean isChecked;

    public SettingsDAO(Context context) {
        this.context = context;
        this.isChecked = Boolean.parseBoolean(getParameter("isChecked"));
    }

    public void saveBeacon(Beacon beacon, Event event) throws IOException, JSONException {
        JSONObject save = getJSON();

        if (searchBeacon(beacon, event) == null) {
            JSONArray beacons = save.optJSONArray(event.getId());

            if (beacons == null) {
                beacons = new JSONArray();
                save.put(event.getId(), beacons);
            }
            JSONObject object = new JSONObject();

            object.put("id", beacon.getId());
            object.put("date", beacon.getFound().getTime());

            beacons.put(object);

            saveJSON(save);
        }
    }

    private JSONObject getJSON() throws JSONException, IOException {
        try (FileInputStream inputStream = context.openFileInput(GAME_FILE)) {

            String result = new String(IOUtils.toByteArray(inputStream), "UTF-8");
            return new JSONObject(result);
        } catch (FileNotFoundException e) {
            return new JSONObject();
        }
    }

    private void saveJSON(JSONObject object) throws IOException, JSONException {
        try (FileOutputStream outputStream = context.openFileOutput(GAME_FILE, Context.MODE_PRIVATE)) {

            outputStream.write(object.toString(4).getBytes());
        }
    }

    public Date searchBeacon(Beacon beacon, Event event) throws IOException, JSONException {
        JSONObject save = getJSON();

        JSONArray beacons = save.optJSONArray(event.getId());

        if (beacons != null) {
            for (int i = 0; i < beacons.length(); i++) {
                if (beacons.getJSONObject(i).getInt("id") == beacon.getId()) {
                    return new Date(beacons.getJSONObject(i).getLong("date"));
                }
            }
        }

        return null;
    }

    public void loadBeacons(Event event) throws IOException, JSONException {
        JSONObject save = getJSON();
        JSONArray beacons = save.optJSONArray(event.getId());

        if (beacons != null) {
            for (int i = 0; i < beacons.length(); i++) {
                int id = beacons.getJSONObject(i).getInt("id");
                long date = beacons.getJSONObject(i).getLong("date");

                // No quicker way to do it for now
                for (Zone zone : event.getZones()) {
                    for (Beacon beacon : zone.getBeacons()) {
                        if (beacon.getId() == id) {
                            beacon.setFound(new Date(date));
                        }
                    }
                }
            }
        }
    }

    public String getParameter(String key){
        SharedPreferences sharedPref = context.getSharedPreferences("SharedPreferencesFile", Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public void saveParameter(String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences("SharedPreferencesFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void syncIsChecked() {
        isChecked = Boolean.parseBoolean(getParameter("isChecked"));
    }

    public void clear() {
        context.deleteFile(GAME_FILE);
    }
}
