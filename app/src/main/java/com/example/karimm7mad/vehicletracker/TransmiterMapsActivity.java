package com.example.karimm7mad.vehicletracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TransmiterMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "asdasd";
    private GoogleMap mMap;

    public String currCarKey;
    public String currUserKey;

    public DatabaseReference firebaseDBman;
    public LocationManager lMan = null;
    public Criteria currLocCriteria = null;
    public Switch toggle;
    public Location lastKnownLocationOfCar;

    public boolean isToggleChecked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmiter_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.currCarKey = this.getIntent().getStringExtra("carkey");
        this.currUserKey = this.getIntent().getStringExtra("userkey");
        isToggleChecked = false;
        toggle = findViewById(R.id.toggleBtn);

        this.firebaseDBman = FirebaseDatabase.getInstance().getReference("Cars").child(this.currUserKey).child(this.currCarKey);

        if (hasPermission()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            this.lMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            this.currLocCriteria = new Criteria();
            this.lastKnownLocationOfCar = lMan.getLastKnownLocation(lMan.getBestProvider(currLocCriteria, false));
        }
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
                    Toast.makeText(getBaseContext(), "ACCESS GRANTED, It's OK now", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getBaseContext(), "ACCESS DENIED, Allow Location Access", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        isToggleChecked = true;
                        Toast.makeText(getBaseContext(), "true", Toast.LENGTH_SHORT).show();
                        if (hasPermission()) {
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            Location currLocation = lMan.getLastKnownLocation(lMan.getBestProvider(currLocCriteria, true));
                            viewLocationOnMap(currLocation);
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "False", Toast.LENGTH_SHORT).show();
                        isToggleChecked = false;
                        if (hasPermission()) {
                            mMap.setMyLocationEnabled(false);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            mMap.clear();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    public void viewLocationOnMap(Location l) {
        mMap.clear();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        LatLng currPosition;
        try {
            currPosition = new LatLng(l.getLatitude(), l.getLongitude());
        } catch (Exception e) {
            currPosition = new LatLng(0, 0);
        }
        if (hasPermission()) {
            mMap.addMarker(new MarkerOptions().position(currPosition).title("You are Here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currPosition));
            firebaseDBman.child("currentLatitude").setValue(currPosition.latitude);
            firebaseDBman.child("currentLongitude").setValue(currPosition.longitude);
            lastKnownLocationOfCar.setLatitude(currPosition.latitude);
            lastKnownLocationOfCar.setLongitude(currPosition.longitude);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasPermission())
            this.lMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 3, (LocationListener) this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        System.exit(0);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isToggleChecked) {
            double distanceMoved = this.lastKnownLocationOfCar.distanceTo(location);
            if (distanceMoved > 3) {
                try {
                    Toast.makeText(getBaseContext(), "ALARM", Toast.LENGTH_SHORT).show();
                    firebaseDBman.child("currentLatitude").setValue(location.getLatitude());
                    firebaseDBman.child("currentLongitude").setValue(location.getLongitude());
                    firebaseDBman.child("isCarMoving").setValue(true);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            this.lastKnownLocationOfCar.setLongitude(location.getLongitude());
            this.lastKnownLocationOfCar.setLatitude(location.getLatitude());
            viewLocationOnMap(this.lastKnownLocationOfCar);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
