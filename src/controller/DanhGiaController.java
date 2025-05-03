package controller;

import model.DanhGia;
import model.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DanhGiaController {
    private Connection conn;

    public DanhGiaController() {
        this.conn = Database.getConnection();
    }

    // Thêm đánh giá mới
    public boolean themDanhGia(int nguoiDungId, int phongId, int datPhongId, int diemDanhGia, String noiDung) {
        try {
            // Câu lệnh SQL để thêm đánh giá mới
            String sql = "INSERT INTO danh_gia (nguoi_dung_id, phong_id, dat_phong_id, diem_danh_gia, noi_dung, ngay_danh_gia) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_DATE)";

            // Chuẩn bị câu lệnh SQL
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Gán giá trị cho các tham số
            stmt.setInt(1, nguoiDungId);
            stmt.setInt(2, phongId);
            stmt.setInt(3, datPhongId);
            stmt.setInt(4, diemDanhGia);
            stmt.setString(5, noiDung);

            // Thực thi câu lệnh và kiểm tra kết quả
            int result = stmt.executeUpdate();
            return result > 0; // Trả về true nếu thêm thành công

        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm đánh giá: " + e.getMessage());
            return false;
        }
    }


    // Lấy tất cả đánh giá với tên người đánh giá
    public List<DanhGia> layTatCaDanhGia() {
        List<DanhGia> danhSach = new ArrayList<>();
        String sql = "SELECT dg.id, dg.nguoi_dung_id, dg.phong_id, dg.diem_danh_gia, dg.noi_dung, dg.ngay_danh_gia, nd.ho_ten " +
                "FROM danh_gia dg " +
                "JOIN nguoi_dung nd ON dg.nguoi_dung_id = nd.id";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DanhGia dg = new DanhGia(
                        rs.getInt("id"),
                        rs.getInt("nguoi_dung_id"),
                        rs.getInt("phong_id"),
                        rs.getInt("diem_danh_gia"),
                        rs.getString("noi_dung"),
                        rs.getString("ngay_danh_gia"),
                        rs.getString("ho_ten") // Đảm bảo cột này có trong SELECT
                );
                danhSach.add(dg);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách đánh giá: " + e.getMessage());
        }
        return danhSach;
    }


    // Xóa đánh giá theo ID
    public boolean xoaDanhGia(int id) {
        String sql = "DELETE FROM danh_gia WHERE id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa đánh giá: " + e.getMessage());
            return false;
        }
    }

    // Tìm đánh giá theo phòng với tên người đánh giá
    public List<DanhGia> layDanhGiaTheoPhong(int phongId) {
        List<DanhGia> danhSach = new ArrayList<>();
        String sql = "SELECT dg.id, dg.nguoi_dung_id, dg.phong_id, dg.diem_danh_gia, dg.noi_dung, dg.ngay_danh_gia, nd.ho_ten " +
                "FROM danh_gia dg " +
                "JOIN nguoi_dung nd ON dg.nguoi_dung_id = nd.id " +
                "WHERE dg.phong_id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, phongId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DanhGia dg = new DanhGia(
                        rs.getInt("id"),
                        rs.getInt("nguoi_dung_id"),
                        rs.getInt("phong_id"),
                        rs.getInt("diem_danh_gia"),
                        rs.getString("noi_dung"),
                        rs.getString("ngay_danh_gia"),
                        rs.getString("ho_ten")  // Lấy tên người đánh giá từ bảng nguoi_dung
                );
                danhSach.add(dg);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tìm đánh giá theo phòng: " + e.getMessage());
        }
        return danhSach;
    }
}
