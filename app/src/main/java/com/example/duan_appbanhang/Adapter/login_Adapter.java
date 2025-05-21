package com.example.duan_appbanhang.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login_Adapter {
    private Context context;
    private LoginCallback callback;

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailure(String message);
        void onLoginStart();
        void onLoginEnd();
    }

    public login_Adapter(Context context, LoginCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void performLogin(String tenDangNhap, String matKhau) {
        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            callback.onLoginFailure("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        callback.onLoginStart();

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("tenDangNhap", tenDangNhap);
            loginData.put("matKhau", matKhau);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onLoginEnd();
            callback.onLoginFailure("Lỗi xử lý dữ liệu");
            return;
        }

        Log.d("LoginAdapter", "Request URL: " + Utils.getLoginUrl());
        Log.d("LoginAdapter", "Request body: " + loginData.toString());

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                Utils.getLoginUrl(),
                loginData,
                response -> {
                    callback.onLoginEnd();
                    Log.d("LoginAdapter", "Response: " + response.toString());
                    
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        
                        if (success && response.has("data")) {
                            JSONObject userData = response.getJSONObject("data");
                            
                            String loaiTaiKhoan = userData.getString("loaiTaiKhoan");
                            if (!"KhachHang".equals(loaiTaiKhoan)) {
                                callback.onLoginFailure("Tài khoản không có quyền truy cập ứng dụng này");
                                return;
                            }
                            
                            saveUserData(userData);
                            callback.onLoginSuccess();
                        } else {
                            callback.onLoginFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onLoginFailure("Lỗi xử lý dữ liệu: " + e.getMessage());
                    }
                },
                error -> {
                    callback.onLoginEnd();
                    Log.e("LoginAdapter", "Error: " + error.toString());
                    String message = "Lỗi kết nối";
                    if (error.networkResponse != null) {
                        message += " (Mã lỗi: " + error.networkResponse.statusCode + ")";
                    }
                    callback.onLoginFailure(message);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(loginRequest);
    }

    private void saveUserData(JSONObject userData) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putBoolean("isLoggedIn", true);
            editor.putInt("maTaiKhoan", userData.getInt("maTaiKhoan"));
            editor.putString("tenDangNhap", userData.getString("tenDangNhap"));
            editor.putString("email", userData.getString("email"));
            editor.putString("loaiTaiKhoan", userData.getString("loaiTaiKhoan"));
            editor.putString("trangThaiTaiKhoan", userData.getString("trangThaiTaiKhoan"));
            
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onLoginFailure("Lỗi lưu thông tin đăng nhập");
        }
    }
}
