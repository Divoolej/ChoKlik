package com.braincode.okap.choklik;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setRetainInstance(true);
            new FetchSearchResultsTask().execute("samsung");

            imageThread = new ImageDownloader<>(new Handler());
            imageThread.setListener(new ImageDownloader.Listener<ImageView>() {
                @Override
                public void onImageDownloaded(ImageView imageView, Bitmap image) {
                    if (isVisible()) {
                        imageView.setImageBitmap(image);
                    }
                }
            });
            imageThread.start();
            imageThread.getLooper();
            Log.i(TAG, "Background thread started");
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_choklik, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_search:
                    getActivity().onSearchRequested();
                    return true;
                case R.id.menu_clear:
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
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

                TextView remainingTimeView = (TextView)convertView
                        .findViewById(R.id.remainingTime);
                remainingTimeView.setText(offer.getEndingTimeString());

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
                        startActivity(new Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse(getMyOffer().getOfferUrl())));
                    }
                });

                return convertView;
            }
        }
    }
}
