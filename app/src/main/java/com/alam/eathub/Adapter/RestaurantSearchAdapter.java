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
import com.alam.eathub.Model.EventBus.MyMenuEvent;
import com.alam.eathub.Model.Favorite;
import com.alam.eathub.Model.My.Rrestaurant;
import com.alam.eathub.Model.RestaurantModel;
import com.alam.eathub.R;
import com.alam.eathub.RestaurantMainActivity;
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

public class RestaurantSearchAdapter extends RecyclerView.Adapter<RestaurantSearchAdapter.MyViewHolder>{

    Context context;
    List<Rrestaurant> restaurantList;

    public RestaurantSearchAdapter(Context context, List<Rrestaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantSearchAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.custom_search_res_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantSearchAdapter.MyViewHolder holder, int position) {

        Picasso.get().load(restaurantList.get(position).getFeaturedImage()).placeholder(R.drawable.app_icon).into(holder.ResImage);
        holder.ResName.setText(restaurantList.get(position).getName());
        holder.ResAddress.setText(restaurantList.get(position).getLocation().getLocalityVerbose());
        if(restaurantList.get(position).getIsDeliveringNow() == 0)
            holder.isDelivering.setVisibility(View.VISIBLE);

        //Event
        holder.setListener((view, position1) -> {
            Common.myCurrentRestaurant = restaurantList.get(position);
            EventBus.getDefault().postSticky(new MyMenuEvent(true,restaurantList.get(position)));
            context.startActivity(new Intent(context , RestaurantMainActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ResImage)
        ImageView ResImage;
        @BindView(R.id.ResName)
        TextView ResName;
        @BindView(R.id.ResAddress)
        TextView ResAddress;
        @BindView(R.id.isDelivering)
        TextView isDelivering;

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

