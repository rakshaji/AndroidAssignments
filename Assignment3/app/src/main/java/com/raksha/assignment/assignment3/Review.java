package com.raksha.assignment.assignment3;

/**
 * Created by Raksha on 3/7/2015.
 */
public class Review {

    private String comment;
    private String date;

    public Review(String comment, String date){
        this.comment = comment;
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
