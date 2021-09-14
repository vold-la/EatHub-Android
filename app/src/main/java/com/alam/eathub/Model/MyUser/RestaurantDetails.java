package com.alam.eathub.Model.MyUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantDetails {

    @SerializedName("restaurantName")
    @Expose
    private String restaurantName;
    @SerializedName("restaurantLocation")
    @Expose
    private String restaurantLocation;
    @SerializedName("restaurantImage")
    @Expose
    private String restaurantImage;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantLocation() {
        return restaurantLocation;
    }

    public void setRestaurantLocation(String restaurantLocation) {
        this.restaurantLocation = restaurantLocation;
    }

    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

}
