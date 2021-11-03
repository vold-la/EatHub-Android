package com.alam.eathub;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Retrofit.IMyy;
import com.alam.eathub.Retrofit.RC;
import com.alam.eathub.Retrofit.Services.PicassoImageLoadingService;
import com.alam.eathub.fragments.MyProfileFragment;
import com.alam.eathub.fragments.RestaurantFragment;
import com.alam.eathub.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;
import ss.com.bannerslider.Slider;

public class HomeActivity extends AppCompatActivity  {

    private BottomNavigationView bottomNav;
    IMyy myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        changestatusbarcolor();
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new RestaurantFragment())
                .commit();


        init();
       // loadRestaurant();

    }


    private void changestatusbarcolor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_restaurants:
                        Common.RESTAURANT_FRAGMENT_VIEW= 0;
                        selectedFragment = new RestaurantFragment();
                        break;
                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.nav_dineout:
                        Common.RESTAURANT_FRAGMENT_VIEW= 1;
                        selectedFragment = new RestaurantFragment();
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new MyProfileFragment();
                        break;
                    case R.id.imageBadgeView:
                        bottomNav.setVisibility(View.GONE);
                        break;
                    default:
                        selectedFragment = new RestaurantFragment();

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        Objects.requireNonNull(selectedFragment)).commit();
                return true;
            };

/*
    private void loadRestaurant() {
        dialog.show();

        Map<String , String> headers = new HashMap<>();
        headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
        compositeDisposable.add(myRestaurantAPI.getRestaurant(headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantModel -> {
                            //we wiil use EventBus to send local set adapter and slider event
                            EventBus.getDefault().post(new RestaurantLoadEvent(true,restaurantModel.getResult()));
                        } ,
                        throwable -> {
                            EventBus.getDefault().post(new RestaurantLoadEvent(false,throwable.getMessage()));
                        }
                )
        );

    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

   // @Override
    //public boolean onSupportNavigateUp() {
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //return NavigationUI.navigateUp(navController, mAppBarConfiguration)
           //     || super.onSupportNavigateUp();
   // }
/*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        Log.d("onNavigationItemSelect" , "== "+id);
        if(id == R.id.nav_logout){
            SignOut();
        }
        else if(id == R.id.nav_nearby){
            startActivity(new Intent(HomeActivity.this , NearbyRestaurantActivity.class));
        }
        else if(id == R.id.nav_order_history){
            startActivity(new Intent(HomeActivity.this , ViewOrderActivityWithPagination.class));
        }
        else if(id == R.id.nav_update_info){
            startActivity(new Intent(HomeActivity.this , UpdateInfoActivity.class));
        }
        else if(id == R.id.nav_fav){
            startActivity(new Intent(HomeActivity.this , FavoriteActivity.class));
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
*/

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);

        Slider.init(new PicassoImageLoadingService());
    }

    // REGISTER EVENT BUS

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Log.e("res : ", String.valueOf(requestCode));
        if (resultCode == Activity.RESULT_OK && requestCode == Common.REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
         //   Log.e("res : ", String.valueOf(feature.center()));
           // Toast.makeText(this, "ok", Toast.LENGTH_LONG).show();
        }
        else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment f : fragments) {
                if (f instanceof MyProfileFragment) {
                    f.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}