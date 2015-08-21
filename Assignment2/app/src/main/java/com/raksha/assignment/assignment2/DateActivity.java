package com.raksha.assignment.assignment2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class DateActivity extends ActionBarActivity {

    private String selectedDate;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        // show app icon
        showAppIcon();

        // show last saved data in date picker
        showLastSavedDate();

        // add click listener to the 'set' button
        addClickListenerOnSetButton();
    }

    private void showAppIcon() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    private void showLastSavedDate() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, 0);
        int day = sharedPreferences.getInt(AppConstants.DAY, 0);
        int month = sharedPreferences.getInt(AppConstants.MONTH, 0);
        int year = sharedPreferences.getInt(AppConstants.YEAR, 0);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        if (day != 0) {
            datePicker.updateDate(year, month, day);
        }
    }

    private void addClickListenerOnSetButton() {
        Button setBtn = (Button) findViewById(R.id.setButton);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = (datePicker.getMonth()+1)
                        + AppConstants.DATE_SEPARATOR + datePicker.getDayOfMonth()
                        + AppConstants.DATE_SEPARATOR + datePicker.getYear();
                String message = String.format(getString(R.string.confirm_date_title), selectedDate);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DateActivity.this);
                alertDialog.setTitle(R.string.confirm_title)
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(AppConstants.DAY, datePicker.getDayOfMonth());
                                editor.putInt(AppConstants.MONTH, datePicker.getMonth());
                                editor.putInt(AppConstants.YEAR, datePicker.getYear());
                                editor.commit();

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(AppConstants.SELECTED_DATE, selectedDate);
                                setResult(Activity.RESULT_OK, resultIntent);
                            }
                        })
                        .setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void goBack() {
        finish();
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
