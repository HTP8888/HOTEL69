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
                String tien_nghi = rs.getString("tien_nghi"); // Thêm tiện nghi nếu cần
                Phong phong = new Phong(id, ten, loai, gia, trang_thai, tien_nghi);
                danhSachPhong.add(phong);
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
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
     


    
}
