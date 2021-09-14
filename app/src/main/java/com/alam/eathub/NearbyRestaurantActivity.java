package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
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
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

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

public class NearbyRestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    Marker userMarker;

    boolean isFirstLoad = false;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurant);

        init();
        initView();
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        buildLocationRequest();
        buildLocationCallback();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                currentLocation = locationResult.getLastLocation();
                addMarkerAndMoveCamera(locationResult.getLastLocation());

                if(!isFirstLoad){
                    isFirstLoad = !isFirstLoad;
                    requestNearbyRestaurant(locationResult.getLastLocation().getLatitude() , locationResult.getLastLocation().getLongitude() , 10);
                }
            }
        };
    }

    private void requestNearbyRestaurant(double latitude, double longitude, int distance) {
        dialog.show();

        Map<String , String> headers = new HashMap<>();
        headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
        compositeDisposable.add(myRestaurantAPI.getNearbyRestaurant(headers , latitude , longitude , distance)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantModel -> {
                    if(restaurantModel.isSuccess()){
                        addRestaurantMarker(restaurantModel.getResult());
                    }
                    else {
                        Toast.makeText(this, ""+restaurantModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                } , throwable -> {
                    dialog.dismiss();
                    Toast.makeText(this, "[NEARBY RESTAURANT]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                })
        );
    }

    private void addRestaurantMarker(List<Restaurant> restaurantList) {
        for(Restaurant restaurant : restaurantList){

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker))
                    .position(new LatLng(restaurant.getLat() , restaurant.getLng()))
                    .snippet(restaurant.getAddress())
                    .title(new StringBuilder()
                    .append(restaurant.getId())
                    .append(".")
                    .append(restaurant.getName()).toString())
            );
        }
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

        toolbar.setTitle(getString(R.string.nearby_Restaurant));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

        mMap.setOnInfoWindowClickListener(marker -> {
            String id =marker.getTitle().substring(0 , marker.getTitle().indexOf("."));
            if(TextUtils.isEmpty(id)){
                Map<String , String> headers = new HashMap<>();
                headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
                compositeDisposable.add(myRestaurantAPI.getRestaurantById(headers , id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(restaurantByIdModel -> {
                            if(restaurantByIdModel.isSuccess()){
                                Common.currentRestaurant = restaurantByIdModel.getResult().get(0);
                                EventBus.getDefault().postSticky(new MenuItemEvent(true , Common.currentRestaurant ));
                                startActivity(new Intent(NearbyRestaurantActivity.this , MainActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(this, ""+restaurantByIdModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } , throwable -> {
                            Toast.makeText(this, "[GET RESTAURANT BY ID]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );
            }
        });
    }
}