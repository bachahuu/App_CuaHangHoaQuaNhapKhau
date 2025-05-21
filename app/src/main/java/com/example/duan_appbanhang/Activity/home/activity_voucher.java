package com.example.duan_appbanhang.Activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.duan_appbanhang.Adapter.VoucherAdapter;
import com.example.duan_appbanhang.Model.item_voucher;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class activity_voucher extends AppCompatActivity {
    private static final String TAG = "activity_voucher";

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;
    private List<item_voucher> voucherList;
    private TextView txtSelectedCount, txtDiscountAmount;
    private ImageView btnBack;
    private Button btnConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        recyclerView = findViewById(R.id.recyclerViewVouchers);
        txtSelectedCount = findViewById(R.id.txtSelectedCount);
        txtDiscountAmount = findViewById(R.id.txtDiscountAmount);
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Khởi tạo danh sách voucher
        voucherList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter
        adapter = new VoucherAdapter(voucherList, () -> updateSummary());
        recyclerView.setAdapter(adapter);

        // Lấy danh sách voucher
        getVouchers();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Đồng ý
        btnConfirm.setOnClickListener(v -> {
            updateSummary();
            finish();
        });
    }

    private void getVouchers() {
        String API_URL = Utils.getMagiamgia();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                response -> {
                    voucherList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            item_voucher voucher = new item_voucher(
                                    item.getInt("maKhuyenMai"),
                                    item.getString("maGiamGia"),
                                    item.getString("mucGiamGia"),
                                    item.getString("loaiGiamGia"),
                                    item.optString("sanPhamApDung", ""),
                                    item.getString("giaTriDonHangToiThieu"),
                                    item.getString("hanSuDung"),
                                    item.getString("apDungCho"),
                                    false
                            );
                            voucherList.add(voucher);
                        }
                        Log.d(TAG, "Total vouchers: " + voucherList.size());
                        adapter.notifyDataSetChanged();
                        updateSummary();
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(activity_voucher.this, "Lỗi khi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "API Error: " + error.toString());
                    Toast.makeText(activity_voucher.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                });
        ApiClient.getInstance(this).addToRequestQueue(request);
    }

    private void updateSummary() {
        int selectedCount = 0;
        double totalDiscount = 0.0;
        double orderTotal = getIntent().getDoubleExtra("totalPrice", 0);
        int maKhuyenMai = -1;
        String maGiamGia = "";

        // Chỉ cần một vòng lặp để tính toán và lấy mã
        for (item_voucher voucher : voucherList) {
            if (voucher.isSelected()) {
                selectedCount++;
                double discountValue = Double.parseDouble(voucher.getMucGiamGia());
                if ("SoTien".equals(voucher.getLoaiGiamGia())) {
                    totalDiscount += discountValue;
                } else {
                    totalDiscount += (discountValue / 100) * orderTotal;
                }
                maKhuyenMai = voucher.getMaKhuyenMai();
                maGiamGia = voucher.getMaGiamGia();
                break; // Chỉ lấy 1 voucher được chọn
            }
        }

        txtSelectedCount.setText(selectedCount + " Voucher đã được chọn");
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedDiscount = decimalFormat.format((int) totalDiscount);
        txtDiscountAmount.setText("Đã áp dụng ưu đãi giảm -đ" + formattedDiscount);

        double finalPrice = orderTotal - totalDiscount;

        Intent resultIntent = new Intent();
        resultIntent.putExtra("discountedPrice", finalPrice);
        resultIntent.putExtra("discountAmount", totalDiscount);
        resultIntent.putExtra("maGiamGia", maGiamGia);
        resultIntent.putExtra("maKhuyenMai", maKhuyenMai); // Gửi kiểu int
        setResult(RESULT_OK, resultIntent);
    }
}