package com.example.duan_appbanhang.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.duan_appbanhang.Model.item_menu;
import com.example.duan_appbanhang.R;
import com.example.duan_appbanhang.Activity.home.chitietsp;

import java.text.DecimalFormat;
import java.util.List;

public class popularadapter extends RecyclerView.Adapter<popularadapter.ViewHolder> {
    private List<item_menu> array;
    private Context context;


    // Constructor
    public popularadapter(Context context, List<item_menu> array) {
        this.array = array;
        this.context = context;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_sp;
        TextView danhgiatrungbinh, tensp, giathanh, mota;
        String giaFormatted; // Lưu giá đã định dạng để sử dụng trong onClick

        public ViewHolder(View itemView) {
            super(itemView);
            tensp = itemView.findViewById(R.id.pop_name);
            mota = itemView.findViewById(R.id.pop_des);
            danhgiatrungbinh = itemView.findViewById(R.id.pop_rating);
            giathanh = itemView.findViewById(R.id.pop_giaban);
            img_sp = itemView.findViewById(R.id.pop_img);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.popular_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        item_menu item = array.get(position);
        holder.tensp.setText(item.getProductName());
        holder.mota.setText(item.getMota());
        holder.danhgiatrungbinh.setText(String.valueOf(item.getDanhgiatrungbinh()));
        holder.giathanh.setText(item.getPrice());
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
        holder.giathanh.setText(holder.giaFormatted);
        Glide.with(context).load(item.getImageUrl()).into(holder.img_sp);
        // Thêm sự kiện click vào item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để mở ChiTietSanPhamActivity
                Intent intent = new Intent(context, chitietsp.class);

                // Đưa thông tin sản phẩm vào Intent
                intent.putExtra("TenSanPham", item.getProductName());
                intent.putExtra("maSanPham", item.getMasanpham());
                intent.putExtra("danhGiaTrungBinh", item.getDanhgiatrungbinh());     // nếu có
                intent.putExtra("GiaBan", item.getPrice());
                intent.putExtra("GiaFormat", holder.giaFormatted);
                intent.putExtra("HinhAnh", item.getImageUrl());
                intent.putExtra("MoTa",item.getMota());

                // Mở Activity chi tiết sản phẩm
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }
}