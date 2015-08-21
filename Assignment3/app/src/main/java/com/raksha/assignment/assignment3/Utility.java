package com.raksha.assignment.assignment3;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raksha on 3/14/2015.
 */
public class Utility {

    private static final String TAG = Utility.class.toString();

    public static void insertReviews(String responseBody, DatabaseHelper databaseHelper, String instructorId) {
        // parse the response and save to database
        try {
            JSONArray dataArray = new JSONArray(responseBody);
            if(dataArray.length() == 0){
                return;
            }

            databaseHelper.deleteAllReviews(instructorId);

            UserReviews ratingAndReviews = new UserReviews();
            for(int i = 0; i < dataArray.length(); i++){
                JSONObject review = (JSONObject) dataArray.get(i);
                ratingAndReviews.setComment(review.getString(AppConstants.COMMENT_TEXT));
                ratingAndReviews.setDate(review.getString(AppConstants.COMMENT_DATE));
                ratingAndReviews.setInstructorId(Integer.valueOf(instructorId).intValue());
                databaseHelper.insertUserReviews(ratingAndReviews);
            }
        }
        catch (JSONException e) {
            Log.e(TAG, null, e);
        }
    }

    public static InstructorInfo getInstructorInfoFromJSON(String jsonString){
        InstructorInfo instructorInfo = new InstructorInfo();
        try {
            JSONObject data = new JSONObject(jsonString);
            if(data.length() == 0){
                return null;
            }

            // parse json data
            instructorInfo.setInstructorId(Integer.valueOf(data.getString(AppConstants.INSTRUCTOR_ID)).intValue());
            instructorInfo.setOffice(data.getString(AppConstants.INSTRUCTOR_OFFICE));
            instructorInfo.setPhone(data.getString(AppConstants.INSTRUCTOR_PHONE));
            instructorInfo.setEmail(data.getString(AppConstants.INSTRUCTOR_EMAIL));
            instructorInfo.setFirstName(data.getString(AppConstants.INSTRUCTOR_FIRST_NAME));
            instructorInfo.setLastName(data.getString(AppConstants.INSTRUCTOR_LAST_NAME));

            String ratingJsonString = data.getString(AppConstants.INSTRUCTOR_RATING);
            JSONObject ratingJsonData = new JSONObject(ratingJsonString);

            instructorInfo.setAverageRating(ratingJsonData.getString(AppConstants.AVERAGE_RATING));
            instructorInfo.setTotalReviews(Integer.valueOf(ratingJsonData.getString(AppConstants.TOTAL_RATING)).intValue());

        } catch (JSONException e) {
            Log.e(TAG, null, e);
        }
        return instructorInfo;
    }

}
