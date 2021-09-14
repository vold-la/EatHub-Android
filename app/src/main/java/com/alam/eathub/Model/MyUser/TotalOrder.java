
package com.alam.eathub.Model.MyUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TotalOrder {

    @SerializedName("dish")
    @Expose
    private String dish;
    @SerializedName("cost")
    @Expose
    private String cost;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("veg")
    @Expose
    private Boolean veg;
    @SerializedName("votes")
    @Expose
    private Integer votes;
    @SerializedName("ratings")
    @Expose
    private Double ratings;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getVeg() {
        return veg;
    }

    public void setVeg(Boolean veg) {
        this.veg = veg;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Double getRatings() {
        return ratings;
    }

    public void setRatings(Double ratings) {
        this.ratings = ratings;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
