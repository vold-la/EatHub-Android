package com.alam.eathub.Model.EventBus;

import com.alam.eathub.Model.My.Rrestaurant;

public class MyMenuEvent {
    private boolean success;
    private Rrestaurant rrestaurant;

    public MyMenuEvent(boolean success, Rrestaurant rrestaurant) {
        this.success = success;
        this.rrestaurant = rrestaurant;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Rrestaurant getRrestaurant() {
        return rrestaurant;
    }

    public void setRrestaurant(Rrestaurant rrestaurant) {
        this.rrestaurant = rrestaurant;
    }
}
