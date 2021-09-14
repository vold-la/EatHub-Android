package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.alam.eathub.Adapter.MyAddonAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.Database.CartDataSource;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.CartItem;
import com.alam.eathub.Database.LocalCartDataSource;
import com.alam.eathub.Model.EventBus.AddOnEventChange;
import com.alam.eathub.Model.EventBus.AddonLoadEvent;
import com.alam.eathub.Model.EventBus.FoodDetailEvent;
import com.alam.eathub.Model.EventBus.SizeLoadEvent;
import com.alam.eathub.Model.Food;
import com.alam.eathub.Model.Size;
import com.alam.eathub.Model.SizeModel;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity {

    @BindView(R.id.fab_add_to_cart)
    FloatingActionButton fab_add_to_cart;
    @BindView(R.id.btn_view_cart)
    Button btn_view_cart;
    @BindView(R.id.txt_money)
    TextView txt_money;
    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;
    @BindView(R.id.recycler_addon)
    RecyclerView recycler_addon;
    @BindView(R.id.txt_description)
    TextView txt_description;
    @BindView(R.id.img_food_detail)
    KenBurnsView img_food_detail;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    CartDataSource cartDataSource;
    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    Food selectedFood;
    Double originalPrice;
    private double sizePrice =0.0;
    private String sizeSelected;
    private double addonPrice =0.0;
    private double extraPrice;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        fab_add_to_cart.setOnClickListener(view ->{
            CartItem cartItem = new CartItem();
            cartItem.setFoodId(selectedFood.getId());
            cartItem.setFoodName(selectedFood.getName());
            cartItem.setFoodPrice(selectedFood.getPrice());
            cartItem.setFoodImage(selectedFood.getImage());
            cartItem.setFoodQuantity(1);
            cartItem.setUserPhone(Common.currentUser.getUserPhone());
            cartItem.setRestaurantId(Common.currentRestaurant.getId());
            cartItem.setFoodAddon(new Gson().toJson(Common.addonList));
            cartItem.setFoodSize(sizeSelected);
            cartItem.setFoodExtraPrice(extraPrice);
            cartItem.setFbid(Common.currentUser.getFbid());

            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {

                                Toast.makeText(FoodDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                            },
                            throwable -> {
                                Toast.makeText(FoodDetailActivity.this, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );
        });

        btn_view_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodDetailActivity.this , CartListActivity.class));

            }
        });

    }


    private void init() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Event Bus
    @Override
    protected void onStop() {
        Log.d("Fooddetail" , "stopped");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Fooddetail" , "started");
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true , threadMode = ThreadMode.MAIN)
    public void displayFoodDetail(FoodDetailEvent event) {

        if(event.isSuccess()){
        toolbar.setTitle(event.getFood().getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedFood = event.getFood();
        originalPrice = event.getFood().getPrice();
        txt_money.setText(String.valueOf(originalPrice));
        txt_description.setText(event.getFood().getDescription());
        Picasso.get().load(event.getFood().getImage()).into(img_food_detail);

        if (event.getFood().isSize() && event.getFood().isAddon()) {

            Log.d("fd" , "if executed");
            //load addon & size from server
            dialog.show();
            Map<String , String> headers = new HashMap<>();
            headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
            compositeDisposable.add(myRestaurantAPI.getSizeOfFood(headers, event.getFood().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sizeModel -> {
                                Log.d("fd" , "before sizemodel");
                                //send local event bus
                                EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));

                                Log.d("fd" , "after sizemodel");
                                //load addon after load size
                                //dialog.show();
                                compositeDisposable.add(myRestaurantAPI.getAddonOfFood(headers, event.getFood().getId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(addonModel -> {

                                                    Log.d("fd" , "before addonmodel");
                                                    dialog.dismiss();
                                                    EventBus.getDefault().post(new AddonLoadEvent(true, addonModel.getResult()));

                                                    Log.d("fd" , "after addonmodel");
                                                },
                                                throwable -> {
                                                    dialog.dismiss();
                                                    Toast.makeText(this, "[LOAD ADDON]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                })
                                );
                            }
                            , throwable -> {
                                dialog.dismiss();
                                Toast.makeText(this, "[load size]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );
        } else {
            Log.d("fd" , "else executed");
            if (event.getFood().isSize()) {

                Log.d("fd" , "size executed");
                dialog.show();
                Map<String , String> headers = new HashMap<>();
                headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
                compositeDisposable.add(myRestaurantAPI.getSizeOfFood(headers, event.getFood().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sizeModel -> {
                                    //send local event bus
                                    dialog.dismiss();
                                    EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));

                                    Log.d("fd" , "got size ");
                                }
                                , throwable -> {
                                    dialog.dismiss();
                                    Toast.makeText(this, "[load size]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                );
            }
            else if (event.getFood().isAddon()) {
                Log.d("fd" , "addon executed");

                //load addon after load size
                dialog.show();
                Map<String , String> headers = new HashMap<>();
                headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
                compositeDisposable.add(myRestaurantAPI.getAddonOfFood(headers, event.getFood().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(addonModel -> {
                                    dialog.dismiss();
                                    Log.d("fd" , "got addon");
                                    EventBus.getDefault().post(new AddonLoadEvent(true, addonModel.getResult()));
                                },
                                throwable -> {
                                    dialog.dismiss();
                                    Toast.makeText(this, "[LOAD ADDON]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                );
            }
        }
    }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displaySize(SizeLoadEvent event){
        if(event.isSuccess()){
            //create radio button based on length
            for(Size size : event.getSizeList()){
                RadioButton radioButton = new RadioButton(this);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                            sizePrice = size.getExtraPrice();

                        calculatePrice();
                        sizeSelected = size.getDescription();
                    }
                });

                LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1.0f);
                radioButton.setLayoutParams(params);
                radioButton.setText(size.getDescription());
                radioButton.setTag(size.getExtraPrice());

                rdi_group_size.addView(radioButton);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayAddon(AddonLoadEvent event){

        if(event.isSuccess()){
            recycler_addon.setHasFixedSize(true);
            recycler_addon.setLayoutManager(new LinearLayoutManager(this));
            recycler_addon.setAdapter(new MyAddonAdapter(FoodDetailActivity.this , event.getAddonList()));
        }
    }

    @Subscribe(sticky = true , threadMode = ThreadMode.MAIN)
    public void priceChange(AddOnEventChange eventChange){
        if(eventChange.isAdd())
            addonPrice += eventChange.getAddon().getExtraPrice();
        else
            addonPrice -=eventChange.getAddon().getExtraPrice();

        calculatePrice();
    }

    private void calculatePrice() {

        extraPrice = 0.0;
        double newPrice;

        extraPrice += sizePrice;
        extraPrice += addonPrice;
        newPrice = originalPrice + extraPrice;
        txt_money.setText(String.valueOf(newPrice));
    }
}