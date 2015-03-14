package com.braincode.okap.choklik;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AllegroClient {
    public static final String TAG = "AlegroClient";

    public static final String ENDPOINT = "https://api.natelefon.pl/";
    public static final String METHOD_OFFERS = "v2/allegro/offers";

    public static String sessionToken = "a3c4b2d53999682eb279e6efd8873ba6";

    ArrayList<Offer> offers = new ArrayList<>();

    public ArrayList<Offer> fetchSearchResults(String searchTerm) {
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
        HttpResponse response;
        JSONObject json = new JSONObject();
        ArrayList<String> searchQueries = new ArrayList<>();

        String tokenParam = "?access_token=" + sessionToken;
        String url = ENDPOINT + METHOD_OFFERS + tokenParam;

        try {
            searchQueries = Words.getPossibleMisspelledWords(searchTerm);
        } catch (Words.WordsException we) {
            Log.i(TAG, we.getMessage());
        }

        try {
            HttpPost post = new HttpPost(url);
            for (String part : searchQueries) {
                json.put("searchString", part);
                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent();
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(in, writer);
                    JSONObject jsonObject = new JSONObject(writer.toString());

                    JSONArray array = jsonObject.getJSONArray("offers");

                    for (int i = 0; i < array.length(); i++) {
                        offers.add(new Offer(array.getJSONObject(i)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error while sending POST request, ", e);
        }
        return offers;
    }
}