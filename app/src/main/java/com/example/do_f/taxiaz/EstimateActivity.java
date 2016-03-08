package com.example.do_f.taxiaz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.do_f.taxiaz.ApiGoogleMaps.GoogleClient;
import com.example.do_f.taxiaz.ApiGoogleMaps.json.DistanceResponse;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstimateActivity extends AppCompatActivity {

    private final static String TAG = "EstimateActivity";
    private final static String ARG_LAT = "_latitude";
    private final static String ARG_LGN = "_longitude";
    private final static String ARG_ADD = "_address";

    private double      lat;
    private double      lgn;
    private String      add;

    private TextView    mPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navigate_before_black_48dp);

        Intent i = getIntent();
        lat = i.getExtras().getDouble(ARG_LAT);
        lgn = i.getExtras().getDouble(ARG_LGN);
        add = i.getExtras().getString(ARG_ADD);

        mPrice = (TextView) findViewById(R.id.es_text_price);

        Log.d(TAG, "Lat : " + lat + " lgn : " + lgn);

        TextView esFrom = (TextView) findViewById(R.id.es_from);
        esFrom.setText(add);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                String origins = lat+","+lgn;
                String destinations = place.getLatLng().latitude+","+place.getLatLng().longitude;
                String language = "fr-FR";
                String key = "AIzaSyA2SvsMpksW8OZISeg9t0CzLAvbHETgFFw"; //String.format(getResources().getString(R.string.google_maps_key));

                Log.d(TAG, "Query : origins="+origins+"&destinations="+destinations+"&language="+language+"&key="+key);

                Call<DistanceResponse>  call = GoogleClient.get().getDistance(origins, destinations, language, key);
                call.enqueue(new Callback<DistanceResponse>() {
                    @Override
                    public void onResponse(Response<DistanceResponse> response) {
                        if (response.body().getRows().size() > 0)
                        {
                            int km = response.body().getRows().get(0).getElements().get(0).getDistance().getValue();
                            Log.d(TAG, "KM : "+km);
                            if (km > 0)
                            {
                                km /= 1000;
                            }
                            Log.d(TAG, "KM : "+km);
                            int price = km * 2;
                            mPrice.setText(String.format(getResources().getString(R.string.es_price), Integer.toString(price)));
                        }
                        Log.d(TAG, "EL DEST : " + response.body().getDest().size());
                        Log.d(TAG, "EL ORIGIN : " + response.body().getOrigin().size());
                        Log.d(TAG, "EL Rows : " + response.body().getRows().size());
                        Log.d(TAG, "error: "+response.body().getStatus());
                        Log.d(TAG, "message: "+response.body().getError());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(TAG, "FAIL : " + t.getMessage());
                    }
                });
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
