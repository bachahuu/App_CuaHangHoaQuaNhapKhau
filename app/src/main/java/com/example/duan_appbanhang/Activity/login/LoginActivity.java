package com.example.duan_appbanhang.Activity.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.duan_appbanhang.Activity.home.home_activity;
import com.example.duan_appbanhang.Adapter.GoogleSignInAdapter;
import com.example.duan_appbanhang.Adapter.login_Adapter;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.SplashActivity;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements login_Adapter.LoginCallback, GoogleSignInAdapter.GoogleSignInCallback {
    private EditText edtTenDangNhap, edtMatKhau;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private ProgressDialog progressDialog;
    private login_Adapter loginAdapter;
    private GoogleSignInAdapter googleSignInAdapter;
    private LinearLayout btnGoogleLogin;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
        
        loginAdapter = new login_Adapter(this, this);
        googleSignInAdapter = new GoogleSignInAdapter(this, this);
    }

    private void initViews() {
        edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String tenDangNhap = edtTenDangNhap.getText().toString().trim();
            String matKhau = edtMatKhau.getText().toString().trim();
            loginAdapter.performLogin(tenDangNhap, matKhau);
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnGoogleLogin.setOnClickListener(v -> googleSignInAdapter.signIn(this));
    }

    private void getAccountInfo(String tenDangNhap, SharedPreferences.Editor editor) {
        String url = Utils.getKhachHangTaiKhoanUrl(); // Sử dụng URL từ Utils
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Duyệt danh sách để tìm bản ghi khớp với tenDangNhap
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            if (item.getString("tenDangNhap").equals(tenDangNhap)) {
                                int maTaiKhoan = item.getInt("maTaiKhoan");
                                int maKhachHang = item.getInt("maKhachHang");
                                Log.d(TAG, "Found: maTaiKhoan=" + maTaiKhoan + ", maKhachHang=" + maKhachHang);
                                // Lưu vào SharedPreferences
                                editor.putInt("maTaiKhoan", maTaiKhoan);
                                editor.putInt("maKhachHang", maKhachHang);
                                editor.putString("tenDangNhap", tenDangNhap);
                                editor.apply();
                                // Chuyển sang home_activity
                                startHomeActivity();
                                return;
                            }
                        }
                        // Nếu không tìm thấy tenDangNhap
                        Toast.makeText(this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi kết nối API: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onLoginSuccess() {
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        String tenDangNhap = edtTenDangNhap.getText().toString().trim();
        editor.putString("tenDangNhap", tenDangNhap); // Lưu tenDangNhap
        editor.apply(); // hoặc .commit()
        // Gọi getAccountInfo để lấy maKhachHang
        getAccountInfo(tenDangNhap, editor);

        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        startHomeActivity();
    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginStart() {
        progressDialog.show();
    }

    @Override
    public void onLoginEnd() {
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GoogleSignInAdapter.getSignInRequestCode()) {
            googleSignInAdapter.handleSignInResult(data);
        }
    }

    @Override
    public void onSignInStart() {
        progressDialog.show();
    }

    @Override
    public void onSignInSuccess(JSONObject userData) {
        progressDialog.dismiss();
        try {
            SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", true);

            // Lấy tenDangNhap từ userData
            String tenDangNhap = userData.getString("tenDangNhap");

            // Lưu các thông tin khác từ userData nếu cần
            editor.putString("email", userData.getString("email"));
            editor.putString("loaiTaiKhoan", userData.getString("loaiTaiKhoan"));
            editor.putString("trangThaiTaiKhoan", userData.getString("trangThaiTaiKhoan"));

            // Gọi API để lấy maTaiKhoan và maKhachHang
            getAccountInfo(tenDangNhap, editor);
            
        } catch (JSONException e) {
            Toast.makeText(this, "Lỗi xử lý dữ liệu đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSignInFailure(String message) {
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignInEnd() {
        progressDialog.dismiss();
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Xóa tất cả dữ liệu
        editor.apply();
    }
}
