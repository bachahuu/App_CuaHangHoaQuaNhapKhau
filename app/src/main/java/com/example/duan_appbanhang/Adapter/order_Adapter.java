package com.example.duan_appbanhang.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan_appbanhang.Module.order_item;
import com.example.duan_appbanhang.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class order_Adapter extends RecyclerView.Adapter<order_Adapter.OrderViewHolder> {
    private Context context;
    private List<order_item> orderlist;

    public order_Adapter(Context context, List<order_item> orderlist) {
        this.context = context;
        this.orderlist = orderlist;
    }

    @NonNull
    @Override
    public order_Adapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull order_Adapter.OrderViewHolder holder, int position) {
        order_item order = orderlist.get(position);
        holder.txtOrderId.setText("Mã đơn hàng:" + order.getId());
        // Định dạng ngày đặt hàng
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.txtOrderDate.setText("Ngày đặt: " + dateFormat.format(order.getOrderDate()));
        // Định dạng tổng tiền
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedAmount;
        try {
            formattedAmount = decimalFormat.format(order.getTotalAmount()) + " VND";
        } catch (NumberFormatException e) {
            formattedAmount = order.getTotalAmount() + " VND";
        }
        holder.txtTotalAmount.setText("Tổng tiền: " + formattedAmount);
        // Định dạng trạng thái đơn hàng
        holder.txtStatus.setText("Trạng thái: " + order.getStatus().getDisplayName());

        // Định dạng phương thức thanh toán
        holder.txtPaymentMethod.setText("Phương thức thanh toán: " + order.getPaymentMethod());
    }

    @Override
    public int getItemCount() {
        return orderlist.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderDate, txtTotalAmount, txtStatus, txtPaymentMethod;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.order_id);
            txtOrderDate = itemView.findViewById(R.id.order_date);
            txtTotalAmount = itemView.findViewById(R.id.order_total);
            txtStatus = itemView.findViewById(R.id.order_status);
            txtPaymentMethod = itemView.findViewById(R.id.order_phuongthucthanhtoan);
        }
    }
}
