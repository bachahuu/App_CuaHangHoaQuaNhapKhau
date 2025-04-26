package com.example.duan_appbanhang.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.duan_appbanhang.Adapter.CartAdapter;
import com.example.duan_appbanhang.Module.CartItem;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class activity_cart extends AppCompatActivity implements CartAdapter.OnCartChangeListener , CartAdapter.OnCartItemDeleteListener {
    ImageView back;
    TextView txttitle;
    TextView txtBadge;
    TextView txtprice;
    TextView emptyCartMessage;
    RecyclerView recyclerView_cart;
    CartAdapter cartAdapter;
    List<CartItem> cartList;
    private static final String TAG = "activity_cart";
    private  final String API_URL = Utils.getBaseUrl() + "get_cart.php"; // API lấy danh sách sản phẩm
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        anhxa();
        // Thiết lập LayoutManager cho RecyclerView (dùng LinearLayoutManager với hướng dọc)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_cart.setLayoutManager(layoutManager);
        // Khởi tạo danh sách dữ liệu
        cartList = new ArrayList<>();
        // Khởi tạo Adapter
       cartAdapter = new CartAdapter(this,cartList,this::tongtien,this);
        // Thiết lập Adapter cho RecyclerView
        recyclerView_cart.setAdapter(cartAdapter);
        fetchcrat();
        fetchCartCount();
        intent();
    }

    private void fetchCartCount() {
        String COUNT_API_URL = Utils.getBaseUrl() + "index_cart.php"; // URL của file PHP đếm số lượng
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(Request.Method.GET, COUNT_API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int count = response.getInt("SoLuong"); // Lấy giá trị SoLuong từ JSON
                            updateCartTitle(count); // Cập nhật giao diện với số lượng từ API
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                            updateCartTitle(0); // Nếu lỗi, hiển thị 0
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error: " + error.toString());
                Toast.makeText(activity_cart.this, "Lỗi khi tải số lượng giỏ hàng!", Toast.LENGTH_SHORT).show();
                updateCartTitle(0); // Nếu lỗi, hiển thị 0
            }
        });

        ApiClient.getInstance(this).addToRequestQueue(request);
    }

    private void tongtien() {
        double total = 0;
        for (CartItem item : cartList) {
            if (item.isSelected()) { // Chỉ cộng tiền sản phẩm được chọn
                total += item.getGiaThanh();
            }
        }
        txtprice.setText(String.format("Tổng: %,d VND", (int) total));
    }


    @Override
    public void onCartItemCheckedChanged() {
        tongtien();// Chỉ tính tổng tiền dựa trên sản phẩm đã chọn
    }

    private void updateCartTitle(int itemCount) {
        txttitle.setText("Giỏ hàng (" + itemCount + ")");
        if (itemCount > 0) {
            txtBadge.setText(String.valueOf(itemCount));
            txtBadge.setVisibility(View.VISIBLE);
            recyclerView_cart.setVisibility(View.VISIBLE);
            emptyCartMessage.setVisibility(View.GONE);
            txtprice.setVisibility(View.VISIBLE);
        } else {
            txtBadge.setText("0");
            txtBadge.setVisibility(View.GONE);
            recyclerView_cart.setVisibility(View.GONE);
            emptyCartMessage.setVisibility(View.VISIBLE);
            txtprice.setText("Tổng: 0đ");
            txtprice.setVisibility(View.VISIBLE);
        }
    }
    private void updateCartCount(int count) {
        SharedPreferences sharedPreferences = getSharedPreferences("cart_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("cart_count", count);
        editor.apply();
        Log.d(TAG, "Updated cart_count: " + count); // Kiểm tra giá trị lưu
        // Gửi kết quả về home_activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("cart_count", count);
        setResult(RESULT_OK, resultIntent);
    }

    private void anhxa() {
        // Khởi tạo RecyclerView
        recyclerView_cart = findViewById(R.id.list_item);
        back = findViewById(R.id.btnBack);
        txttitle = findViewById(R.id.txtTitle);
        txtBadge = findViewById(R.id.txtBadge);
        txtprice = findViewById(R.id.txtTotalPrice);
        emptyCartMessage = findViewById(R.id.empty_cart_message);
    }
    private void intent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCartCount(cartList.size()); // Cập nhật số lượng trước khi thoát
                finish(); // Đảm bảo kết thúc Activity đúng cách
            }
        });
    }

    private void fetchcrat() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cartList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                               CartItem product = new CartItem(
                                       item.getString("MaSanPham"),
                                        item.getString("TenSanPham"),
                                        item.getInt("SoLuong"),
                                       item.getString("HinhAnh"),
                                        item.getDouble("GiaThanh"),
                                       item.getString("GhiChu"),
                                       item.getDouble("GiaBan")
                                );
                                cartList.add(product);
                            }
                            cartAdapter.notifyDataSetChanged();
                           fetchCartCount();
                            tongtien();
                            updateCartCount(cartList.size());
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error: " + error.toString());
                Toast.makeText(activity_cart.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                fetchCartCount();
                txtprice.setText("Tổng: 0 VND");
            }
        });

        ApiClient.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateCartCount(cartList.size()); // Cập nhật số lượng giỏ hàng trước khi activity bị dừng
    }

    @Override
    public void onCartItemDeleted() {
        fetchCartCount();// Gọi API để cập nhật số lượng từ server
    }
}
