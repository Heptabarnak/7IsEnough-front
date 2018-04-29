package com.heptabargames.a7isenough;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.heptabargames.a7isenough.adapter.BeaconViewAdapter;
import com.heptabargames.a7isenough.listeners.AccordionClickListener;
import com.heptabargames.a7isenough.models.Beacon;

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_beacon, container, false);

        foundButton = (ImageButton) view.findViewById(R.id.beacon_found_dropdown);
        notFoundButton = (ImageButton) view.findViewById(R.id.beacon_not_found_dropdown);

        foundRecyclerView = (RecyclerView) view.findViewById(R.id.beacon_found_recyclerview);
        BeaconViewAdapter foundBeaconViewAdapter = new BeaconViewAdapter(getContext(), foundBeacons);
        foundRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        foundRecyclerView.setAdapter(foundBeaconViewAdapter);

        notFoundRecyclerView = (RecyclerView) view.findViewById(R.id.beacon_not_found_recyclerview);
        BeaconViewAdapter notFoundBeaconViewAdapter = new BeaconViewAdapter(getContext(), notFoundBeacons);
        notFoundRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notFoundRecyclerView.setAdapter(notFoundBeaconViewAdapter);

        foundButton.setOnClickListener(new AccordionClickListener(foundButton, foundRecyclerView));
        notFoundButton.setOnClickListener(new AccordionClickListener(notFoundButton, notFoundRecyclerView));


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foundBeacons = new ArrayList<Beacon>();
        foundBeacons.add(new Beacon(1, "test", 3, null, "Balise 1", "Description 1", "Test"));
        foundBeacons.add(new Beacon(2, "test", 4, null, "Balise 2", "Description 2", "Test"));
        foundBeacons.add(new Beacon(3, "test", 2, null, "Balise 3", "Description 3", "Test"));
        foundBeacons.add(new Beacon(4, "test", 1, null, "Balise 4", "Description 4", "Test"));
        foundBeacons.add(new Beacon(5, "test", 3, null, "Balise 5", "Description 5", "Test"));
        foundBeacons.add(new Beacon(6, "test", 5, null, "Balise 6", "Description 6", "Test"));
        foundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        foundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        foundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        foundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons = new ArrayList<Beacon>();
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        notFoundBeacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
    }
}
