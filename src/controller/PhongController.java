package controller;

import model.Database;
import model.Phong;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Statement;

public class PhongController {

    private Connection conn;

    public PhongController() {
        this.conn = Database.getConnection();
    }

    public List<Phong> layDanhSachPhong() {
        List<Phong> danhSachPhong = new ArrayList<>();
        String sql = "SELECT * FROM phong";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten_phong");
                String loai = rs.getString("loai_phong");
                double gia = rs.getDouble("gia");
                String trang_thai = rs.getString("trang_thai");
                String tien_nghi = rs.getString("tien_nghi"); // Lấy tiện nghi từ cơ sở dữ liệu
                String mo_ta = rs.getString("mo_ta"); // Lấy mô tả từ cơ sở dữ liệu

                // Tạo đối tượng Phong với tất cả các thông tin
                Phong phong = new Phong(id, ten, loai, gia, trang_thai, tien_nghi, mo_ta);
                danhSachPhong.add(phong); // Thêm vào danh sách
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSachPhong;
    }

    // Phương thức tìm kiếm phòng theo loại
    public List<Phong> timKiemPhongTheoLoai(String loai) {
        List<Phong> danhSachPhong = layDanhSachPhong(); // Lấy danh sách phòng từ cơ sở dữ liệu
        List<Phong> result = new ArrayList<>();
        for (Phong p : danhSachPhong) {
            if (p.getLoaiPhong().equalsIgnoreCase(loai)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Phong> timKiemPhongTheoTen(String tenPhong) {
        List<Phong> danhSachPhong = layDanhSachPhong(); // Lấy danh sách phòng từ cơ sở dữ liệu
        List<Phong> result = new ArrayList<>();
        for (Phong p : danhSachPhong) {
            if (p.getTenPhong().equalsIgnoreCase(tenPhong)) {
                result.add(p);
            }
        }
        return result;
    }

    // Phương thức tìm phòng theo ID
    public Phong getPhongById(int id) {
        List<Phong> danhSachPhong = layDanhSachPhong();
        for (Phong p : danhSachPhong) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public boolean checkTenPhong(String tenPhong) {
        try {
            String sql = "SELECT COUNT(*) FROM phong WHERE ten_phong = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tenPhong);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return false;  // Tên phòng đã tồn tại
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra tên phòng: " + e.getMessage());
        }
        return true;  // Tên phòng không trùng
    }

    public boolean themPhongVaoHeThong(String tenPhong, String loaiPhong, double gia, String moTa, String tienNghi) {
        try {
            // Câu lệnh SQL để thêm phòng mới
            String sql = "INSERT INTO phong (ten_phong, loai_phong, gia, mo_ta, tien_nghi) VALUES (?, ?, ?, ?, ?)";

            // Chuẩn bị câu lệnh SQL
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Set giá trị cho các tham số trong câu lệnh SQL
            stmt.setString(1, tenPhong);
            stmt.setString(2, loaiPhong);
            stmt.setDouble(3, gia);
            stmt.setString(4, moTa);
            stmt.setString(5, tienNghi);

            // Thực thi câu lệnh và kiểm tra kết quả
            int result = stmt.executeUpdate();
            return result > 0;  // Trả về true nếu thêm phòng thành công, ngược lại là false

        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm phòng: " + e.getMessage());
            return false;
        }
    }

    public boolean xoaPhongKhoiHeThong(String tenPhong) {
        try {
            // Kiểm tra trạng thái phòng trong bảng phong theo tên phòng
            String checkStatusSql = "SELECT trang_thai FROM phong WHERE ten_phong = ?";
            PreparedStatement checkStatusStmt = conn.prepareStatement(checkStatusSql);
            checkStatusStmt.setString(1, tenPhong);
            ResultSet rs = checkStatusStmt.executeQuery();

            if (rs.next()) {
                String trangThai = rs.getString("trang_thai");
                // Nếu phòng đang ở trạng thái "Đã đặt", không cho phép xóa
                if (trangThai.equalsIgnoreCase("Đã Đặt")) {
                    System.out.println("Phòng đang được đặt. Không thể xóa.");
                    return false;
                }
            } else {
                // Nếu không tìm thấy phòng theo tên
                System.out.println("Không tìm thấy phòng với tên: " + tenPhong);
                return false;
            }

            // Nếu trạng thái là "Trống", tiến hành xóa phòng
            String sql = "DELETE FROM phong WHERE ten_phong = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tenPhong);

            int result = stmt.executeUpdate();
            return result > 0; // Trả về true nếu xóa thành công, ngược lại là false

        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa phòng: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatPhong(String tenPhongCu, String tenPhongMoi, String loaiPhong, double gia, String moTa, String tienNghi, String trangThai) {
        try {
            String sql = "UPDATE phong SET ten_phong = ?, loai_phong = ?, gia = ?, mo_ta = ?, tien_nghi = ?, trang_thai = ? WHERE ten_phong = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tenPhongMoi);  // tên mới
            stmt.setString(2, loaiPhong);
            stmt.setDouble(3, gia);
            stmt.setString(4, moTa);
            stmt.setString(5, tienNghi);
            stmt.setString(6, trangThai);
            stmt.setString(7, tenPhongCu);   // tên cũ để tìm phòng trong WHERE

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật thông tin phòng: " + e.getMessage());
            return false;
        }
    }

    public void setPhong(List<Phong> dsPhongChon){
        String sql = "UPDATE phong SET trang_thai = 'Trống' WHERE id = ? && trang_thai='Đang được đặt'";
        try{
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (Phong phong : dsPhongChon) {
                stmt.setInt(1, phong.getId());
                stmt.executeUpdate();
            }
            System.out.println("Đã cập nhật trạng thái phòng về Trống.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi cập nhật trạng thái phòng.");
        }
    }

    // Phương thức đặt phòng
    public boolean datPhong(int phong_id) {
        Phong p = getPhongById(phong_id);
        if (p != null && p.getTrangThai().equalsIgnoreCase("Trống")) {
            try {
                // Bước 2 (tuỳ chọn): Cập nhật trạng thái phòng
                String capNhatTrangThai = "UPDATE phong SET trang_thai = 'Đang được đặt' WHERE id = ?";
                PreparedStatement ps2 = conn.prepareStatement(capNhatTrangThai);
                ps2.setInt(1, phong_id);
                ps2.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public void datNhieuPhong(int nguoiDungId, List<Phong> dsPhongChon, String ngayNhan, String ngayTra) {
        PreparedStatement datPhongStmt = null;
        PreparedStatement chiTietStmt = null;

        java.sql.Date ngayNhanSql = java.sql.Date.valueOf(ngayNhan);

        java.sql.Date ngayTraSql = java.sql.Date.valueOf(ngayTra);
        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm vào bảng dat_phong
            String sqlDatPhong = "INSERT INTO dat_phong (nguoi_dung_id, ngay_dat, ngay_nhan, ngay_tra) VALUES (?, CURRENT_DATE(), ?, ?)";
            datPhongStmt = conn.prepareStatement(sqlDatPhong, Statement.RETURN_GENERATED_KEYS);
            datPhongStmt.setInt(1, nguoiDungId);
            datPhongStmt.setDate(2, ngayNhanSql);
            datPhongStmt.setDate(3, ngayTraSql);
            datPhongStmt.executeUpdate();

            ResultSet rs = datPhongStmt.getGeneratedKeys();
            int datPhongId = 0;
            if (rs.next()) {
                datPhongId = rs.getInt(1);
            } else {
                throw new SQLException("Không thể lấy ID của bản ghi đặt phòng.");
            }
            // 2. Thêm từng phòng vào bảng chi_tiet_dat_phong
            String sqlChiTiet = "INSERT INTO chi_tiet_dat_phong (dat_phong_id, phong_id, gia_phong) VALUES (?, ?, ?)";
            chiTietStmt = conn.prepareStatement(sqlChiTiet);

            for (Phong phong : dsPhongChon) {
                chiTietStmt.setInt(1, datPhongId);
                chiTietStmt.setInt(2, phong.getId());
                chiTietStmt.setDouble(3, phong.getGia());
                chiTietStmt.addBatch();
            }
            chiTietStmt.executeBatch();
            //3. cập nhật trạng thái phòng từ đang được đặt thành đã đặt
            String capNhatTrangThai = "UPDATE phong SET trang_thai = 'Đã đặt' WHERE id = ?";
            PreparedStatement ps2 = conn.prepareStatement(capNhatTrangThai);
            for(Phong phong:dsPhongChon){
                ps2.setInt(1,phong.getId());
                ps2.executeUpdate();
            }
            conn.commit(); // Hoàn tất transaction
            System.out.println("Đặt phòng thành công!");

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // Nếu lỗi thì rollback
            } catch (SQLException se) {
                se.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (datPhongStmt != null) datPhongStmt.close();
                if (chiTietStmt != null) chiTietStmt.close();
                if (conn != null) conn.setAutoCommit(true);
                //if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
