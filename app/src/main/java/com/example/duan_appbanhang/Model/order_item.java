package com.example.duan_appbanhang.Model;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

public class order_item implements Serializable {
    private int id;
    private Date orderDate;
    private double totalAmount;
    private OrderStatus status;
    private String paymentMethod;

    // Enum cho các trạng thái đơn hàng
    public enum OrderStatus {
        WAITING_CONFIRMATION("Chờ Xác Nhận"),
        PREPARING("Đang Xử Lý"),
        DELIVERING("Đang Giao"),
        DELIVERED("Đã Giao"),
        DELETED("Đã Hủy");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
        // Thêm phương thức để lấy OrderStatus từ tên hiển thị
        public static OrderStatus fromDisplayName(String displayName) {
            if ("ChoXacNhan".equalsIgnoreCase(displayName)) {
                return WAITING_CONFIRMATION;
            }
            if ("DangXuLy".equalsIgnoreCase(displayName)) {
                return PREPARING;
            }
            if ("DangGiao".equalsIgnoreCase(displayName)) {
                return DELIVERING;
            }
            if ("DaGiao".equalsIgnoreCase(displayName)) {
                return DELIVERED;
            }
            if ("DaHuy".equalsIgnoreCase(displayName)) {
                return DELETED;
            }
            for (OrderStatus status : OrderStatus.values()) {
                if (status.displayName.equals(displayName)) {
                    return status;
                }
            }
            Log.e("OrderStatus", "Unknown status: " + displayName);
            return WAITING_CONFIRMATION; // Giá trị mặc định
        }
    }

    public order_item() {
    }

    public order_item(int id, Date orderDate, double totalAmount, OrderStatus status, String paymentMethod) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public int getId() {
        return id;
    }

    public String getFormattedMaDonHang() {
        return "DH" + id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }



    public void setId(int id) {
        this.id = id;
    }


    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


}
