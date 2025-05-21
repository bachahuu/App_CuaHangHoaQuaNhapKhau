package com.example.duan_appbanhang.Activity.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.util.Log;
import android.content.Intent;
import android.os.Handler;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duan_appbanhang.Adapter.signup_Adapter;
import com.example.duan_appbanhang.R;

public class SignUpActivity extends AppCompatActivity implements signup_Adapter.SignUpCallback {
    private EditText edtTenDangNhap, edtHoTen, edtMatKhau, edtNhapLaiMatKhau, edtEmail, edtSoDienThoai;
    private Button btnDangKy;
    private TextView tvDangNhap;
    private ProgressDialog progressDialog;
    private signup_Adapter signupAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();
        setupListeners();
        
        signupAdapter = new signup_Adapter(this, this);
    }

    private void initView() {
        edtHoTen = findViewById(R.id.etFullName);
        edtTenDangNhap = findViewById(R.id.etUsername);
        edtMatKhau = findViewById(R.id.etPassword);
        edtNhapLaiMatKhau = findViewById(R.id.etConfirmPassword);
        edtEmail = findViewById(R.id.etEmail);
        edtSoDienThoai = findViewById(R.id.etPhone);
        btnDangKy = findViewById(R.id.btnRegister);
        tvDangNhap = findViewById(R.id.tvLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý đăng ký...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnDangKy.setOnClickListener(v -> validateAndSignUp());
        
        tvDangNhap.setOnClickListener(v -> {
            finish(); // Quay lại màn hình đăng nhập
        });
    }

    private void validateAndSignUp() {
        String tenDangNhap = edtTenDangNhap.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String nhapLaiMatKhau = edtNhapLaiMatKhau.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String soDienThoai = edtSoDienThoai.getText().toString().trim();

        // Validate
        if (tenDangNhap.isEmpty() || matKhau.isEmpty() || nhapLaiMatKhau.isEmpty() 
            || email.isEmpty() || hoTen.isEmpty() || soDienThoai.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!matKhau.equals(nhapLaiMatKhau)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        signupAdapter.performSignUp(tenDangNhap, matKhau, email, hoTen, soDienThoai);
    }

    @Override
    public void onSignUpStart() {
        progressDialog.show();
    }

    @Override
    public void onSignUpSuccess() {
        runOnUiThread(() -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
            
            // Thêm độ trễ nhỏ để người dùng kịp đọc thông báo
            new Handler().postDelayed(() -> {
                // Chuyển về màn hình đăng nhập
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }, 1500); // Độ trễ 1.5 giây
        });
    }

    @Override
    public void onSignUpFailure(String message) {
        runOnUiThread(() -> {
            progressDialog.dismiss();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.e("SignUpActivity", "Sign up failed: " + message);
        });
    }

    @Override
    public void onSignUpEnd() {
        progressDialog.dismiss();
    }
}
