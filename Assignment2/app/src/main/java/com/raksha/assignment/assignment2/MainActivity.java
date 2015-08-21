package com.raksha.assignment.assignment2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements DessertsListFragment.DessertsListSelectionListener {

    private Spinner spinner;
    private int selectedDessertPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // show app icon
        showAppIcon();

        // set data in spinner
        spinner = (Spinner)findViewById(R.id.spinner);

        // populate spinner with data
        populateSpinner();

        // 'Activate' button click Listener
        addListenerOnButton();

        // register context menu on desserts list
        DessertsListFragment dessertsListFragment = (DessertsListFragment)(getFragmentManager().findFragmentById(R.id.main_list_fragment));
        registerForContextMenu(dessertsListFragment.getListView());
    }

    private void populateSpinner() {
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.date_activity));
        list.add(getString(R.string.keyboard_activity));
        list.add(getString(R.string.list_activity));

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    /**
     * Shows application icon
     */
    private void showAppIcon() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // add items to context menu
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.context_menu);
        menu.add(0, v.getId(), 0, R.string.delete_context_menu);
    }

    /**
     * Get the selected dropdown list value
     */
    public void addListenerOnButton() {
        Button activateBtn = (Button) findViewById(R.id.activateButton);
        activateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String selectedItem = String.valueOf(spinner.getSelectedItem());

                // open activity based on selected item in spinner
                if(selectedItem.equals(getString(R.string.date_activity)))
                {
                    startActivityForResult(getActivityIntent(DateActivity.class), AppConstants.DATE_ACTIVITY_REQUEST_CODE);
                }
                else if(selectedItem.equals(getString(R.string.keyboard_activity)))
                {
                    startActivity(getActivityIntent(KeyboardActivity.class));
                }
                else
                {
                    startActivityForResult(getActivityIntent(ListActivity.class), AppConstants.LIST_ACTIVITY_REQUEST_CODE);
                }
            }
        });
    }

    private Intent getActivityIntent(Class aClass){
        EditText editTextField = (EditText)findViewById(R.id.editText);
        String editText = editTextField.getText().toString();

        Intent selectedActivityIntent = new Intent(MainActivity.this, aClass);
        selectedActivityIntent.putExtra(AppConstants.EDIT_TEXT_VALUE, editText);
        selectedActivityIntent.putExtra(AppConstants.SELECTED_DESSERT_POSITION, selectedDessertPosition);
        return selectedActivityIntent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case AppConstants.DATE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    String date = data.getStringExtra(AppConstants.SELECTED_DATE);

                    EditText editTextField = (EditText)findViewById(R.id.editText);
                    editTextField.setText(date);
                }
                break;
            }
            case AppConstants.LIST_ACTIVITY_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    selectedDessertPosition = data.getIntExtra(AppConstants.SELECTED_DESSERT_POSITION, -1);

                    if(selectedDessertPosition != -1) {
                        updateListSelection();
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
            case R.id.action_date_activity:
                startActivityForResult(getActivityIntent(DateActivity.class), AppConstants.DATE_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.action_keyboard_activity:
                startActivity(getActivityIntent(KeyboardActivity.class));
                return true;
            case R.id.action_list_activity:
                startActivityForResult(getActivityIntent(ListActivity.class), AppConstants.LIST_ACTIVITY_REQUEST_CODE);
                return true;
        }
        return true;
    }

    @Override
    public void onDessertSelected(int position) {
        selectedDessertPosition = position;
    }

    public void updateListSelection(){
        DessertsListFragment dessertsListFragment = (DessertsListFragment)(getFragmentManager().findFragmentById(R.id.main_list_fragment));

        if (dessertsListFragment != null) {
            // Call a method in the DessertsListFragment to update its content
            dessertsListFragment.updateListSelection(selectedDessertPosition);
        }
    }

}
