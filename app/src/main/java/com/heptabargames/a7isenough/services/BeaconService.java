package com.heptabargames.a7isenough.services;

import android.content.Context;

import com.heptabargames.a7isenough.daos.SettingsDAO;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class BeaconService {

    private SettingsDAO settingsDAO;

    public BeaconService(Context context) {
        this.settingsDAO = new SettingsDAO(context);
    }

    public Beacon checkBeacon(Event event, String token) throws IOException, JSONException {
        // Create instance
        Argon2 argon2 = Argon2Factory.create();

        Beacon found = null;

        for (Zone zone : event.getZones()) {
            for (Beacon beacon : zone.getBeacons()) {
                if (argon2.verify(beacon.getHash(), token)) {
                    found = beacon;
                    break;
                }
            }
            if (found != null) break;
        }

        if (found != null) {
            found.setFound(new Date());
            settingsDAO.saveBeacon(found, event);
            return found;
        }
        return null;
    }
}
