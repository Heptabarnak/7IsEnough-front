package com.heptabargames.a7isenough.services;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static com.google.android.gms.games.leaderboard.LeaderboardVariant.COLLECTION_PUBLIC;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_ALL_TIME;


public class BeaconService {

    private SettingsDAO settingsDAO;

    private static final int SCORE_COEF = 4;

    private Context context;


    public BeaconService(Context context) {
        this.settingsDAO = new SettingsDAO(context);
        this.context = context;
    }

    public Beacon checkBeacon(final Event event, String token) throws IOException, JSONException {
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

        if (found != null && found.getFound() == null) {
            found.setFound(new Date());
            settingsDAO.saveBeacon(found, event);

            if (event.getScoreboardId() != null) {
                final Beacon finalFound = found;
                Games.getLeaderboardsClient(context, GoogleSignIn.getLastSignedInAccount(context))
                        .loadCurrentPlayerLeaderboardScore(event.getScoreboardId(), TIME_SPAN_ALL_TIME, COLLECTION_PUBLIC)
                        .addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardScore>>() {
                            @Override
                            public void onSuccess(AnnotatedData<LeaderboardScore> data) {
                                LeaderboardScore dataR = data.get();

                                long score = dataR == null ? 0 : dataR.getRawScore();

                                score += finalFound.getDifficulty() * SCORE_COEF;

                                Games.getLeaderboardsClient(context, GoogleSignIn.getLastSignedInAccount(context))
                                        .submitScore(event.getScoreboardId(), score);
                            }
                        });
            }

        } else if (found != null && found.getFound() != null) {
            //TODO : Display a message to inform the user that he has already found this beacon
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
