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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        tracker = new LocationTracker(this);
    }

    public void onButtonClick(View view){
        if(view.getId() == R.id.sendNewStatus){
            Intent i = new Intent(MainActivity.this, NewDamageStatusActivity.class);
            startActivity(i);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        LatLng position = new LatLng(loc.getLatitude(),loc.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,19.0f));
        mMap.addMarker(new MarkerOptions().title("YOUR POSITION").position(position));
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
