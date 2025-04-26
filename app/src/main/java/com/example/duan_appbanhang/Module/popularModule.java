package com.example.duan_appbanhang.Module;

import com.google.gson.annotations.SerializedName;

public class popularModule {
    @SerializedName("TenSanPham")
    private String TenSanPham;

    @SerializedName("MoTa")
    private String MoTa;

    @SerializedName("SoSao")
    private String SoSao;

    @SerializedName("GiaBan")
    private String GiaBan;

    @SerializedName("HinhAnh")
    private String HinhAnh;

    public popularModule() {
    }

    public popularModule(String tenSanPham, String moTa, String soSao, String giaBan, String hinhAnh) {
        this.TenSanPham = tenSanPham;
        this.MoTa = moTa;
        this.SoSao = soSao;
        this.GiaBan = giaBan;
        this.HinhAnh = hinhAnh;
    }

    public String getTenSanPham() {
        return TenSanPham;
    }

    public String getMoTa() {
        return MoTa;
    }

    public String getSoSao() {
        return SoSao;
    }

    public String getGiaBan() {
        return GiaBan;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setTenSanPham(String tenSanPham) {
        TenSanPham = tenSanPham;
    }

    public void setMoTa(String moTa) {
        MoTa = moTa;
    }

    public void setSoSao(String soSao) {
        SoSao = soSao;
    }

    public void setGiaBan(String giaBan) {
        GiaBan = giaBan;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }
// Remove the previous isEmpty() method
}