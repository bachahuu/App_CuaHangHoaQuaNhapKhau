package com.example.duan_appbanhang.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.duan_appbanhang.Adapter.popularadapter;
import com.example.duan_appbanhang.Module.popularModule;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class home_activity extends AppCompatActivity {
    ImageView giohang,Menu;
    RecyclerView popularRecyclerView;
    popularadapter popularAdapter;
    ViewFlipper slide;
    List<popularModule> popularItemList;
    TextView cart_index;
    private static final String TAG = "home_activity";
    private  final String API_URL = Utils.getBaseUrl() + "get_sanpham_high_rating.php"; // API lấy danh sách sản phẩm
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);
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
        intent_giohang();
        intent_menu();
        updateCartBadge();
    }

    private void intent_menu() {
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_activity.this,activity_Menu.class);
                startActivity(intent);
            }
        });
    }

    private void intent_giohang() {
        giohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_activity.this, activity_cart.class);
                startActivityForResult(intent, 1); // Chỉ dùng startActivityForResult
            }
        });
    }

    private void anhxa() {
        Menu = findViewById(R.id.img_thucdon);
        giohang = findViewById(R.id.icon_giohang);
        cart_index = findViewById(R.id.activity_menu_sl);
    }
    private void updateCartBadge() {
        String COUNT_API_URL = Utils.getBaseUrl() + "index_cart.php"; // URL của file PHP đếm số lượng
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
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        popularItemList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                popularModule product = new popularModule(
                                        item.getString("TenSanPham"),
                                        item.getString("MoTa"),
                                        item.getString("SoSao"),
                                        item.getString("GiaBan"),
                                        item.getString("HinhAnh")
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

