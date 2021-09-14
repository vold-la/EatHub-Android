package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.alam.eathub.Adapter.MyCategoryAdapter;
import com.alam.eathub.Adapter.MyFoodAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.Model.Category;
import com.alam.eathub.Model.EventBus.FoodListEvent;
import com.alam.eathub.Model.EventBus.MenuItemEvent;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public class FoodListActivity extends AppCompatActivity {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    MyFoodAdapter adapter , searchAdapter;

    @BindView(R.id.img_category)
    KenBurnsView img_category;
    @BindView(R.id.recycler_food_list)
    RecyclerView recycle_food_list;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Category selectedCategory;
    LayoutAnimationController layoutAnimationController;


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        if(adapter != null)
            adapter.onStop();

        if(searchAdapter != null)
            searchAdapter.onStop();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearchFood(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //restore to original adapter when use close search
                recycle_food_list.setAdapter(adapter);
                return true;
            }
        });
        return  true;
    }

    private void startSearchFood(String query) {
        dialog.show();
        Map<String , String> headers = new HashMap<>();
        headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
        compositeDisposable.add(myRestaurantAPI.searchFood(headers, query , selectedCategory.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(foodModel -> {
                                if(foodModel.isSuccess()){
                                    searchAdapter = new MyFoodAdapter(FoodListActivity.this , foodModel.getResult());
                                    recycle_food_list.setAdapter(searchAdapter);
                                    recycle_food_list.setLayoutAnimation(layoutAnimationController);
                                }
                                else{
                                    if(foodModel.getMessage().contains("Empty")) {
                                        recycle_food_list.setAdapter(null);
                                        recycle_food_list.setLayoutAnimation(layoutAnimationController);
                                        Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                dialog.dismiss();
                            } , throwable -> {
                                dialog.dismiss();
                                Toast.makeText(this, "[SEARCH FOOD]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycle_food_list.setLayoutManager(layoutManager);
        recycle_food_list.addItemDecoration(new DividerItemDecoration(this , layoutManager.getOrientation()));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(this , R.anim.layout_item_from_left);

    }

    private void init() {
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
    public void loadFoodListByCategory(FoodListEvent event){
        if(event.isSuccess()){

            selectedCategory = event.getCategory();

            Picasso.get().load(event.getCategory().getImage()).into(img_category);
            toolbar.setTitle(event.getCategory().getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //request Category by restaurant Id
            Map<String , String> headers = new HashMap<>();
            headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
            compositeDisposable.add(myRestaurantAPI.getFoodOfMenu(headers,event.getCategory().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foodModel -> {
                                if(foodModel.isSuccess()){
                                    adapter = new MyFoodAdapter(this,foodModel.getResult());
                                    recycle_food_list.setAdapter(adapter);
                                    recycle_food_list.setLayoutAnimation(layoutAnimationController);
                                }
                                else{
                                    Toast.makeText(this, "[GET FOOD RESULT]"+foodModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            },
                            throwable -> {
                                dialog.dismiss();
                                Toast.makeText(this, "[GET Food]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );
        }
        else{

        }
    }
    
}