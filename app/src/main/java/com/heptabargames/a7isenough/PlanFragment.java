package com.heptabargames.a7isenough;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.models.Rectangle;
import com.heptabargames.a7isenough.models.Zone;
import com.heptabargames.a7isenough.services.BackgroundService

public class PlanFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    LocalizationService localizationService;
    Event currentEvent;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_plan, container, false);
        localizationService = new LocalizationService();

        ((MainActivity) getActivity()).getEventService().addOnEventLoadedListener(new OnEventLoaded() {
            @Override
            public void onEvent(Event event) {
                currentEvent = event;
                updateZones();
            }

            @Override
            public void onError(Exception e) {
                Snackbar snackbar = Snackbar.make(mView, R.string.event_load_error, 5000);
                snackbar.show();
            }
        });
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent intent = new Intent(getContext(), BackgroundService.class);
        backgroundService.startService(intent);
        
        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
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

    }

    private void updateZones() {
        if (mGoogleMap == null || currentEvent == null) return;
        mGoogleMap.clear();
        Toast.makeText(getContext(), currentEvent.getName(), Toast.LENGTH_SHORT).show();
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
