package com.raksha.assignment.assignment3;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class InstructorListActivity extends ActionBarActivity {

    private static final String TAG = InstructorListActivity.class.toString();
    public static final String ARG_ITEM_ID = "item_position";
    private ProgressBar progressSpinner;
    private ListView instructorList;
    private TreeMap<String, Integer> instructors;
    private DatabaseHelper databaseHelper;
    private BackgroundSyncLooperThread looperThread;
    private HttpClient httpclient;
    private ConcurrentHashMap<String, InstructorInfo> pendingInstructorsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_list);

        showAppIcon();

        databaseHelper = DatabaseHelper.getInstance(this);
        httpclient = AndroidHttpClient.newInstance(null);
        progressSpinner = (ProgressBar)findViewById(R.id.reviewProgress);

        instructorList = (ListView)findViewById(R.id.instructor_list);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1);
        instructorList.setAdapter(arrayAdapter);

        final Intent detailActivityIntent = new Intent(this, InstructorDetailActivity.class);
        instructorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = arrayAdapter.getItem(position);
                Integer instructorId = instructors.get(name);
                detailActivityIntent.putExtra(ARG_ITEM_ID, instructorId.toString());
                startActivity(detailActivityIntent);
            }
        });

        if(isNetworkAvailable()) {
            // fetch data from network
            FetchInstructorListTask task = new FetchInstructorListTask();
            task.execute();
        } else {
            // fetch data from database
            ArrayList<InstructorInfo> allInstructorInfo = databaseHelper.getAllInstructorInfo();
            if(allInstructorInfo != null) {
                instructors = new TreeMap<>();
                for (InstructorInfo info : allInstructorInfo) {
                    String fullName = String.format(AppConstants.FULL_NAME_FORMAT,
                            info.getFirstName(),
                            info.getLastName());
                    instructors.put(fullName, Integer.valueOf(info.getInstructorId()));
                }
                ((ArrayAdapter) instructorList.getAdapter()).addAll(instructors.keySet());
                progressSpinner.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showAppIcon() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_Instructors));
    }

    private class FetchInstructorListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            instructorList.setVisibility(View.INVISIBLE);
            progressSpinner.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... arg0) {
            String url = AppConstants.GET_ALL_INSTRUCTORS_URL;
            return getDataFromURL(url, TAG);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            instructors = new TreeMap<>();
            pendingInstructorsMap = new ConcurrentHashMap<>();

            try {
                JSONArray data = new JSONArray(jsonString);
                if(data.length() == 0){
                    return;
                }

                for(int i = 0; i < data.length(); i++){
                    JSONObject info = (JSONObject) data.get(i);

                    final InstructorInfo instructorInfo = new InstructorInfo();
                    instructorInfo.setFirstName(info.getString(AppConstants.INSTRUCTOR_FIRST_NAME));
                    instructorInfo.setLastName(info.getString(AppConstants.INSTRUCTOR_LAST_NAME));
                    instructorInfo.setInstructorId(Integer.valueOf(info.getString(AppConstants.INSTRUCTOR_ID)).intValue());

                    final int instructorId = instructorInfo.getInstructorId();
                    String fullName = String.format(AppConstants.FULL_NAME_FORMAT, instructorInfo.getFirstName()
                            , instructorInfo.getLastName());

                    // add each instructor to the TreeMap so that we can get a sorted list of instructors
                    // and also know their ids even when they are shuffled or sorted.
                    instructors.put(fullName, Integer.valueOf(instructorId));
                    pendingInstructorsMap.put(String.valueOf(instructorId), instructorInfo);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // update database with new data
                            databaseHelper.insertInstructorInfoIfNotExist(instructorInfo);
                        }
                    }).start();
                }
                ((ArrayAdapter)instructorList.getAdapter()).addAll(instructors.keySet());

                // start the background synchronization with database
                if(looperThread == null || !looperThread.isAlive()){
                    startLooper();
                }

            } catch (JSONException e) {
               Log.e(TAG, "JSONException", e);
            }

            instructorList.setVisibility(View.VISIBLE);
            progressSpinner.setVisibility(View.GONE);
        }
    }

    class BackgroundSyncLooperThread extends HandlerThread {
        public Handler handler;

        public BackgroundSyncLooperThread() {
            super("BackgroundSyncLooperThread");
        }

        @Override
        public void onLooperPrepared() {

            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    final int instructorId = msg.what;

                    if(!isNetworkAvailable()){
                        looperThread.quitSafely();
                    }

                    // save info
                    String url = AppConstants.GET_INSTRUCTOR_DETAILS_URL + instructorId;
                    String responseBody = getDataFromURL(url, TAG);
                    if(responseBody != null && !responseBody.equals(AppConstants.BLANK_STRING)) {
                        final InstructorInfo instructorInfo = Utility.getInstructorInfoFromJSON
                                (responseBody);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                databaseHelper.updateInstructorInfo(instructorInfo);
                            }
                        }).start();
                    }

                    // save all reviews
                    url = AppConstants.GET_REVIEWS_URL + instructorId;
                    final String response = getDataFromURL(url, TAG);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Utility.insertReviews(response, databaseHelper,
                                    String.valueOf(instructorId));
                        }
                    }).start();

                    // after the fetching of data for an instructor is completed,
                    // remove it from the pending list
                    pendingInstructorsMap.remove(String.valueOf(instructorId));
                }
            };
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(looperThread != null )
            looperThread.quitSafely();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // restart the background sync if was paused for some reason
        startLooper();
    }

    private void startLooper(){
        if(httpclient == null) {
            httpclient = AndroidHttpClient.newInstance(null);
        }

        if(pendingInstructorsMap != null && pendingInstructorsMap.size() > 0) {
            if(looperThread == null || !looperThread.isAlive()) {
                looperThread = new BackgroundSyncLooperThread();
                looperThread.start();
                looperThread.handler = new Handler();
            }

            Iterator it = pendingInstructorsMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                InstructorInfo instructorInfo = (InstructorInfo) pair.getValue();

                if (looperThread != null) {
                    looperThread.handler.sendEmptyMessage(instructorInfo.getInstructorId());
                }
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(httpclient != null)
            httpclient.getConnectionManager().shutdown();
    }
}
