package com.alam.eathub.Model.EventBus;

import com.alam.eathub.MyDatabase.MyCartItem;

import java.util.List;

public class MyCartItemEvent {
    boolean  success;
    List<MyCartItem> cartItems;

    public MyCartItemEvent(boolean success , List<MyCartItem> cartItems) {
        this.success = success;
        this.cartItems = cartItems;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<MyCartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<MyCartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
