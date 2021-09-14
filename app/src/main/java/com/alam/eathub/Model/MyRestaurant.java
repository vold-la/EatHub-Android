package com.alam.eathub.Model;

import java.util.List;

public class MyRestaurant {
    String id , name , thumb , currency , featured_image , price_range , timings ;
    private List<String> establishment;
    private List<String> highlights;
    private  int average_cost_for_two , has_online_delivery
            , is_delivering_now , has_table_booking , all_reviews_count , photo_count ;
    private List<String> cuisines;
  //  private List<String> collection_id;
    private List<AllReviews> all_reviews;
    private List<MyMenu> menu;
    private List<Integer> collection_id;
    private List<MyLocation> locations;
    private List<MyUserRating> user_rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFeatured_image() {
        return featured_image;
    }

    public void setFeatured_image(String featured_image) {
        this.featured_image = featured_image;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public List<String> getEstablishment() {
        return establishment;
    }

    public void setEstablishment(List<String> establishment) {
        this.establishment = establishment;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public int getAverage_cost_for_two() {
        return average_cost_for_two;
    }

    public void setAverage_cost_for_two(int average_cost_for_two) {
        this.average_cost_for_two = average_cost_for_two;
    }

    public int getHas_online_delivery() {
        return has_online_delivery;
    }

    public void setHas_online_delivery(int has_online_delivery) {
        this.has_online_delivery = has_online_delivery;
    }

    public int getIs_delivering_now() {
        return is_delivering_now;
    }

    public void setIs_delivering_now(int is_delivering_now) {
        this.is_delivering_now = is_delivering_now;
    }

    public int getHas_table_booking() {
        return has_table_booking;
    }

    public void setHas_table_booking(int has_table_booking) {
        this.has_table_booking = has_table_booking;
    }

    public int getAll_reviews_count() {
        return all_reviews_count;
    }

    public void setAll_reviews_count(int all_reviews_count) {
        this.all_reviews_count = all_reviews_count;
    }

    public int getPhoto_count() {
        return photo_count;
    }

    public void setPhoto_count(int photo_count) {
        this.photo_count = photo_count;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    public List<AllReviews> getAll_reviews() {
        return all_reviews;
    }

    public void setAll_reviews(List<AllReviews> all_reviews) {
        this.all_reviews = all_reviews;
    }

    public List<MyMenu> getMenu() {
        return menu;
    }

    public void setMenu(List<MyMenu> menu) {
        this.menu = menu;
    }

    public List<Integer> getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(List<Integer> collection_id) {
        this.collection_id = collection_id;
    }

    public List<MyLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<MyLocation> locations) {
        this.locations = locations;
    }

    public List<MyUserRating> getUser_rating() {
        return user_rating;
    }

    public void setUser_rating(List<MyUserRating> user_rating) {
        this.user_rating = user_rating;
    }
}
