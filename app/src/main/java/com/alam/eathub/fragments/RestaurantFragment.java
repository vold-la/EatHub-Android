package com.alam.eathub.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Adapter.MyAdapter;
import com.alam.eathub.Adapter.MyCuisineCheckboxAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.LocationActivity;
import com.alam.eathub.Model.EventBus.RestaurantLoadEvent;
import com.alam.eathub.Model.My.Rrestaurant;
import com.alam.eathub.R;
import com.alam.eathub.Retrofit.IMyy;
import com.alam.eathub.Retrofit.RC;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.nikartm.support.ImageBadgeView;


public class RestaurantFragment extends Fragment {


    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    JsonObject filters;
    JsonObject allFilters;
    LinearLayoutManager linearLayoutManager;
    private double lat, lng;
    List<Integer> b;
    private RecyclerView mRestaurantRecyclerView;
    private ImageBadgeView mImageBadgeView;
    MyCuisineCheckboxAdapter adapter;
    String flagSort;
    int flagRating , flagCost;
    private String[] fruitNames = {"Biryani","North Indian","South Indian","Cakes","Desserts","Burgers","Chinese","Rolls","Pizza"};
    private String biryaniImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fbiryani.jpg?alt=media&token=6eceb101-07c1-49ff-95a3-e540b1e0fb35";
    private String southIndianImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fsouth_indian.jpg?alt=media&token=e925ee1d-5855-484a-9928-9716025ecc43";
    private String northIndianImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fnorth_indian.jpg?alt=media&token=d24f26e0-071e-4eec-93f5-f8f063b0ad5a";
    private String rollsImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Frolls.jpg?alt=media&token=d52c1f03-0558-44a8-a7c3-90b8fda6d77f";
    private String burgersImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fburgers.jpg?alt=media&token=f6f2ca46-fc84-4ed6-84f2-2d199686f45e";
    private String pizzaImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fpizza.jpg?alt=media&token=3239cc9d-c2e6-4a8c-8a76-e8ee307ec72e";
    private String cakesImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fcake.jpeg?alt=media&token=1ded9af6-25a6-4deb-8b1a-0681d7a154d5";
    private String chineseImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fchinese.jpg?alt=media&token=b407830e-0e8d-47d2-8fd0-de43b34bb4d2";
    private String dessertsImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fdesserts.jpg?alt=media&token=209f4592-7b36-4768-9b71-f35d5c05ef74";
    private String[] fruitImages = {biryaniImg,northIndianImg,southIndianImg,cakesImg,dessertsImg,burgersImg,chineseImg,rollsImg,pizzaImg};

    IMyy myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;
    TextView cityTitle;
    Button sort , cuisines , cost , ratings;

    LayoutAnimationController layoutAnimationController;

   // @BindView(R.id.recycler_restaurant)
   // RecyclerView recycler_restaurant;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        init(view , container);
        checkCityIdAndLoad();

        return view;
    }

    private void init(View view , ViewGroup cont) {

        dialog = new SpotsDialog.Builder().setContext(requireActivity()).setCancelable(false).build();
        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);
      //  Slider.init(new PicassoImageLoadingService());

        LinearLayout mAddressContainer = view.findViewById(R.id.addressContainer);

        sort = view.findViewById(R.id.sort);
        cuisines = view.findViewById(R.id.cuisines);
        ratings = view.findViewById(R.id.ratings);
        cost = view.findViewById(R.id.cost);

        TextView searchRestaurant  = view.findViewById(R.id.searchRestaurant);

        searchRestaurant.setOnClickListener(view1 ->{
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new SearchFragment())
                    .addToBackStack(null)
                    .commit();
        });

        filters = new JsonObject();
        allFilters = new JsonObject();

        GridView mCuisineFoodView = view.findViewById(R.id.cuisineGridView);
        CuisineImageAdapter adapter = new CuisineImageAdapter();
        mCuisineFoodView.setAdapter(adapter);
        mAddressContainer.setOnClickListener(view1 -> {
          /*
          for Mapbox auto complete
            PlaceOptions placeOptions = PlaceOptions.builder()
                    .backgroundColor(Color.parseColor("#FFFFFF"))
                    .limit(10)
                    .country("IN")
                    .build();

            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Common.MAPBOX_API_KEY)
                    .placeOptions(placeOptions)
                    .build(getActivity());
            startActivityForResult(intent, Common.REQUEST_CODE_AUTOCOMPLETE);
*/
            Intent intent = new Intent(getActivity(), LocationActivity.class);
            startActivity(intent);
        });
        Toolbar mToolbar = view.findViewById(R.id.customToolBar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        cityTitle = view.findViewById(R.id.userLocation);
        mRestaurantRecyclerView = view.findViewById(R.id.recycler_restaurant);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        mRestaurantRecyclerView.setLayoutManager(linearLayoutManager);
        mRestaurantRecyclerView.setHasFixedSize(true);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(requireActivity() , R.anim.layout_item_from_left);
        mImageBadgeView = view.findViewById(R.id.imageBadgeView);

        mImageBadgeView.setOnClickListener(view1 -> Toast.makeText(getActivity(), "Multiple Cart Coming Soon", Toast.LENGTH_SHORT).show());

        sort.setOnClickListener(view1 -> {
            showBottomSheetDialog(1,view , "Sort By" , getLayoutInflater().inflate(R.layout.layout_bottomsheet_sort , cont , false));
        });
        cuisines.setOnClickListener(view1 -> {
            showBottomSheetDialog(2,view , "Cuisines" , getLayoutInflater().inflate(R.layout.layout_bottomsheet_cuisines , cont , false));
        });
        cost.setOnClickListener(view1 -> {
            showBottomSheetDialog(3,view , "Cost Per Person" , getLayoutInflater().inflate(R.layout.layout_bottomsheet_cost , cont , false));
        });
        ratings.setOnClickListener(view1 -> {
            showBottomSheetDialog(4,view , "Rating" , getLayoutInflater().inflate(R.layout.layout_bottomsheet_ratings, cont , false));
        });

    }

    private void showBottomSheetDialog(int i , View view , String title , View currentFilterView) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.layout_bottomsheet_allfilters);

        Button apply = bottomSheetDialog.findViewById(R.id.apply_filter);
        Button clear = bottomSheetDialog.findViewById(R.id.clear_filter);
        TextView filter_title = bottomSheetDialog.findViewById(R.id.filter_title);
        ImageView close = bottomSheetDialog.findViewById(R.id.close_filter);
        LinearLayout filterContainer = bottomSheetDialog.findViewById(R.id.filterContainer);

        filterContainer.addView(currentFilterView);
        filter_title.setText(title);
        //allFilters.addProperty("sort" , "popularity");
        switch (i){
            case 1:
                initSort(currentFilterView);
            break;
            case 2:
                initCuisines(currentFilterView);
            break;
            case 3:
                initCost(currentFilterView);
            break;
            case 4:
                initRatings(currentFilterView);
            break;
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter != null) {
                    List<String> cuisineNames = adapter.getCheckedName();
                    b = adapter.getCheckedIndex();
                    JsonArray n = new JsonArray();
                    if (cuisineNames != null) {
                        for (int i = 0; i < cuisineNames.size(); i++)
                            n.add(cuisineNames.get(i));

                        filters.add("cuisines", n);
                        allFilters.add("filters", filters);
                        Log.e("ress  :" , String.valueOf(n));
                        Common.setFILTERS(filters);
                        Common.setALL_FILTERS(allFilters);
                    }
                }
                if(flagCost != 0 )
                    Common.COST = flagCost;
                if(flagRating != 0)
                    Common.RATINGS = flagRating;
                if(flagSort != null)
                    Common.SORT = flagSort;

                bottomSheetDialog.dismiss();
                checkCityIdAndLoad();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.COST=0;
                Common.RATINGS=0;
                Common.SORT="popularity";
                Common.setALL_FILTERS(new JsonObject());
                Common.setFILTERS(new JsonObject());
                bottomSheetDialog.dismiss();
                checkCityIdAndLoad();
            }
        });

        bottomSheetDialog.show();
    }

    private void initSort(View v){
        RadioGroup rdi_sort = v.findViewById(R.id.rdi_sort);

        if(rdi_sort != null) {
            if(Common.SORT != null){
                if(Common.SORT.equals("popularity"))
                    rdi_sort.check(R.id.popularity);
                else if(Common.SORT.equals("rating_desc"))
                    rdi_sort.check(R.id.rating_desc);
                else if(Common.SORT.equals("delivery"))
                    rdi_sort.check(R.id.delivery);
                else if(Common.SORT.equals("cost_asc"))
                    rdi_sort.check(R.id.cost_asc);
                else if(Common.SORT.equals("cost_desc"))
                    rdi_sort.check(R.id.cost_desc);
            }
            rdi_sort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    flagSort = getResources().getResourceEntryName(checkedId);
                }
            });
        }
    }

    private void initCuisines(View v){
        RecyclerView mCuisineRecycler = v.findViewById(R.id.searchCuisineRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mCuisineRecycler.setLayoutManager(linearLayoutManager);
         adapter = new MyCuisineCheckboxAdapter(getContext() ,b);
        mCuisineRecycler.setAdapter(adapter);
    }

    private void initRatings(View v){
        SeekBar seekBar = v.findViewById(R.id.slider);

        if(Common.RATINGS != 0)
            seekBar.setProgress(Common.RATINGS);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                flagRating = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initCost(View v){

        SeekBar seekBar = v.findViewById(R.id.range_slider);

        if(Common.COST != 0)
            seekBar.setProgress(Common.COST);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                flagCost = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public  class CuisineImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return fruitImages.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view1 = getLayoutInflater().inflate(R.layout.custom_cuisine_layout,null);
            //getting view in row_data
            TextView name = view1.findViewById(R.id.cuisineName);
            ImageView image = view1.findViewById(R.id.cuisineImage);

            name.setText(fruitNames[i]);
            Glide.with(requireContext())
                    .load(fruitImages[i])
                    .apply(new RequestOptions()
                            .override(200,200))
                    .into(image);
            return view1;
        }
    }

    private void checkCityIdAndLoad(){

        if(Common.CITY_ID==0) {
            buildLocationRequest();
            buildLocationCallback();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
        else {
            JsonObject jsonObject = Common.getALL_FILTERS();
            JsonObject filterObject = Common.getFILTERS();
            filterObject.addProperty("location.city_id" , Common.CITY_ID);
            if(Common.RATINGS != 0) {
                JsonObject userRating = new JsonObject();
                userRating.addProperty("aggregate_rating" , String.valueOf(Common.COST));
                filterObject.add("user_rating", userRating);
            }
            if(Common.COST !=0)
            {
                JsonObject avg_cost = new JsonObject();
                avg_cost.addProperty("$lte" , Common.COST);
                avg_cost.addProperty("$gte" , 0); // todo give lower bound
                filterObject.add("average_cost_for_two" , avg_cost);
            }

            jsonObject.add("filters" , filterObject);
            if(Common.SORT != null)
                jsonObject.addProperty("sort" , Common.SORT);

            loadRestaurant(jsonObject);
        }

    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                lng = locationResult.getLastLocation().getLongitude();
                lat = locationResult.getLastLocation().getLatitude();

                Log.e("res la : " , lat +" == "+lng);

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("long", locationResult.getLastLocation().getLongitude());
                jsonObject.addProperty("lat", locationResult.getLastLocation().getLatitude());

                compositeDisposable.add(myRestaurantAPI.getCityId(jsonObject)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(cityId -> {
                            Common.CITY_TITLE = cityId.getTitle();
                            Common.CITY_ID = Integer.parseInt(cityId.getCityId());
                            JsonObject jsonObject1 = new JsonObject();
                            JsonObject filterObject = new JsonObject();
                            filterObject.addProperty("location.city_id" , Common.CITY_ID);
                            jsonObject1.add("filters" , filterObject);

                            loadRestaurant(jsonObject1);

                        })
                );

            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setNumUpdates(1);
    }

    private void loadRestaurant(JsonObject jsonObject) {
        dialog.show();
        cityTitle.setText(Common.CITY_TITLE);
        compositeDisposable.add(myRestaurantAPI.getMyRestaurant(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myyRestaurant -> {
                            //we wiil use EventBus to send local set adapter and slider event
                            EventBus.getDefault().post(new RestaurantLoadEvent(true,myyRestaurant.getRestaurant()));
                        } ,
                        throwable -> {
                            EventBus.getDefault().post(new RestaurantLoadEvent(false,throwable.getMessage()));
                        }
                )
        );

    }

    // REGISTER EVENT BUS

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    //Listen Event Bus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processRestaurantLoadEvent(RestaurantLoadEvent event){
        if(event.getRestaurantList().size() > 0){
            displayRestaurant(event.getRestaurantList());
        }
        else{
            Toast.makeText(requireActivity(), "[RESTAURANT LOoooAD]  "+event.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    private void displayRestaurant(List<Rrestaurant> restaurantList) {

        MyAdapter adapter = new MyAdapter(requireActivity(),restaurantList);
        mRestaurantRecyclerView.setAdapter(adapter);
        mRestaurantRecyclerView.setLayoutAnimation(layoutAnimationController);
    }
}
