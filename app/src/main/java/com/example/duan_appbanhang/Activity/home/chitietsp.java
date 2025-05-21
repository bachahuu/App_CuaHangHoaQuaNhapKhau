package com.example.duan_appbanhang.Activity.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class chitietsp extends AppCompatActivity {
    private ImageView productImage,add_to_cart;
    private TextView priceSaleText, soldCountText, shopNameText, descriptionText;
    private Button buyWithVoucherButton;
    private ImageView arrowLeft, cartIcon;
    private TextView cartBadge,danhgiatrungbinh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietsp);
        anhxa();
        // Lấy dữ liệu từ Intent
        if (getIntent() != null) {
            String tenSanPham = getIntent().getStringExtra("TenSanPham");
            String moTa = getIntent().getStringExtra("MoTa");
            String giaFormat = getIntent().getStringExtra("GiaFormat");
            String hinhAnh = getIntent().getStringExtra("HinhAnh");
            double danhgiasanpham = getIntent().getDoubleExtra("danhGiaTrungBinh", 0.0);
            Integer masanpham = getIntent().getIntExtra("maSanPham",0);
            if (danhgiasanpham>0){
                String danhgiaFormat = String.format("%.1f", danhgiasanpham); // VD: 4.5 ★
                danhgiatrungbinh.setText(danhgiaFormat);
            }else {
                danhgiatrungbinh.setText("Chưa có đánh giá");
            }

            // Hiển thị dữ liệu lên giao diện
            priceSaleText.setText(giaFormat != null ? giaFormat : "");
            descriptionText.setText(moTa != null ? moTa : "");
            shopNameText.setText("Fruit store"); // Tên shop mặc định

            // Cập nhật tiêu đề (nếu muốn)
            if (tenSanPham != null) {
                setTitle(tenSanPham);
            }

            // Load hình ảnh sản phẩm
            if (hinhAnh != null && !hinhAnh.isEmpty()) {
                String cleanImageUrl = hinhAnh.trim().replaceAll("\\r|\\n", "");
                Glide.with(this)
                        .load(cleanImageUrl)
                        .into(productImage);
            }

        }
        addsp();
     button_back();
    }
    private void addsp() {
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu sản phẩm từ Intent
                Integer maSanPham = getIntent().getIntExtra("maSanPham",0);
                String tenSanPham = getIntent().getStringExtra("TenSanPham");
                String hinhAnh = getIntent().getStringExtra("HinhAnh");
                String giaBanStr = getIntent().getStringExtra("GiaBan"); // Lấy giá bán dạng chuỗi
                int soLuong = 1;
                double giaBan = 0.0;
                try {
                    giaBan = Double.parseDouble(giaBanStr); // Ép kiểu sang double
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    giaBan = 0.0; // fallback nếu lỗi
                }
                double giaThanh = giaBan * soLuong;
                // Tạo đối tượng JSON để gửi đến server
                JSONObject cartItem = new JSONObject();
                try {
                    cartItem.put("maSanPham",maSanPham);
                    cartItem.put("tenSanPham", tenSanPham);
                    cartItem.put("soLuong", soLuong);
                    cartItem.put("hinhAnh", hinhAnh);
                    cartItem.put("giaThanh", giaThanh);
                    cartItem.put("ghiChu", ""); // Ghi chú mặc định
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Gửi yêu cầu POST đến server
                String url = "http://192.168.0.106:8080/api/cart/add"; // Địa chỉ API
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, cartItem,
                        response -> {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    Toast.makeText(chitietsp.this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                    updateCartBadge(); // Tăng số lượng trong giỏ hàng
                                } else {
                                    String message = response.getString("message");
                                    Toast.makeText(chitietsp.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            Toast.makeText(chitietsp.this, "Lỗi khi thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        });

                // Thêm yêu cầu vào hàng đợi
                RequestQueue queue = Volley.newRequestQueue(chitietsp.this);
                queue.add(request);
            }
        });
    }
    private void updateCartBadge() {
        try {
            int currentCount = Integer.parseInt(cartBadge.getText().toString());
            cartBadge.setText(String.valueOf(currentCount + 1));
        } catch (NumberFormatException e) {
            cartBadge.setText("1");
        }
    }

    private void button_back() {
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity hiện tại
            }
        });
    }

    private void anhxa() {
        // Ánh xạ các view
        add_to_cart =findViewById(R.id.add_to_cart);
        productImage = findViewById(R.id.product_image);
        priceSaleText = findViewById(R.id.price_sale);
        soldCountText = findViewById(R.id.sold_count);
        shopNameText = findViewById(R.id.shop_name);
        descriptionText = findViewById(R.id.description);
        buyWithVoucherButton = findViewById(R.id.buy_button);
        arrowLeft = findViewById(R.id.arrow_left);
        cartIcon = findViewById(R.id.cart_icon);
        cartBadge = findViewById(R.id.cart_badge);
        danhgiatrungbinh = findViewById(R.id.danhgiatrungbinh);
    }
}
