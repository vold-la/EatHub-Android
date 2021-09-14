package com.alam.eathub.MyDatabase;

import com.alam.eathub.Database.CartItem;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface MyCartDao {
    //we only add Cart by Restaurant Id
    //because each restaurant id will have different order receipt
    //because each restro has different payment link so we cannot make one cart for all
    @androidx.room.Query("SELECT * FROM MyCartTable WHERE fbid=:fbid AND restaurantId=:restaurantId")
    Flowable<List<MyCartItem>> getAllCart(String fbid, int restaurantId);

    @Query("SELECT COUNT(*) from MyCartTable where fbid=:fbid AND restaurantId=:restaurantId")
    Single<Integer> countItemInCart(String fbid, int restaurantId);

    @Query("SELECT SUM(foodPrice*foodQuantity) from MyCartTable where fbid=:fbid AND restaurantId=:restaurantId")
    Single<Long> sumPrice(String fbid,int restaurantId);

    @Query("SELECT * FROM MyCartTable WHERE foodName=:foodId AND fbid=:fbid AND restaurantId=:restaurantId")
    Single<MyCartItem> getItemInCart(String foodId ,String fbid,int restaurantId);

    @Insert(onConflict = OnConflictStrategy.REPLACE) //If conflict foodId , we will update information
    Completable insertOrReplaceAll(MyCartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCart(MyCartItem cart);

    @Delete
    Single<Integer> deleteCart(MyCartItem cart);

    @Query("DELETE FROM MyCartTable WHERE fbid=:fbid AND restaurantId=:restaurantId")
    Single<Integer> cleanCart(String fbid,int restaurantId);

}
