
package com.alam.eathub.Model.MyUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("type")
    @Expose
    private Object type;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

}
