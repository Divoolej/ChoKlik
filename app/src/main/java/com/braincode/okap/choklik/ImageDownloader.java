package com.braincode.okap.choklik;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by hubert on 14.03.15.
 */
public class ImageDownloader<Token> extends HandlerThread {
    public static final String TAG = "ImageDownloader";

    public ImageDownloader() {
        super(TAG);
    }

    public void queueImage(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
    }
}
