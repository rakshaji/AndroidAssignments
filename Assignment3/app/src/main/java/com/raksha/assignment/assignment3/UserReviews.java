package com.raksha.assignment.assignment3;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raksha on 3/13/2015.
 */
public class UserReviews {
    private int ratingId;
    private String comment;
    private String date;
    private int instructorId;

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
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

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", instructorId);
            object.put("comment", comment);
            object.put("date", date);
        }catch (JSONException e){
            Log.e(UserReviews.class.toString(), object.toString());
        }

        return  object.toString();
    }
}
