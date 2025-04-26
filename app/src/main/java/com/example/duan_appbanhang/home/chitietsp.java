package com.example.duan_appbanhang.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.duan_appbanhang.R;

public class chitietsp extends AppCompatActivity {
    private ImageView productImage,add_to_cart;
    private TextView priceSaleText, soldCountText, shopNameText, descriptionText;
    private Button buyWithVoucherButton;
    private ImageButton arrowLeft, cartIcon;
    private TextView cartBadge;

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
                Glide.with(this)
                        .load(hinhAnh)
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
                Toast.makeText(chitietsp.this,"Đã thêm sản phẩm vào giỏ hàng",Toast.LENGTH_SHORT).show();
                // Tăng số lượng trong giỏ hàng
                updateCartBadge();
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
        buyWithVoucherButton = findViewById(R.id.buy_with_voucher_button);
        arrowLeft = findViewById(R.id.arrow_left);
        cartIcon = findViewById(R.id.cart_icon);
        cartBadge = findViewById(R.id.cart_badge);
    }
}
