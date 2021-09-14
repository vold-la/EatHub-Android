package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Adapter.MyCartAdapter;
import com.alam.eathub.Adapter.MyCategoryAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.Database.CartDataSource;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.LocalCartDataSource;
import com.alam.eathub.Model.EventBus.CalculatePriceEvent;
import com.alam.eathub.Model.EventBus.SendTotalCashEvent;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.alam.eathub.Utils.SpacesItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CartListActivity extends AppCompatActivity {

    @BindView(R.id.recycler_cart)
    RecyclerView recycle_cart;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_final_price)
    TextView txt_final_price;
    @BindView(R.id.btn_order)
    Button btn_order;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
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
        setContentView(R.layout.activity_cart_list);

        init();
        initView();

        getAllItemInCart();
    }

    private void getAllItemInCart() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getFbid() , Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    if(cartItems.isEmpty()){
                        btn_order.setText(getString(R.string.empty_cart));
                        btn_order.setEnabled(false);
                        btn_order.setBackgroundResource(android.R.color.darker_gray);
                    }
                    else {

                        btn_order.setText(getString(R.string.place_order));
                        btn_order.setEnabled(true);
                        btn_order.setBackgroundResource(R.color.colorPrimary);

                        MyCartAdapter adapter  =new MyCartAdapter(CartListActivity.this , cartItems);
                        recycle_cart.setAdapter(adapter);

                        calculateCartTotalPrice();
                    }
                        }
                , throwable -> {

                })
        );
    }

    private void calculateCartTotalPrice() {

        cartDataSource.sumPrice(Common.currentUser.getFbid() , Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        if(aLong <= 0){
                            btn_order.setText(getString(R.string.empty_cart));
                            btn_order.setEnabled(false);
                            btn_order.setBackgroundResource(android.R.color.darker_gray);
                        }
                        else{
                            btn_order.setText(getString(R.string.place_order));
                            btn_order.setEnabled(true);
                            btn_order.setBackgroundResource(R.color.colorPrimary);
                        }
                        txt_final_price.setText(String.valueOf(aLong));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e.getMessage().contains("Query returned empty"))
                            txt_final_price.setText("0");
                        else
                            Toast.makeText(CartListActivity.this, "[SUM CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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


    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    private void initView() {
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.cart));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycle_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager =  new LinearLayoutManager(this);
        recycle_cart.setLayoutManager(layoutManager);
        recycle_cart.addItemDecoration(new DividerItemDecoration(this , layoutManager.getOrientation()));
        recycle_cart.setLayoutAnimation(layoutAnimationController);

        btn_order.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new SendTotalCashEvent(txt_final_price.getText().toString()));
            startActivity(new Intent(CartListActivity.this , PlaceOrderActivity.class));
        });
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(this , R.anim.layout_item_from_left);

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
    public void calculatePrice(CalculatePriceEvent event){
        if(event != null)
            calculateCartTotalPrice();
    }

}