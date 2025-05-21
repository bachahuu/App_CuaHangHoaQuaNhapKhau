package com.example.duan_appbanhang.Activity.home;

import static com.example.duan_appbanhang.Model.order_item.OrderStatus.DELIVERED;
import static com.example.duan_appbanhang.Model.order_item.OrderStatus.DELIVERING;
import static com.example.duan_appbanhang.Model.order_item.OrderStatus.PREPARING;
import static com.example.duan_appbanhang.Model.order_item.OrderStatus.WAITING_CONFIRMATION;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.duan_appbanhang.Adapter.order_Adapter;
import com.example.duan_appbanhang.Model.order_item;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
import com.example.duan_appbanhang.utils.BaseActivity;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private order_Adapter orderAdapter;
    private List<order_item> orderList;
    private List<order_item> filteredOrderList;
    private Button btnWaitingConfirm, btn_chuanbi, btnDelivering, btnDelivered,btnDelete;
    private ImageView btnBack;
    private TextView cart_index;
    private static final String TAG = "home_activity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        // Khởi tạo các view
        recyclerView = findViewById(R.id.menu);
        btnWaitingConfirm = findViewById(R.id.btn_choxacnhan);
        btn_chuanbi = findViewById(R.id.btn_dangcb);
        btnDelivering = findViewById(R.id.btn_danggiao);
        btnDelivered = findViewById(R.id.btn_dagiao);
        btnDelete = findViewById(R.id.btn_dahuy);
        btnBack = findViewById(R.id.btnBack);
        cart_index = findViewById(R.id.activity_menu_sl);
        // Khởi tạo danh sách đơn hàng
        orderList = new ArrayList<>();
        filteredOrderList = new ArrayList<>();
        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new order_Adapter(this,filteredOrderList);
        recyclerView.setAdapter(orderAdapter);
        // Lấy dữ liệu từ API
        getOrders();
        // Thiết lập sự kiện cho các nút trạng thái
        btnWaitingConfirm.setOnClickListener(v -> filterOrderList(WAITING_CONFIRMATION));
        btn_chuanbi.setOnClickListener(v -> filterOrderList(PREPARING));
        btnDelivering.setOnClickListener(v -> filterOrderList(DELIVERING));
        btnDelivered.setOnClickListener(v -> filterOrderList(DELIVERED));
        btnDelete.setOnClickListener(v -> filterOrderList(DELIVERED));
        // Thiết lập sự kiện cho BottomAppBar
        setupBottomAppBar();
        updateCartBadge();
    }

    private void setupBottomAppBar() {

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.imageView6).setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, home_activity.class);
            startActivity(intent);
        });

        findViewById(R.id.img_thucdon).setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, activity_Menu.class);
            startActivity(intent);
        });

        findViewById(R.id.icon_giohang).setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, activity_cart.class);
            startActivityForResult(intent, 1); // Chỉ dùng startActivityForResult
        });

        findViewById(R.id.img_donhang).setOnClickListener(v -> {
            // Đã ở OrderActivity, không cần chuyển
        });

//        findViewById(R.id.img_thongtin).setOnClickListener(v -> {
//            Intent intent = new Intent(OrderActivity.this, SettingActivity.class);
//            startActivity(intent);
//        });
    }
    private void updateCartBadge() {
        String COUNT_API_URL = Utils.getCartCountUrl(); // URL của file PHP đếm số lượng
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
                Toast.makeText(OrderActivity.this, "Lỗi khi tải số lượng giỏ hàng!", Toast.LENGTH_SHORT).show();
                updateCartBadge();
            }
        });

        ApiClient.getInstance(this).addToRequestQueue(request);
    }

    private void filterOrderList(order_item.OrderStatus orderStatus) {
        filteredOrderList.clear();
        filteredOrderList.addAll(orderList.stream().filter(orderItem -> orderItem.getStatus() == orderStatus)
                .collect(Collectors.toList()));
        orderAdapter.notifyDataSetChanged();
        // Cập nhật giao diện nút trạng thái
        resetButtonBackgrounds();
        switch (orderStatus) {
            case WAITING_CONFIRMATION:
                btnWaitingConfirm.setBackgroundResource(R.drawable.order_status_button_selected);
                break;
            case PREPARING:
                btn_chuanbi.setBackgroundResource(R.drawable.order_status_button_selected);
                break;
            case DELIVERING:
                btnDelivering.setBackgroundResource(R.drawable.order_status_button_selected);
                break;
            case DELIVERED:
                btnDelivered.setBackgroundResource(R.drawable.order_status_button_selected);
                break;
            case DELETED:
                btnDelete.setBackgroundResource(R.drawable.order_status_button_selected);
        }
        
    }
    private void resetButtonBackgrounds() {
        btnWaitingConfirm.setBackgroundResource(R.drawable.order_status_button_background);
        btn_chuanbi.setBackgroundResource(R.drawable.order_status_button_background);
        btnDelivering.setBackgroundResource(R.drawable.order_status_button_background);
        btnDelivered.setBackgroundResource(R.drawable.order_status_button_background);
        btnDelete.setBackgroundResource(R.drawable.order_status_button_background);
    }

    private void getOrders() {
        String API_URL = Utils.getOrdersUrl();
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null, // Tham số jsonRequest (null vì là GET request)
                response -> {
                    Log.d(TAG, "API Response: " + response.toString());
                    orderList.clear();
                    filteredOrderList.clear();
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            Date orderDate;
                            try {
                                orderDate = dateFormat.parse(item.getString("ngayDatHang"));
                            } catch (ParseException e) {
                                Log.e(TAG, "Date Parsing Error: " + e.getMessage());
                                orderDate = new Date(); // Giá trị mặc định nếu lỗi
                            }
                            order_item order = new order_item(
                                    item.getInt("maDonHang"),
                                    orderDate,
                                    item.getDouble("tongThanhToan"),
                                    order_item.OrderStatus.fromDisplayName(item.getString("trangThaiDonHang")),
                                    item.getString("phuongThucThanhToan")
                            );
                            orderList.add(order);
                        }
                        filteredOrderList.addAll(orderList);
                        orderAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Total orders: " + orderList.size());
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(OrderActivity.this, "Lỗi khi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "API Error: " + error.toString());
                    Toast.makeText(OrderActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                }
        );

        ApiClient.getInstance(this).addToRequestQueue(request);
    }
}
