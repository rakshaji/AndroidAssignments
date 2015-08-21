package com.raksha.assignment.assignment2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


//import com.raksha.assignment.assignment2.dummy.Desserts;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class DessertsListFragment extends ListFragment {

    private int selectedItem = -1;
    private ArrayList dessertsList;
    private DessertsListSelectionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
  java.lang.String   */
    public DessertsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // populate list with desserts
        populateList();
    }

    private void populateList() {
        dessertsList = new ArrayList<String>();
        dessertsList.add(getString(R.string.item_cupcake));
        dessertsList.add(getString(R.string.item_donut));
        dessertsList.add(getString(R.string.item_gingerbread));
        dessertsList.add(getString(R.string.item_ice_cream));
        dessertsList.add(getString(R.string.item_jellybean));

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, dessertsList));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (DessertsListSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DessertsListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        l.setItemChecked(position, true);
        selectedItem = position;

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onDessertSelected(position);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (selectedItem != -1) {
            updateListSelection(selectedItem);
        }
    }

    public void updateListSelection(int position){
        getListView().performItemClick(getListView().getAdapter().getView(position, null, null), position, position);
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public interface DessertsListSelectionListener {
        void onDessertSelected(int position);
    }
}
