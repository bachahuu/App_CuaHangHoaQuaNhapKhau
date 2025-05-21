package com.example.duan_appbanhang.Activity.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.duan_appbanhang.Adapter.listsp_selected_apdater;
import com.example.duan_appbanhang.Model.listsp_selected;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class activity_thanhtoan extends AppCompatActivity {
    private static final String TAG = "activity_thanhtoan";
    private ImageView back;
    private LinearLayout address;
    private EditText editOrderNotes;
    private TextView txtfinaldiscount, txtTotalPrice, txtDiscountInfo, txtTotal, txtdiscount, txtAddress;
    private RecyclerView recyclerViewOrderItems;
    private Button btnConfirmOrder;
    private List<listsp_selected> selectedItemsList;
    private double totalPrice, finaltotal, giamGiaApDung;
    private int maKhuyenMai = -1; // Kiểu int
    private String maGiamGia = ""; // Kiểu String
    private LinearLayout discount, payment_cash, payment_banking;
    private RadioButton rdo_cash, rdo_banking;
    private String paymentMethod = "cash";
    private int maKhachHang = -1;
    private String tenKhachHang = "";
    private String soDienThoaiKhachHang = "";
    private String diaChiGiaoHang = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongtindonhang);

        anhxa();
        getUserData();
        getDataFromIntent();
        displayData();
        setupPaymentMethod();
        setupEventHandlers();
    }

    private void anhxa() {
        back = findViewById(R.id.btnBack);
        txtfinaldiscount = findViewById(R.id.txtDiscount);
        txtTotalPrice = findViewById(R.id.txtSubtotal);
        recyclerViewOrderItems = findViewById(R.id.recyclerViewCartItems);
        btnConfirmOrder = findViewById(R.id.btnCheckout);
        discount = findViewById(R.id.btnSelectVoucher);
        txtDiscountInfo = findViewById(R.id.discount);
        txtTotal = findViewById(R.id.txtTotal);
        txtdiscount = findViewById(R.id.maKhuyenMai);
        txtAddress = findViewById(R.id.txtAddress);
        payment_cash = findViewById(R.id.layoutCash);
        payment_banking = findViewById(R.id.layoutBIDV);
        rdo_cash = findViewById(R.id.radioCash);
        rdo_banking = findViewById(R.id.radioBIDV);
        address = findViewById(R.id.lnl_address);
        editOrderNotes = findViewById(R.id.editOrderNotes);

        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getUserData() {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        maKhachHang = prefs.getInt("maKhachHang", -1);
        String tenDangNhap = prefs.getString("tenDangNhap", "");
        tenKhachHang = prefs.getString("tenKhachHang", "");
        soDienThoaiKhachHang = prefs.getString("soDienThoai", "");
        diaChiGiaoHang = prefs.getString("diaChi", "");
        Log.d(TAG, "SharedPreferences: maKhachHang=" + maKhachHang + ", tenDangNhap=" + tenDangNhap);

        if (maKhachHang == -1 || tenDangNhap.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, com.example.duan_appbanhang.Activity.login.LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (!tenKhachHang.isEmpty() && !soDienThoaiKhachHang.isEmpty() && !diaChiGiaoHang.isEmpty()) {
            String addressText = String.format("%s (+84) %s\n%s", tenKhachHang, soDienThoaiKhachHang, diaChiGiaoHang);
            txtAddress.setText(addressText);
        }
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String selectedItemsJson = intent.getStringExtra("selectedItems");
            totalPrice = intent.getDoubleExtra("totalPrice", 0);
            finaltotal = totalPrice;
            giamGiaApDung = 0;
            selectedItemsList = new ArrayList<>();

            if (selectedItemsJson != null) {
                try {
                    JSONArray jsonArray = new JSONArray(selectedItemsJson);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        listsp_selected selectedItem = new listsp_selected(
                                item.getString("imageUrl"),
                                item.getString("name"),
                                item.getString("price"),
                                item.getInt("quantity")
                        );
                        selectedItemsList.add(selectedItem);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Error: " + e.getMessage());
                    Toast.makeText(this, "Có lỗi xảy ra khi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }
            // Lấy maSanPham từ SharedPreferences
            SharedPreferences prefs = getSharedPreferences("CartData", MODE_PRIVATE);
            String maSanPhamJson = prefs.getString("maSanPhamList", "[]");
            try {
                JSONArray maSanPhamArray = new JSONArray(maSanPhamJson);
                List<Integer> maSanPhamList = new ArrayList<>();
                for (int i = 0; i < maSanPhamArray.length(); i++) {
                    maSanPhamList.add(maSanPhamArray.getInt(i));
                }
                Log.d(TAG, "Retrieved maSanPhamList: " + maSanPhamList.toString());
                // Sử dụng maSanPhamList ở đây nếu cần (ví dụ: gửi lên server khi đặt hàng)
            } catch (JSONException e) {
                Log.e(TAG, "JSON Error when parsing maSanPham: " + e.getMessage());
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void displayData() {
        txtTotalPrice.setText(String.format("Tổng: %,d VND", (int) totalPrice));
        txtTotal.setText(String.format("Tổng: %,d VND", (int) finaltotal));
        txtdiscount.setText(maGiamGia.isEmpty() ? "Chưa áp dụng mã" : maGiamGia);
        txtDiscountInfo.setText(String.format("Giảm giá: -%,d VND", (int) giamGiaApDung));
        txtfinaldiscount.setText(String.format("-%,d VND", (int) giamGiaApDung));

        listsp_selected_apdater adapter = new listsp_selected_apdater(this, selectedItemsList);
        recyclerViewOrderItems.setAdapter(adapter);
    }

    private void setupEventHandlers() {
        back.setOnClickListener(v -> finish());
        discount.setOnClickListener(v -> {
            Intent intent = new Intent(activity_thanhtoan.this, com.example.duan_appbanhang.Activity.home.activity_voucher.class);
            intent.putExtra("totalPrice", totalPrice);
            startActivityForResult(intent, 100);
        });
        address.setOnClickListener(v -> {
            Intent intent = new Intent(activity_thanhtoan.this, addressActivity.class);
            startActivityForResult(intent, 200);
        });
        btnConfirmOrder.setOnClickListener(v -> {
            if (validateOrderInfo()) {
                submitOrder();
            }
        });
    }

    private void setupPaymentMethod() {
        payment_cash.setOnClickListener(v -> {
            rdo_cash.setChecked(true);
            rdo_banking.setChecked(false);
            paymentMethod = "cash";
        });
        payment_banking.setOnClickListener(v -> {
            rdo_cash.setChecked(false);
            rdo_banking.setChecked(true);
            paymentMethod = "bidv";
        });
    }

    private boolean validateOrderInfo() {
        if (maKhachHang == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedItemsList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tenKhachHang.isEmpty() || soDienThoaiKhachHang.isEmpty() || diaChiGiaoHang.isEmpty()) {
            Toast.makeText(this, "Vui lòng cập nhật thông tin giao hàng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!soDienThoaiKhachHang.matches("\\d{9,11}")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Kiểm tra maSanPham
        SharedPreferences prefs = getSharedPreferences("CartData", MODE_PRIVATE);
        String maSanPhamJson = prefs.getString("maSanPhamList", "[]");
        try {
            JSONArray maSanPhamArray = new JSONArray(maSanPhamJson);
            if (maSanPhamArray.length() != selectedItemsList.size()) {
                Toast.makeText(this, "Dữ liệu sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
            Toast.makeText(this, "Lỗi dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submitOrder() {
        JSONObject orderData = new JSONObject();
        try {
            // Tạo ghiChuDonHang
            String ghiChuDonHang = editOrderNotes.getText().toString().trim();
            if (ghiChuDonHang.isEmpty()) {
                ghiChuDonHang = "Không có ghi chú";
            }

            // Tính phí vận chuyển và thuế
            BigDecimal phiVanChuyen = new BigDecimal("15000");
            BigDecimal thue = BigDecimal.ZERO;
            BigDecimal tongTienHang = new BigDecimal(totalPrice);
            BigDecimal giamGia = new BigDecimal(giamGiaApDung);
            BigDecimal tongThanhToan = tongTienHang.add(phiVanChuyen).add(thue).subtract(giamGia);

            // Tạo ngày đặt hàng và ngày giao hàng dự kiến
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            String ngayDatHang = sdf.format(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String ngayGiaoHangDuKien = sdf.format(calendar.getTime());

            // Lấy maSanPham từ SharedPreferences
            SharedPreferences prefs = getSharedPreferences("CartData", MODE_PRIVATE);
            String maSanPhamJson = prefs.getString("maSanPhamList", "[]");
            // Log chuỗi JSON
            Log.d(TAG, "maSanPhamJson: " + maSanPhamJson);

            // Kiểm tra tính hợp lệ của chuỗi JSON
            if (maSanPhamJson.isEmpty() || !maSanPhamJson.startsWith("[")) {
                Log.e(TAG, "Invalid maSanPhamJson: " + maSanPhamJson);
                Toast.makeText(this, "Dữ liệu sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray maSanPhamArray;
            try {
                maSanPhamArray = new JSONArray(maSanPhamJson);
                // Log số lượng phần tử
                Log.d(TAG, "maSanPhamArray length: " + maSanPhamArray.length());
                // Kiểm tra từng phần tử là số nguyên
                for (int i = 0; i < maSanPhamArray.length(); i++) {
                    if (!maSanPhamArray.get(i).getClass().equals(Integer.class)) {
                        Log.e(TAG, "Invalid maSanPham at index " + i + ": " + maSanPhamArray.get(i));
                        Toast.makeText(this, "Dữ liệu sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSON Parse Error: " + e.getMessage());
                Toast.makeText(this, "Lỗi dữ liệu sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra số lượng maSanPham khớp với selectedItemsList
            if (maSanPhamArray.length() != selectedItemsList.size()) {
                Log.e(TAG, "maSanPhamArray size (" + maSanPhamArray.length() + ") does not match selectedItemsList size (" + selectedItemsList.size() + ")");
                Toast.makeText(this, "Lỗi dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo JSON cho đơn hàng
            orderData.put("maKhachHang", maKhachHang);
            orderData.put("maKhuyenMai", maKhuyenMai == -1 ? JSONObject.NULL : maKhuyenMai);
            orderData.put("tenKhachHang", tenKhachHang);
            orderData.put("soDienThoaiKhachHang", soDienThoaiKhachHang);
            orderData.put("diaChiGiaoHang", diaChiGiaoHang);
            orderData.put("tongTienHang", tongTienHang);
            orderData.put("phiVanChuyen", phiVanChuyen);
            orderData.put("thue", thue);
            orderData.put("giamGiaApDung", giamGia);
            orderData.put("tongThanhToan", tongThanhToan);
            orderData.put("phuongThucThanhToan", paymentMethod.equals("cash") ? "TienMat" : "ViMomo");
            orderData.put("trangThaiThanhToan", "ChuaThanhToan");
            orderData.put("trangThaiDonHang", "ChoXacNhan");
            orderData.put("ngayDatHang", ngayDatHang);
            orderData.put("ngayGiaoHangDuKien", ngayGiaoHangDuKien);
            orderData.put("ngayGiaoHangThucTe", JSONObject.NULL);
            orderData.put("ngayCapNhat", JSONObject.NULL);
            orderData.put("ghiChuDonHang", ghiChuDonHang);
            orderData.put("lyDoHuy", JSONObject.NULL);

            // Tạo danh sách chi tiết đơn hàng
            JSONArray chiTietDonHangs = new JSONArray();
            for (int i = 0; i < selectedItemsList.size(); i++) {
                listsp_selected item = selectedItemsList.get(i);
                JSONObject chiTiet = new JSONObject();
                chiTiet.put("maSanPham", maSanPhamArray.getInt(i));
                chiTiet.put("soLuong", item.getQuantity());
                BigDecimal donGia = new BigDecimal(item.getPrice().replaceAll("[^0-9]", ""));
                chiTiet.put("donGia", donGia);
                chiTiet.put("thanhTien", donGia.multiply(new BigDecimal(item.getQuantity())));
                chiTiet.put("ngayThem", ngayDatHang);
                chiTiet.put("ghiChu", JSONObject.NULL);
                chiTietDonHangs.put(chiTiet);
            }
            orderData.put("chiTietDonHangs", chiTietDonHangs);

            // Log JSON
            Log.d(TAG, "Order JSON: " + orderData.toString(2));

        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi tạo dữ liệu đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Utils.createOrderUrl();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, orderData,
                response -> {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getSharedPreferences("CartData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("maSanPhamList");
                    editor.apply();
                    Intent intent = new Intent(this, home_activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Log.e(TAG, "API Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Response Code: " + error.networkResponse.statusCode);
                        try {
                            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e(TAG, "Response Data: " + responseBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage());
                        }
                    } else {
                        Log.e(TAG, "No network response available");
                    }
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Lỗi server không xác định";
                    Toast.makeText(this, "Lỗi khi đặt hàng: " + errorMessage, Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Xử lý dữ liệu từ activity_voucher
            if (data.hasExtra("discountedPrice") && data.hasExtra("maKhuyenMai") && data.hasExtra("maGiamGia")) {
                finaltotal = data.getDoubleExtra("discountedPrice", totalPrice);
                giamGiaApDung = data.getDoubleExtra("discountAmount", 0);
                maKhuyenMai = data.getIntExtra("maKhuyenMai", -1);
                maGiamGia = data.getStringExtra("maGiamGia");

                txtTotal.setText(String.format("Tổng: %,d VND", (int) finaltotal));
                txtdiscount.setText(maGiamGia.isEmpty() ? "Chưa áp dụng mã" : maGiamGia);
                txtDiscountInfo.setText(String.format("Giảm giá: -%,d VND", (int) giamGiaApDung));
                txtfinaldiscount.setText(String.format("-%,d VND", (int) giamGiaApDung));
            } else {
                finaltotal = totalPrice;
                giamGiaApDung = 0;
                maKhuyenMai = -1;
                maGiamGia = "";
                txtTotal.setText(String.format("Tổng: %,d VND", (int) finaltotal));
                txtdiscount.setText("Chưa áp dụng mã");
                txtDiscountInfo.setText("Giảm giá: 0 VND");
                txtfinaldiscount.setText("0 VND");
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            // Xử lý dữ liệu từ addressActivity
            tenKhachHang = data.getStringExtra("hoTen");
            soDienThoaiKhachHang = data.getStringExtra("soDienThoai");
            diaChiGiaoHang = data.getStringExtra("diaChiChiTiet");

            // Lưu vào SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("tenKhachHang", tenKhachHang);
            editor.putString("soDienThoai", soDienThoaiKhachHang);
            editor.putString("diaChi", diaChiGiaoHang);
            editor.apply();

            // Hiển thị trong txtAddress
            String addressText = String.format("%s (+84) %s\n%s", tenKhachHang, soDienThoaiKhachHang, diaChiGiaoHang);
            txtAddress.setText(addressText);
        }
    }
}