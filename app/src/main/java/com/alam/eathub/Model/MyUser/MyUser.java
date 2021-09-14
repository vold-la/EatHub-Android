
package com.alam.eathub.Model.MyUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyUser {

    @SerializedName("err")
    @Expose
    private Boolean err;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
