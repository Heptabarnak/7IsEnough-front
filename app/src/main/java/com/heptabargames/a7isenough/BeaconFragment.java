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
import com.heptabargames.a7isenough.listeners.ZoneListener;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Zone;
import com.heptabargames.a7isenough.services.LocalizationManager;

import java.util.ArrayList;
import java.util.List;

public class BeaconFragment extends Fragment {

    View view;
    private RecyclerView notFoundRecyclerView;
    private RecyclerView foundRecyclerView;
    private List<Beacon> notFoundBeacons;
    private List<Beacon> foundBeacons;
    private ImageButton notFoundButton;
    private ImageButton foundButton;
    private LocalizationManager localizationManager;

    private ZoneListener zoneListener = new ZoneListener() {
        @Override
        public void onAllZoneChanged(Zone zone) {
            Log.d("BeaconFragment", "onAllZoneChanged()");
        }

        @Override
        public void onZoneChanged(Zone zone) {

            Log.d("BeaconFragment", "onZoneChanged()");
            if(zone == null){
                foundBeacons = new ArrayList<>();
                notFoundBeacons = new ArrayList<>();
            }else {
                Log.d("BeaconFragment", "Zone is now "+zone.getName());
                foundBeacons = zone.getFoundBeacons();
                notFoundBeacons = zone.getNotFoundBeacons();
            }
            if(foundRecyclerView != null && notFoundRecyclerView != null){
                updateView();
            }
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


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        foundBeacons = new ArrayList<Beacon>();
        notFoundBeacons = new ArrayList<Beacon>();
        localizationManager = ((MainActivity) getActivity()).getLocalizationManager();
        localizationManager.addZoneListener(zoneListener);
        ((MainActivity) getActivity()).getEventService().addOnEventLoadedListener(localizationManager);
    }

    @Override
    public void onDetach() {

        localizationManager.removeZoneListener(zoneListener);
        ((MainActivity)getActivity()).getEventService().removeOnEventLoadedListener(localizationManager);
        super.onDetach();
    }

    public void updateView(){
        BeaconViewAdapter foundBeaconViewAdapter = new BeaconViewAdapter(getContext(), foundBeacons);
        BeaconViewAdapter notFoundBeaconViewAdapter = new BeaconViewAdapter(getContext(), notFoundBeacons);
        foundRecyclerView.setAdapter(foundBeaconViewAdapter);
        notFoundRecyclerView.setAdapter(notFoundBeaconViewAdapter);
    }
}
