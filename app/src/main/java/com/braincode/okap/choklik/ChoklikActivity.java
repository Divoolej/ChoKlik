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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
        ImageDownloader<ImageView> imageThread;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setRetainInstance(true);
            new FetchSearchResultsTask().execute("samsung");

            imageThread = new ImageDownloader<>();
            imageThread.start();
            imageThread.getLooper();
            Log.i(TAG, "Background thread started");
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

        @Override
        public void onDestroy() {
            super.onDestroy();
            imageThread.quit();
            Log.i(TAG, "Background thread destroyed");
        }

        private void setupAdapter() {
            if (getActivity() == null || itemsListView == null) return;

            if (offers != null) {
//                itemsListView.setAdapter(new ArrayAdapter<>(getActivity(),
//                        android.R.layout.simple_list_item_1, offers));
                itemsListView.setAdapter(new SingleOfferAdapter(offers));
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

        private class SingleOfferAdapter extends ArrayAdapter<Offer> {
            public SingleOfferAdapter(ArrayList<Offer> offers) {
                super(getActivity(), 0, offers);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater()
                            .inflate(R.layout.single_offer_layout, parent, false);
                }


                ImageView imageView = (ImageView)convertView
                        .findViewById(R.id.offerImage);
                imageView.setImageResource(R.drawable.loading);

                Offer offer = getItem(position);

                return convertView;
            }
        }
    }
}
