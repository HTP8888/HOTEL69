package model;

public class DanhGia {
    private int id;
    private int nguoiDungId;
    private int phongId;
    private int datPhongId; // <--- THÊM DÒNG NÀY
    private int diemDanhGia;
    private String noiDung;
    private String ngayDanhGia;
    private String tenNguoiDanhGia;

    public DanhGia(int id, int nguoiDungId, int phongId, int diemDanhGia, String noiDung, String ngayDanhGia, String tenNguoiDanhGia) {
        this.id = id;
        this.nguoiDungId = nguoiDungId;
        this.phongId = phongId;
        this.diemDanhGia = diemDanhGia;
        this.noiDung = noiDung;
        this.ngayDanhGia = ngayDanhGia;
        this.tenNguoiDanhGia = tenNguoiDanhGia;
    }

    public DanhGia(int id, int nguoiDungId, int phongId, int datPhongId, int diemDanhGia, String noiDung, String ngayDanhGia, String tenNguoiDanhGia) {
        this.id = id;
        this.nguoiDungId = nguoiDungId;
        this.phongId = phongId;
        this.datPhongId = datPhongId;
        this.diemDanhGia = diemDanhGia;
        this.noiDung = noiDung;
        this.ngayDanhGia = ngayDanhGia;
        this.tenNguoiDanhGia = tenNguoiDanhGia;
    }

    // Getter và Setter cho tất cả các thuộc tính
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(int nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public int getPhongId() {
        return phongId;
    }

    public void setPhongId(int phongId) {
        this.phongId = phongId;
    }

    public int getDiemDanhGia() {
        return diemDanhGia;
    }

    public void setDiemDanhGia(int diemDanhGia) {
        this.diemDanhGia = diemDanhGia;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getNgayDanhGia() {
        return ngayDanhGia;
    }

    public void setNgayDanhGia(String ngayDanhGia) {
        this.ngayDanhGia = ngayDanhGia;
    }

    public String getTenNguoiDanhGia() {
        return tenNguoiDanhGia;
    }

    public void setTenNguoiDanhGia(String tenNguoiDanhGia) {
        this.tenNguoiDanhGia = tenNguoiDanhGia;
    }

    public int getDatPhongId() {
        return datPhongId;
    }

    public void setDatPhongId(int datPhongId) {
        this.datPhongId = datPhongId;
    }
}
