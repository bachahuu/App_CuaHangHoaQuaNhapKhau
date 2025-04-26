package com.example.duan_appbanhang.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.duan_appbanhang.Module.CartItem;
import com.example.duan_appbanhang.Module.popularModule;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.ApiClient;
import com.example.duan_appbanhang.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                xoaSanPhamKhoiDatabase(item.getMasanpham(), holder.getAdapterPosition());
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
    }

    private void xoaSanPhamKhoiDatabase(String masanpham, int position) {
        String url = Utils.getBaseUrl() + "delete_cart.php";  // API xóa sản phẩm
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
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
                },
                error -> error.printStackTrace()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("MaSanPham", String.valueOf(masanpham));  // Gửi ID sản phẩm cần xóa
                return params;
            }
        };

        ApiClient.getInstance(context).addToRequestQueue(stringRequest);
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
        ImageView imgHinhAnh;
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
        }
    }
}
