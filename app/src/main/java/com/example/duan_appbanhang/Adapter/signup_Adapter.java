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

public class signup_Adapter {
    private Context context;
    private SignUpCallback callback;

    public interface SignUpCallback {
        void onSignUpStart();
        void onSignUpSuccess();
        void onSignUpFailure(String message);
        void onSignUpEnd();
    }

    public signup_Adapter(Context context, SignUpCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void performSignUp(String tenDangNhap, String matKhau, String email, 
                             String hoTen, String soDienThoai) {
        callback.onSignUpStart();

        JSONObject signUpData = new JSONObject();
        try {
            signUpData.put("tenDangNhap", tenDangNhap);
            signUpData.put("matKhau", matKhau);
            signUpData.put("email", email);
            signUpData.put("hoTen", hoTen);
            signUpData.put("soDienThoai", soDienThoai);

            Log.d("SignUpAdapter", "Request URL: " + Utils.getRegisterUrl());
            Log.d("SignUpAdapter", "Request body: " + signUpData.toString());

            JsonObjectRequest signUpRequest = new JsonObjectRequest(Request.Method.POST,
                    Utils.getRegisterUrl(),  // Use register endpoint
                    signUpData,
                    response -> {
                        callback.onSignUpEnd();
                        Log.d("SignUpAdapter", "Response: " + response.toString());
                        
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            
                            if (success) {
                                callback.onSignUpSuccess();
                            } else {
                                callback.onSignUpFailure(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onSignUpFailure("Lỗi xử lý phản hồi từ server: " + e.getMessage());
                        }
                    },
                    error -> {
                        callback.onSignUpEnd();
                        Log.e("SignUpAdapter", "Error: " + error.toString());
                        
                        String message = "Lỗi đăng ký";
                        if (error.networkResponse != null) {
                            try {
                                String errorResponse = new String(error.networkResponse.data);
                                Log.e("SignUpAdapter", "Error response: " + errorResponse);
                                JSONObject errorJson = new JSONObject(errorResponse);
                                if (errorJson.has("message")) {
                                    message = errorJson.getString("message");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        callback.onSignUpFailure(message);
                    });

            signUpRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(context).add(signUpRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onSignUpEnd();
            callback.onSignUpFailure("Lỗi khi xử lý dữ liệu");
        }
    }
}
