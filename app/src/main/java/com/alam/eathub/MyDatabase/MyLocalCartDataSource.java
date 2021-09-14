package com.alam.eathub.MyDatabase;

import com.alam.eathub.Database.CartDAO;
import com.alam.eathub.Database.CartItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class MyLocalCartDataSource implements MyCartDataSource{

    private MyCartDao cartDAO;

    public MyLocalCartDataSource(MyCartDao cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<MyCartItem>> getAllCart(String fbid, int restaurantId) {
        return cartDAO.getAllCart(fbid,restaurantId);
    }

    @Override
    public Single<Integer> countItemInCart(String fbid, int restaurantId) {
        return cartDAO.countItemInCart(fbid, restaurantId);
    }

    @Override
    public Single<Long> sumPrice(String fbid, int restaurantId) {
        return cartDAO.sumPrice(fbid, restaurantId);
    }

    @Override
    public Single<MyCartItem> getItemInCart(String foodId, String fbid, int restaurantId) {
        return cartDAO.getItemInCart(foodId, fbid, restaurantId);
    }

    @Override
    public Completable insertOrReplaceAll(MyCartItem... cartItems) {
        return cartDAO.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCart(MyCartItem cart) {
        return cartDAO.updateCart(cart);
    }

    @Override
    public Single<Integer> deleteCart(MyCartItem cart) {
        return cartDAO.deleteCart(cart);
    }

    @Override
    public Single<Integer> cleanCart(String fbid, int restaurantId) {
        return cartDAO.cleanCart(fbid, restaurantId);
    }
}
