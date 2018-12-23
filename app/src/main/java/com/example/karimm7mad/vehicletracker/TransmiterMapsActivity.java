package com.example.karimm7mad.vehicletracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

public class TransmiterMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "asdasd";
    private GoogleMap mMap;
    public String currCarKey;
    public DatabaseReference firebaseDBman;
    public LocationManager lMan = null;
    public Criteria currLocCriteria = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmiter_maps);
        this.currCarKey = this.getIntent().getStringExtra("carkey");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.lMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.defineCurrLocCriteria();


    }

    public void defineCurrLocCriteria() {
        this.currLocCriteria = new Criteria();
        this.currLocCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        this.currLocCriteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        this.currLocCriteria.setAltitudeRequired(false);
        this.currLocCriteria.setBearingRequired(true);
        this.currLocCriteria.setSpeedRequired(true);
        this.currLocCriteria.setCostAllowed(false);
    }

    public boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ReceiverMapsActivity.ReqNo);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ReceiverMapsActivity.ReqNo:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.lMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    this.defineCurrLocCriteria();
                    Toast.makeText(getBaseContext(), "ACCESS GRANTED, It's OK now", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getBaseContext(), "ACCESS DENIED, Allow Location Access", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (hasPermission()) {
            Location currLocation = lMan.getLastKnownLocation(lMan.getBestProvider(currLocCriteria, false));
            // Add a marker in Sydney and move the camera
            LatLng currPosition = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currPosition).title("Transmitter Map"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currPosition));
        }

    }


    @Override
    public void onBackPressed() {
        this.finish();
        System.exit(0);
    }

}
