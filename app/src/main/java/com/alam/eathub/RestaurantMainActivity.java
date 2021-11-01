package com.alam.eathub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.alam.eathub.Adapter.MyAdapter;
import com.alam.eathub.Adapter.MyCategoryAdapter;
import com.alam.eathub.Adapter.MyMenuAdapter;
import com.alam.eathub.Common.Common;
import com.alam.eathub.Database.CartDataSource;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.LocalCartDataSource;
import com.alam.eathub.Model.EventBus.CartChangeEvent;
import com.alam.eathub.Model.EventBus.MenuItemEvent;
import com.alam.eathub.Model.EventBus.MyCartItemEvent;
import com.alam.eathub.Model.EventBus.MyMenuEvent;
import com.alam.eathub.MyDatabase.MyCartDataSource;
import com.alam.eathub.MyDatabase.MyCartDatabase;
import com.alam.eathub.MyDatabase.MyCartItem;
import com.alam.eathub.MyDatabase.MyLocalCartDataSource;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.IMyy;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;

public class RestaurantMainActivity extends AppCompatActivity {


    FloatingActionButton btn_cart;
    NotificationBadge badge;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    List<MyCartItem> cartItem;

    MyMenuAdapter adapter;
    MyCartDataSource myCartDataSource;

    LinearLayoutManager linearLayoutManager;
    private RecyclerView mMenuItemRecyclerView;
    private NestedScrollView mRootView;
    private LottieAnimationView mFavoriteAnim;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        init();
        getCartItemsAndSetMenuAdapter();
        //loadMenuItem();
        countCartByRestaurant();

    }

    @SuppressLint("SetTextI18n")
    private void init() {
        mRootView = findViewById(R.id.content1);

        myCartDataSource = new MyLocalCartDataSource(MyCartDatabase.getInstance(this).myCartDAO());

        TextView mResNameToolBar = findViewById(R.id.restaurantTitleToolbar);
        TextView mResNameText = findViewById(R.id.mainResName);
        TextView mReviewsText = findViewById(R.id.reviewText);
        TextView mResDistanceText = findViewById(R.id.mainResDistance);
        TextView mResAvgPriceText = findViewById(R.id.restaurantAvgPrice);
        TextView mResDeliveryTimeText = findViewById(R.id.restaurantDeliveryTime);
        TextView mResCuisine = findViewById(R.id.mainResCuisine);
        TextView mResAddress = findViewById(R.id.mainResAddress);
        TextView restaurantRating = findViewById(R.id.restaurantRating);
        mFavoriteAnim = findViewById(R.id.favoriteAnim);
        ImageView mBackBtnView = findViewById(R.id.backBtn);

        mReviewsText.setOnClickListener(view -> {
            Intent intent = new Intent(this, MenuActivity.class); // *Todo - change to review act.
            startActivity(intent);
        });
        checkFavRes();
        mFavoriteAnim.setOnClickListener(view -> {
            if (mFavoriteAnim.getProgress() >= 0.1f){
                mFavoriteAnim.setSpeed(-1);
                mFavoriteAnim.playAnimation();
                delFavRes();
            }else if(mFavoriteAnim.getProgress() == 0.0f){
                mFavoriteAnim.setSpeed(1);
                mFavoriteAnim.playAnimation();
                favRes();
            }
        });

        mResAddress.setText(Common.myCurrentRestaurant.getLocation().getLocalityVerbose());
        restaurantRating.setText("\u2605 " + Common.myCurrentRestaurant.getUserRating().getAggregateRating());

        String cus = "";
        for(int i=0;i<Common.myCurrentRestaurant.getCuisines().size();i++){
            cus = cus+ Common.myCurrentRestaurant.getCuisines().get(i) +" , ";
        }
        mResCuisine.setText(cus);
        mResNameToolBar.setText(Common.myCurrentRestaurant.getName());
        mResNameText.setText(Common.myCurrentRestaurant.getName());
      //  mResDeliveryTimeText.setText("30min"); // Todo- add new field in db
        mResAvgPriceText.setText(String.valueOf(Common.myCurrentRestaurant.getAverageCostForTwo()));
      //  mResDistanceText.setText( "6 kms"); // change
        mBackBtnView.setOnClickListener(view -> {
            this.onBackPressed();
            this.overridePendingTransition(0,0);
        });

        btn_cart = findViewById(R.id.fab_cart);
        badge = findViewById(R.id.badge_count);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new MyCartItemEvent(true , cartItem));
                startActivity(new Intent(RestaurantMainActivity.this , CartActivity.class));
            }
        });

        mMenuItemRecyclerView = findViewById(R.id.menuItemRecylerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMenuItemRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void getCartItemsAndSetMenuAdapter() {

        compositeDisposable.add(myCartDataSource.getAllCart(Common.myCurrentUser.getId(),
                Integer.parseInt(Common.myCurrentRestaurant.getId()))
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myCartItems -> {
                    adapter = new MyMenuAdapter(this,Common.myCurrentRestaurant.getMenu() , myCartItems);
                    cartItem = myCartItems;
                    mMenuItemRecyclerView.setAdapter(adapter);
                }, throwable -> Log.e("ress :", "Error in getCartItems"))
        );
    }

    private void countCartByRestaurant() {

     //   Log.e("countCartByRestaurant" , "Started = "+Common.myCurrentUser.getPhone()+" ; id ="+Common.myCurrentRestaurant.getId());
        myCartDataSource.countItemInCart(Common.myCurrentUser.getId(),
                Integer.parseInt(Common.myCurrentRestaurant.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e("countCartByRestaurant" , "badged = "+integer);
                        badge.setText(String.valueOf(integer));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(RestaurantMainActivity.this, "[COUNT CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkFavRes() {
                        // Todo - check condition from user db
                        if (true){
                            mFavoriteAnim.setProgress(0.1f);
                            mFavoriteAnim.resumeAnimation();
                        }else {
                            mFavoriteAnim.setProgress(0.0f);
                        }

    }

    private void favRes() {

    }

    private void delFavRes() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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

    //since in MyAdapter we use postSticky ,so this event need set sticky too
    //for future usage
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void countCart(CartChangeEvent event){
        //if(event.isSuccess()){
            countCartByRestaurant();
        //}
    }
}
