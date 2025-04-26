package com.example.duan_appbanhang.Module;

public class item_menu {
    private String imageUrl;
    //private String discount;
    private String productName;
    //private String announce;
    private String price;

    public item_menu(String imageUrl, String productName, String price) {
        this.imageUrl = imageUrl;
        //this.discount = discount;
        this.productName = productName;
        //this.announce = announce;
        this.price = price;
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
