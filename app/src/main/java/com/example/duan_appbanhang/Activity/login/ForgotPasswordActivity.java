package com.example.duan_appbanhang.Activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.duan_appbanhang.Adapter.ForgotPasswordAdapter;
import com.example.duan_appbanhang.R;

public class ForgotPasswordActivity extends AppCompatActivity 
        implements ForgotPasswordAdapter.ForgotPasswordCallback {
    
    private EditText etEmail;
    private Button btnSendCode;
    private TextView tvLogin;
    private ProgressDialog progressDialog;
    private ForgotPasswordAdapter forgotPasswordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_email);

        initViews();
        setupListeners();
        
        forgotPasswordAdapter = new ForgotPasswordAdapter(this, this);
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        btnSendCode = findViewById(R.id.btnSendCode);
        tvLogin = findViewById(R.id.tvLogin);
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("ForgotPassword", "Sending reset request for email: " + email);
            forgotPasswordAdapter.requestPasswordReset(email);
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
        
        // Chuyển sang màn hình nhập mã xác minh
        Intent intent = new Intent(this, VerificationActivity.class);
        intent.putExtra("email", etEmail.getText().toString().trim());
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