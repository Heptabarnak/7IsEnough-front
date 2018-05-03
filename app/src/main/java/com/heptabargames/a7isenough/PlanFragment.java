package com.heptabargames.a7isenough;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Rectangle;
import com.heptabargames.a7isenough.models.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlanFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "PlanFragment";
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    Event currentEvent;
    OnEventLoaded onEventLoadedListener;
    final int ALPHA = 75;
    final int[] COLORS = new int[]{
            Color.argb(ALPHA, 252, 92, 101),
            Color.argb(ALPHA, 253, 150, 68),
            Color.argb(ALPHA, 254, 211, 48),
            Color.argb(ALPHA, 38, 222, 129),
            Color.argb(ALPHA, 43, 203, 186),
            Color.argb(ALPHA, 69, 170, 242),
            Color.argb(ALPHA, 75, 123, 236),
            Color.argb(ALPHA, 165, 94, 234),
            Color.argb(ALPHA, 119, 140, 163)
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onEventLoadedListener = new OnEventLoaded() {
            @Override
            public void onEvent(Event event) {
                currentEvent = event;
                updateZones();
            }

            @Override
            public void onError(Exception e) {
                if(mView.getParent() != null){
                    Snackbar snackbar = Snackbar.make(mView, R.string.event_load_error, 5000);
                    snackbar.show();
                }
            }
        };
        ((MainActivity)getActivity()).getEventService().addOnEventLoadedListener(onEventLoadedListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity)getActivity()).getEventService().removeOnEventLoadedListener(onEventLoadedListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_plan, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        LatLng qgLatLng = new LatLng(45.759028, 4.845361);
        CameraPosition lyon = CameraPosition.builder().target(qgLatLng).zoom(13).bearing(0).tilt(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(lyon));
        updateZones();

    }

    private void updateZones() {
        if (mGoogleMap == null || currentEvent == null) return;
        mGoogleMap.clear();
        for (Zone zone : currentEvent.getZones()) {
            int color = COLORS[new Random().nextInt(COLORS.length)];
            for (Rectangle rectangle : zone.getPolygons()) {
                mGoogleMap.addPolygon(new PolygonOptions()
                        .addAll(rectangle.getAllPoints())
                        .fillColor(color)
                        .strokeWidth(0)
                );
            }
        }
    }
}
