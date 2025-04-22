package model;

public class NguoiDung {
    private int id;
    private String hoTen;
    private String email;
    private String matKhau;
    private String vaiTro;
    private String sdt;
    private String diaChi;

    public NguoiDung(int id, String hoTen, String email, String matKhau, String vaiTro, String sdt, String diaChi) {
        this.id = id;
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getEmail() {
        return email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public String getSdt() {
        return sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    // Setters
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}