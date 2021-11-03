package com.alam.eathub.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Adapter.MyAdapter;
import com.alam.eathub.Adapter.MyCuisineCheckboxAdapter;
import com.alam.eathub.CollectionsActivity;
import com.alam.eathub.Common.Common;
import com.alam.eathub.Model.EventBus.CollectionsRestaurant;
import com.alam.eathub.Model.EventBus.MyMenuEvent;
import com.alam.eathub.Model.EventBus.RestaurantLoadEvent;
import com.alam.eathub.Model.My.CityCollection.Collection;
import com.alam.eathub.Model.My.CityCollection.Collections;
import com.alam.eathub.Model.My.Rrestaurant;
import com.alam.eathub.OrderHistoryActivity;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.nikartm.support.ImageBadgeView;


public class RestaurantFragment extends Fragment {


    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    JsonObject filters;
    JsonObject allFilters;
    LinearLayoutManager linearLayoutManager ,linearLayoutManager1 ;
    private double lat, lng;
    List<Integer> b;
    private RecyclerView mRestaurantRecyclerView;
    private ImageBadgeView mImageBadgeView;
    MyCuisineCheckboxAdapter adapter;
    RecyclerView mTopSliderView;
    String flagSort;
    int flagRating, flagCost;
    Collections mCityCollection;
    List<Rrestaurant> restaurantList;
    private String[] fruitNames = {"Biryani", "North Indian", "South Indian", "Cakes", "Desserts", "Burgers", "Chinese", "Rolls", "Pizza"};
    private String biryaniImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fbiryani.jpg?alt=media&token=6eceb101-07c1-49ff-95a3-e540b1e0fb35";
    private String southIndianImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fsouth_indian.jpg?alt=media&token=e925ee1d-5855-484a-9928-9716025ecc43";
    private String northIndianImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fnorth_indian.jpg?alt=media&token=d24f26e0-071e-4eec-93f5-f8f063b0ad5a";
    private String rollsImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Frolls.jpg?alt=media&token=d52c1f03-0558-44a8-a7c3-90b8fda6d77f";
    private String burgersImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fburgers.jpg?alt=media&token=f6f2ca46-fc84-4ed6-84f2-2d199686f45e";
    private String pizzaImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fpizza.jpg?alt=media&token=3239cc9d-c2e6-4a8c-8a76-e8ee307ec72e";
    private String cakesImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fcake.jpeg?alt=media&token=1ded9af6-25a6-4deb-8b1a-0681d7a154d5";
    private String chineseImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fchinese.jpg?alt=media&token=b407830e-0e8d-47d2-8fd0-de43b34bb4d2";
    private String dessertsImg = "https://firebasestorage.googleapis.com/v0/b/munche-be7a5.appspot.com/o/cuisine_images%2Fdesserts.jpg?alt=media&token=209f4592-7b36-4768-9b71-f35d5c05ef74";
    private String[] fruitImages = {biryaniImg, northIndianImg, southIndianImg, cakesImg, dessertsImg, burgersImg, chineseImg, rollsImg, pizzaImg};

    IMyy myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;
    TextView cityTitle , topSlider;
    Button sort, cuisines, cost, ratings;

    LayoutAnimationController layoutAnimationController;

    // @BindView(R.id.recycler_restaurant)
    // RecyclerView recycler_restaurant;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        if (fusedLocationProviderClient != null || locationCallback != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        init(view, container);
        checkCityIdAndLoad();

        return view;
    }

    private void init(View view, ViewGroup cont) {

        dialog = new SpotsDialog.Builder().setContext(requireActivity()).setCancelable(false).build();
        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);
        //  Slider.init(new PicassoImageLoadingService());

        LinearLayout mAddressContainer = view.findViewById(R.id.addressContainer);

        sort = view.findViewById(R.id.sort);
        cuisines = view.findViewById(R.id.cuisines);
        ratings = view.findViewById(R.id.ratings);
        cost = view.findViewById(R.id.cost);

        TextView searchRestaurant = view.findViewById(R.id.searchRestaurant);
        topSlider = view.findViewById(R.id.txt_top_slider);
        mTopSliderView = view.findViewById(R.id.cuisineGridView);


        if(Common.RESTAURANT_FRAGMENT_VIEW == 0 ){
            topSlider.setText(R.string.topSliderDelivery);
        }
        else {

            topSlider.setText(R.string.topSliderDineOut);
        }

        searchRestaurant.setOnClickListener(view1 -> {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new SearchFragment())
                    .addToBackStack(null)
                    .commit();
        });

        filters = new JsonObject();
        allFilters = new JsonObject();

        mAddressContainer.setOnClickListener(view1 -> {
            //          for Mapbox auto complete
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
        });

        Toolbar mToolbar = view.findViewById(R.id.customToolBar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        cityTitle = view.findViewById(R.id.userLocation);
        mRestaurantRecyclerView = view.findViewById(R.id.recycler_restaurant);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        mRestaurantRecyclerView.setLayoutManager(linearLayoutManager);
        mRestaurantRecyclerView.setHasFixedSize(true);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(requireActivity(), R.anim.layout_item_from_left);
        mImageBadgeView = view.findViewById(R.id.imageBadgeView);
        linearLayoutManager1 = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);

        mImageBadgeView.setOnClickListener(view1 -> Toast.makeText(getActivity(), "Multiple Cart Coming Soon", Toast.LENGTH_SHORT).show());

        if (Common.RESTAURANT_FRAGMENT_VIEW == 0) {
            TopSliderAdapter adapter = new TopSliderAdapter(this.getContext());
            mTopSliderView.setLayoutManager(linearLayoutManager1);
            mTopSliderView.setAdapter(adapter);
        }

        sort.setOnClickListener(view1 -> {
            showBottomSheetDialog(1, view, "Sort By", getLayoutInflater().inflate(R.layout.layout_bottomsheet_sort, cont, false));
        });
        cuisines.setOnClickListener(view1 -> {
            showBottomSheetDialog(2, view, "Cuisines", getLayoutInflater().inflate(R.layout.layout_bottomsheet_cuisines, cont, false));
        });
        cost.setOnClickListener(view1 -> {
            showBottomSheetDialog(3, view, "Cost Per Person", getLayoutInflater().inflate(R.layout.layout_bottomsheet_cost, cont, false));
        });
        ratings.setOnClickListener(view1 -> {
            showBottomSheetDialog(4, view, "Rating", getLayoutInflater().inflate(R.layout.layout_bottomsheet_ratings, cont, false));
        });

    }

    private void showBottomSheetDialog(int i, View view, String title, View currentFilterView) {

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
        switch (i) {
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
                if (adapter != null) {
                    List<String> cuisineNames = adapter.getCheckedName();
                    b = adapter.getCheckedIndex();
                    JsonArray n = new JsonArray();
                    if (cuisineNames != null) {
                        for (int i = 0; i < cuisineNames.size(); i++)
                            n.add(cuisineNames.get(i));

                        filters.add("cuisines", n);
                        allFilters.add("filters", filters);
//                        Log.e("ress  :", String.valueOf(n));
                        Common.setFILTERS(filters);
                        Common.setALL_FILTERS(allFilters);
                    }
                }
                if (flagCost != 0)
                    Common.COST = flagCost;
                if (flagRating != 0)
                    Common.RATINGS = flagRating;
                if (flagSort != null)
                    Common.SORT = flagSort;

                bottomSheetDialog.dismiss();
                checkCityIdAndLoad();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFilter();
                bottomSheetDialog.dismiss();
                checkCityIdAndLoad();
            }
        });

        bottomSheetDialog.show();
    }

    private void clearAllFilter(){
        Common.COST = 0;
        Common.RATINGS = 0;
        Common.SORT = "popularity";
        Common.setALL_FILTERS(new JsonObject());
        Common.setFILTERS(new JsonObject());
    }

    private void initSort(View v) {
        RadioGroup rdi_sort = v.findViewById(R.id.rdi_sort);

        if (rdi_sort != null) {
            if (Common.SORT != null) {
                if (Common.SORT.equals("popularity"))
                    rdi_sort.check(R.id.popularity);
                else if (Common.SORT.equals("rating_desc"))
                    rdi_sort.check(R.id.rating_desc);
                else if (Common.SORT.equals("delivery"))
                    rdi_sort.check(R.id.delivery);
                else if (Common.SORT.equals("cost_asc"))
                    rdi_sort.check(R.id.cost_asc);
                else if (Common.SORT.equals("cost_desc"))
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

    private void initCuisines(View v) {
        RecyclerView mCuisineRecycler = v.findViewById(R.id.searchCuisineRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mCuisineRecycler.setLayoutManager(linearLayoutManager);
        EditText searchCuisine  = v.findViewById(R.id.searchCuisine);

        adapter = new MyCuisineCheckboxAdapter(getContext(), b , null);
        mCuisineRecycler.setAdapter(adapter);

        searchCuisine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("res srch :" , s.toString());
                adapter = new MyCuisineCheckboxAdapter(getContext(), b , s.toString());
                mCuisineRecycler.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void initRatings(View v) {
        SeekBar seekBar = v.findViewById(R.id.slider);

        if (Common.RATINGS != 0)
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

    private void initCost(View v) {

        SeekBar seekBar = v.findViewById(R.id.range_slider);

        if (Common.COST != 0)
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

    public class TopSliderAdapter extends RecyclerView.Adapter<TopSliderAdapter.MyViewHolder> {

        Context context;

        public TopSliderAdapter(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_cuisine_layout, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (Common.RESTAURANT_FRAGMENT_VIEW == 0) {
                holder.name.setText(fruitNames[position]);
                Glide.with(requireContext())
                        .load(fruitImages[position])
                        .apply(new RequestOptions()
                                .override(200, 200))
                        .into(holder.image);

                holder.image.setOnClickListener(view -> {
                   JsonObject jsonObject =  Common.getFILTERS();
                   clearAllFilter();

                   JsonArray jsonArray = new JsonArray();
                   jsonArray.add(fruitNames[position]);
                   jsonObject.add("cuisines" , jsonArray);
                   Common.setFILTERS(jsonObject);
                   checkCityIdAndLoad();
                });

            } else {
                String title = mCityCollection.getCollections().get(position).getCollection().getTitle();
                int textLength = title.length();
                if(textLength > 14)
                    title = title.substring(0,12)+"...";
                title += "(" + mCityCollection.getCollections().get(position).getCollection().getResCount() + ")";

                holder.name.setText(title);
                Glide.with(requireContext())
                        .load(mCityCollection.getCollections().get(position).getCollection().getImageUrl())
                        .apply(new RequestOptions()
                                .override(200, 200))
                        .into(holder.image);

                holder.image.setOnClickListener(view -> {
                    List<Rrestaurant> restaurantCollections  = new ArrayList<>();
                    for(Rrestaurant restaurant : restaurantList){
                        for(Integer id :restaurant.getCollectionId()){
                            if(mCityCollection.getCollections().get(position).getCollection().getCollectionId().equals(id)){
                                restaurantCollections.add(restaurant);
                                break;
                            }
                        }
                    }
                    EventBus.getDefault()
                            .postSticky(new CollectionsRestaurant(restaurantCollections ,
                                    mCityCollection.getCollections().get(position).getCollection().getTitle(),
                                    mCityCollection.getCollections().get(position).getCollection().getDescription(),
                                    mCityCollection.getCollections().get(position).getCollection().getResCount(),
                                    mCityCollection.getCollections().get(position).getCollection().getImageUrl()
                                    ));
                    Intent intent = new Intent(getActivity(), CollectionsActivity.class);
                    startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            if(Common.RESTAURANT_FRAGMENT_VIEW==0)
                return fruitImages.length;
            else
                return mCityCollection.getCollections().size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.cuisineName)
            TextView name;
            @BindView(R.id.cuisineImage)
            ImageView image;

            Unbinder unbinder;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                unbinder = ButterKnife.bind(this, itemView);
            }
        }
    }

        private void checkCityIdAndLoad() {

            if (Common.CITY_ID == 0) {
                buildLocationRequest();
                buildLocationCallback();

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            } else {
                JsonObject jsonObject = Common.getALL_FILTERS();
                JsonObject filterObject = Common.getFILTERS();
                filterObject.addProperty("location.city_id", Common.CITY_ID);
                if (Common.RATINGS != 0) {
                    JsonObject userRating = new JsonObject();
                    userRating.addProperty("aggregate_rating", String.valueOf(Common.COST));
                    filterObject.add("user_rating", userRating);
                }
                if (Common.COST != 0) {
                    JsonObject avg_cost = new JsonObject();
                    avg_cost.addProperty("$lte", Common.COST);
                    avg_cost.addProperty("$gte", 0); // todo give lower bound
                    filterObject.add("average_cost_for_two", avg_cost);
                }

                jsonObject.add("filters", filterObject);
                if (Common.SORT != null)
                    jsonObject.addProperty("sort", Common.SORT);

                loadRestaurant(jsonObject);
            }

        }

        private void getCityIdFromLocation(double lat, double lng) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("long", lng);
            jsonObject.addProperty("lat", lat);

            compositeDisposable.add(myRestaurantAPI.getCityId(jsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cityId -> {
                        Common.CITY_TITLE = cityId.getTitle();
                        Common.CITY_ID = Integer.parseInt(cityId.getCityId());
                        JsonObject jsonObject1 = new JsonObject();
                        JsonObject filterObject = new JsonObject();
                        filterObject.addProperty("location.city_id", Common.CITY_ID);
                        jsonObject1.add("filters", filterObject);

                        loadRestaurant(jsonObject1);

                    })
            );
        }

        private void buildLocationCallback() {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    lng = locationResult.getLastLocation().getLongitude();
                    lat = locationResult.getLastLocation().getLatitude();

                    getCityIdFromLocation(lat, lng);

                    Log.e("res la : ", lat + " == " + lng);
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

            if(Common.RESTAURANT_FRAGMENT_VIEW == 0 ) {
                jsonObject.remove("has_table_booking");
                jsonObject.addProperty("has_online_delivery", 1);
            }
            else {
                jsonObject.remove("has_online_delivery");
                jsonObject.addProperty("has_table_booking", 1);
            }

            Log.e("res call : " , String.valueOf(jsonObject));

            compositeDisposable.add(myRestaurantAPI.getMyRestaurant(jsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(myyRestaurant -> {
                                //we wiil use EventBus to send local set adapter and slider event
                                if (Common.RESTAURANT_FRAGMENT_VIEW == 1)
                                    loadCityCollection();

                                EventBus.getDefault().post(new RestaurantLoadEvent(true, myyRestaurant.getRestaurant()));
                            },
                            throwable -> {
                                EventBus.getDefault().post(new RestaurantLoadEvent(false, throwable.getMessage()));
                            }
                    )
            );

        }

        private void loadCityCollection() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("city_id", Common.CITY_ID);
            compositeDisposable.add(myRestaurantAPI.getCityCollection(jsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(collections -> {
                        mCityCollection = collections;
                        TopSliderAdapter adapter = new TopSliderAdapter(this.getContext());
                        mTopSliderView.setLayoutManager(linearLayoutManager1);
                        mTopSliderView.setAdapter(adapter);
                    })
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

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            //  Log.e("ressss2 : ", String.valueOf(requestCode));
            if (resultCode == Activity.RESULT_OK && requestCode == Common.REQUEST_CODE_AUTOCOMPLETE) {
                CarmenFeature feature = PlaceAutocomplete.getPlace(data);
                getCityIdFromLocation(feature.center().latitude(), feature.center().longitude());
                //  Log.e("ressss2 : ", String.valueOf(feature.center()));

            }
        }

        //Listen Event Bus
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void processRestaurantLoadEvent(RestaurantLoadEvent event) {
            if (event.getRestaurantList().size() > 0) {
                displayRestaurant(event.getRestaurantList());
            } else {
                Toast.makeText(requireActivity(), "[RESTAURANT LOoooAD]  " + event.getMessage(), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }

        private void displayRestaurant(List<Rrestaurant> restaurantList) {

            if(Common.RESTAURANT_FRAGMENT_VIEW == 1){
                this.restaurantList = restaurantList;
            }

            MyAdapter adapter = new MyAdapter(requireActivity(), restaurantList);
            mRestaurantRecyclerView.setAdapter(adapter);
            mRestaurantRecyclerView.setLayoutAnimation(layoutAnimationController);
        }
}