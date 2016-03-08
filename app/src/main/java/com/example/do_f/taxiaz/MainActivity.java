package com.example.do_f.taxiaz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.do_f.taxiaz.Fragment.MapsFragment;
import com.example.do_f.taxiaz.History.HistoryActivity;
import com.example.do_f.taxiaz.api.RestClient;
import com.example.do_f.taxiaz.api.json.TaxiResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MapsFragment.OnFragmentInteractionListener,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnCameraChangeListener,
        View.OnClickListener {

    private final static String ARG_LAT = "_latitude";
    private final static String ARG_LGN = "_longitude";
    private final static String ARG_ADD = "_address";

    private final static String DEBUG_TAG = "MainActivity";
    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 750;
    private long lastCallMs = Long.MIN_VALUE;

    private GoogleMap       mMap;
    private LocationManager lm;
    private Toolbar         toolbar;
    private DrawerLayout    mDrawerLayout;

    private ImageButton     cLocat;
    private Button          valid;

    private double          latitude;
    private double          longitude;

    private boolean         firstUpdate;
    private Geocoder        geocoder;
    private CameraPosition  cameraPosition;
    private Address         cAddress;

    // Pin Update

    private ProgressWheel   wheel;
    private TextView        nbTaxi;
    private String          _nbTaxi;
    private String          _minTaxi;

    //Search Bar
    private TextView        pinLocation;
    private TextView        currentLocation;

    //Zoom Mode
    private boolean         isSelected;
    private float           cZoom = 15;
    private Drawable        cToolbarIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstUpdate = false;
        isSelected = false;
        geocoder = new Geocoder(this, Locale.getDefault());
        cLocat = (ImageButton) findViewById(R.id.map_location);
        valid = (Button) findViewById(R.id.map_valid_taxi);
        wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        nbTaxi = (TextView) findViewById(R.id.map_text_taxi);

        pinLocation = (TextView) findViewById(R.id.map_text_pin_location);
        currentLocation = (TextView) findViewById(R.id.map_text_current_location);

        View view = this.findViewById(R.id.layout_test).getRootView();

        valid.setVisibility(View.INVISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cLocat.setOnClickListener(this);
        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra(ARG_LAT, cameraPosition.target.latitude);
                i.putExtra(ARG_LGN, cameraPosition.target.longitude);
                i.putExtra(ARG_ADD, cAddress.getAddressLine(0) + " " + cAddress.getLocality() + " " + cAddress.getPostalCode());
                Log.d(DEBUG_TAG, "Lat : " + cameraPosition.target.latitude + " " + cameraPosition.target.longitude + " | " + cAddress.getAddressLine(0) + " " + cAddress.getLocality() + " " + cAddress.getPostalCode());

                startActivity(i);
                exitZoom();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (isSelected)
                    exitZoom();
                else if (!mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);

        Intent intent = getIntent();
        latitude = intent.getExtras().getDouble(ARG_LAT);
        longitude = intent.getExtras().getDouble(ARG_LGN);
        LatLng cPos = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cPos));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    public void onCameraChange(CameraPosition _cameraPosition)
    {
        final long snap = System.currentTimeMillis();
        if (lastCallMs + CAMERA_MOVE_REACT_THRESHOLD_MS > snap)
        {
            //Log.d(DEBUG_TAG, "Delay");
            lastCallMs = snap;
            return;
        }

        lastCallMs = snap;
        cameraPosition = _cameraPosition;
        List<Address> addresses = null;
        if (cameraPosition.target.latitude != 0 && cameraPosition.target.longitude != 0)
        {
            try {
                addresses = geocoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                cAddress = addresses.get(0);
                currentLocation.setText(address+", "+city);
                if (!isSelected)
                {
                    nbTaxi.setVisibility(View.INVISIBLE);
                    wheel.setVisibility(View.VISIBLE);
                    getTaxi();
                }
                Log.d("setOnCameraChange", address+" "+city);
                Log.d("setOnCameraChange", "Latitude : "+cameraPosition.target.latitude+" Longitude : "+cameraPosition.target.longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // onClick Selector
    @Override
    public void onClick(View v)
    {
        if (!isSelected)
        {
            isSelected = true;
            cToolbarIcon = toolbar.getNavigationIcon();
            toolbar.setNavigationIcon(R.drawable.ic_navigate_before_black_48dp);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition.target));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 500, null);
            valid.setVisibility(View.VISIBLE);
            final Animation animationFadeIn = AnimationUtils.loadAnimation(getApplication(), R.anim.fadein);
            valid.setAnimation(animationFadeIn);
        }
    }

    @Override
    public void onBackPressed()
    {
        Log.d(DEBUG_TAG, "onBackPressed()");
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (isSelected)
                exitZoom();
            else
                super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent i = new Intent(MainActivity.this, EstimateActivity.class);
            i.putExtra(ARG_LAT, cameraPosition.target.latitude);
            i.putExtra(ARG_LGN, cameraPosition.target.longitude);
            i.putExtra(ARG_ADD, cAddress.getAddressLine(0));
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //exitZoom();
        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String newStatus = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                newStatus = "OUT_OF_SERVICE";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                newStatus = "TEMPORARILY_UNAVAILABLE";
                break;
            case LocationProvider.AVAILABLE:
                newStatus = "AVAILABLE";
                break;
        }
        String msg = String.format(getResources().getString(R.string.provider_new_status), provider, newStatus);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderEnabled(String provider) {
        String msg = String.format(getResources().getString(R.string.provider_enabled), provider);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        String msg = String.format(getResources().getString(R.string.provider_disabled), provider);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    protected void exitZoom()
    {
        toolbar.setNavigationIcon(cToolbarIcon);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(cZoom), 1000, null);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(getApplication(), R.anim.fadeout);
        valid.setAnimation(animationFadeOut);
        valid.setVisibility(View.INVISIBLE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        isSelected = false;
    }

    protected void    getTaxi()
    {
        Call<TaxiResponse> taxi = RestClient.get().getTaxi();
        taxi.enqueue(new Callback<TaxiResponse>() {
            @Override
            public void onResponse(Response<TaxiResponse> response)
            {
                _nbTaxi = response.body().getInfos().get(0).getValue();
                _minTaxi = response.body().getInfos().get(1).getValue();
                new PrefetchData().execute();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            wheel.setVisibility(View.INVISIBLE);
            nbTaxi.setVisibility(View.VISIBLE);
            String msg = String.format(getResources().getString(R.string.map_taxi), _nbTaxi, _minTaxi);
            nbTaxi.setText(msg);
        }
    }
}
