package com.example.duan_appbanhang.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan_appbanhang.Module.item_menu;
import com.example.duan_appbanhang.R;

import java.text.DecimalFormat;
import java.util.List;

public class menu_adapter extends RecyclerView.Adapter<menu_adapter.ViewHolder> {
    private Context context;
    private List<item_menu> itemList;

    public menu_adapter(Context context, List<item_menu> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public menu_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull menu_adapter.ViewHolder holder, int position) {
        item_menu item = itemList.get(position);
        String imageUrl = item.getImageUrl();

        Log.d("Glide Debug", "Position: " + position + " - Image URL: " + imageUrl);

        // Hủy ảnh cũ để tránh lỗi ViewHolder tái sử dụng sai ảnh
        Glide.with(context).clear(holder.imgProduct);

        // Load ảnh mới
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imgProduct);

        holder.txtProductName.setText(item.getProductName());
        // Định dạng giá thành
        String giaBan = item.getPrice(); // Giả sử giaBan là chuỗi số, ví dụ "100000"
        try {
            // Chuyển chuỗi giá thành sang số
            double gia = Double.parseDouble(giaBan);
            // Định dạng với dấu chấm phân cách hàng nghìn
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            holder.giaFormatted = decimalFormat.format(gia)+" VND/kg";
        } catch (NumberFormatException e) {
            holder.giaFormatted = giaBan + " VND/kg"; // Nếu lỗi, giữ nguyên giá trị
        }
        // Hiển thị giá trên giao diện
        holder.txtPrice.setText(holder.giaFormatted);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtDiscount, txtProductName, txtannounce, txtPrice;
        String giaFormatted;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            txtDiscount = itemView.findViewById(R.id.txt_discount);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtannounce = itemView.findViewById(R.id.txt_promotion);
            txtPrice = itemView.findViewById(R.id.txt_price);
        }
    }
}
