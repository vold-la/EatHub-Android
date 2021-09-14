package com.alam.eathub.Model;

import android.util.Log;

public class BraintreeToken {
    private boolean success;
    private  String clientToken;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }
}
