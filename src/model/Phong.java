package model;

public class Phong {
    private int id;
    private String tenPhong, loaiPhong, trangThai, moTa, tienNghi;
    private double gia;

    public Phong(int id, String tenPhong, String loaiPhong, double gia, String trangThai, String tienNghi) {
        this.id = id;
        this.tenPhong = tenPhong;
        this.loaiPhong = loaiPhong;
        this.gia = gia;
        this.trangThai = trangThai;
        this.tienNghi = tienNghi;
    }

    // Getter và setter cho các thuộc tính
    public int getId() { return id; }
    public String getTenPhong() { return tenPhong; }
    public String getLoaiPhong() { return loaiPhong; }
    public double getGia() { return gia; }
    public String getTrangThai() { return trangThai; }
    public String getMoTa() { return moTa; }  // Thêm phương thức getter cho MoTa
    public String getTienNghi() { return tienNghi; }  // Thêm phương thức getter cho Tiện nghi
}
