package com.heptabargames.a7isenough;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.heptabargames.a7isenough.adapter.BeaconViewAdapter;
import com.heptabargames.a7isenough.listeners.AccordionClickListener;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.ZoneListener;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Zone;
import com.heptabargames.a7isenough.services.BackgroundService;
import com.heptabargames.a7isenough.services.LocalizationManager;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeaconFragment extends Fragment {

    View view;
    private RecyclerView notFoundRecyclerView;
    private RecyclerView foundRecyclerView;
    private List<Beacon> notFoundBeacons;
    private List<Beacon> foundBeacons;
    private ImageButton notFoundButton;
    private ImageButton foundButton;
    private Event currentEvent;
    private List<Zone> currentZoneGPS = new ArrayList<>();
    private List<Zone> currentZoneNet = new ArrayList<>();
    final String TAG = "BeaconFragment";

    private ZoneListener zoneListener = new ZoneListener() {

        @Override
        public void onZonesCheckedByGPS(List<Zone> zones) {
            Log.d(TAG, "onZoneCheckedByGPS(), Size = "+zones.size());
            currentZoneGPS = zones;
            onZonesChecked();
        }

        @Override
        public void onZonesCheckedByNetwork(List<Zone> zones) {
            Log.d(TAG, "onZoneCheckedByNetwork(), Size = "+zones.size());
            currentZoneNet = zones;
            onZonesChecked();
        }

        public void onZonesChecked() {
            foundBeacons = new ArrayList<>();
            notFoundBeacons = new ArrayList<>();
            Set<Zone> zones = new HashSet<Zone>();
            zones.addAll(currentZoneGPS);
            zones.addAll(currentZoneNet);

            if(currentEvent != null){
                for (Zone zone : zones) {
                    if(currentEvent.getZones().contains(zone)){
                        foundBeacons.addAll(zone.getFoundBeacons());
                        notFoundBeacons.addAll(zone.getNotFoundBeacons());
                    }
                }
            }
            if(foundRecyclerView != null && notFoundRecyclerView != null){
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_beacon, container, false);

        foundButton = (ImageButton) view.findViewById(R.id.beacon_found_dropdown);
        notFoundButton = (ImageButton) view.findViewById(R.id.beacon_not_found_dropdown);

        foundRecyclerView = (RecyclerView) view.findViewById(R.id.beacon_found_recyclerview);
        foundRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        BeaconViewAdapter foundBeaconViewAdapter = new BeaconViewAdapter(getContext(), foundBeacons);
        foundRecyclerView.setAdapter(foundBeaconViewAdapter);
        notFoundRecyclerView = (RecyclerView) view.findViewById(R.id.beacon_not_found_recyclerview);
        notFoundRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateView();

        foundButton.setOnClickListener(new AccordionClickListener(foundButton, foundRecyclerView));
        notFoundButton.setOnClickListener(new AccordionClickListener(notFoundButton, notFoundRecyclerView));


        ((MainActivity)(getActivity())).getEventService().addOnEventLoadedListener(eventListener);
        BackgroundService.getBackgroundService().addZoneListener(zoneListener);


        return view;
    }

    @Override
    public void onDestroyView() {
        BackgroundService.getBackgroundService().removeZoneListener(zoneListener);
        ((MainActivity)(getActivity())).getEventService().removeOnEventLoadedListener(eventListener);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        foundBeacons = new ArrayList<Beacon>();
        notFoundBeacons = new ArrayList<Beacon>();
        currentEvent =  null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateView(){
        Log.d(TAG, "Update view");
        BeaconViewAdapter foundBeaconViewAdapter = new BeaconViewAdapter(getContext(), foundBeacons);
        BeaconViewAdapter notFoundBeaconViewAdapter = new BeaconViewAdapter(getContext(), notFoundBeacons);
        foundRecyclerView.setAdapter(foundBeaconViewAdapter);
        notFoundRecyclerView.setAdapter(notFoundBeaconViewAdapter);
    }
}
