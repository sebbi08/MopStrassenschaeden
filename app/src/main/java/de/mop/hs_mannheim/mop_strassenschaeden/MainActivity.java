package de.mop.hs_mannheim.mop_strassenschaeden;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks{


    private GoogleMap mMap;
    private LocationTracker tracker;
    private GoogleApiClient googleApiClient;
    private LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        tracker = new LocationTracker(this);
    }

    public void onButtonClick(View view){
        if(position == null){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Bitte wählen sie einen Standort für die Schadensmeldung");
            alert.setTitle("Keine Standort");
            alert.create().show();
            return;
        }
        if(view.getId() == R.id.sendNewStatus){
            Intent newDatamageIntent = new Intent(MainActivity.this, NewDamageStatusActivity.class);
            newDatamageIntent.putExtra("Lat", position.latitude);
            newDatamageIntent.putExtra("Lon", position.longitude);
            startActivity(newDatamageIntent);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
                mMap.addMarker(new MarkerOptions().title("Deine Position").position(latLng));
                position = latLng;
            }
        });
        tracker.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiClient = tracker.getmGoogleApiClient();

        setLocation();

    }

    public void setLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},555);
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(loc != null) {
            position = new LatLng(loc.getLatitude(),loc.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,17.0f));
            mMap.addMarker(new MarkerOptions().title("Deine Position").position(position));
        }else{
            position = null;
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Bitte wählen Sie manuell den Standort");
            alert.setTitle("Keine Position Gefunden");
            alert.create().show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 555){
            for(int i = 0;i<permissions.length;i++){
                String permision = permissions[i];
                int result = grantResults[i];
                if(Objects.equals(permision, Manifest.permission.ACCESS_FINE_LOCATION) && result == PackageManager.PERMISSION_GRANTED){
                    setLocation();
                }
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        tracker.disconnect();
    }
}
