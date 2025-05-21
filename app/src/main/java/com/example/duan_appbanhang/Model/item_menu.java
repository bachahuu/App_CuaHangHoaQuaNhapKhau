package com.example.duan_appbanhang.Model;

public class item_menu {
    private int masanpham;
    private String imageUrl;
    //private String discount;
    private String productName;
    //private String announce;
    private String price;
    private String Mota;
    private Double danhgiatrungbinh;

    public item_menu(int masanpham,String imageUrl, String productName, String price,Double danhgiatrungbinh,String Mota) {
        this.masanpham = masanpham;
        this.imageUrl = imageUrl;
        //this.discount = discount;
        this.productName = productName;
        //this.announce = announce;
        this.price = price;
        this.danhgiatrungbinh = danhgiatrungbinh;
        this.Mota = Mota;

    }

    public String getMota() {
        return Mota;
    }

    public void setMota(String mota) {
        Mota = mota;
    }

    public int getMasanpham() {
        return masanpham;
    }

    public void setMasanpham(int masanpham) {
        this.masanpham = masanpham;
    }

    public Double getDanhgiatrungbinh() {
        return danhgiatrungbinh;
    }

    public void setDanhgiatrungbinh(Double danhgiatrungbinh) {
        this.danhgiatrungbinh = danhgiatrungbinh;
    }


    public String getImageUrl() {
        return imageUrl;
    }



    public String getProductName() {
        return productName;
    }



    public String getPrice() {
        return price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



    public void setProductName(String productName) {
        this.productName = productName;
    }


    public void setPrice(String price) {
        this.price = price;
    }
}
