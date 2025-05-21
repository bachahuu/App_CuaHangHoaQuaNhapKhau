package com.example.duan_appbanhang;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duan_appbanhang.Activity.home.home_activity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ImageView logo = findViewById(R.id.logo_app);
        TextView name =  findViewById(R.id.text_fruit_shop);
        Animation fadeScale = AnimationUtils.loadAnimation(this,R.anim.fade_scale);
        // Áp dụng animation cho logo
        logo.startAnimation(fadeScale);
        name.startAnimation(fadeScale);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, home_activity.class);
                startActivity(intent);
                finish();// Đóng SplashActivity để không quay lại
            }
        },1200);// Delay (1.2 giây)
    }
}
