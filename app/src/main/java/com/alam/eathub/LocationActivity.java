package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Model.EventBus.MenuItemEvent;
import com.alam.eathub.Model.Restaurant;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    Marker userMarker;
    Button saveLocation;

    boolean isFirstLoad = false;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        init();
        initView();
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        saveLocation =findViewById(R.id.saveLocation);

        buildLocationRequest();
        buildLocationCallback();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLocation == null)
                    buildLocationCallback();
// Todo -  make Api call to update user address
            }
        });
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                currentLocation = locationResult.getLastLocation();
                addMarkerAndMoveCamera(locationResult.getLastLocation());

            }
        };
    }

    private void addMarkerAndMoveCamera(Location lastLocation) {
        if(userMarker != null)
            userMarker.remove();

        LatLng userLatLng = new LatLng(lastLocation.getLatitude() , lastLocation.getLongitude());
        userMarker = mMap.addMarker(new MarkerOptions().position(userLatLng).title(Common.currentUser.getName()));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(userLatLng , 17);
        mMap.animateCamera(yourLocation);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void initView() {
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this , R.raw.map_style));
            if(!success)
                Log.e("ERROR MAP" , "Load Style Error");
        }catch (Resources.NotFoundException e){
            Log.e("ERROR MAP", "Resources not found");
        }

    }
}