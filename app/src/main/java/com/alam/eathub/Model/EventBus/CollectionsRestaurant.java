package com.alam.eathub.Model.EventBus;

import com.alam.eathub.Model.My.Rrestaurant;

import java.util.List;

public class CollectionsRestaurant {
    private List<Rrestaurant> collectionRestaurant;
    private String title;
    private String description;
    private int numOfPlaces;
    private String imageUrl;

    public CollectionsRestaurant(List<Rrestaurant> collectionRestaurant, String title, String description, int numOfPlaces, String imageUrl) {
        this.collectionRestaurant = collectionRestaurant;
        this.title = title;
        this.description = description;
        this.numOfPlaces = numOfPlaces;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumOfPlaces() {
        return numOfPlaces;
    }

    public void setNumOfPlaces(int numOfPlaces) {
        this.numOfPlaces = numOfPlaces;
    }

    public List<Rrestaurant> getCollectionRestaurant() {
        return collectionRestaurant;
    }

    public void setCollectionRestaurant(List<Rrestaurant> collectionRestaurant) {
        this.collectionRestaurant = collectionRestaurant;
    }
}
