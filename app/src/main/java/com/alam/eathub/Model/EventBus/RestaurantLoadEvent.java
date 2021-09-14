package com.alam.eathub.Model.EventBus;

import com.alam.eathub.Model.MyRestaurant;
import com.alam.eathub.Model.My.Rrestaurant;
import java.util.List;

public class RestaurantLoadEvent {
    private boolean err;
    private String message;
    private List<Rrestaurant> restaurantList;

    public RestaurantLoadEvent(boolean err, String message) {
        this.err = err;
        this.message = message;
    }

    public RestaurantLoadEvent(boolean err, List<Rrestaurant> restaurantList) {
        this.err = err;
        this.restaurantList = restaurantList;
    }

    public boolean isErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Rrestaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Rrestaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }
}
