package com.heptabargames.a7isenough;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    Toast.makeText(MainActivity.this, "Plan clicked", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_beacon:
                    Toast.makeText(MainActivity.this, "Beacon clicked", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_profil:
                    Toast.makeText(MainActivity.this, "Profil clicked", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    private FloatingActionButton.OnClickListener fabQRListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "QR Clicked", Toast.LENGTH_SHORT).show();
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


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.app_bottombar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.qr_button);
        button.setOnClickListener(fabQRListener);
    }

    ;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}