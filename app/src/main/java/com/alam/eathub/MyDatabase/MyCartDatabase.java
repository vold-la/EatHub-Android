package com.alam.eathub.MyDatabase;

import android.content.Context;

import com.alam.eathub.Database.CartDAO;
import com.alam.eathub.Database.CartDatabase;
import com.alam.eathub.Database.CartItem;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = MyCartItem.class, exportSchema = false)
public abstract class MyCartDatabase extends RoomDatabase {

    public abstract MyCartDao myCartDAO();

    private static MyCartDatabase instance;

    public static MyCartDatabase getInstance(Context context){
        if(instance == null)
            instance = Room.databaseBuilder(context,MyCartDatabase.class,"EatHubCartt").build();
        return instance;
    }
}