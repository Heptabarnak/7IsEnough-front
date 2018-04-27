package com.heptabargames.a7isenough;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private NavigationView navigationView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_map:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlanFragment()).commit();
                    navigationView.setCheckedItem(R.id.navigation_map);
                    return true;
                case R.id.bottom_beacon:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BeaconFragment()).commit();
                    navigationView.setCheckedItem(R.id.navigation_beacon);
                    return true;
                case R.id.bottom_profil:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    navigationView.setCheckedItem(R.id.navigation_profil);
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlanFragment()).commit();
                break;
            case R.id.navigation_beacon:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BeaconFragment()).commit();
                break;
            case R.id.navigation_profil:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.select_currents_event:
                Toast.makeText(MainActivity.this, "Event clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.notification_checking:
                Toast.makeText(MainActivity.this, "Notif clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private FloatingActionButton.OnClickListener fabQRListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "QR Clicked", Toast.LENGTH_SHORT).show();

            Intent qrScannerIntent = new Intent(MainActivity.this, QRScanner.class);
            MainActivity.this.startActivityForResult(qrScannerIntent, 1);
            Log.d("Button","QR Clicked");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar lateralbar = findViewById(R.id.app_topbar);
        setSupportActionBar(lateralbar);

        drawer = findViewById(R.id.app_lateralbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, lateralbar, R.string.lateral_menu_open, R.string.lateral_menu_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.app_bottombar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FloatingActionButton button = findViewById(R.id.qr_button);
        button.setOnClickListener(fabQRListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlanFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_map);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}