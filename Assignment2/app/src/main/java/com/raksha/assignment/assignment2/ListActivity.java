package com.raksha.assignment.assignment2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class ListActivity extends ActionBarActivity implements DessertsListFragment.DessertsListSelectionListener {
    private int selectedItemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // show app icon
        showAppIcon();

        // add click listener on back button
        addClickListenerToBackButton();

        // initialize default selection for list with the item selected in main activity
        setDefaultSelectionForList();
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

    private void goBack() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppConstants.SELECTED_DESSERT_POSITION, selectedItemPosition);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onDessertSelected(int position) {
        selectedItemPosition = position;
    }

    public void setDefaultSelectionForList(){
        Intent callerActivityIntent = getIntent();
        selectedItemPosition = callerActivityIntent.getIntExtra(AppConstants.SELECTED_DESSERT_POSITION, selectedItemPosition);

        DessertsListFragment dessertsListFragment = (DessertsListFragment)(getFragmentManager().findFragmentById(R.id.list_fragment));
        if (dessertsListFragment != null) {
            // Call a method in the DessertsListFragment to update its content
            dessertsListFragment.setSelectedItem(selectedItemPosition);
        }
    }
}
