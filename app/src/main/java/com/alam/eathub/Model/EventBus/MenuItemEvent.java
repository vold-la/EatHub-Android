package com.alam.eathub.Model.EventBus;

import com.alam.eathub.Model.My.MyyRestaurant;
import com.alam.eathub.Model.My.Rrestaurant;
import com.alam.eathub.Model.MyRestaurant;
import com.alam.eathub.Model.My.Rrestaurant;
import com.alam.eathub.Model.Restaurant;

public class MenuItemEvent {
    private boolean success;
    private Rrestaurant rrestaurant;
    private MyyRestaurant myRestaurant;
    private Restaurant restaurant;

    public MenuItemEvent(boolean success, MyyRestaurant myRestaurant) {
        this.success = success;
        this.myRestaurant = myRestaurant;
    }

    public MenuItemEvent(boolean success, Restaurant restaurant) {
        this.success = success;
        this.restaurant = restaurant;
    }

    public MenuItemEvent(boolean success, Rrestaurant rrestaurant) {
        this.success = success;
        this.rrestaurant = rrestaurant;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
