package com.heptabargames.a7isenough;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.heptabargames.a7isenough.daos.SettingsDAO;
import com.heptabargames.a7isenough.listeners.OnEventLoaded;
import com.heptabargames.a7isenough.listeners.OnManifestLoaded;
import com.heptabargames.a7isenough.models.Beacon;
import com.heptabargames.a7isenough.models.Event;
import com.heptabargames.a7isenough.services.BackgroundService;
import com.heptabargames.a7isenough.services.BeaconService;
import com.heptabargames.a7isenough.services.EventService;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnManifestLoaded {

    public static final String TAG = "BackgroundService";

    //Déclaration des services
    private EventService eventService;

    private DrawerLayout drawer;

    private NavigationView navigationView;


    private BeaconService beaconService;

    private Menu drawerMenu;

    private List<Event> events;
    private Event currEvent;

    private final static int RC_SIGN_IN = 9001;

    private Switch notificationSwitch;
    private SettingsDAO settingsDAO;

    private boolean signinFailedLastTime = false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_map:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlanFragment(), PlanFragment.TAG).commit();
                    navigationView.setCheckedItem(R.id.navigation_map);
                    return true;
                case R.id.bottom_beacon:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BeaconFragment(), BeaconFragment.TAG).commit();
                    navigationView.setCheckedItem(R.id.navigation_beacon);
                    return true;
                case R.id.bottom_profil:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(), ProfileFragment.TAG).commit();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlanFragment(), PlanFragment.TAG).commit();
                break;
            case R.id.navigation_beacon:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BeaconFragment(), BeaconFragment.TAG).commit();
                break;
            case R.id.navigation_profil:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(), ProfileFragment.TAG).commit();
                break;
            case R.id.select_currents_event:
                Toast.makeText(MainActivity.this, "Event clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.notification_checking:
                Toast.makeText(MainActivity.this, "Notif clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.send_mail:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email_dest)});
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
                i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));
                try {
                    startActivity(Intent.createChooser(i, "Envoyer un email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(MainActivity.this, item.getItemId() + ": " + item.getTitle(), Toast.LENGTH_SHORT).show();
                setCurrentEvent(events.get(item.getItemId()));
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
            Log.d(TAG, "QR Clicked");
        }
    };

    private Switch.OnCheckedChangeListener switchListener = new Switch.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Toast.makeText(MainActivity.this, "Notifications activées", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Notifications désactivées", Toast.LENGTH_SHORT).show();
            }
            settingsDAO.saveParameter("isChecked", Boolean.toString(isChecked));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventService = new EventService(this);
        eventService.addOnEventLoadedListener(new OnEventLoaded() {
            @Override
            public void onEvent(Event event) {
                BackgroundService.getBackgroundService().setCurrentEvent(event);
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "An error occured : " + e.getMessage());
            }
        });

        settingsDAO = new SettingsDAO(getApplicationContext());

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        //Déclaration des éléments principaux
        Toolbar lateralbar = findViewById(R.id.app_topbar);
        setSupportActionBar(lateralbar);


        drawer = findViewById(R.id.app_lateralbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, lateralbar, R.string.lateral_menu_open, R.string.lateral_menu_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();


        BottomNavigationView navigation = findViewById(R.id.app_bottombar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FloatingActionButton button = findViewById(R.id.qr_button);
        button.setOnClickListener(fabQRListener);

        MenuItem item = drawerMenu.findItem(R.id.notification_checking);
        View actionSwitch = item.getActionView();
        notificationSwitch = actionSwitch.findViewById(R.id.switch_notification);

        notificationSwitch.setOnCheckedChangeListener(switchListener);
        notificationSwitch.setChecked(Boolean.parseBoolean(settingsDAO.getParameter("isChecked")));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlanFragment(), PlanFragment.TAG).commit();
            navigationView.setCheckedItem(R.id.navigation_map);
            Intent intent = new Intent(MainActivity.this, BackgroundService.class);
            startService(intent);
        }
        beaconService = new BeaconService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        eventService.getManifest(this);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!signinFailedLastTime) {
            signInSilently();
        }
    }

    @Override
    public void onManifest(List<Event> eventList) {

        events = eventList;
        currEvent = eventList.get(0);

        MenuItem eventsItem = drawerMenu.findItem(R.id.select_currents_event);
        if (eventsItem.hasSubMenu()) {
            SubMenu eventsSubMenu = eventsItem.getSubMenu();
            eventsSubMenu.clear();
            int order = 1;
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                if (event.isPermanent()) {
                    eventsSubMenu.add(eventsItem.getGroupId(), i, 0, event.getName());
                    MenuItem item = eventsSubMenu.getItem(i);
                    item.setIcon(R.drawable.ic_place_black_24dp);

                } else if (event.getEndDate() == null || !(new Date()).after(event.getEndDate())) {
                    eventsSubMenu.add(eventsItem.getGroupId(), i, order, event.getName());
                    MenuItem item = eventsSubMenu.getItem(i);
                    item.setIcon(R.drawable.ic_access_time_black_24dp);
                    if (event.getStartDate() != null && (new Date()).before(event.getStartDate())) {
                        item.setActionView(R.layout.coming_soon_layout);
                        item.setEnabled(false);
                    }
                    order++;
                }
            }
        }
        eventService.loadEvent(currEvent);
    }

    @Override
    public void onError(Exception e) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fragment_container), R.string.manifest_error, 5000);

        snackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventService.getManifest(MainActivity.this);
            }
        });

        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String token = data.getStringExtra("result");

                try {
                    Beacon beacon = beaconService.checkBeacon(currEvent, token);
                    if (beacon != null) {
                        Toast.makeText(this, "Beacon found: " + beacon.getName(), Toast.LENGTH_LONG).show();
                        // TODO Redirect to beacon fragment and beacon description

                        BeaconFragment beaconFragment = (BeaconFragment) getSupportFragmentManager()
                                .findFragmentByTag(BeaconFragment.TAG);

                        if (beaconFragment == null) {
                            beaconFragment = new BeaconFragment();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, beaconFragment, BeaconFragment.TAG)
                                    .commit();

                            navigationView.setCheckedItem(R.id.navigation_beacon);
                        }
                        beaconFragment.goToBeacon(beacon);
                    } else {
                        Toast.makeText(this, "It's not a beacon :( ", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (!result.isSuccess()) {
                signinFailedLastTime = true;

                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    public void setCurrentEvent(Event event) {
        currEvent = event;
        eventService.loadEvent(event);
    }

    public EventService getEventService() {
        return eventService;
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            Toast.makeText(this, getString(R.string.signed_in_fmt), Toast.LENGTH_LONG).show();
        }
    }

    private void signInSilently() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            // The signed in account is stored in the task's result.
                            task.getResult();
                        } else {
                            // Player will need to sign-in explicitly using via UI
                            ApiException exception = (ApiException) task.getException();

                            if (exception != null && exception.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {
                                startSignInIntent();
                            }
                        }
                    }
                });
    }

    private void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }
}
