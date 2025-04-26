package com.example.duan_appbanhang.Module;

public class CartItem {
    private String tenSanPham;
    private int soLuong;
    private String hinhAnh;
    private double giaThanh;
    private double giaBanGoc; // Thêm trường này
    private String note;
    private boolean isSelected; // Lưu trạng thái checkbox
    private  String masanpham;
    public CartItem( String masanpham,  String tenSanPham, int soLuong, String hinhAnh, double giaThanh,String note,double giaBanGoc) {
        this.masanpham = masanpham;
        this.tenSanPham = tenSanPham;
        this.soLuong = soLuong;
        this.hinhAnh = hinhAnh;
        this.giaThanh = giaThanh;
        this.giaBanGoc = giaBanGoc;
        this.note = note;
        this.isSelected = false; // Mặc định không chọn
    }
    public int tinhTongTien() {
        return (int) (soLuong * giaThanh);
    }

    public double getGiaBanGoc() {
        return giaBanGoc;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public String getTenSanPham() {
        return tenSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public double getGiaThanh() {
        return giaThanh;
    }

    public String getNote() {
        return note;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public void setGiaThanh(double giaThanh) {
        this.giaThanh = giaThanh;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setGiaBanGoc(double giaBanGoc) {
        this.giaBanGoc = giaBanGoc;
    }

    public String getMasanpham() {
        return masanpham;
    }

    public void setMasanpham(String masanpham) {
        this.masanpham = masanpham;
    }
}
