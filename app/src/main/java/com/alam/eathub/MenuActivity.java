package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alam.eathub.Adapter.MyCategoryAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.Database.CartDataSource;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.LocalCartDataSource;
import com.alam.eathub.Model.EventBus.MenuItemEvent;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.alam.eathub.Utils.SpacesItemDecoration;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.img_restaurant)
    KenBurnsView img_restaurant;
    @BindView(R.id.recycler_category)
    RecyclerView recycle_category;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton btn_cart;
    @BindView(R.id.badge)
    NotificationBadge badge;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    MyCategoryAdapter adapter;

    CartDataSource cartDataSource;
    LayoutAnimationController layoutAnimationController;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();
        initView();

        countCartByRestaurant();
        loadFavoriteByRestaurant();
    }

    private void loadFavoriteByRestaurant() {


        Map<String , String> headers = new HashMap<>();
        headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
        compositeDisposable.add(myRestaurantAPI.getFavoriteByRestaurant(headers, Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteOnlyIdModel -> {
                    if(favoriteOnlyIdModel.isSuccess()){
                        if(favoriteOnlyIdModel.getResult() != null && favoriteOnlyIdModel.getResult().size() >0){
                            Common.currentFavOfRestaurant = favoriteOnlyIdModel.getResult();
                        }
                        else {
                            Common.currentFavOfRestaurant = new ArrayList<>();
                        }
                    }
                    else{
                       // Toast.makeText(this, "[GET FAVORITE BY ID]"+favoriteOnlyIdModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                        } ,
                        throwable -> {
                            Toast.makeText(this, "[GET FAVORITE BY ID]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartByRestaurant();
    }

    private void countCartByRestaurant() {

        Log.d("countCartByRestaurant" , "Started = "+Common.currentUser.getUserPhone()+" ; id ="+Common.currentRestaurant.getId());
        cartDataSource.countItemInCart(Common.currentUser.getFbid(),
                Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d("countCartByRestaurant" , "badged = "+integer);
                        badge.setText(String.valueOf(integer));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MenuActivity.this, "[COUNT CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    private void initView() {
        ButterKnife.bind(this);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(this , R.anim.layout_item_from_left);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this , CartListActivity.class));
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //this code will select item view type
        //if item is last, it will set full width on grid layout
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter != null){

                    switch (adapter.getItemViewType(position)){

                        case Common.DEFAULT_COLUMN_TYPE: return 1;
                        case Common.FULL_WIDTH_COLUMN: return 2;
                        default: return -1;
                    }
                }
                else
                    return -1;
            }
        });
        recycle_category.setLayoutManager(layoutManager);
        recycle_category.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Event Bus
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //since in MyAdapter we use postSticky ,so this event need set sticky too
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void loadMenuByRestaurant(MenuItemEvent event){
        if(event.isSuccess()){

            Log.d("loadMenuByRestaurant" , ""+event.getRestaurant().getName());
            Picasso.get().load(event.getRestaurant().getImage()).into(img_restaurant);
            toolbar.setTitle(event.getRestaurant().getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            //request Category by restaurant Id
            Map<String , String> headers = new HashMap<>();
            headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
            compositeDisposable.add(myRestaurantAPI.getCategories(headers,event.getRestaurant().getId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(menuModel -> {
                adapter = new MyCategoryAdapter(MenuActivity.this,menuModel.getResult());
                recycle_category.setAdapter(adapter);
                recycle_category.setLayoutAnimation(layoutAnimationController);
                },
                throwable -> {
                    Toast.makeText(this, "[GET CATEGORY]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                })
            );
        }
        else{
            dialog.dismiss();
            Toast.makeText(this, "[GET CATEGORY EVENT NOT LOAD]", Toast.LENGTH_SHORT).show();
        }
    }
}