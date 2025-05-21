package com.example.duan_appbanhang.Activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.duan_appbanhang.Adapter.ForgotPasswordAdapter;
import com.example.duan_appbanhang.R;

public class VerificationActivity extends AppCompatActivity
        implements ForgotPasswordAdapter.ForgotPasswordCallback {
    
    private EditText etVerificationCode;
    private Button btnVerifyCode;
    private TextView tvLogin;
    private ProgressDialog progressDialog;
    private ForgotPasswordAdapter forgotPasswordAdapter;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_code);

        email = getIntent().getStringExtra("email");
        if (email == null) {
            finish();
            return;
        }

        initViews();
        setupListeners();
        
        forgotPasswordAdapter = new ForgotPasswordAdapter(this, this);
    }

    private void initViews() {
        etVerificationCode = findViewById(R.id.etVerificationCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        tvLogin = findViewById(R.id.tvLogin);
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xác minh...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnVerifyCode.setOnClickListener(v -> {
            String code = etVerificationCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã xác minh", Toast.LENGTH_SHORT).show();
                return;
            }
            forgotPasswordAdapter.verifyCode(email, code);
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
        
        // Chuyển sang màn hình đặt lại mật khẩu
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("code", etVerificationCode.getText().toString().trim());
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