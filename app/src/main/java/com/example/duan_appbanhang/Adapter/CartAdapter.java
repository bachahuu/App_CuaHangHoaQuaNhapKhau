package com.example.duan_appbanhang.Adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.duan_appbanhang.Model.CartItem;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
import com.example.duan_appbanhang.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<CartItem> cartList;
    private List<Boolean> checkedStates; // Danh sách trạng thái chọn của các mặt hàng
    private OnCartChangeListener cartChangeListener; // Listener để thông báo thay đổi
    private OnCartItemDeleteListener deleteListener; // Interface mới
    // Interface để thông báo khi CheckBox thay đổi
    public interface OnCartChangeListener {
        void onCartItemCheckedChanged();
    }
    // Interface để thông báo khi sản phẩm bị xóa
    public interface OnCartItemDeleteListener {
        void onCartItemDeleted();
    }
    public CartAdapter(Context context,List<CartItem> cartList,OnCartChangeListener listener , OnCartItemDeleteListener deleteListener) {
        this.cartList = cartList;
        this.context = context;
        this.cartChangeListener = listener;
        this.deleteListener = deleteListener; // Khởi tạo deleteListener
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.txtTenSanPham.setText(item.getTenSanPham());
        holder.txtnote.setText(item.getNote());
        holder.txtSoLuong.setText(String.valueOf(item.getSoLuong()));
        // Hiển thị giá hiện tại
        updatePriceDisplay(holder, item);
        // Định dạng giá thành
        double giaBan = item.getGiaThanh();
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        try {
            // Định dạng với dấu chấm phân cách hàng nghìn

            holder.giaFormatted = decimalFormat.format(giaBan)+" VND";
        } catch (NumberFormatException e) {
            holder.giaFormatted = giaBan + " VND"; // Nếu lỗi, giữ nguyên giá trị
        }
        // Hiển thị giá trên giao diện
        holder.txtGiaThanh.setText(holder.giaFormatted);
        // Load ảnh sản phẩm
        Glide.with(context).load(item.getHinhAnh()).into(holder. imgHinhAnh);
        //tang/giam
        holder.tang.setOnClickListener(v -> {
            int newQuantity = item.getSoLuong() + 1;
            item.setSoLuong(newQuantity);
            holder.txtSoLuong.setText(String.valueOf(newQuantity));
            // Cập nhật giá tổng sử dụng giá gốc
            double newTotalPrice = item.getGiaBanGoc() * newQuantity;
            item.setGiaThanh(newTotalPrice);
            // Cập nhật hiển thị giá
            updatePriceDisplay(holder, item);

            if (cartChangeListener != null) {
                cartChangeListener.onCartItemCheckedChanged();
            }
        });
        holder.giam.setOnClickListener(v -> {
            if (item.getSoLuong()>1){
                int newQuantity = item.getSoLuong() - 1;
                item.setSoLuong(newQuantity);
                holder.txtSoLuong.setText(String.valueOf(newQuantity));
                // Cập nhật giá tổng sử dụng giá gốc
                double newTotalPrice = item.getGiaBanGoc() * newQuantity;
                item.setGiaThanh(newTotalPrice);
                // Cập nhật hiển thị giá
                updatePriceDisplay(holder, item);

                if (cartChangeListener != null) {
                    cartChangeListener.onCartItemCheckedChanged();
                }
            } else  {
                // Hiển thị hộp thoại xác nhận trước khi xóa
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Xác nhận xoá")
                        .setMessage("Bạn có chắc muốn xoá sản phẩm này khỏi giỏ hàng không?")
                        .setPositiveButton("Xoá", (dialog, which) -> {
                            xoaSanPhamKhoiDatabase(item.getMaCart(), holder.getAdapterPosition());
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        // Thiết lập trạng thái CheckBox
        holder.checkBox.setOnCheckedChangeListener(null);//Xóa listener cũ
        holder.checkBox.setChecked(item.isSelected()); // Gắn trạng thái
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (cartChangeListener != null) {
                cartChangeListener.onCartItemCheckedChanged();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị hộp thoại xác nhận trước khi xóa
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Xác nhận xoá")
                        .setMessage("Bạn có chắc muốn xoá sản phẩm này khỏi giỏ hàng không?")
                        .setPositiveButton("Xoá", (dialog, which) -> {
                            xoaSanPhamKhoiDatabase(item.getMaCart(), holder.getAdapterPosition());
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    private void xoaSanPhamKhoiDatabase(int maCart, int position) {
        String url = Utils.deleteCartItemUrl(maCart); // Sử dụng URL từ Utils
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            if (success) {
                                // Xóa khỏi danh sách giỏ hàng
                                cartList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, cartList.size());

                                if (cartChangeListener != null) {
                                    cartChangeListener.onCartItemCheckedChanged();
                                }
                                if (deleteListener != null) {
                                    deleteListener.onCartItemDeleted(); // Thông báo xóa để cập nhật số lượng
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                            Toast.makeText(context, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "API Error: " + error.toString());
                        Toast.makeText(context, "Lỗi khi xóa sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        ApiClient.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void updatePriceDisplay(ViewHolder holder, CartItem item) {
        // Lấy giá tổng
        double tongGia = item.getGiaThanh();
        // Định dạng giá thành
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        try {
            holder.giaFormatted = decimalFormat.format(tongGia) + " VND";
        } catch (NumberFormatException e) {
            holder.giaFormatted = tongGia + " VND";
        }
        // Hiển thị giá trên giao diện
        holder.txtGiaThanh.setText(holder.giaFormatted);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
    // Lấy danh sách sản phẩm đã chọn
    public List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new java.util.ArrayList<>();
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenSanPham, txtSoLuong, txtGiaThanh,txtnote;
        ImageView imgHinhAnh,delete;
        String giaFormatted; // Lưu giá đã định dạng để sử dụng trong onClick
        CheckBox checkBox;
        Button tang , giam;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenSanPham = itemView.findViewById(R.id.product_name);
            txtSoLuong = itemView.findViewById(R.id.quantity_text);
            txtGiaThanh = itemView.findViewById(R.id.original_price);
            imgHinhAnh = itemView.findViewById(R.id.product_image);
            txtnote = itemView.findViewById(R.id.product_note);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            tang = itemView.findViewById(R.id.increase_button);
            giam = itemView.findViewById(R.id.decrease_button);
            delete = itemView.findViewById(R.id.delete_button);
        }
    }
}
