package com.example.duan_appbanhang.Activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.duan_appbanhang.Activity.login.LoginActivity;
import com.example.duan_appbanhang.Adapter.ForgotPasswordAdapter;
import com.example.duan_appbanhang.R;

public class ResetPasswordActivity extends AppCompatActivity
        implements ForgotPasswordAdapter.ForgotPasswordCallback {
    
    private EditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private TextView tvLogin;
    private ProgressDialog progressDialog;
    private ForgotPasswordAdapter forgotPasswordAdapter;
    private String email, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_reset);

        email = getIntent().getStringExtra("email");
        code = getIntent().getStringExtra("code");
        if (email == null || code == null) {
            finish();
            return;
        }

        initViews();
        setupListeners();
        
        forgotPasswordAdapter = new ForgotPasswordAdapter(this, this);
    }

    private void initViews() {
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvLogin = findViewById(R.id.tvLogin);
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đặt lại mật khẩu...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnResetPassword.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            
            forgotPasswordAdapter.resetPassword(email, code, newPassword);
        });

        tvLogin.setOnClickListener(v -> finish());
    }

    @Override
    public void onRequestStart() {
        progressDialog.show();
    }

    @Override
    public void onRequestSuccess(String message) {
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        
        // Quay về màn hình đăng nhập
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestFailure(String message) {
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestEnd() {
        progressDialog.dismiss();
    }
}
