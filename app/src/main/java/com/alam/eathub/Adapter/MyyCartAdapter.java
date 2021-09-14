package com.alam.eathub.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam.eathub.Database.CartDataSource;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.CartItem;
import com.alam.eathub.Database.LocalCartDataSource;
import com.alam.eathub.Interface.IOnImageViewAdapterClickListener;
import com.alam.eathub.Model.EventBus.CalculatePriceEvent;
import com.alam.eathub.Model.EventBus.SendTotalCashEvent;
import com.alam.eathub.MyDatabase.MyCartDataSource;
import com.alam.eathub.MyDatabase.MyCartDatabase;
import com.alam.eathub.MyDatabase.MyCartItem;
import com.alam.eathub.MyDatabase.MyLocalCartDataSource;
import com.alam.eathub.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

public class MyyCartAdapter extends RecyclerView.Adapter<MyyCartAdapter.MyViewHolder> {
    Context context;
    List<MyCartItem> cartItemList;
    MyCartDataSource cartDataSource;

    public MyyCartAdapter(Context context, List<MyCartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        cartDataSource  =new MyLocalCartDataSource(MyCartDatabase.getInstance(context).myCartDAO());
    }

    @NonNull
    @Override
    public MyyCartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_items_layout, parent, false);
        EventBus.getDefault().postSticky(new SendTotalCashEvent(calculateCash()));
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyyCartAdapter.MyViewHolder holder, int position) {

        String specImage = "Veg"; // Todo -- change
        if (specImage.equals("Veg")) {
            Picasso.get().load(R.drawable.veg_symbol).into(holder.mFoodMarkImg);
        } else {
            Picasso.get().load(R.drawable.non_veg_symbol).into(holder.mFoodMarkImg);
        }

        holder.mItemCartName.setText(cartItemList.get(position).getFoodName());
        String itemCount = String.valueOf(cartItemList.get(position).getFoodQuantity());
        holder.quantity.setText(itemCount);
        int getItemPrice = Integer.parseInt(cartItemList.get(position).getFoodPrice());
        int finalPrice = cartItemList.get(position).getFoodQuantity() * getItemPrice;
        String finalPriceShow = "\u20b9 "+ finalPrice;
        holder.mItemCartPrice.setText( finalPriceShow);


        holder.decrease.setOnClickListener(view -> {

            holder.decrease.setClickable(false);

            if(cartItemList.get(position).getFoodQuantity() >1) {
                cartItemList.get(position).setFoodQuantity(cartItemList.get(position).getFoodQuantity() - 1);
                //update cart
                cartDataSource.updateCart(cartItemList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                holder.quantity.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[UPDATE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
            else {
                cartDataSource.deleteCart(cartItemList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                cartItemList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position , cartItemList.size());
                                //Todo - add event
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[DELETE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            String total = calculateCash();
            EventBus.getDefault().postSticky(new SendTotalCashEvent(total));
            holder.decrease.setClickable(true);
        });

        holder.increase.setOnClickListener(view -> {

                holder.increase.setClickable(false);

                if(cartItemList.get(position).getFoodQuantity() <99) {
                    cartItemList.get(position).setFoodQuantity(cartItemList.get(position).getFoodQuantity() + 1);
                    //update cart
                    cartDataSource.updateCart(cartItemList.get(position))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    holder.quantity.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(context, "[UPDATE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                String total = calculateCash();
                EventBus.getDefault().postSticky(new SendTotalCashEvent(total));
                holder.increase.setClickable(true);
            });
}

    private String calculateCash() {
        int total = 0;
        if (cartItemList != null) {
            for (int i = 0; i < cartItemList.size(); i++) {
                total = total + (cartItemList.get(i).getFoodQuantity() * Integer.parseInt(cartItemList.get(i).getFoodPrice()));
            }
        }

        return String.valueOf(total);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.foodMarkCart)
        ImageView mFoodMarkImg;
        @BindView(R.id.itemNameCart)
        TextView mItemCartName;
        @BindView(R.id.itemPriceCart)
        TextView mItemCartPrice;
        @BindView(R.id.quantity)
        TextView quantity;
        @BindView(R.id.decrease)
        ImageView decrease;
        @BindView(R.id.increase)
        ImageView increase;

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this , itemView);
        }

    }
}