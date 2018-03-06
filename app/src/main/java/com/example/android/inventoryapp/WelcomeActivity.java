package com.example.android.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    private final static int TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        new Handler().postDelayed(new Runnable() {

            //            show welcome image for 3 sec
            @Override

            public void run() {
                Intent startMain = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(startMain);
                finish();
            }
        }, TIME_OUT);
    }
}
