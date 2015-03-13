package com.braincode.okap.choklik;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static us.monoid.web.Resty.*;
import us.monoid.web.Resty.*;

import us.monoid.web.Resty;

/**
 * Created by hubert on 13.03.15.
 */
public class AllegroClient {
    public static final String TAG = "AlegroClient";

    public static final String ENDPOINT = "https://api.natelefon.pl/";
    public static final String METHOD_OFFERS = "v2/allegro/offers";
    public static final String PARAM_SEARCH = "searchString";

    public static String sessionToken = "cec5362e688b9a122846bb09c135295c";

    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public static void fetchSearchResults() {
        try {
            Resty r = new Resty();
            r.withHeader("Content-Type", "application/json");

            String tokenParam = "?access_token=" + sessionToken;

            String url = ENDPOINT + METHOD_OFFERS + tokenParam;
            Log.i(TAG, "Search method url: " + url);

            Log.i(TAG, r.text(url, form(
                    data("searchString", "nokia"))).toString()
            );
        } catch (IOException ioe) {
            Log.e(TAG, "error while fetching search results", ioe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}