package com.alam.eathub.Model.EventBus;

import com.alam.eathub.Model.Category;

public class FoodListEvent {
    private boolean success;
    private Category category;

    public FoodListEvent(boolean success, Category category) {
        this.success = success;
        this.category = category;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
