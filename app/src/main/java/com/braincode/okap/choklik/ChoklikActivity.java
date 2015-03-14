package com.braincode.okap.choklik;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ChoklikActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choklik);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choklik, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public static final String TAG = "PlaceholderFragment";

        ListView itemsListView;
        ArrayList<Offer> offers;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setRetainInstance(true);
            new FetchSearchResultsTask().execute("samsung");
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_choklik, container, false);

            itemsListView = (ListView)rootView.findViewById(R.id.itemsListView);

            setupAdapter();

            return rootView;
        }

        private void setupAdapter() {
            if (getActivity() == null || itemsListView == null) return;

            if (offers != null) {
                itemsListView.setAdapter(new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, offers));
            } else {
                itemsListView.setAdapter(null);
            }
        }

        private class FetchSearchResultsTask extends AsyncTask<String, Void, ArrayList<Offer>> {
            @Override
            protected ArrayList<Offer> doInBackground(String... params) {
                return new AllegroClient().fetchSearchResults(params[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<Offer> offerList) {
                offers = offerList;
                setupAdapter();
            }
        }
    }
}
