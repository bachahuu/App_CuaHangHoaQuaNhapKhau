package com.example.duan_appbanhang.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan_appbanhang.Model.listsp_selected;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.utils.Utils;

import java.util.List;

public class listsp_selected_apdater extends RecyclerView.Adapter<listsp_selected_apdater.viewHolder> {
    private Context context;
    private List<listsp_selected> selectedItemsList;

    public listsp_selected_apdater(Context context, List<listsp_selected> selectedItemsList) {
        this.context = context;
        this.selectedItemsList = selectedItemsList;
    }

    @NonNull
    @Override
    public listsp_selected_apdater.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listsp, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listsp_selected_apdater.viewHolder holder, int position) {
        listsp_selected item = selectedItemsList.get(position);

        // Hiển thị thông tin sản phẩm
        holder.textProductName.setText(item.getName());
        holder.textProductPrice.setText(item.getPrice());
        holder.textProductQuantity.setText("x" + item.getQuantity());

// Hiển thị hình ảnh sản phẩm
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Nếu imageUrl là URL đầy đủ
            if (imageUrl.startsWith("http")) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground) // Thay thế bằng hình placeholder của bạn
                        .error(R.drawable.ic_launcher_background) // Thay thế bằng hình lỗi của bạn
                        .into(holder.imageProduct);
            } else {
                // Nếu imageUrl chỉ là tên file, thêm base URL
                Glide.with(context)
                        .load(Utils.getBaseUrl() + "images/" + imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground) // Thay thế bằng hình placeholder của bạn
                        .error(R.drawable.ic_launcher_background) // Thay thế bằng hình lỗi của bạn
                        .into(holder.imageProduct);
            }
        }
    }

    @Override
    public int getItemCount() {
        return selectedItemsList.size();
    }
    public static class viewHolder extends RecyclerView.ViewHolder{
        ImageView imageProduct;
        TextView textProductName,textProductPrice,textProductQuantity;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
            textProductQuantity = itemView.findViewById(R.id.textProductQuantity);
        }
    }
}
