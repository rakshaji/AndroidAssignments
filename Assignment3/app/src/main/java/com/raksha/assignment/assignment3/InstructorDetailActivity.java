package com.raksha.assignment.assignment3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * An activity representing a single Instructor detail screen.
 */
public class InstructorDetailActivity extends ActionBarActivity {

    private static final String TAG = InstructorDetailActivity.class.toString();
    private ProgressBar infoProgressBar;
    private ProgressBar reviewProgressBar;
    private String selectedInstructorId;
    private ListView reviews;
    private InstructorDetailActivity detailActivity;
    private RatingBar ratingBar;
    private DatabaseHelper databaseHelper;
    private HttpClient httpclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_detail);
        showAppIcon();

        databaseHelper = DatabaseHelper.getInstance(this);
        httpclient = AndroidHttpClient.newInstance(null);

        detailActivity = this;
        selectedInstructorId = getIntent().getStringExtra(InstructorListActivity.ARG_ITEM_ID);

        reviews = (ListView) findViewById(R.id.listView);
        infoProgressBar = (ProgressBar)findViewById(R.id.infoProgress);
        reviewProgressBar = (ProgressBar)findViewById(R.id.reviewProgress);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingClickListener());

        if(isNetworkAvailable()) {
            // fetch selected instructor's details in a separate thread
            InstructorInfoHttpClientTask instructorInfoHttpClientTask = new InstructorInfoHttpClientTask();
            instructorInfoHttpClientTask.execute();

            // fetch all reviews for selected instructor in a separate thread
            AllReviewsHttpClientTask getReviewTask = new AllReviewsHttpClientTask();
            getReviewTask.execute();

        } else {
            // fetch data from database & update ui
            InstructorInfo instructorInfo = databaseHelper.getInstructorInfo(selectedInstructorId);
            updateInfoInUI(instructorInfo);

            // update reviews in UI
            ArrayList<UserReviews> userReviews = databaseHelper.getAllReviews();
            ArrayList<Review> reviewArrayList = new ArrayList<>();
            for(UserReviews review : userReviews) {
                reviewArrayList.add(new Review(review.getComment(), review.getDate()));
            }
            reviews.setAdapter(new ReviewListAdapter(detailActivity, reviewArrayList));

            infoProgressBar.setVisibility(View.GONE);
            reviewProgressBar.setVisibility(View.GONE);
        }
    }

    private void showAppIcon() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class InstructorInfoHttpClientTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            infoProgressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... arg0) {
            String url = AppConstants.GET_INSTRUCTOR_DETAILS_URL + selectedInstructorId;
            return getDataFromURL(url, TAG);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);

            if(jsonString != null && !jsonString.equals(AppConstants.BLANK_STRING)) {
                // update UI
                final InstructorInfo instructorInfo = Utility.getInstructorInfoFromJSON(jsonString);
                infoProgressBar.setVisibility(View.GONE);
                updateInfoInUI(instructorInfo);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // update database with new data
                        databaseHelper.updateInstructorInfo(instructorInfo);
                    }
                }).start();

            }
        }
    }

    private  void updateInfoInUI(InstructorInfo instructorInfo){

        if(instructorInfo.getInstructorId() != 0) {
            TextView nameView = (TextView) findViewById(R.id.instructorName);
            nameView.setText(String.format(AppConstants.FULL_NAME_FORMAT, instructorInfo.getFirstName()
                    , instructorInfo.getLastName()));

            TextView officeView = (TextView) findViewById(R.id.instructorOffice);
            officeView.setText(instructorInfo.getOffice());

            TextView phoneView = (TextView) findViewById(R.id.instructorPhone);
            phoneView.setText(instructorInfo.getPhone());

            TextView emailView = (TextView) findViewById(R.id.instructorEmail);
            emailView.setText(instructorInfo.getEmail());

            TextView avgRatingView = (TextView) findViewById(R.id.average_rating);
            if(!instructorInfo.getAverageRating().equals(AppConstants.BLANK_STRING)) {
                avgRatingView.setText(String.format(AppConstants.AVG_RATING_FORMAT, instructorInfo.getAverageRating()
                        , instructorInfo.getTotalReviews()));
            }
        } else {
            // prompt user that this action cannot be done since he/she is offline.
            AlertDialog.Builder alert = new AlertDialog.Builder(InstructorDetailActivity.this);
            alert.setTitle(R.string.error_title)
                    .setMessage(R.string.no_internet_error)
                    .setCancelable(true)
                    .setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alert.show();
            return;
        }
    }

    class AllReviewsHttpClientTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reviewProgressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... arg0) {
            // fetch reviews
            String url = AppConstants.GET_REVIEWS_URL + selectedInstructorId;
            return getDataFromURL(url, TAG);
        }

        @Override
        protected void onPostExecute(final String jsonString) {
            super.onPostExecute(jsonString);

            // update UI
            TextView noReview = (TextView)findViewById(R.id.noReviewLabel);
            try {
                JSONArray dataArray = new JSONArray(jsonString);
                if(dataArray.length() == 0){
                    noReview.setVisibility(View.VISIBLE);
                    return;
                }

                noReview.setVisibility(View.INVISIBLE);

                ArrayList<Review> reviewList = new ArrayList<>();
                for(int i = 0; i < dataArray.length(); i++){
                    JSONObject review = (JSONObject) dataArray.get(i);
                    String comment = review.getString(AppConstants.COMMENT_TEXT);
                    String date = review.getString(AppConstants.COMMENT_DATE);
                    reviewList.add(new Review(comment, date));
                }
                reviews.setAdapter(new ReviewListAdapter(detailActivity, reviewList));

            } catch (JSONException e) {
                Log.e(TAG, null, e);
            }

            reviewProgressBar.setVisibility(View.GONE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // update database with new data
                    Utility.insertReviews(jsonString, databaseHelper, selectedInstructorId);
                }
            }).start();

        }
    }

    class SubmitCommentRatingHttpClientTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {
            String rating = null;
            String commentText = null;

            if(args.length > 0){
                rating = args[0];
            }
            if(args.length > 1) {
                commentText = args[1];
            }

            // post comment to the server
            String responseBody = null;
            if(commentText != null && !commentText.equals(AppConstants.BLANK_STRING)) {
                String url = AppConstants.POST_COMMENT_URL + selectedInstructorId;
                responseBody = postDataToURL(url, TAG, commentText);
            }

            // post rating to the server
            if(rating != null && !rating.equals("")) {
                String url = AppConstants.POST_RATING_URL + selectedInstructorId
                        + File.separator + rating;
                responseBody = postDataToURL(url, TAG, null);

                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    jsonObject.put(AppConstants.COMMENT_TEXT, commentText);
                    responseBody = jsonObject.toString();
                }catch (JSONException e){
                    Log.e(TAG, null, e);
                }
            }

            return responseBody;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            String commentText = null;
            String averageRate = null;
            String totalRates = null;
            final UserReviews userReviews;
            final InstructorInfo instructorInfo;

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                averageRate = jsonObject.getString(AppConstants.AVERAGE_RATING);
                totalRates = jsonObject.getString(AppConstants.TOTAL_RATING);
                commentText = jsonObject.getString(AppConstants.COMMENT_TEXT);
            }
            catch (JSONException t){
                Log.e(TAG, null, t);
            }

            // update comment to list instead of fetching from network
            String time = null;
            if(commentText != null && !commentText.equals(AppConstants.BLANK_STRING)) {
                SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_FORMAT);
                Date date = new Date();
                time = sdf.format(date);
                ReviewListAdapter.reviews.add(0, new Review(commentText, time));
                ((ReviewListAdapter) reviews.getAdapter()).notifyDataSetChanged();

                // update database with new data
                userReviews = new UserReviews();
                userReviews.setComment(commentText);
                userReviews.setDate(time);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // update database
                        databaseHelper.insertUserReviews(userReviews);
                    }
                }).start();
            }

            // update average ratings to list instead of fetching from network
            TextView averageRatingView = (TextView)findViewById(R.id.average_rating);
            averageRatingView.setText(String.format(AppConstants.AVG_RATING_FORMAT, averageRate, totalRates));

            instructorInfo = new InstructorInfo();
            instructorInfo.setInstructorId(Integer.valueOf(selectedInstructorId).intValue());
            instructorInfo.setAverageRating(averageRate);
            instructorInfo.setTotalReviews(Integer.valueOf(totalRates).intValue());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // update database
                    databaseHelper.updateInstructorInfo(instructorInfo);
                }
            }).start();
        }
    }

    class RatingClickListener implements RatingBar.OnRatingBarChangeListener {

        @Override
        public void onRatingChanged(final RatingBar ratingBar, final float rating, boolean fromUser) {
            if(!fromUser)
                return;

            if (!isNetworkAvailable()) {
                // prompt user that this action cannot be done since he is offline.
                AlertDialog.Builder alert = new AlertDialog.Builder(InstructorDetailActivity.this);
                alert.setTitle(R.string.error_title)
                        .setMessage(R.string.no_internet_error)
                        .setCancelable(true)
                        .setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // reset the rating bar
                                ratingBar.setRating(0.0f);
                                dialog.cancel();
                            }
                        });
                alert.show();
                return;
            }

            AlertDialog.Builder addCommentDialog = new AlertDialog.Builder(InstructorDetailActivity.this);

            final EditText commentEditText = new EditText(InstructorDetailActivity.this);
            commentEditText.setHint(R.string.enter_comment_hint);
            commentEditText.setMaxLines(5);
            commentEditText.setLines(5);

            addCommentDialog.setView(commentEditText)
                    .setTitle(getString(R.string.comment_title))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.submit_btn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // reset the rating bar
                            ratingBar.setRating(0.0f);
                            dialog.cancel();

                            // save the rating and comment(if any)
                            SubmitCommentRatingHttpClientTask commentHttpClientTask = new SubmitCommentRatingHttpClientTask();
                            commentHttpClientTask.execute(String.valueOf(rating), commentEditText.getText().toString());
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // reset the rating bar
                            ratingBar.setRating(0.0f);
                            dialog.cancel();
                        }
                    });
            addCommentDialog.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getDataFromURL(String url, String TAG){
        HttpGet getMethod = new HttpGet(url);
        String responseBody = null;
        try {
            ResponseHandler<String> responseHandler =
                    new BasicResponseHandler();
            responseBody = httpclient.execute(getMethod,
                responseHandler);
        } catch (Throwable t) {
            Log.e(TAG, null, t);
        }
        return responseBody;
    }

    public String postDataToURL(String url, String TAG, String data){
        HttpPost httpPost = new HttpPost(url);
        String responseBody = null;

        try {
            ResponseHandler<String> responseHandler =
                    new BasicResponseHandler();
            if(data != null) {
                httpPost.setEntity(new StringEntity(data));
            }
            responseBody = httpclient.execute(httpPost,
                responseHandler);
        } catch (Throwable t) {
            Log.e(TAG, null, t);
        }
        return responseBody;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(httpclient != null)
            httpclient.getConnectionManager().shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(httpclient == null)
            httpclient = AndroidHttpClient.newInstance(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(httpclient != null)
            httpclient.getConnectionManager().shutdown();
    }
}