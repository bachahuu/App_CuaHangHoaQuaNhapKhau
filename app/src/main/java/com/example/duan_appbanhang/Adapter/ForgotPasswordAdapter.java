package com.example.duan_appbanhang.Adapter;

import android.content.Context;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordAdapter {
    private Context context;
    private ForgotPasswordCallback callback;

    public interface ForgotPasswordCallback {
        void onRequestStart();
        void onRequestSuccess(String message);
        void onRequestFailure(String message);
        void onRequestEnd();
    }

    public ForgotPasswordAdapter(Context context, ForgotPasswordCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void requestPasswordReset(String email) {
        callback.onRequestStart();

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("email", email);

            // Log để debug
            Log.d("ForgotPassword", "Request URL: " + Utils.getForgotPasswordUrl());
            Log.d("ForgotPassword", "Request body: " + requestData.toString());

        } catch (JSONException e) {
            callback.onRequestEnd();
            callback.onRequestFailure("Lỗi dữ liệu: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                Utils.getForgotPasswordUrl(),
                requestData,
                response -> {
                    Log.d("ForgotPassword", "Response: " + response.toString());
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        if (success) {
                            callback.onRequestSuccess(message);
                        } else {
                            callback.onRequestFailure(message);
                        }
                    } catch (JSONException e) {
                        callback.onRequestFailure("Lỗi xử lý dữ liệu: " + e.getMessage());
                    }
                    callback.onRequestEnd();
                },
                error -> {
                    Log.e("ForgotPassword", "Error: " + error.toString());
                    String errorMessage = "Lỗi kết nối";
                    if (error.networkResponse != null) {
                        try {
                            String errorData = new String(error.networkResponse.data, "UTF-8");
                            Log.e("ForgotPassword", "Error data: " + errorData);
                            JSONObject errorJson = new JSONObject(errorData);
                            errorMessage = errorJson.optString("message", errorMessage);
                        } catch (Exception e) {
                            Log.e("ForgotPassword", "Error parsing error response", e);
                        }
                    }
                    callback.onRequestFailure(errorMessage);
                    callback.onRequestEnd();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(request);
    }

    public void verifyCode(String email, String code) {
        callback.onRequestStart();

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("email", email);
            requestData.put("code", code);
        } catch (JSONException e) {
            callback.onRequestFailure("Lỗi dữ liệu");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                Utils.getBaseUrl() + "/api/accounts/verify-reset-code",
                requestData,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        if (success) {
                            callback.onRequestSuccess(message);
                        } else {
                            callback.onRequestFailure(message);
                        }
                    } catch (JSONException e) {
                        callback.onRequestFailure("Lỗi xử lý dữ liệu");
                    }
                    callback.onRequestEnd();
                },
                error -> {
                    callback.onRequestFailure("Lỗi kết nối: " + error.getMessage());
                    callback.onRequestEnd();
                });

        Volley.newRequestQueue(context).add(request);
    }

    public void resetPassword(String email, String code, String newPassword) {
        callback.onRequestStart();

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("email", email);
            requestData.put("code", code);
            requestData.put("newPassword", newPassword);
        } catch (JSONException e) {
            callback.onRequestFailure("Lỗi dữ liệu");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                Utils.getBaseUrl() + "/api/accounts/reset-password",
                requestData,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        if (success) {
                            callback.onRequestSuccess(message);
                        } else {
                            callback.onRequestFailure(message);
                        }
                    } catch (JSONException e) {
                        callback.onRequestFailure("Lỗi xử lý dữ liệu");
                    }
                    callback.onRequestEnd();
                },
                error -> {
                    callback.onRequestFailure("Lỗi kết nối: " + error.getMessage());
                    callback.onRequestEnd();
                });

        Volley.newRequestQueue(context).add(request);
    }
}