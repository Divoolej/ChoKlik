package com.braincode.okap.choklik;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChoklikActivity extends ActionBarActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choklik);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
//        EditText editText = new EditText(this);
//        editText.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setElevation(4);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        EditText editText = (EditText)findViewById(R.id.searchEditText);
        editText.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Widget_ActionBar_Title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement

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

        EditText editText;
        ImageButton searchButton;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            setRetainInstance(true);
//            setHasOptionsMenu(true);


            imageThread = new ImageDownloader<>(new Handler());
            imageThread.setListener(new ImageDownloader.Listener<ImageView>() {
                @Override
                public void onImageDownloaded(ImageView imageView, Bitmap image) {
                    if (isVisible()) {
                        imageView.setImageBitmap(image);
                    }
                }
            });

            editText = (EditText)getActivity().findViewById(R.id.searchEditText);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        performSearch(editText.getText().toString());
                        InputMethodManager imm = (InputMethodManager)getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });

            searchButton = (ImageButton)getActivity().findViewById(R.id.searchButton);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performSearch(editText.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            });

            imageThread.start();
            imageThread.getLooper();
            Log.i(TAG, "Background thread started");
        }

        public PlaceholderFragment() {
        }

//        @Override
//        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//            super.onCreateOptionsMenu(menu, inflater);
//            inflater.inflate(R.menu.menu_choklik, menu);
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.menu_search:
//                    getActivity().onSearchRequested();
//                    return true;
//                case R.id.menu_clear:
//                    return true;
//                default:
//                    return super.onOptionsItemSelected(item);
//            }
//        }

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

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            imageThread.clearQueue();
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

        public void performSearch(String searchQuery) {
            new FetchSearchResultsTask().execute(searchQuery);
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

                // Set the image:

                ImageView imageView = (ImageView)convertView
                        .findViewById(R.id.offerImage);
                imageView.setImageResource(R.drawable.camera);

                Offer offer = getItem(position);
                if (offer.getPhotoUrl() != "shit")
                    imageThread.queueImage(imageView, offer.getPhotoUrl());

                // Set the remaining fields:

                TextView descriptionView = (TextView)convertView
                        .findViewById(R.id.descriptionText);
                descriptionView.setText(offer.getDescription());

                if (offer.getEndingTimeString() != "") {
                    TextView remainingTimeView = (TextView) convertView
                            .findViewById(R.id.remainingTime);
                    remainingTimeView.setText(offer.getEndingTimeString());
                }

                TextView sellerNameView = (TextView)convertView
                        .findViewById(R.id.sellerName);
                sellerNameView.setText(offer.getSellerName());

                TextView auctionPrice, bidText, buyNowPrice, buyNowText;
                auctionPrice = (TextView)convertView.findViewById(R.id.auctionPrice);
                bidText = (TextView)convertView.findViewById(R.id.typeAuction);
                buyNowPrice = (TextView)convertView.findViewById(R.id.buyNowPrice);
                buyNowText = (TextView)convertView.findViewById(R.id.typeBuyNow);

                auctionPrice.setText("");
                bidText.setText("");
                buyNowPrice.setText("");
                buyNowText.setText("");

                if (offer.isAuction()) {
                    auctionPrice.setText(Double.toString(offer.getAuctionPrice()) + "zł");
                    bidText.setText("Licytacja od:");
                }

                if (offer.isBuyNow()) {
                    buyNowPrice.setText(Double.toString(offer.getBuyNowPrice()) + "zł");
                    buyNowText.setText("Kup teraz:");
                }

                convertView.setOnClickListener(new OfferClickListener(offer) {
                    @Override
                    public void onClick(View v) {
                        if (getMyOffer().getOfferUrl() != "") {
                            startActivity(new Intent(Intent.ACTION_VIEW)
                                    .setData(Uri.parse(getMyOffer().getOfferUrl())));
                        }
                    }
                });

                return convertView;
            }
        }
    }
}
