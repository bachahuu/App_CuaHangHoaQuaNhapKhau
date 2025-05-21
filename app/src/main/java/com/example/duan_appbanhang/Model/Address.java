package com.example.duan_appbanhang.Model;

public class Address {
    private String hoTen;

    private String soDienThoai;
    private String diaChiChiTiet;
    private  int id;
    private int maKhachHang;
    private boolean laMacDinh;
    private String loaiDiaChi;

    public Address(String hoTen, String soDienThoai, String diaChiChiTiet, int id, int maKhachHang, boolean laMacDinh, String loaiDiaChi) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChiChiTiet = diaChiChiTiet;
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.laMacDinh = laMacDinh;
        this.loaiDiaChi = loaiDiaChi;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getDiaChiChiTiet() {
        return diaChiChiTiet;
    }

    public int getId() {
        return id;
    }

    public int getMaKhachHang() {
        return maKhachHang;
    }

    public boolean isLaMacDinh() {
        return laMacDinh;
    }

    public String getLoaiDiaChi() {
        return loaiDiaChi;
    }
}
