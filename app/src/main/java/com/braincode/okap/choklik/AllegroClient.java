package com.braincode.okap.choklik;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.RestAdapter;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;

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

    interface SearchInterface {
        @POST(METHOD_OFFERS)
        void getSearchResults(@Query("searchString") String searchString);
    }

    public static void fetchSearchResults() {
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
        HttpResponse response;
        JSONObject json = new JSONObject();

        String tokenParam = "?access_token=" + sessionToken;
        String url = ENDPOINT + METHOD_OFFERS + tokenParam;

        try {
            HttpPost post = new HttpPost(url);
            json.put("searchString", "nokia");
            StringEntity se = new StringEntity(json.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            response = client.execute(post);

            if(response!=null) {
                InputStream in = response.getEntity().getContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(in, writer);
                String result = writer.toString();
                Log.i(TAG, "result: " + result);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while sending POST request, ", e);
        }
    }
//        try {
//            RestAdapter restAdapter = new RestAdapter.Builder()
//                    .setEndpoint(ENDPOINT)
//                    .build();
//
//            SearchInterface searchService = restAdapter.create(SearchInterface.class);
//            searchService.getSearchResults("nokia");
//
////            r.withHeader("Content-Type", "application/json");
//
//
//            Log.i(TAG, "Search method url: " + url);
//
//        } catch (IOException ioe) {
//            Log.e(TAG, "error while fetching search results", ioe);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}