package com.alam.eathub.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alam.eathub.Common.Common;
import com.alam.eathub.Interface.ILoadMore;
import com.alam.eathub.Model.MyUser.Order;
import com.alam.eathub.Model.MyUser.TotalOrder;
import com.alam.eathub.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrderHistoryAdapter extends RecyclerView.Adapter<MyOrderHistoryAdapter.MyViewHolder>{

    Context context;
    List<Order> orderList;
    SimpleDateFormat simpleDateFormat;


    public MyOrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.history_restaurant_name.setText(orderList.get(position).getRestaurantDetails().getRestaurantName());
        holder.history_restaurant_address.setText(orderList.get(position).getRestaurantDetails().getRestaurantLocation());
        holder.history_order_number.setText(orderList.get(position).getOrderId());
        Picasso.get().load(orderList.get(position).getRestaurantDetails().getRestaurantImage()).into(holder.history_restaurant_image);
       // holder.history_order_date.setText(new StringBuilder(simpleDateFormat.format(orderList.get(position).getTimeStamp())));

        Log.e("j: ", orderList.get(position).getTimeStamp().substring(0,9));

        String items ="" ;
        int cost=0;
        for(TotalOrder orders : orderList.get(position).getTotalOrder()){
            items += orders.getDish()+" ,";
            cost += Integer.valueOf(orders.getCost())*orders.getQuantity();
        }

        holder.history_order_total_price.setText(String.valueOf(cost));
        holder.history_items.setText(items);

    }

    @NonNull
    @Override
    public MyOrderHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = (LayoutInflater.from(context).inflate(R.layout.layout_order_history , parent , false));
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.history_restaurant_image)
        ImageView history_restaurant_image;
        @BindView(R.id.history_order_number)
        TextView history_order_number;
        @BindView(R.id.history_order_status)
        TextView history_order_status;
        @BindView(R.id.history_restaurant_name)
        TextView history_restaurant_name;
        @BindView(R.id.history_restaurant_address)
        TextView history_restaurant_address;
        @BindView(R.id.history_order_date)
        TextView history_order_date;
        @BindView(R.id.history_order_total_price)
        TextView history_order_total_price;
        @BindView(R.id.history_items)
        TextView history_items;
        @BindView(R.id.history_payment_method)
        TextView history_payment_method;


        Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this , itemView);
        }
    }
}
