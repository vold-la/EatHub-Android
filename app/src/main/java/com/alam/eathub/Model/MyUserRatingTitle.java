package com.alam.eathub.Model;

public class MyUserRatingTitle {
    private  String text , bg_color ;
    private int votes;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBg_color() {
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
