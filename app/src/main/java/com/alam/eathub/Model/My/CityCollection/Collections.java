
package com.alam.eathub.Model.My.CityCollection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collections {

    @SerializedName("err")
    @Expose
    private Boolean err;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("collections")
    @Expose
    private List<Collection> collections = null;

    public Boolean getErr() {
        return err;
    }

    public void setErr(Boolean err) {
        this.err = err;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

}