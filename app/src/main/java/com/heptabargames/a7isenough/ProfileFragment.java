package com.heptabargames.a7isenough;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.models.Event;

public class ProfileFragment extends Fragment {

    private static final int RC_LEADERBOARD_UI = 1523;

    private View mView;
    private Event currentEvent;
    private OnEventLoaded onEventLoadedListener;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onEventLoadedListener = new OnEventLoaded() {
            @Override
            public void onEvent(Event event) {
                currentEvent = event;
            }

            @Override
            public void onError(Exception e) {
                if (mView.getParent() != null) {
                    Snackbar snackbar = Snackbar.make(mView, R.string.event_load_error, 5000);
                    snackbar.show();
                }
            }
        };
        ((MainActivity) getActivity()).getEventService().addOnEventLoadedListener(onEventLoadedListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).getEventService().removeOnEventLoadedListener(onEventLoadedListener);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        Button button = mView.findViewById(R.id.leaderboardButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaderboard();
            }
        });

        return mView;
    }

    private void showLeaderboard() {
        if (currentEvent != null && currentEvent.getScoreboardId() != null) {
            Games.getLeaderboardsClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getContext()))
                    .getLeaderboardIntent(currentEvent.getScoreboardId())
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Il n'y a pas de scoreboard pour cette carte", Toast.LENGTH_LONG).show();
        }
    }
}
