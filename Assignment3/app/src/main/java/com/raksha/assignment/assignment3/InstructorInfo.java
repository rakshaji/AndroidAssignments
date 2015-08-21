package com.raksha.assignment.assignment3;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raksha on 3/13/2015.
 */
public class InstructorInfo {
    private int instructorId;
    private String firstName;
    private String lastName;
    private String office;
    private String email;
    private String phone;
    private String averageRating;
    private int totalReviews;
    private UserReviews ratingAndReviews;

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public UserReviews getRatingAndReviews() {
        return ratingAndReviews;
    }

    public void setRatingAndReviews(UserReviews ratingAndReviews) {
        this.ratingAndReviews = ratingAndReviews;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", instructorId);
            object.put("firstname", firstName);
            object.put("lastname", lastName);
            object.put("phone", phone);
            object.put("office", office);
            object.put("email", email);
            object.put("averageRate", averageRating);
            object.put("totalReviews", totalReviews);
        }catch (JSONException e){
            Log.e(InstructorInfo.class.toString(), object.toString());
        }

        return  object.toString();
    }
}
