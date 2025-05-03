package model;

import java.util.ArrayList;
import java.util.List;
public class DatPhong {
    private int id;
    private String trangThai;
    private List<Phong> danhSachPhong;

    public DatPhong(int id, String trangThai) {
        this.id = id;
        this.trangThai = trangThai;
        this.danhSachPhong = new ArrayList<>();
    }

    public void themPhong(Phong phong) {
        danhSachPhong.add(phong);
    }

    // Getter
    public int getId() {
        return id;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public List<Phong> getDanhSachPhong() {
        return danhSachPhong;
    }
}