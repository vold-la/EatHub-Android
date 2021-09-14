package com.alam.eathub.MyDatabase;

import com.alam.eathub.Database.CartItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface MyCartDataSource {


    Flowable<List<MyCartItem>> getAllCart(String fbid, int restaurantId);

    Single<Integer> countItemInCart(String fbid, int restaurantId);

    Single<Long> sumPrice(String fbid,int restaurantId);

    Single<MyCartItem> getItemInCart(String foodId ,String fbid,int restaurantId);

    Completable insertOrReplaceAll(MyCartItem... cartItems);

    Single<Integer> updateCart(MyCartItem cart);

    Single<Integer> deleteCart(MyCartItem cart);

    Single<Integer> cleanCart(String fbid,int restaurantId);

}
