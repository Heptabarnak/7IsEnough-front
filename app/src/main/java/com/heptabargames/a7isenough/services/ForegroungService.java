package com.heptabargames.a7isenough.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class ForegroungService extends IntentService{

    public ForegroungService(){
        super("ForegroundService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Thread.sleep(5000); //for the moment

        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }
}
