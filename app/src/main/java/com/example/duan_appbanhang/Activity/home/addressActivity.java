package com.example.duan_appbanhang.Activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.duan_appbanhang.Adapter.AddressAdapter;
import com.example.duan_appbanhang.Model.Address;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.BaseActivity;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class addressActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout btnAdd;
    private ImageView btnback;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_selection);
        recyclerView = findViewById(R.id.recyclerViewAddresses);
        btnAdd = findViewById(R.id.btnAddNewAddress);
        btnback = findViewById(R.id.btnBack);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadAddressFromApi();
    }
    private void back(){
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó
            }
        });

    }

    private void loadAddressFromApi() {
        String url = Utils.getAddress();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    addressList = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Address address = new Address(
                                    obj.getString("hoTen"),
                                    obj.getString("soDienThoai"),
                                    obj.getString("diaChiChiTiet"),
                                    obj.getInt("id"),
                                    obj.getInt("maKhachHang"),
                                    obj.getBoolean("laMacDinh"),
                                    obj.getString("loaiDiaChi")
                            );
                            addressList.add(address);
                        }

                        addressAdapter = new AddressAdapter(addressList);
                        addressAdapter.setOnAddressItemClickListener(new AddressAdapter.OnAddressItemClickListener() {
                            @Override
                            public void onAddressSelected(int position) {
                                Address selectedAddress = addressList.get(position);

                                // Trả về dữ liệu cho Activity trước đó (ví dụ: ThanhToanActivity)
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("hoTen", selectedAddress.getHoTen());
                                resultIntent.putExtra("soDienThoai", selectedAddress.getSoDienThoai());
                                resultIntent.putExtra("diaChiChiTiet", selectedAddress.getDiaChiChiTiet());
                                setResult(RESULT_OK, resultIntent);
                                finish(); // Quay về Activity trước (ThanhToanActivity)
                            }

                            @Override
                            public void onEditClick(int position) {
                                // Code xử lý nút sửa (nếu có)
                            }
                        });

                        recyclerView.setAdapter(addressAdapter);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}
