package com.alam.eathub.Model.My;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityId {

    @SerializedName("err")
    @Expose
    private Boolean err;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("city_id")
    @Expose
    private String cityId;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
