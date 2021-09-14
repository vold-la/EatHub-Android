package com.alam.eathub.MyDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MyCartTable")
public class MyCartItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "foodName")
    private String foodName;
    @ColumnInfo(name = "foodImage")
    private String foodImage;
    @ColumnInfo(name = "foodPrice")
    private String foodPrice;
    @ColumnInfo(name = "foodQuantity")
    private int foodQuantity;
    @ColumnInfo(name = "restaurantId")
    private int restaurantId;
    @ColumnInfo(name = "fbid")
    private String fbid;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "veg")
    private boolean veg;
    @ColumnInfo(name = "votes")
    private int votes;
    @ColumnInfo(name = "ratings")
    private Double ratings;


    public MyCartItem() {
    }

    @NonNull
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(@NonNull String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public int getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(int foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getFbid() {
        return fbid;
    }

    public void setFbid(String fbid) {
        this.fbid = fbid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVeg() {
        return veg;
    }

    public void setVeg(boolean veg) {
        this.veg = veg;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Double getRatings() {
        return ratings;
    }

    public void setRatings(Double ratings) {
        this.ratings = ratings;
    }
}
