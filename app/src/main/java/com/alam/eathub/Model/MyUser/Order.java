package com.alam.eathub.Model.MyUser;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("totalOrder")
    @Expose
    private List<TotalOrder> totalOrder = null;
    @SerializedName("timeStamp")
    @Expose
    private String timeStamp;
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("restaurantDetails")
    @Expose
    private RestaurantDetails restaurantDetails;

    public List<TotalOrder> getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(List<TotalOrder> totalOrder) {
        this.totalOrder = totalOrder;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public RestaurantDetails getRestaurantDetails() {
        return restaurantDetails;
    }

    public void setRestaurantDetails(RestaurantDetails restaurantDetails) {
        this.restaurantDetails = restaurantDetails;
    }

}
