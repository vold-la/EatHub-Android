package com.alam.eathub.Model.EventBus;

public class CartChangeEvent {
    boolean success;

    public CartChangeEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
