package com.example.duan_appbanhang.utils;

import android.os.Build;

public class Utils {
    public static String getBaseUrl() {
        boolean isEmulator = Build.FINGERPRINT.contains("generic")
                || Build.MODEL.contains("Emulator")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"));

        if (isEmulator) {
            return "http://10.0.2.2:8080/api_banhang/";  // Máy ảo
        } else {
            return "http://192.168.0.104:8080/api_banhang/"; // Đổi IP theo máy tính của bạn
        }
    }
}
