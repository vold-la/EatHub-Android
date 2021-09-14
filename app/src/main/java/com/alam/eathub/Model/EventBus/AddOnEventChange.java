package com.alam.eathub.Model.EventBus;

import com.alam.eathub.Model.Addon;

public class AddOnEventChange {
    private boolean isAdd;
    private Addon addon;

    public AddOnEventChange(boolean isAdd, Addon addon) {
        this.isAdd = isAdd;
        this.addon = addon;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public Addon getAddon() {
        return addon;
    }

    public void setAddon(Addon addon) {
        this.addon = addon;
    }
}
