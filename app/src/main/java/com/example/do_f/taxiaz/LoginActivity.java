package com.example.do_f.taxiaz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.do_f.taxiaz.api.RestClient;
import com.example.do_f.taxiaz.api.json.RegistersResponse;
import com.example.do_f.taxiaz.api.json.TaxiResponse;
import com.example.do_f.taxiaz.api.post.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
{
    private final static String DEBUG_TAG = "LoginActivity";

    private final static String ARG_LAT = "_latitude";
    private final static String ARG_LGN = "_longitude";
    private final static String ARG_ADD = "_address";

    private RelativeLayout  login;
    private RelativeLayout  finish;

    private CircleProgressView  mCircleView;

    private Button      submit;
    private EditText    mail;
    private EditText    phone;
    private Spinner     paiement;

    private double      lat;
    private double      lgn;
    private String      add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (RelativeLayout) findViewById(R.id.login_layout_login);
        finish = (RelativeLayout) findViewById(R.id.login_layout_finish);
        mCircleView = (CircleProgressView) findViewById(R.id.circleView);


        submit = (Button) findViewById(R.id.login_submit);
        mail = (EditText) findViewById(R.id.login_mail);
        phone = (EditText) findViewById(R.id.login_phone);
        paiement = (Spinner) findViewById(R.id.login_paiement);

        Intent i = getIntent();
        lat = i.getExtras().getDouble(ARG_LAT);
        lgn = i.getExtras().getDouble(ARG_LGN);
        add = i.getExtras().getString(ARG_ADD);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation animationFadeOut = AnimationUtils.loadAnimation(getApplication(), R.anim.fadeout);
                login.setAnimation(animationFadeOut);
                login.setVisibility(View.INVISIBLE);

                final Animation animationFadeIn = AnimationUtils.loadAnimation(getApplication(), R.anim.fadein);
                mCircleView.setValue(0);
                mCircleView.spin();
                finish.setAnimation(animationFadeIn);
                finish.setVisibility(View.VISIBLE);

                //public User(String mail, String phone, String address, Double lng, Double lat) {

                if (mail.getText().toString().length() == 0)
                    add = "";

                User user = new User(
                    mail.getText().toString(),
                    phone.getText().toString(),
                    add,
                    lgn,
                    lat
                );

                Call<RegistersResponse> call = RestClient.get().postCustomer(user);
                call.enqueue(new Callback<RegistersResponse>() {
                    @Override
                    public void onResponse(Response<RegistersResponse> response) {
                        getTaxi();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(DEBUG_TAG, "FAIL : " + t.getMessage());
                    }
                });
            }
        });
    }

    private void    getTaxi()
    {
        Call<TaxiResponse> taxi = RestClient.get().getTaxi();
        taxi.enqueue(new Callback<TaxiResponse>() {
            @Override
            public void onResponse(Response<TaxiResponse> response)
            {
                int t = 0;
                int time = Integer.parseInt(response.body().getInfos().get(1).getValue());
                if (time > 0) {
                    t = ((time * 100) / 60);
                }
                Log.d(DEBUG_TAG, "lol "+t);
                mCircleView.setTextMode(TextMode.TEXT);
                mCircleView.setText(Integer.toString(time));
                mCircleView.setValueAnimated(t);
                new PrefetchData().execute();
                Log.d(DEBUG_TAG, "SUCCESS : " + response.body().getInfos().get(1).getValue());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(DEBUG_TAG, "FAIL : " + t.getMessage());
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
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            finish();
        }
    }
}
