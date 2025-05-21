package com.example.duan_appbanhang.Activity.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.duan_appbanhang.Adapter.CartAdapter;
import com.example.duan_appbanhang.Model.CartItem;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    Button btnmuahang;
    private static final String TAG = "activity_cart";
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
        getcart();
        fetchCartCount();
        intent();
    }

    private void fetchCartCount() {
        String COUNT_API_URL = Utils.getCartCountUrl(); // Sử dụng URL từ Utils.getCartCountUrl()
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(Request.Method.GET, COUNT_API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            long count = response.getLong("SoLuong"); // Lấy giá trị SoLuong từ JSON
                            updateCartTitle((int) count); // Cập nhật giao diện với số lượng từ API
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
        btnmuahang = findViewById(R.id.btnCheckout);
    }

    private void intent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCartCount(cartList.size()); // Cập nhật số lượng trước khi thoát
                finish(); // Đảm bảo kết thúc Activity đúng cách
            }
        });
        btnmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo danh sách sản phẩm đã chọn
                List<CartItem> selectedItems = new ArrayList<>();
                double totalPrice = 0;

                for (CartItem item : cartList) {
                    if (item.isSelected()) {
                        selectedItems.add(item);
                        totalPrice += item.getGiaThanh();
                    }
                }

                // Kiểm tra xem có sản phẩm nào được chọn không
                if (selectedItems.isEmpty()) {
                    Toast.makeText(activity_cart.this, "Vui lòng chọn ít nhất một sản phẩm!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Lưu maSanPham vào SharedPreferences
                SharedPreferences prefs = getSharedPreferences("CartData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                JSONArray maSanPhamArray = new JSONArray();
                try {
                    for (CartItem item : selectedItems) {
                        maSanPhamArray.put(item.getMasanpham());
                    }
                    editor.putString("maSanPhamList", maSanPhamArray.toString());
                    editor.apply();
                } catch (Exception e) {
                    Log.e(TAG, "Error saving maSanPham: " + e.getMessage());
                }
                // Chuyển sang activity_thanhtoan
                Intent intent = new Intent(activity_cart.this, com.example.duan_appbanhang.Activity.home.activity_thanhtoan.class);

                // Tạo JSONArray để lưu trữ danh sách sản phẩm đã chọn dựa trên model listsp_selected
                JSONArray jsonArray = new JSONArray();
                try {
                    for (CartItem item : selectedItems) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("imageUrl", item.getHinhAnh());
                        jsonObject.put("name", item.getTenSanPham());
                        jsonObject.put("price", String.format("%,.0f", item.getGiaThanh())); // Format giá tiền
                        jsonObject.put("quantity", item.getSoLuong());
                        jsonArray.put(jsonObject);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Error: " + e.getMessage());
                }

                // Truyền dữ liệu qua Intent
                intent.putExtra("selectedItems", jsonArray.toString());
                intent.putExtra("totalPrice", totalPrice);

                // Khởi động activity_thanhtoan
                startActivity(intent);
            }
        });
    }
    private void getcart(){
        // Sử dụng URL từ lớp Utils
        String url = Utils.getCartUrl();
        // Gọi API thông qua ApiClient
        ApiClient.getInstance(this).getArray(url, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {

            }

            @Override
            public void onSuccess(JSONArray response) {
                cartList.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);
                        CartItem product = new CartItem(
                                item.getInt("maSanPham"),
                                item.getString("tenSanPham"),
                                item.getInt("soLuong"),
                                item.getString("hinhAnh"),
                                item.getDouble("giaThanh"),
                                item.getString("ghiChu"),
                                item.getDouble("giaBan"),
                                item.getInt("maCart")
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
            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
                Toast.makeText(activity_cart.this, "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
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
