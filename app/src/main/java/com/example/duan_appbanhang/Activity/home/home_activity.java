package com.example.duan_appbanhang.Activity.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.duan_appbanhang.Activity.login.LoginActivity;
import com.example.duan_appbanhang.Adapter.popularadapter;
import com.example.duan_appbanhang.Model.item_menu;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
import com.example.duan_appbanhang.utils.BaseActivity;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class home_activity extends AppCompatActivity {
    ImageView giohang, Menu, Order;
    RecyclerView popularRecyclerView;
    popularadapter popularAdapter;
    ViewFlipper slide;
    List<item_menu> popularItemList;
    TextView cart_index;
    private static final String TAG = "home_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

        // Kiểm tra đăng nhập khi vào home
        if (!isLoggedIn()) {
            // Nếu chưa đăng nhập thì chuyển về màn hình login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        slide_quangcao();
        // Khởi tạo RecyclerView
        popularRecyclerView = findViewById(R.id.pop_rec);
        anhxa();
        // Thiết lập LayoutManager cho RecyclerView (dùng LinearLayoutManager với hướng ngang)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popularRecyclerView.setLayoutManager(layoutManager);

        // Khởi tạo danh sách dữ liệu
        popularItemList = new ArrayList<>();
        // Khởi tạo Adapter
        popularAdapter = new popularadapter(this, popularItemList);
        // Thiết lập Adapter cho RecyclerView
        popularRecyclerView.setAdapter(popularAdapter);
        fetchPopularProducts();
        setupBottomAppBar();
        updateCartBadge();
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    // Thêm hàm đăng xuất
    private void logout() {
        LoginActivity.logout(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomAppBar() {
        findViewById(R.id.imageView6).setOnClickListener(v -> {
            // Đã ở OrderActivity, không cần chuyển
        });

        Menu.setOnClickListener(v -> {
            Intent intent = new Intent(home_activity.this, activity_Menu.class);
            startActivity(intent);
        });

        giohang.setOnClickListener(v -> {
            Intent intent = new Intent(home_activity.this, activity_cart.class);
            startActivityForResult(intent, 1); // Chỉ dùng startActivityForResult
        });

        Order.setOnClickListener(v -> {
            Intent intent = new Intent(home_activity.this, com.example.duan_appbanhang.Activity.home.OrderActivity.class);
            startActivity(intent);
        });

//        findViewById(R.id.img_thongtin).setOnClickListener(v -> {
//            Intent intent = new Intent(OrderActivity.this, SettingActivity.class);
//            startActivity(intent);
//        });
    }

    private void anhxa() {
        Menu = findViewById(R.id.img_thucdon);
        giohang = findViewById(R.id.icon_giohang);
        cart_index = findViewById(R.id.activity_menu_sl);
        Order = findViewById(R.id.img_donhang);
    }

    private void updateCartBadge() {
        String COUNT_API_URL = Utils.getCartCountUrl();
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(Request.Method.GET, COUNT_API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int count = response.getInt("SoLuong"); // Lấy giá trị SoLuong từ JSON
                            cart_index.setText(String.valueOf(count));
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                            cart_index.setText("0");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error: " + error.toString());
                Toast.makeText(home_activity.this, "Lỗi khi tải số lượng giỏ hàng!", Toast.LENGTH_SHORT).show();
                updateCartBadge();
            }
        });

        ApiClient.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int cartCount = data.getIntExtra("cart_count", 0);
            cart_index.setText(String.valueOf(cartCount));
        }
    }

    private void slide_quangcao() {
        slide = findViewById(R.id.slide);
        // Thiết lập tự động chuyển đổi
        slide.setAutoStart(true); // Bắt đầu tự động khi activity được tạo
        slide.setFlipInterval(2000); // Chuyển đổi mỗi 2 giây
        slide.startFlipping(); // Bắt đầu quá trình chuyển đổi

        // Thêm hiệu ứng chuyển đổi
        slide.setInAnimation(this, android.R.anim.slide_in_left); // Hiệu ứng khi slide vào
        slide.setOutAnimation(this, android.R.anim.slide_out_right); // Hiệu ứng khi slide ra
    }

    private void fetchPopularProducts() {
        String API_URL = Utils.gethighrating();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        popularItemList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                item_menu product = new item_menu(
                                        item.getInt("maSanPham"),
                                        item.getString("hinhAnh"),
                                        item.getString("tenSanPham"),
                                        item.getString("giaBan"),
                                        item.getDouble("danhGiaTrungBinh"),
                                        item.getString("moTa")
                                );
                                popularItemList.add(product);
                            }
                            popularAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error: " + error.toString());
                Toast.makeText(home_activity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });

        ApiClient.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dừng ViewFlipper khi activity bị hủy để tránh leak
        slide.stopFlipping();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
}

