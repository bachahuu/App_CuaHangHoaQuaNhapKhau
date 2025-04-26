package com.example.duan_appbanhang.home;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
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
import com.example.duan_appbanhang.Module.item_menu;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
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
    ImageView list;
    Spinner spin;
    private static final String TAG = "home_activity";
    private  final String API_URL = Utils.getBaseUrl() + "get_menu.php"; // API lấy danh sách sản phẩm
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
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "API Response: " + response.toString()); // Kiểm tra dữ liệu trả về
                        ItemList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                Log.d(TAG, "Image URL: " + item.getString("HinhAnh")); // Kiểm tra đường dẫn ảnh
                                item_menu product = new item_menu(
                                        item.getString("HinhAnh"),
                                        item.getString("TenSanPham"),
                                        item.getString("GiaBan")
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
    }
}
