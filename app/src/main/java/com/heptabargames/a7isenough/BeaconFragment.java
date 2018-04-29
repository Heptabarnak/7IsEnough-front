package com.heptabargames.a7isenough;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heptabargames.a7isenough.adapter.BeaconViewAdapter;
import com.heptabargames.a7isenough.models.Beacon;

import java.util.ArrayList;
import java.util.List;

public class BeaconFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private List<Beacon> beacons;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_beacon, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.beacon_recyclerview);
        BeaconViewAdapter beaconViewAdapter = new BeaconViewAdapter(getContext(), beacons);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(beaconViewAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beacons = new ArrayList<>();
        beacons.add(new Beacon(1, "test", 3, null, "Balise 1", "Description 1", "Test"));
        beacons.add(new Beacon(2, "test", 4, null, "Balise 2", "Description 2", "Test"));
        beacons.add(new Beacon(3, "test", 2, null, "Balise 3", "Description 3", "Test"));
        beacons.add(new Beacon(4, "test", 1, null, "Balise 4", "Description 4", "Test"));
        beacons.add(new Beacon(5, "test", 3, null, "Balise 5", "Description 5", "Test"));
        beacons.add(new Beacon(6, "test", 5, null, "Balise 6", "Description 6", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
        beacons.add(new Beacon(7, "test", 2, null, "Balise 7", "Description 7", "Test"));
    }
}
