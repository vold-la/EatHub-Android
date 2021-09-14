package com.alam.eathub.Adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Interface.IOnRecyclerViewClickListener;
import com.alam.eathub.MenuActivity;
import com.alam.eathub.Model.EventBus.MenuItemEvent;
import com.alam.eathub.Model.EventBus.MyMenuEvent;
import com.alam.eathub.Model.My.MyyRestaurant;
import com.alam.eathub.Model.MyRestaurant;
import com.alam.eathub.Model.My.Rrestaurant;
import com.alam.eathub.R;
import com.alam.eathub.RestaurantMainActivity;
import com.alam.eathub.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    List<Rrestaurant> restaurantList;
 //   Fragment mFragment;

    public MyAdapter(Context context, List<Rrestaurant> restaurantList ) {
        this.context = context;
        this.restaurantList = restaurantList;
   //     this.mFragment = f;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_restaurant,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Picasso.get().load(restaurantList.get(position).getFeaturedImage()).into(holder.img_restaurant);
        holder.txt_restaurant_address.setText(new StringBuilder(restaurantList.get(position).getLocation().getCity()));
        holder.txt_restaurant_name.setText(new StringBuilder(restaurantList.get(position).getName()));

        //precaution to avoid crash
        holder.setListener((view, position1) -> {
            Common.myCurrentRestaurant = restaurantList.get(position);
            //postSticky -> event can be listen from other activity unlike post
            EventBus.getDefault().postSticky(new MyMenuEvent(true,restaurantList.get(position)));
            context.startActivity(new Intent(context , RestaurantMainActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_restaurant_name)
        TextView txt_restaurant_name;
        @BindView(R.id.txt_restaurant_address)
        TextView txt_restaurant_address;
        @BindView(R.id.img_restaurant)
        ImageView img_restaurant;


        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
