package com.alam.eathub.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.FoodDetailActivity;
import com.alam.eathub.Interface.IOnRecyclerViewClickListener;
import com.alam.eathub.Model.EventBus.FoodDetailEvent;
import com.alam.eathub.Model.Favorite;
import com.alam.eathub.Model.Restaurant;
import com.alam.eathub.Model.RestaurantModel;
import com.alam.eathub.R;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.RetrofitClient;
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
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.MyViewHolder> {

    Context context;
    List<Favorite> favoriteList;
    CompositeDisposable compositeDisposable;
    IMyRestaurantAPI myRestaurantAPI;

    public void onDestroy() {
        compositeDisposable.clear();
    }

    public MyFavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
        compositeDisposable = new CompositeDisposable();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_favorite_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Picasso.get().load(favoriteList.get(position).getFoodImage()).placeholder(R.drawable.app_icon).into(holder.img_food);
        holder.txt_food_name.setText(favoriteList.get(position).getFoodName());
        holder.txt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(favoriteList.get(position).getPrice()));
        holder.txt_restaurant_name.setText(favoriteList.get(position).getRestaurantName());

        //Event
        holder.setListener((view, position1) -> {
            Map<String , String> headers = new HashMap<>();
            headers.put("Authorization" , Common.buildJWT(Common.API_KEY));

            compositeDisposable.add(myRestaurantAPI.getFoodById(headers, favoriteList.get(position).getFoodId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((foodModel) -> {
                                if (foodModel.isSuccess()) {
                                    //when user click to favorite item just start FoodDetailActivity
                                    context.startActivity(new Intent(context, FoodDetailActivity.class));
                                    if (Common.currentRestaurant == null) {

                                        compositeDisposable.add(myRestaurantAPI.getRestaurantById(headers , String.valueOf(favoriteList.get(position).getRestaurantId()))
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<RestaurantModel>() {
                                                    @Override
                                                    public void accept(RestaurantModel restaurantModel) throws Exception {
                                                        if(restaurantModel.isSuccess()){
                                                            Common.currentRestaurant = restaurantModel.getResult().get(0);
                                                            EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodModel.getResult().get(0)));
                                                        }
                                                        else{
                                                            Toast.makeText(context, ""+restaurantModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                } , throwable -> {
                                                    Toast.makeText(context, "[GET RESTAURANT BY ID]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                })
                                        );
                                    }
                                    else{
                                        EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodModel.getResult().get(0)));
                                    }
                                }
                                else {
                                    Toast.makeText(context, "[GET FOOD BY RESULT]" + foodModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            },
                            throwable -> {
                                Toast.makeText(context, "[GET FOOD BY ID]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );

        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_food)
        ImageView img_food;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.txt_restaurant_name)
        TextView txt_restaurant_name;

        Unbinder unbinder;
        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
