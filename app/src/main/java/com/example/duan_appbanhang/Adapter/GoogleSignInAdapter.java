package com.example.duan_appbanhang.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.duan_appbanhang.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GoogleSignInAdapter {
    private static final int RC_SIGN_IN = 9001;
    private final GoogleSignInClient mGoogleSignInClient;
    private final Context context;
    private final GoogleSignInCallback callback;

    public interface GoogleSignInCallback {
        void onSignInStart();
        void onSignInSuccess(JSONObject userData);
        void onSignInFailure(String message);
        void onSignInEnd();
    }

    public GoogleSignInAdapter(Context context, GoogleSignInCallback callback) {
        this.context = context;
        this.callback = callback;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void signIn(Activity activity) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(Intent data) {
        callback.onSignInStart();
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            
            // Tạo request gửi lên server
            JSONObject requestData = new JSONObject();
            requestData.put("email", account.getEmail());
            requestData.put("tenDangNhap", account.getEmail());
            requestData.put("hoTen", account.getDisplayName());
            requestData.put("phuongThucDangKy", "Google");
            
            Log.d("GoogleSignIn", "Request data: " + requestData.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Utils.getGoogleSignInUrl(),
                    requestData,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                JSONObject userData = response.getJSONObject("data");
                                callback.onSignInSuccess(userData);
                            } else {
                                String message = response.getString("message");
                                callback.onSignInFailure(message);
                            }
                        } catch (JSONException e) {
                            callback.onSignInFailure("Lỗi xử lý dữ liệu: " + e.getMessage());
                        }
                        callback.onSignInEnd();
                    },
                    error -> {
                        String message = "Lỗi kết nối";
                        if (error.networkResponse != null) {
                            try {
                                String errorData = new String(error.networkResponse.data, "UTF-8");
                                JSONObject errorJson = new JSONObject(errorData);
                                message = errorJson.optString("message", message);
                            } catch (Exception e) {
                                Log.e("GoogleSignIn", "Error parsing error response", e);
                            }
                        }
                        callback.onSignInFailure(message);
                        callback.onSignInEnd();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(context).add(request);

        } catch (ApiException e) {
            Log.e("GoogleSignIn", "Google sign in failed", e);
            callback.onSignInFailure("Đăng nhập Google thất bại: " + e.getMessage());
            callback.onSignInEnd();
        } catch (JSONException e) {
            Log.e("GoogleSignIn", "Error creating request data", e);
            callback.onSignInFailure("Lỗi xử lý dữ liệu: " + e.getMessage());
            callback.onSignInEnd();
        }
    }

    public static int getSignInRequestCode() {
        return RC_SIGN_IN;
    }
}