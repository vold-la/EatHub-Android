package com.alam.eathub.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Database.CartDataSource;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.CartItem;
import com.alam.eathub.Database.LocalCartDataSource;
import com.alam.eathub.FoodDetailActivity;
import com.alam.eathub.Interface.IFoodDetail;
import com.alam.eathub.Model.EventBus.FoodDetailEvent;
import com.alam.eathub.Model.FavoriteOnlyId;
import com.alam.eathub.Model.Food;
import com.alam.eathub.R;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder> {

    Context context;
    List<Food> foodList;

    CompositeDisposable compositeDisposable;
    CartDataSource cartDataSource;
    IMyRestaurantAPI myRestaurantAPI;

    public void onStop(){
        compositeDisposable.clear();
    }

    public MyFoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
        compositeDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_food,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(foodList.get(position).getImage()).placeholder(R.drawable.app_icon).into(holder.img_food);
        holder.txt_food_name.setText(foodList.get(position).getName());
        holder.getTxt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(foodList.get(position).getPrice()));

        //check favorite
        if(Common.currentFavOfRestaurant != null &&Common.currentFavOfRestaurant.size()>0){
            if(Common.checkFavorite(foodList.get(position).getId() )){
                holder.img_fav.setImageResource(R.drawable.ic_primary_fill_favorite);
                holder.img_fav.setTag(true);
            }
            else {
                holder.img_fav.setImageResource(R.drawable.ic_primary_favorite);
                holder.img_fav.setTag(false);
            }
        }
        else {
            // default , all item is not fav
            holder.img_fav.setTag(false);
        }

        //Event
        holder.img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView fav = (ImageView) v;
                if((Boolean)fav.getTag()){
                    Map<String , String> headers = new HashMap<>();
                    headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
                    compositeDisposable.add(myRestaurantAPI.removeFavorite(headers , foodList.get(position).getId() , Common.currentRestaurant.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(favoriteModel -> {
                                if(favoriteModel.isSuccess() && favoriteModel.getMessage().contains("Success")){
                                    fav.setImageResource(R.drawable.ic_primary_favorite);
                                    fav.setTag(false);
                                    if(Common.currentFavOfRestaurant != null)
                                        Common.removeFavorite(foodList.get(position).getId());
                                }
                                    }
                            , throwable -> {
                                        Toast.makeText(context, "[REMOVE FAV]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
                }
                else {
                    Map<String , String> headers = new HashMap<>();
                    headers.put("Authorization" , Common.buildJWT(Common.API_KEY));
                    compositeDisposable.add(myRestaurantAPI.insertFavorite(headers,
                            foodList.get(position).getId() ,
                            Common.currentRestaurant.getId() ,
                            Common.currentRestaurant.getName() ,
                            foodList.get(position).getName() ,
                            foodList.get(position).getImage() ,
                            foodList.get(position).getPrice())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(favoriteModel -> {
                                        if(favoriteModel.isSuccess() && favoriteModel.getMessage().contains("Success")){
                                            fav.setImageResource(R.drawable.ic_primary_fill_favorite);
                                            fav.setTag(true);
                                            if(Common.currentFavOfRestaurant != null)
                                                Common.currentFavOfRestaurant.add(new FavoriteOnlyId(foodList.get(position).getId()));
                                        }

                                        else {
                                            Log.d("favorite model insert" , "not succesful");
                                        }
                                    }
                                    , throwable -> {
                                        Toast.makeText(context, "[ADD FAV]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
                }
            }
        });
        holder.setListener((view, position1, isDetail) -> {
            if(isDetail){
                context.startActivity(new Intent(context , FoodDetailActivity.class));
                EventBus.getDefault().postSticky(new FoodDetailEvent(true , foodList.get(position)));
            }
            else {
                //Create Cart
                CartItem cartItem = new CartItem();
                cartItem.setFoodId(foodList.get(position).getId());
                cartItem.setFoodName(foodList.get(position).getName());
                cartItem.setFoodPrice(foodList.get(position).getPrice());
                cartItem.setFoodImage(foodList.get(position).getImage());
                cartItem.setFoodQuantity(1);
                cartItem.setUserPhone(Common.currentUser.getUserPhone());
                cartItem.setRestaurantId(Common.currentRestaurant.getId());
                cartItem.setFoodAddon("NORMAL");
                cartItem.setFoodSize("NORMAL");
                cartItem.setFoodExtraPrice(0.0);
                cartItem.setFbid(Common.currentUser.getFbid());

                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {

                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                        },
                        throwable -> {
                            Toast.makeText(context, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_food)
        ImageView img_food;
        @BindView(R.id.img_fav)
        ImageView img_fav;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView getTxt_food_price;
        @BindView(R.id.img_detail)
        ImageView img_detail;
        @BindView(R.id.img_cart)
        ImageView img_add_cart;

        IFoodDetail listener;

        public void setListener(IFoodDetail listener) {
            this.listener = listener;
        }

        Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this ,itemView);

            img_detail.setOnClickListener(this);
            img_add_cart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.img_detail)
                listener.OnFoodItemClickListener(v, getAdapterPosition(),true);
            else if(v.getId() == R.id.img_cart)
                listener.OnFoodItemClickListener(v, getAdapterPosition(),false);


        }
    }
}
