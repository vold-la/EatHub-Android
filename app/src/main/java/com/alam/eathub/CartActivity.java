package com.alam.eathub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Adapter.MyMenuAdapter;
import com.alam.eathub.Adapter.MyyCartAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.LocalCartDataSource;
import com.alam.eathub.Model.EventBus.MyCartItemEvent;
import com.alam.eathub.Model.EventBus.SendTotalCashEvent;
import com.alam.eathub.MyDatabase.MyCartDataSource;
import com.alam.eathub.MyDatabase.MyCartDatabase;
import com.alam.eathub.MyDatabase.MyLocalCartDataSource;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.IMyy;
import com.alam.eathub.Retrofit.RC;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity implements PaymentResultListener {

    LinearLayoutManager linearLayoutManager;
    MyyCartAdapter adapter;
    private RecyclerView mCartItemRecyclerView;
    private TextView mRestaurantCartName;
    private TextView mUserAddressText;
    private TextView mTotalAmountText;
    private RadioButton cod;
    private RadioButton online_payement;
    private EditText mExtraInstructionsText;
    String idOrder;
    int totalAmount;

    MyCartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyy myRestaurantAPI;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
    }

    private void init() {

        TextView mToolBarText = findViewById(R.id.confirmOrderText);
   //     mToolBarText.setText("Confirm Order");
        mRestaurantCartName = findViewById(R.id.restaurantCartName);
        TextView mChangeAddressText = findViewById(R.id.changeAddressText);
        ImageView mCartBackBtn = findViewById(R.id.cartBackBtn);
        mExtraInstructionsText = findViewById(R.id.extraInstructionEdiText);
        mUserAddressText = findViewById(R.id.userDeliveryAddress);
        mTotalAmountText = findViewById(R.id.totAmount);
        Button mCheckoutBtn = findViewById(R.id.payAmountBtn);
        cod = findViewById(R.id.cod);
        online_payement = findViewById(R.id.online_payment);

        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);
        cartDataSource  =new MyLocalCartDataSource(MyCartDatabase.getInstance(this).myCartDAO());

        mRestaurantCartName.setText(Common.myCurrentRestaurant.getName());
       // mUserAddressText.setText(Common.myCurrentUser.getAddress().get(0).getAddress());

        mChangeAddressText.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UpdateInfoActivity.class);
            startActivity(intent);
            finish();
        });

        mCartBackBtn.setOnClickListener(view -> {
            this.onBackPressed();
        });

        mCheckoutBtn.setOnClickListener(view -> {

            if (cod.isChecked()) {
                getOrderId(totalAmount , true);
            } else if (online_payement.isChecked()) {
                getOrderId(totalAmount , false);
            }
            else
                Toast.makeText(this, "Select Payment Method", Toast.LENGTH_SHORT).show();
        });
    }

    private void getOrderId(int amount , boolean isCod) {
        Log.e("res getorderid : " , String.valueOf(isCod));
        compositeDisposable.add(myRestaurantAPI.getOrderId(amount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderId -> {
                    idOrder = orderId.getId();
                    placeOrder(isCod);
                })
        );
    }

    private void placeOrder(boolean b) {
        Log.e("res placeorder : " , String.valueOf(b));
        if(!b){
            Checkout checkout = new Checkout();
            checkout.setKeyID(Common.RAZOR_PAY_KEY_ID);
            //checkout.setImage(R.drawable.logo);
            final Activity activity = CartActivity.this;

            try {
                JSONObject options = new JSONObject();

                options.put("name", "EatHub");
                options.put("order_id", idOrder);
                options.put("theme.color", "#000000");
                options.put("currency", "INR");
                options.put("amount", totalAmount*100);//300 X 100
                options.put("prefill.email", Common.myCurrentUser.getEmail());
                options.put("prefill.contact",Common.myCurrentUser.getPhone());
                checkout.open(activity, options);
            } catch(Exception e) {
                Log.e("TAG", "Error in starting Razorpay Checkout", e);
            }
        }
        else {
            updateDetails();
        }
    }

    private void updateDetails() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        compositeDisposable.add(cartDataSource.getAllCart(Common.myCurrentUser.getId(),
                Integer.parseInt(Common.myCurrentRestaurant.getId()))
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myCartItems -> {
                    for(int i=0;i<myCartItems.size();i++){
                        JsonObject orderObject = new JsonObject();
                        orderObject.addProperty("dish" , myCartItems.get(i).getFoodName());
                        orderObject.addProperty("cost" , myCartItems.get(i).getFoodPrice());
                        orderObject.addProperty("image" , myCartItems.get(i).getFoodName());
                        orderObject.addProperty("description" , myCartItems.get(i).getDescription());
                        orderObject.addProperty("veg" , myCartItems.get(i).isVeg());
                        orderObject.addProperty("votes" , myCartItems.get(i).getVotes());
                        orderObject.addProperty("ratings" , myCartItems.get(i).getRatings());
                        orderObject.addProperty("quantity" , myCartItems.get(i).getFoodQuantity());
                        jsonArray.add(orderObject);
                    }
                    JsonObject resJson = new JsonObject();
                    resJson.addProperty("restaurantName" , Common.myCurrentRestaurant.getName());
                    resJson.addProperty("restaurantLocation" , Common.myCurrentRestaurant.getLocation().getLocalityVerbose());
                    resJson.addProperty("restaurantImage" , Common.myCurrentRestaurant.getFeaturedImage());

                    jsonObject.add("order" , jsonArray);
                    String uuid = "112601783846664872143";
                    jsonObject.addProperty("amount" , totalAmount);
                    jsonObject.addProperty("order_id" , idOrder);
                    jsonObject.addProperty("userId" , uuid);
                    jsonObject.add("restaurantDetails" , resJson);

                    compositeDisposable.add(myRestaurantAPI.pushOrder(jsonObject)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(pushOrder -> {
                                 // make order success
                                cartDataSource.cleanCart(Common.myCurrentUser.getId(), Integer.parseInt(Common.myCurrentRestaurant.getId()))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<Integer>() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(@NonNull Integer integer) {
                                                Toast.makeText(CartActivity.this, "Order Success", Toast.LENGTH_SHORT).show();
                                                movetoHome();
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {

                                            }
                                        });
                            } , throwable -> Toast.makeText(CartActivity.this, "Pushing error", Toast.LENGTH_SHORT).show())
                    );

                }, throwable -> Toast.makeText(CartActivity.this, "Order error", Toast.LENGTH_SHORT).show())
        );

    }

    private void movetoHome() {
       Log.e("res : " , "move to home");
         Intent intent = new Intent(CartActivity.this, HomeActivity.class);
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(intent);
         finish();
    }

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

    @Override
    public void onPaymentSuccess(String s)
    {
        updateDetails();
    }

    @Override
    public void onPaymentError(int i, String s) {
        //do something
        movetoHome();
        Toast.makeText(this, "Fialed", Toast.LENGTH_SHORT).show();

    }

    //since in MyAdapter we use postSticky ,so this event need set sticky too
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setTotalCash(SendTotalCashEvent event) {
        Log.e("Ress amount: ", event.getCash());
        totalAmount = Integer.parseInt(event.getCash());
        if(totalAmount == 0){}
          //  movetoHome();
        else
            mTotalAmountText.setText(String.valueOf(event.getCash()));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setCartItem(MyCartItemEvent event) {
        Log.e("res set cart: " , String.valueOf(event.isSuccess()));
        if(event.isSuccess()){
            mCartItemRecyclerView = findViewById(R.id.cartItemRecyclerView);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mCartItemRecyclerView.setLayoutManager(linearLayoutManager);
            adapter = new MyyCartAdapter(this ,event.getCartItems());
            mCartItemRecyclerView.setAdapter(adapter);
        }
    }
}