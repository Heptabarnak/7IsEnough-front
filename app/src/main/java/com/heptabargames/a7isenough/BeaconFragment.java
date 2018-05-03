package com.heptabargames.a7isenough;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heptabargames.a7isenough.adapter.BeaconViewAdapter;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.ZoneListener;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;
import com.heptabargames.a7isenough.services.BackgroundService;

import java.util.ArrayList;
import java.util.List;

public class BeaconFragment extends Fragment {
    public static final String TAG = "BeaconFragment";

    private RecyclerView recyclerView;

    private List<Beacon> beacons;

    private Event currentEvent;

    private List<Zone> currentZoneGPS = new ArrayList<>();
    private List<Zone> currentZoneNet = new ArrayList<>();

    private ZoneListener zoneListener = new ZoneListener() {

        @Override
        public void onZonesCheckedByGPS(List<Zone> zones) {
            Log.d(TAG, "onZoneCheckedByGPS(), Size = " + zones.size());
            currentZoneGPS = zones;
            onZonesChecked();
        }

        @Override
        public void onZonesCheckedByNetwork(List<Zone> zones) {
            Log.d(TAG, "onZoneCheckedByNetwork(), Size = " + zones.size());
            currentZoneNet = zones;
            onZonesChecked();
        }

        public void onZonesChecked() {
            beacons.clear();

            List<Zone> zones = new ArrayList<>(currentZoneNet);
            zones.addAll(currentZoneGPS);

            if (currentEvent != null) {
                Log.d(TAG, "Current event :" + currentEvent.getName());
                for (Zone zone : currentEvent.getZones()) {
                    if (zones.contains(zone)) {
                        beacons.addAll(zone.getBeacons());
                        break;
                    }
                }

                updateView();
            }
        }

        @Override
        public void onError(Exception e) {

        }
    };

    private OnEventLoaded eventListener = new OnEventLoaded() {
        @Override
        public void onEvent(Event event) {
            currentEvent = event;
        }

        @Override
        public void onError(Exception e) {

        }
    };

    public BeaconFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacon, container, false);

        recyclerView = view.findViewById(R.id.beacon_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        BeaconViewAdapter foundBeaconViewAdapter = new BeaconViewAdapter(getContext(), beacons);
        recyclerView.setAdapter(foundBeaconViewAdapter);

        updateView();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).getEventService().addOnEventLoadedListener(eventListener);
        BackgroundService.getBackgroundService().addZoneListener(zoneListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        beacons = new ArrayList<>();
        currentEvent = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BackgroundService.getBackgroundService().removeZoneListener(zoneListener);
        ((MainActivity) getActivity()).getEventService().removeOnEventLoadedListener(eventListener);
    }

    public void updateView() {
        if (recyclerView == null) return;

        Log.d(TAG, "Update view");
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void goToBeacon(Beacon beacon) {
        if (recyclerView == null) return;

        for (int i = 0; i < beacons.size(); i++) {
            if (beacons.get(i) == beacon) {
                recyclerView.scrollToPosition(i);
            }
        }
    }
}
