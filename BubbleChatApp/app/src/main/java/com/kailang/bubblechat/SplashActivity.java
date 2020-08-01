package com.kailang.bubblechat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startActivity(new Intent(this,LoginActivity.class));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                requestPermisson();
//            }
//        }, 100);
//        LogUtil.d(new String(Character.toChars(0x1F60E)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
