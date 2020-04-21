package com.example.backend.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.backend.R;
<<<<<<< HEAD:Backend2/Backend2/app/src/main/java/com/example/backend/Activity/Splash.java
public class Splash extends AppCompatActivity {
=======

public class SplashActivity extends AppCompatActivity {
>>>>>>> 7aef56a2c4a235c9571609a46ebca1ba9a388f7a:Backend2/Backend2/app/src/main/java/com/example/backend/Activity/SplashActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
