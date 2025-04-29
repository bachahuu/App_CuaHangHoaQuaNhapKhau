package com.example.duan_appbanhang.Module;

import java.io.Serializable;
import java.util.Date;

import retrofit2.http.DELETE;

public class order_item implements Serializable {
    private int id;
    private Date orderDate;
    private double totalAmount;
    private OrderStatus status;
    private String paymentMethod;

    // Enum cho các trạng thái đơn hàng
    public enum OrderStatus {
        WAITING_CONFIRMATION("Chờ Xác Nhận"),
        PREPARING("Đang Chuẩn Bị"),
        DELIVERING("Đang Giao"),
        DELIVERED("Giao Thành Công"),
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
            for (OrderStatus status : values()) {
                if (status.getDisplayName().equals(displayName)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No enum constant with display name: " + displayName);
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
