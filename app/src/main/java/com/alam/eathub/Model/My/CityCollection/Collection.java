package com.alam.eathub.Model.My.CityCollection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Collection {

    @SerializedName("collection")
    @Expose
    private Collection__1 collection;

    public Collection__1 getCollection() {
        return collection;
    }

    public void setCollection(Collection__1 collection) {
        this.collection = collection;
    }

}