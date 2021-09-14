package com.alam.eathub.Model;

import java.util.List;

public class MyUserRating {

    private String aggregate_rating , rating_text , rating_color ;
    private List<MyUserRatingTitle> title;

    public String getAggregate_rating() {
        return aggregate_rating;
    }

    public void setAggregate_rating(String aggregate_rating) {
        this.aggregate_rating = aggregate_rating;
    }

    public String getRating_text() {
        return rating_text;
    }

    public void setRating_text(String rating_text) {
        this.rating_text = rating_text;
    }

    public String getRating_color() {
        return rating_color;
    }

    public void setRating_color(String rating_color) {
        this.rating_color = rating_color;
    }

    public List<MyUserRatingTitle> getTitle() {
        return title;
    }

    public void setTitle(List<MyUserRatingTitle> title) {
        this.title = title;
    }
}
