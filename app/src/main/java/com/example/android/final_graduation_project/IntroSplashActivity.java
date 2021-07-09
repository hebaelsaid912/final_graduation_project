package com.example.android.final_graduation_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.android.final_graduation_project.ui.home.HomeActivity;
import com.example.android.final_graduation_project.ui.splash_screen.SplashActivity;

public class IntroSplashActivity extends AppCompatActivity {

    private final int splashTime = 5000;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int permissionCode = 21;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_splash);
        new StatusBar(this, R.color.white);

        SessionManager sessionManager = new SessionManager(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("Intro", "Token " + SessionManager.getAccessToken() + "");
                Log.i("Intro", "Refresh Token " + SessionManager.getRefreshToken() + "");
                //  Log.i("Intro", "is Login ?"+SessionManager.isLogin() + "");
                Log.i("Intro", "has Account ?" + SessionManager.hasAccount() + "");
                checkPermission();
                //  if (SessionManager.getLoginUserToken() != null){
                if (SessionManager.hasAccount() && !SessionManager.getAccessToken().isEmpty()) {
                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getBaseContext(), SplashActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, splashTime);
    }
    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, permissionCode);
            return false;
        }
    }

}