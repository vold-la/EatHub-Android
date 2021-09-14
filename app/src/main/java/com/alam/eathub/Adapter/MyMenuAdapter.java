package com.alam.eathub.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.alam.eathub.Interface.IMenuDetail;
import com.alam.eathub.Model.EventBus.CalculatePriceEvent;
import com.alam.eathub.Model.EventBus.CartChangeEvent;
import com.alam.eathub.Model.EventBus.FoodDetailEvent;
import com.alam.eathub.Model.EventBus.MyMenuEvent;
import com.alam.eathub.Model.EventBus.SendTotalCashEvent;
import com.alam.eathub.Model.FavoriteOnlyId;
import com.alam.eathub.Model.Food;
import com.alam.eathub.Model.My.Menu;
import com.alam.eathub.Model.My.Rrestaurant;
import com.alam.eathub.MyDatabase.MyCartDataSource;
import com.alam.eathub.MyDatabase.MyCartDatabase;
import com.alam.eathub.MyDatabase.MyCartItem;
import com.alam.eathub.MyDatabase.MyLocalCartDataSource;
import com.alam.eathub.R;
import com.alam.eathub.Retrofit.IMyRestaurantAPI;
import com.alam.eathub.Retrofit.IMyy;
import com.alam.eathub.Retrofit.RC;
import com.alam.eathub.Retrofit.RetrofitClient;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

//Todo - check
public class MyMenuAdapter extends RecyclerView.Adapter<MyMenuAdapter.MyViewHolder> {

    Context context;
    List<Menu> menuList;

    CompositeDisposable compositeDisposable;
    MyCartDataSource cartDataSource;
    IMyy myRestaurantAPI;
    List<MyCartItem> cartItemList;

    public void onStop(){
        compositeDisposable.clear();
    }

    public List<MyCartItem> getCartItemList() {
        return cartItemList;
    }

    public MyMenuAdapter(Context context, List<Menu> menuList , List<MyCartItem> cartList) {
        this.context = context;
        this.menuList = menuList;
        this.cartItemList = cartList;
        compositeDisposable = new CompositeDisposable();
        cartDataSource = new MyLocalCartDataSource(MyCartDatabase.getInstance(context).myCartDAO());
        myRestaurantAPI = RC.getInstance(Common.API_RESTAURANT_ENDPOINT_TEST).create(IMyy.class);

    }

    @NonNull
    @Override
    public MyMenuAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyMenuAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.restaurant_menu_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyMenuAdapter.MyViewHolder holder, int position) {
        holder.mItemName.setText(menuList.get(position).getDish());
        holder.mItemPrice.setText(new StringBuilder(context.getString(R.string.money_sign)).append(menuList.get(position).getCost()));
        Picasso.get().load(menuList.get(position).getImage()).into(holder.imageDescription);
        holder.mItemCategory.setText(menuList.get(position).getDescription());

        if(cartItemList !=null) {
            for (int i = 0; i < cartItemList.size(); i++) {
                if (cartItemList.get(i).getFoodName().equals(menuList.get(position).getDish())) {
                    holder.quantity.setText(String.valueOf(cartItemList.get(i).getFoodQuantity()));
                    holder.imageDecrease.setVisibility(View.VISIBLE);
                }
            }
        }

        //mcheck favorite


        //ocheck fav
        if (!menuList.get(position).getVeg()) {
            Picasso.get().load(R.drawable.non_veg_symbol).into(holder.foodSpecification);
            //Todo --check food available.
            //  holder.mItemAddBtn.setClickable(false);
           // holder.mNotAvailableText.setVisibility(View.VISIBLE);
        }
        else if (menuList.get(position).getVeg()) {
            Picasso.get().load(R.drawable.veg_symbol).into(holder.foodSpecification);
        }

        holder.imageDecrease.setOnClickListener(view -> {

            holder.imageDecrease.setClickable(false);
             boolean isInDb = false;
             int p = 0;

            if(cartItemList !=null) {
                for (int i = 0; i < cartItemList.size() && !isInDb; i++) {
                    if (cartItemList.get(i).getFoodName().equals(menuList.get(position).getDish())) {
                        isInDb = true;
                        p = i;
                    }
                }
            }
            final int pos=p;

            if(isInDb && cartItemList.get(pos).getFoodQuantity() > 1) {
                cartItemList.get(pos).setFoodQuantity(cartItemList.get(pos).getFoodQuantity() - 1);

                cartDataSource.updateCart(cartItemList.get(pos))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                holder.quantity.setText(String.valueOf(cartItemList.get(pos).getFoodQuantity()));
                                }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[MY Menu Adapter]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else if(isInDb && cartItemList.get(pos).getFoodQuantity()==1){
                cartDataSource.deleteCart(cartItemList.get(pos))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                holder.quantity.setText(new StringBuilder(context.getString(R.string.add)));
                                holder.imageDecrease.setVisibility(GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[DELETE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            EventBus.getDefault().postSticky(new CartChangeEvent(true));
            holder.imageDecrease.setClickable(true);
           // Toast.makeText(context, "Decr", Toast.LENGTH_SHORT).show();
        });

        holder.imageIncrease.setOnClickListener(view -> {

            holder.imageIncrease.setClickable(false);
            boolean isInDb = false;
            int p = 0;

            if(cartItemList !=null) {
                for (int i = 0; i < cartItemList.size() && !isInDb; i++) {
                    if (cartItemList.get(i).getFoodName().equals(menuList.get(position).getDish())) {
                        isInDb = true;
                        p = i;
                    }
                }
            }
            final int pos=p;

            if(isInDb && cartItemList.get(pos).getFoodQuantity()<99) {
                    cartItemList.get(pos).setFoodQuantity(cartItemList.get(pos).getFoodQuantity() + 1);

                cartDataSource.updateCart(cartItemList.get(pos))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                holder.quantity.setText(String.valueOf(cartItemList.get(pos).getFoodQuantity()));
                                holder.imageDecrease.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
            else if(!isInDb){
                MyCartItem cartItem = new MyCartItem();

                cartItem.setFoodName(menuList.get(position).getDish());
                cartItem.setFoodPrice(menuList.get(position).getCost());
                cartItem.setFoodImage(menuList.get(position).getImage());
                cartItem.setFoodQuantity(1);
                cartItem.setDescription(menuList.get(position).getDescription());
                cartItem.setRatings(menuList.get(position).getRatings());
                cartItem.setVeg(menuList.get(position).getVeg());
                cartItem.setVotes(menuList.get(position).getVotes());
                cartItem.setRestaurantId(Integer.parseInt(Common.myCurrentRestaurant.getId()));
                cartItem.setFbid(Common.myCurrentUser.getId());

                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                },
                                throwable -> {
                                    Toast.makeText(context, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                );
            }

            EventBus.getDefault().postSticky(new CartChangeEvent(true));
            holder.imageIncrease.setClickable(true);
            //Toast.makeText(context, "Incresng", Toast.LENGTH_SHORT).show();
        });

    }


    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.menuItemName)
        TextView mItemName;
        @BindView(R.id.foodMark)
        ImageView foodSpecification;
        @BindView(R.id.menuItemPrice)
        TextView mItemPrice;
        @BindView(R.id.menuItemCategory)
        TextView mItemCategory;
        @BindView(R.id.text_quantity)
        TextView quantity;
        @BindView(R.id.image_decrease)
        ImageView imageDecrease;
        @BindView(R.id.image_increase)
        ImageView imageIncrease;
        @BindView(R.id.img_desc)
        ImageView imageDescription;
        @BindView(R.id.notAvailableText)
        TextView mNotAvailableText;

        IMenuDetail listener ;

        public void setListener(IMenuDetail listener) {
            this.listener = listener;
        }


        Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this ,itemView);

       //     img_detail.setOnClickListener(this);
        //    img_add_cart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.img_detail)
                listener.OnMenuItemClickListener(v, getAdapterPosition(),true);
            else if(v.getId() == R.id.img_cart)
                listener.OnMenuItemClickListener(v, getAdapterPosition(),false);


        }
    }
}