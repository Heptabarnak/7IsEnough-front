package com.heptabargames.a7isenough;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BeaconFragment extends Fragment {
    public static final String TAG = "BeaconFragment";

    private RecyclerView recyclerView;

    private View noBeaconLayout;
    private View noLocalisationLayout;

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

        void onZonesChecked() {
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

        noBeaconLayout = view.findViewById(R.id.no_beacon_layout);
        noLocalisationLayout = view.findViewById(R.id.no_localisation_layout);
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
        if (recyclerView == null || noBeaconLayout == null) return;

        Collections.sort(beacons, new Comparator<Beacon>() {
            public int compare(Beacon obj1, Beacon obj2) {
                // ## Ascending order
                if (obj1.getFound() == null) {
                    if (obj2.getFound() == null) {
                        return 0;
                    }
                    return -1;
                } else if (obj2.getFound() == null) {
                    return 1;
                }

                return obj1.getFound().compareTo(obj2.getFound());
            }
        });

        Log.d(TAG, "Update view");
        noLocalisationLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noBeaconLayout.setVisibility(View.GONE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            noLocalisationLayout.setVisibility(View.VISIBLE);
        }
        if(beacons.isEmpty() && noLocalisationLayout.getVisibility() == View.GONE){
            noBeaconLayout.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
        }

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
