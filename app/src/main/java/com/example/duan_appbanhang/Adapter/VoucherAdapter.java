package com.example.duan_appbanhang.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan_appbanhang.Model.item_voucher;
import com.example.duan_appbanhang.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<item_voucher> voucherList;
    private Runnable onSelectionChanged;

    public VoucherAdapter(List<item_voucher> voucherList, Runnable onSelectionChanged) {
        this.voucherList = voucherList;
        this.onSelectionChanged = onSelectionChanged;
    }

    @NonNull
    @Override
    public VoucherAdapter.VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher,parent,false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherAdapter.VoucherViewHolder holder, int position) {
        item_voucher voucher = voucherList.get(position);
        // Hiển thị mức giảm giá

        String discounttext;
        if ("PhanTram".equals(voucher.getLoaiGiamGia())){
            // Nếu là phần trăm, loại bỏ phần thập phân nếu là số nguyên
            try {
                double percentValue = Double.parseDouble(voucher.getMucGiamGia());
                if (percentValue == (int)percentValue) {
                    // Nếu là số nguyên (25.0 -> 25)
                    discounttext = (int)percentValue + "%";
                } else {
                    // Nếu có phần thập phân (25.5 -> 25.5)
                    discounttext = percentValue + "%";
                }
            } catch (NumberFormatException e) {
                discounttext = voucher.getMucGiamGia() + "%";
            }
        }else {
            // Nếu là số tiền, định dạng với dấu phân cách hàng nghìn
            try {
                double discountAmount = Double.parseDouble(voucher.getMucGiamGia());
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                discounttext = decimalFormat.format(discountAmount) + " VND";
            } catch (NumberFormatException e) {
                discounttext = voucher.getMucGiamGia() + " VND";
            }
        }
        holder.voucherDiscount.setText(discounttext);
        // Hiển thị điều kiện áp dụng
        String conditionText;
        if (voucher.getSanPhamApDung() != null && !voucher.getSanPhamApDung().isEmpty()) {
            conditionText = "Áp dụng cho: " + voucher.getSanPhamApDung();
        } else {
            conditionText = "Áp dụng cho: " + voucher.getApDungCho();
        }
        holder.voucherCondition.setText(conditionText);
        // Hiển thị hạn sử dụng (định dạng lại ngày tháng)
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(voucher.getHanSuDung());
            String date_Text = "Hết hạn: " + outputFormat.format(date);
            holder.voucherExpiry.setText(date_Text);
        } catch (Exception e) {
            holder.voucherExpiry.setText("Hết hạn: " + voucher.getHanSuDung());
        }
        // Xử lý checkbox
        holder.voucherCheckBox.setChecked(voucher.isSelected());
        holder.voucherCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            voucher.setSelected(isChecked);
            if (onSelectionChanged != null) {
                onSelectionChanged.run();
            }
        });


    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }
    public static class VoucherViewHolder extends RecyclerView.ViewHolder{
        ImageView voucherIcon;
        TextView voucherDiscount,voucherMinOrder,voucherCondition,voucherExpiry;
        CheckBox voucherCheckBox;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            voucherIcon = itemView.findViewById(R.id.voucherIcon);
            voucherDiscount = itemView.findViewById(R.id.voucherDiscount);
            voucherMinOrder = itemView.findViewById(R.id.voucherMinOrder);
            voucherCondition = itemView.findViewById(R.id.voucherCondition);
            voucherExpiry = itemView.findViewById(R.id.date_voucher);
            voucherCheckBox = itemView.findViewById(R.id.voucherCheckBox);
        }
    }
}
