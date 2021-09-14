package com.alam.eathub.Database;

import java.util.List;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getAllCart(String fbid, int restaurantId);

    Single<Integer> countItemInCart(String fbid, int restaurantId);

    Single<Long> sumPrice(String fbid,int restaurantId);

    Single<CartItem> getItemInCart(String foodId ,String fbid,int restaurantId);

    Completable insertOrReplaceAll(CartItem... cartItems);

    Single<Integer> updateCart(CartItem cart);

    Single<Integer> deleteCart(CartItem cart);

    Single<Integer> cleanCart(String fbid,int restaurantId);

}
