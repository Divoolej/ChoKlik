package com.braincode.okap.choklik;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hubert on 14.03.15.
 */
public class ImageDownloader<Token> extends HandlerThread {
    public static final String TAG = "ImageDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    Handler handler;
    Map<Token, String> requestMap =
            Collections.synchronizedMap(new HashMap<Token, String>());
    Handler responseHandler;
    Listener<Token> listener;

    public ImageDownloader(Handler responseHandler) {
        super(TAG);
        this.responseHandler = responseHandler;
    }

    public interface Listener<Token> {
        void onImageDownloaded(Token token, Bitmap image);
    }

    public void setListener(Listener<Token> listener) {
        this.listener = listener;
    }

    public void queueImage(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token, url);

        handler
                .obtainMessage(MESSAGE_DOWNLOAD, token)
                .sendToTarget();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token)msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
    }

    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if (url == null)
                return;

            byte[] bitmapBytes = new AllegroClient().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token) != url)
                        return;

                    requestMap.remove(token);
                    listener.onImageDownloaded(token, bitmap);
                }
            });

        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    public void clearQueue() {
        handler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
