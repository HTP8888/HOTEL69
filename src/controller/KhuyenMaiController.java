package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.KhuyenMai;
import model.Database;


public class KhuyenMaiController {
    private Connection conn;

    public KhuyenMaiController() {
        this.conn = Database.getConnection();
    }

    // Thêm mã khuyến mãi
    public boolean themKhuyenMai(String ma, String tenKhuyenMai, String moTa, int phanTramGiam, String ngayBatDau, String ngayKetThuc) {
        try {
            String sql = "INSERT INTO khuyen_mai(ma, ten_khuyen_mai, mo_ta, phan_tram_giam, ngay_bat_dau, ngay_ket_thuc) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, ma);
            stmt.setString(2, tenKhuyenMai);
            stmt.setString(3, moTa);
            stmt.setInt(4, phanTramGiam);
            stmt.setString(5, ngayBatDau);
            stmt.setString(6, ngayKetThuc);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm mã khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật mã khuyến mãi
    public boolean capNhatKhuyenMai(String ma, String tenKhuyenMai, String moTa, int phanTramGiam, String ngayBatDau, String ngayKetThuc) {
        try {
            String sql = "UPDATE khuyen_mai SET ten_khuyen_mai = ?, mo_ta = ?, phan_tram_giam = ?, ngay_bat_dau = ?, ngay_ket_thuc = ? " +
                    "WHERE ma = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tenKhuyenMai);
            stmt.setString(2, moTa);
            stmt.setInt(3, phanTramGiam);
            stmt.setString(4, ngayBatDau);
            stmt.setString(5, ngayKetThuc);
            stmt.setString(6, ma);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật mã khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    // Xóa mã khuyến mãi
    public boolean xoaKhuyenMai(String ma) {
        try {
            String sql = "DELETE FROM khuyen_mai WHERE ma = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, ma);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa mã khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    // Liệt kê tất cả mã khuyến mãi
    public List<KhuyenMai> layDanhSachKhuyenMai() {
        List<KhuyenMai> khuyenMais = new ArrayList<>();
        try {
            String sql = "SELECT * FROM khuyen_mai";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                KhuyenMai khuyenMai = new KhuyenMai(
                        rs.getString("ma"),
                        rs.getString("ten_khuyen_mai"),
                        rs.getString("mo_ta"),
                        rs.getInt("phan_tram_giam"),
                        rs.getString("ngay_bat_dau"),
                        rs.getString("ngay_ket_thuc")
                );
                khuyenMais.add(khuyenMai);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách mã khuyến mãi: " + e.getMessage());
        }
        return khuyenMais;
    }

    // Tìm mã khuyến mãi theo mã
    public KhuyenMai timKhuyenMaiTheoMa(String ma) {
        try {
            String sql = "SELECT * FROM khuyen_mai WHERE ma = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, ma);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new KhuyenMai(
                        rs.getString("ma"),
                        rs.getString("ten_khuyen_mai"),
                        rs.getString("mo_ta"),
                        rs.getInt("phan_tram_giam"),
                        rs.getString("ngay_bat_dau"),
                        rs.getString("ngay_ket_thuc")
                );
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tìm mã khuyến mãi: " + e.getMessage());
        }
        return null;
    }

    public boolean kiemTraMaKhuyenMai(String maKhuyenMai, int nguoiDungId) {
        try {
            // Kiểm tra mã khuyến mãi có tồn tại và còn hiệu lực không
            String sql = "SELECT * FROM khuyen_mai WHERE ma = ? AND " +
                    "(ngay_bat_dau IS NULL OR ngay_bat_dau <= CURRENT_DATE()) AND " +
                    "(ngay_ket_thuc IS NULL OR ngay_ket_thuc >= CURRENT_DATE())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maKhuyenMai);
            ResultSet rs = stmt.executeQuery();

            // Nếu không tìm thấy mã hoặc mã không còn hiệu lực
            if (!rs.next()) {
                System.out.println("Mã khuyến mãi không tồn tại hoặc đã hết hiệu lực.");
                return false;
            }

            // Kiểm tra xem người dùng đã sử dụng mã này chưa
            String checkUsageSql = "SELECT COUNT(*) FROM khuyen_mai_su_dung WHERE ma_khuyen_mai = ? AND nguoi_dung_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUsageSql);
            checkStmt.setString(1, maKhuyenMai);
            checkStmt.setInt(2, nguoiDungId);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {
                // Nếu người dùng đã sử dụng mã này rồi
                System.out.println("Bạn đã sử dụng mã khuyến mãi này rồi.");
                return false;
            }

            // Nếu mã hợp lệ và chưa được sử dụng
            return true;

        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra mã khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    public int layPhanTramGiam(String maKhuyenMai, int nguoiDungId) {
        try {
            // Gọi hàm kiểm tra mã khuyến mãi
            if (!kiemTraMaKhuyenMai(maKhuyenMai, nguoiDungId)) {
                return 0;
            }

            // Nếu hợp lệ, lấy phần trăm giảm
            String sql = "SELECT phan_tram_giam FROM khuyen_mai WHERE ma = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maKhuyenMai);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("phan_tram_giam");
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi lấy phần trăm giảm: " + e.getMessage());
        }
        return 0;
    }

    public void suDungKM(String maKhuyenMai, int nguoiDungId) {
        try {
            String sql = "INSERT INTO khuyen_mai_su_dung (ma_khuyen_mai, nguoi_dung_id, ngay_su_dung) VALUES (?, ?, CURRENT_DATE)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maKhuyenMai);
            stmt.setInt(2, nguoiDungId);
            stmt.executeUpdate();
            System.out.println("Đã ghi nhận mã khuyến mãi đã sử dụng.");
        } catch (Exception e) {
            System.out.println("Lỗi khi cập nhật khuyến mãi đã sử dụng: " + e.getMessage());
        }
    }


}
