package controller;

import model.Phong;
import java.util.List;
import java.util.ArrayList;

public class PhongController {

    private List<Phong> danhSachPhong;

    public PhongController() {
        // Khởi tạo danh sách phòng mẫu
        danhSachPhong = new ArrayList<>();
        danhSachPhong.add(new Phong(1, "Phòng đơn A101", "Đơn", 500000, "Trống", "Wifi, Máy lạnh, TV"));
        danhSachPhong.add(new Phong(2, "Phòng đôi B202", "Đôi", 800000, "Trống", "Wifi, Máy lạnh, TV, Bồn tắm"));
        danhSachPhong.add(new Phong(3, "Phòng VIP C303", "VIP", 1500000, "Đã đặt", "Wifi, Máy lạnh, TV, Jacuzzi, Mini bar"));
    }

    // Phương thức tìm kiếm phòng theo loại
    public List<Phong> timKiemPhongTheoLoai(String loai) {
        List<Phong> result = new ArrayList<>();
        for (Phong p : danhSachPhong) {
            if (p.getLoaiPhong().equalsIgnoreCase(loai)) {
                result.add(p);
            }
        }
        return result;
    }

    // Phương thức tìm phòng theo ID
    public Phong getPhongById(int id) {
        for (Phong p : danhSachPhong) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}
