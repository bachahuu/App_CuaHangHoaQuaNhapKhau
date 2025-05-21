package com.example.duan_appbanhang.utils;

import android.os.Build;

public class Utils {
    public static String getBaseUrl() {
        boolean isEmulator = Build.FINGERPRINT.contains("generic")
                || Build.MODEL.contains("Emulator")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"));

        if (isEmulator) {
            return "http://10.0.2.2:8080";  // Spring Boot mặc định chạy ở cổng 8080 - cho máy ảo
        } else {
            return "http://192.168.0.104:8080"; // Spring Boot cho thiết bị thật - đổi IP theo máy tính của bạn
        }
    }

    // Các endpoints cho Cart dựa vào CartController.java của Spring Boot
    public static String getCartUrl() {
        return getBaseUrl() + "/api/cart";
    }
    // Thêm hàm cho /api/cart/count
    public static String getCartCountUrl() {
        return getBaseUrl() + "/api/cart/count";
    }

    public static String getCartByIdUrl(int cartId) {
        return getBaseUrl() + "/api/cart/" + cartId;
    }

    public static String addToCartUrl() {
        return getBaseUrl() + "/api/cart/add";
    }

    public static String updateCartUrl() {
        return getBaseUrl() + "/api/cart/update";
    }

    public static String deleteCartItemUrl(int maCart) {
        return getBaseUrl() + "/api/cart/" + maCart;
    }

    // Các endpoints cho Order dựa vào OrderController.java của Spring Boot
    public static String getOrdersUrl() {
        return getBaseUrl() + "/api/orders/json";
    }

    public static String getOrderByIdUrl(int orderId) {
        return getBaseUrl() + "/api/orders/" + orderId;
    }
    public static String getAddress(){
        return getBaseUrl() + "/addresses";
    }

    public static String createOrderUrl() {
        return getBaseUrl() + "/api/orders/create";
    }

    public static String updateOrderUrl() {
        return getBaseUrl() + "/api/orders/update";
    }

    public static String cancelOrderUrl(int orderId) {
        return getBaseUrl() + "/api/orders/cancel/" + orderId;
    }
    // Các endpoints cho Order dựa vào SanPhamController.java của Spring Boot
    public static String getsanphamurl(){
        return getBaseUrl()+"/api/sanpham";
    }
    public static String gethighrating(){
        return getBaseUrl()+"/api/sanpham/highrating";
    }
    // Các endpoints cho Order dựa vào KhuyenMaiController.java của Spring Boot
    public static String getMagiamgia(){
        return getBaseUrl()+"/api/khuyenmai";
    }

    public static String getLoginUrl() {
        return getBaseUrl() + "/api/accounts/login";
    }

    public static String getSignUpUrl() {
        return getBaseUrl() + "/api/accounts";
    }

//    public static String getForgotPasswordEmailUrl(){
//        return getBaseUrl()+"/api/accounts/forgot-password/request";
//    }
//
//    public static String getVerifyCodeUrl(){
//        return getBaseUrl()+"/api/accounts/forgot-password/verify";
//    }
//
//    public static String getResetPasswordUrl(){
//        return getBaseUrl()+"/api/accounts/forgot-password/reset";
//    }

    public static String getForgotPasswordUrl() {
        return getBaseUrl() + "/api/accounts/forgot-password"; 
    }

    public static String getVerifyCodeUrl() {
        return getBaseUrl() + "/api/accounts/verify-reset-code";
    }

    public static String getResetPasswordUrl() {
        return getBaseUrl() + "/api/accounts/reset-password";
    }

    public static String getGoogleSignInUrl() {
        return getBaseUrl() + "/api/accounts/google-signin";
    }

    public static String getRegisterUrl() {
        return getBaseUrl() + "/api/accounts/register";
    }
    // hàm kết nôis thông tin giữa hai bảng taikhoan và khachhang
    // Thêm endpoint cho /client/khachhang-taikhoan
    public static String getKhachHangTaiKhoanUrl() {
        return getBaseUrl() + "/client/khachhang-taikhoan";
    }
}