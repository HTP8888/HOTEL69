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
    
     
    // Hủy phòng nếu đang ở trạng thái "Đã đặt"
    public void huyDatPhong(int dat_phong_id) {
        PreparedStatement stmtUpdateDatPhong = null;
        PreparedStatement stmtUpdatePhong = null;
        PreparedStatement stmtDeleteChiTiet = null;
        PreparedStatement stmtSelectPhongIds = null;
        ResultSet rs = null;
    
        try {
            //conn.setAutoCommit(false); // Bắt đầu transaction
    
            // 1. Lấy danh sách phòng liên quan đến dat_phong_id
            String selectPhongIdsSql = "SELECT phong_id FROM chi_tiet_dat_phong WHERE dat_phong_id = ?";
            stmtSelectPhongIds = conn.prepareStatement(selectPhongIdsSql);
            stmtSelectPhongIds.setInt(1, dat_phong_id);
            rs = stmtSelectPhongIds.executeQuery();
    
            List<Integer> phongIds = new ArrayList<>(); //Danh sách ID phòng có mã cần hủy
            while (rs.next()) {
                phongIds.add(rs.getInt("phong_id"));
            }
    
            // 2. Cập nhật trạng thái các phòng thành 'Trống'
            if (!phongIds.isEmpty()) {
                StringBuilder updatePhongSql = new StringBuilder("UPDATE phong SET trang_thai = 'Trống' WHERE id IN (");
                for (int i = 0; i < phongIds.size(); i++) {
                    updatePhongSql.append("?");
                    if (i < phongIds.size() - 1) {
                        updatePhongSql.append(", ");
                    }
                }
                updatePhongSql.append(")");
                
                stmtUpdatePhong = conn.prepareStatement(updatePhongSql.toString());
                for (int i = 0; i < phongIds.size(); i++) {
                    stmtUpdatePhong.setInt(i + 1, phongIds.get(i));
                }
                stmtUpdatePhong.executeUpdate();
            }
    
            // 3. Xóa chi tiết đặt phòng
            String deleteChiTietSql = "DELETE FROM chi_tiet_dat_phong WHERE dat_phong_id = ?";
            stmtDeleteChiTiet = conn.prepareStatement(deleteChiTietSql);
            stmtDeleteChiTiet.setInt(1, dat_phong_id);
            stmtDeleteChiTiet.executeUpdate();
    
            // 4. Cập nhật trạng thái đơn đặt phòng thành 'Đã hủy'
            String updateDatPhongSql = "UPDATE dat_phong SET trang_thai = 'Đã hủy' WHERE id = ?";
            stmtUpdateDatPhong = conn.prepareStatement(updateDatPhongSql);
            stmtUpdateDatPhong.setInt(1, dat_phong_id);
            stmtUpdateDatPhong.executeUpdate();
    
            conn.commit(); // Nếu mọi thứ OK thì commit transaction
            System.out.println("Đã hủy đặt phòng thành công!");
    
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Nếu lỗi thì rollback
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) { }
            try { if (stmtSelectPhongIds != null) stmtSelectPhongIds.close(); } catch (Exception e) { }
            try { if (stmtUpdatePhong != null) stmtUpdatePhong.close(); } catch (Exception e) { }
            try { if (stmtDeleteChiTiet != null) stmtDeleteChiTiet.close(); } catch (Exception e) { }
            try { if (stmtUpdateDatPhong != null) stmtUpdateDatPhong.close(); } catch (Exception e) { }
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (Exception e) { }
        }
    }
    
}
