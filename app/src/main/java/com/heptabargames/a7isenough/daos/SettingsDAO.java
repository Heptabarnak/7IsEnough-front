package com.heptabargames.a7isenough.daos;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.heptabargames.a7isenough.models.Beacon;

import java.time.LocalDateTime;

public class SettingsDAO {

    public static final String TAG = "SettingsDAO";

    private RequestQueue requestQueue;

    public SettingsDAO(Context context){
        requestQueue = Volley.newRequestQueue(context);
    }

    public void saveBeacon(Beacon beacon) {

    }

    public LocalDateTime searchBeacon(Beacon beacon) {

        return null;
    }

    public void clear() {

    }
}
