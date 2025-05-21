package com.example.duan_appbanhang.Model;

public class item_voucher {
    private Integer maKhuyenMai;
    private String maGiamGia;
    private String mucGiamGia;
    private String loaiGiamGia;
    private String sanPhamApDung;
    private String giaTriDonHangToiThieu;
    private String hanSuDung;
    private String apDungCho;
    private boolean isSelected; // Trạng thái checkbox

    public item_voucher(Integer maKhuyenMai, String maGiamGia, String mucGiamGia, String loaiGiamGia, String sanPhamApDung, String giaTriDonHangToiThieu, String hanSuDung, String apDungCho, boolean isSelected) {
        this.maKhuyenMai = maKhuyenMai;
        this.maGiamGia = maGiamGia;
        this.mucGiamGia = mucGiamGia;
        this.loaiGiamGia = loaiGiamGia;
        this.sanPhamApDung = sanPhamApDung;
        this.giaTriDonHangToiThieu = giaTriDonHangToiThieu;
        this.hanSuDung = hanSuDung;
        this.apDungCho = apDungCho;
        this.isSelected = isSelected; // mặc định là chưa chọn
    }

        public Integer getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public String getMucGiamGia() {
        return mucGiamGia;
    }

    public String getLoaiGiamGia() {
        return loaiGiamGia;
    }

    public String getSanPhamApDung() {
        return sanPhamApDung;
    }

    public String getGiaTriDonHangToiThieu() {
        return giaTriDonHangToiThieu;
    }

    public String getHanSuDung() {
        return hanSuDung;
    }

    public String getApDungCho() {
        return apDungCho;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getMaGiamGia() {
        return maGiamGia;
    }

}
