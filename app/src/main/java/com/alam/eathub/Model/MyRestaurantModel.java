package com.alam.eathub.Model;

import java.util.List;

public class MyRestaurantModel {
    private boolean err;
    private String message;
    private List<MyRestaurant> restaurant;

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

    public List<MyRestaurant> getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(List<MyRestaurant> restaurant) {
        this.restaurant = restaurant;
    }
}

//https://eathub-nbackend.herokuapp.com/api/restaurant/filterd