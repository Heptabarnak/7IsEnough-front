package com.heptabargames.a7isenough;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.heptabargames.a7isenough.listeners.OnEventsLoaded;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Rectangle;
import com.heptabargames.a7isenough.models.Zone;

public class PlanFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    Event currentEvent;
    OnEventLoaded onEventLoadedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_plan, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
        LatLng qgLatLng = new LatLng(45.781974, 4.872674);
        googleMap.addMarker(new MarkerOptions().position(qgLatLng).title("QG").snippet("Where all have started"));
        CameraPosition QG = CameraPosition.builder().target(qgLatLng).zoom(16).bearing(0).tilt(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(QG));
        updateZones();

    }

    private void updateZones() {
        if (mGoogleMap == null || currentEvent == null) return;
        mGoogleMap.clear();
        for (Zone zone : currentEvent.getZones()) {
            for (Rectangle rectangle : zone.getPolygons()) {
                mGoogleMap.addPolygon(new PolygonOptions()
                        .addAll(rectangle.getAllPoints())
                        .fillColor(Color.argb(50, 255, 0, 0))
                        .strokeWidth(0)
                );
            }
        }
    }
}
