package com.braincode.okap.choklik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by divoolej on 14.03.15.
 */
public class SplashScreen extends Activity {
    private static final int TIME = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        ActivityStarter starter = new ActivityStarter();
        starter.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent intent = new Intent(SplashScreen.this, ChoklikActivity.class);
        startActivity(intent);
        this.finish();
        return true;
    }

    private class ActivityStarter extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(TIME);
            } catch (Exception e) {
                Log.e("SplashScreen", e.getMessage());
            }

            Intent intent = new Intent(SplashScreen.this, ChoklikActivity.class);
            SplashScreen.this.startActivity(intent);
            SplashScreen.this.finish();
        }
    }
}
