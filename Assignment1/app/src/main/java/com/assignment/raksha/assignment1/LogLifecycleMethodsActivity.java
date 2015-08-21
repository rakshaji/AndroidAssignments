package com.assignment.raksha.assignment1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import java.util.ArrayList;

public class LogLifecycleMethodsActivity extends ActionBarActivity {

    private static final String TAG = LogLifecycleMethodsActivity.class.toString();
    private static final String LAST_SAVED_LIFECYCLE_METHODS = "LAST_SAVED_LIFECYCLE_METHODS";
    private static final String LINE_SEPARATOR = "\n";

    private ArrayList<String> loggedLifecycleMethodNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set view
        setContentView(R.layout.activity_log);
        TextView text = (TextView)findViewById(R.id.logText);
        text.setMovementMethod(new ScrollingMovementMethod());

        // show last saved logged lifecycle methods
        if(savedInstanceState != null && savedInstanceState.size() > 0) {
            ArrayList<String> savedLifecycleMethods = savedInstanceState.getStringArrayList(LAST_SAVED_LIFECYCLE_METHODS);

            if (savedLifecycleMethods != null && savedLifecycleMethods.size() > 0) {
                // initialize list with last saved methods
                loggedLifecycleMethodNames.addAll(savedLifecycleMethods);

                for (String lifecycleMethodName : savedLifecycleMethods) {
                    showLifecycleMethodNameOnScreen(lifecycleMethodName);
                }
            }
        }

        logAndShowLifecycleMethodName(getString(R.string.on_create));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logAndShowLifecycleMethodName(getString(R.string.on_restart));
    }

    @Override
    protected void onStart() {
        super.onStart();
        logAndShowLifecycleMethodName(getString(R.string.on_start));
    }

    @Override
    protected void onPause() {
        super.onPause();
        logAndShowLifecycleMethodName(getString(R.string.on_pause));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        logAndShowLifecycleMethodName(getString(R.string.on_save_instance_state));
        // save all the activities logged till now to the bundle
        outState.putStringArrayList(LAST_SAVED_LIFECYCLE_METHODS, loggedLifecycleMethodNames);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logAndShowLifecycleMethodName(getString(R.string.on_restore_instance_state));
    }

    @Override
    protected void onResume() {
        super.onResume();
        logAndShowLifecycleMethodName(getString(R.string.on_resume));
    }

    /**
     * This method clears logged activities from the screen
     * @param view, clicked view
     */
    public void clearTextView(View view) {
        TextView text = (TextView)findViewById(R.id.logText);
        text.setText(R.string.clear_text);
        // also clear the list of activities
        loggedLifecycleMethodNames.clear();
    }

    /**
     * This method logs lifecycle method name to the LogCat and also shows into the screen
     * @param lifecycleMethodName, lifecycle method name
     */
    private void logAndShowLifecycleMethodName(String lifecycleMethodName) {
        logLifecycleMethodName(lifecycleMethodName);
        showLifecycleMethodNameOnScreen(lifecycleMethodName);
        loggedLifecycleMethodNames.add(lifecycleMethodName);
    }

    /**
     * This method logs lifecycle method name to the LogCat
     * @param lifecycleMethodName, lifecycle method name
     */
    private void logLifecycleMethodName(String lifecycleMethodName) {
        Log.d(TAG, lifecycleMethodName);
    }

    /**
     * This method logs activity to the view/screen
     * @param lifecycleMethodName, lifecycle method name
     */
    private void showLifecycleMethodNameOnScreen(String lifecycleMethodName) {
        TextView text = (TextView)findViewById(R.id.logText);
        text.append(lifecycleMethodName + LINE_SEPARATOR);
    }
}