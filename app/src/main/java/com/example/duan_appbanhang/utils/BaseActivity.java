package com.example.duan_appbanhang.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.duan_appbanhang.Activity.login.LoginActivity;

public class BaseActivity extends AppCompatActivity {
    protected int maKhachHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserData();
    }

    protected void getUserData() {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        maKhachHang = prefs.getInt("maKhachHang", -1);
        String tenDangNhap = prefs.getString("tenDangNhap", "");

        if (maKhachHang == -1 || tenDangNhap.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
