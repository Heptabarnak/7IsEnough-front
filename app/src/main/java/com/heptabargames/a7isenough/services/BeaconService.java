package com.heptabargames.a7isenough.services;

import android.content.Context;

import com.heptabargames.a7isenough.daos.SettingsDAO;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;

import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class BeaconService {

    private SettingsDAO settingsDAO;

    public BeaconService(Context context) {
        this.settingsDAO = new SettingsDAO(context);
    }

    public Beacon checkBeacon(Event event, String token) throws IOException, JSONException {
        Beacon found = null;

        for (Zone zone : event.getZones()) {
            for (Beacon beacon : zone.getBeacons()) {
                if (beacon.getHash().equals(getSHA512(token))) {
                    found = beacon;
                    break;
                }
            }
            if (found != null) break;
        }

        if (found != null) {
            found.setFound(new Date());
            settingsDAO.saveBeacon(found, event);
        }

        return found;
    }

    private String getSHA512(String token) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
