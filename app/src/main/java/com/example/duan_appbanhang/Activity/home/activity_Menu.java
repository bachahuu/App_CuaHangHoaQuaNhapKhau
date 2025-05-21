package com.example.duan_appbanhang.Activity.home;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.duan_appbanhang.Adapter.menu_adapter;
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

public class activity_Menu extends AppCompatActivity {
    RecyclerView recyclerView_menu;
    menu_adapter Adap;
    List<item_menu> ItemList;
    ImageView list,giohang,Menu,Order,btnback;
    TextView cart_index;
    Spinner spin;
    private static final String TAG = "activity_Menu";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        anhxa();
        list.setOnClickListener(view -> showPopupWindow(view));
        // Thiết lập GridLayoutManager với 2 cột
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 là số cột
        recyclerView_menu.setLayoutManager(layoutManager);
        // Khởi tạo danh sách dữ liệu
        ItemList = new ArrayList<>();
        // Khởi tạo Adapter
        Adap = new menu_adapter(this, ItemList);
        // Thiết lập Adapter cho RecyclerView
        recyclerView_menu.setAdapter(Adap);
        getProducts();
        setupBottomAppBar();
        updateCartBadge();
    }
    private void setupBottomAppBar() {
        // Thiết lập sự kiện nút quay lại
        btnback.setOnClickListener(v -> finish());
        //thiết lập nút chuyển tab
        findViewById(R.id.imageView6).setOnClickListener(v -> {
            Intent intent = new Intent(activity_Menu.this, com.example.duan_appbanhang.Activity.home.home_activity.class);
            startActivity(intent);
        });

        Menu.setOnClickListener(v -> {
            //
        });

        giohang.setOnClickListener(v -> {
            Intent intent = new Intent(activity_Menu.this, activity_cart.class);
            startActivityForResult(intent, 1); // Chỉ dùng startActivityForResult
        });

        Order.setOnClickListener(v -> {
            Intent intent = new Intent(activity_Menu.this, com.example.duan_appbanhang.Activity.home.OrderActivity.class);
            startActivity(intent);
        });

//        findViewById(R.id.img_thongtin).setOnClickListener(v -> {
//            Intent intent = new Intent(OrderActivity.this, SettingActivity.class);
//            startActivity(intent);
//        });
    }
    private void updateCartBadge() {
        String COUNT_API_URL = Utils.getCartCountUrl(); // Sử dụng URL từ Utils.getCartCountUrl()
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(Request.Method.GET, COUNT_API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            long count = response.getLong("SoLuong"); // Lấy giá trị SoLuong từ JSON
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
                Toast.makeText(activity_Menu.this, "Lỗi khi tải số lượng giỏ hàng!", Toast.LENGTH_SHORT).show();
                updateCartBadge();
            }
        });

        ApiClient.getInstance(this).addToRequestQueue(request);
    }
    private void showPopupWindow(View anchorView) {
        // Inflate layout
        View popupView = LayoutInflater.from(this).inflate(R.layout.danhmuc, null);

        // Tạo danh sách danh mục
        String[] categories = {"Trái cây Nội Địa", "Trái cây Nhập Khẩu", "Hoa quả sấy", "Trái cây theo mùa"};
        ListView listView = popupView.findViewById(R.id.list_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        listView.setAdapter(adapter);

        // Tạo PopupWindow
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                600,  // Chiều rộng (có thể thay đổi)
                640,  // Chiều cao (có thể thay đổi)
                true  // Bật tắt khi click ra ngoài
        );

        // Hiển thị PopupWindow ngay dưới imgList
        popupWindow.showAsDropDown(anchorView, -150, 10, Gravity.START);
    }


    private void getProducts() {
        String API_URL = Utils.getsanphamurl(); // Sử dụng URL từ Utils.getsanphamurl()
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "API Response: " + response.toString()); // Kiểm tra dữ liệu trả về
                        ItemList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                Log.d(TAG, "Image URL: " + item.getString("hinhAnh")); // Kiểm tra đường dẫn ảnh
                                item_menu product = new item_menu(
                                        item.getInt("maSanPham"),
                                        item.getString("hinhAnh"),
                                        item.getString("tenSanPham"),
                                        item.getString("giaBan"),
                                        item.getDouble("danhGiaTrungBinh"),
                                        item.getString("moTa")
                                );
                                ItemList.add(product);
                            }
                            Log.d(TAG, "Total items: " + ItemList.size()); // Kiểm tra tổng số sản phẩm trong danh sách
                           Adap.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error: " + error.toString());
                Toast.makeText(activity_Menu.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });

        ApiClient.getInstance(this).addToRequestQueue(request);
    }

    private void anhxa() {
        recyclerView_menu = findViewById(R.id.menu);
        list = findViewById(R.id.imglist);
        Menu = findViewById(R.id.img_thucdon);
        giohang = findViewById(R.id.icon_giohang);
        cart_index = findViewById(R.id.activity_menu_sl);
        Order = findViewById(R.id.img_donhang);
        btnback = findViewById(R.id.btnBack);
    }
}
