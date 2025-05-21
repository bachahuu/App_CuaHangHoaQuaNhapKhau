package com.example.duan_appbanhang.Model;

public class listsp_selected {
    private String imageUrl; // URL hình ảnh sản phẩm (hoặc có thể thay bằng int nếu dùng resource ID)
    private String name;     // Tên sản phẩm
    private String price;    // Giá tiền
    private int quantity;    // Số lượng

    public listsp_selected(String imageUrl, String name, String price, int quantity) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
