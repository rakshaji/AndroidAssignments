package com.raksha.assignment.assignment2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class KeyboardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        // show app icon
        showAppIcon();

        // pre-populate top edit text with value from main activity
        populateData();

        addClickListenerToBackButton();
        addClickListenerToHideButton();
    }

    private void populateData() {
        // pre-populate top edit text with value from main activity
        Intent callerActivityIntent = getIntent();
        String text = callerActivityIntent.getStringExtra(AppConstants.EDIT_TEXT_VALUE);
        EditText editTextFieldTop = (EditText)findViewById(R.id.editTextTop);
        editTextFieldTop.setText(text);
    }

    private void addClickListenerToHideButton() {
        Button hideBtn = (Button) findViewById(R.id.hideBtn);
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
    }

    private void addClickListenerToBackButton() {
        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void showAppIcon() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    private void goBack(){
        hideKeyboard();
        finish();
    }

    private void hideKeyboard(){
        EditText editTextFieldTop = (EditText)findViewById(R.id.editTextTop);
        EditText editTextFieldMiddle = (EditText)findViewById(R.id.editTextMiddle);
        EditText editTextFieldBottom = (EditText)findViewById(R.id.editTextBottom);

        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextFieldTop.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editTextFieldMiddle.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editTextFieldBottom.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
        }
        return true;
    }

}
