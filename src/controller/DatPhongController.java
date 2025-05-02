package controller;

import model.Database;
import model.Phong;
import model.DatPhong;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class   DatPhongController {
    private Connection conn;

    public DatPhongController() {
        conn = Database.getConnection();
    }

    // Hàm lấy danh sách phòng đã đặt của 1 người dùng
    public Map<Integer, DatPhong> layDanhSachDonDatPhong(int nguoiDungId) {
        Map<Integer, DatPhong> donDatPhongMap = new HashMap<>();

        try {
            String sql = "SELECT p.*, dp.trang_thai AS trang_thai_dat_phong, ct.dat_phong_id " +
                    "FROM dat_phong dp " +
                    "JOIN chi_tiet_dat_phong ct ON dp.id = ct.dat_phong_id " +
                    "JOIN phong p ON ct.phong_id = p.id " +
                    "WHERE dp.nguoi_dung_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, nguoiDungId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int datPhongId = rs.getInt("dat_phong_id");
                String trangThaiDatPhong = rs.getString("trang_thai_dat_phong");

                int phongId = rs.getInt("id");
                String tenPhong = rs.getString("ten_phong");
                String loaiPhong = rs.getString("loai_phong");
                double gia = rs.getDouble("gia");
                String tienNghi = rs.getString("tien_nghi");

                Phong phong = new Phong(phongId, tenPhong, loaiPhong, gia, trangThaiDatPhong, tienNghi);

                // Nếu đơn chưa có trong map, tạo mới
                if (!donDatPhongMap.containsKey(datPhongId)) {
                    donDatPhongMap.put(datPhongId, new DatPhong(datPhongId, trangThaiDatPhong));
                }

                // Thêm phòng vào đơn
                donDatPhongMap.get(datPhongId).themPhong(phong);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donDatPhongMap;
    }



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
            try { if (conn != null) conn.setAutoCommit(true); conn.setAutoCommit(true); } catch (Exception e) { }
        }
    }

    public void xacNhanThanhToan(int nguoiDungId, int phongId, String phuongThuc) {
        try {
            // 1. Tìm dat_phong_id và trạng thái đơn đặt phòng liên quan đến phòng
            String timDatPhongSql = "SELECT dp.id, dp.trang_thai FROM dat_phong dp " +
                    "JOIN chi_tiet_dat_phong ct ON dp.id = ct.dat_phong_id " +
                    "WHERE dp.nguoi_dung_id = ? AND ct.phong_id = ?";
            PreparedStatement timStmt = conn.prepareStatement(timDatPhongSql);
            timStmt.setInt(1, nguoiDungId);
            timStmt.setInt(2, phongId);
            ResultSet rs = timStmt.executeQuery();

            if (rs.next()) {
                int datPhongId = rs.getInt("id");
                String trangThaiDatPhong = rs.getString("trang_thai");

                // 2. Kiểm tra trạng thái đơn đặt phòng
                if ("Đã thanh toán".equalsIgnoreCase(trangThaiDatPhong)) {
                    System.out.println("Đơn đặt phòng này đã được thanh toán trước đó. Không thể thanh toán lại.");
                    return;
                }
                if ("Đã hủy".equalsIgnoreCase(trangThaiDatPhong)) {
                    System.out.println("Đơn đặt phòng này đã bị hủy. Không thể thanh toán.");
                    return;
                }

                // 3. Nếu chưa thanh toán, tiến hành cập nhật
                String capNhatTrangThaiSql = "UPDATE dat_phong SET trang_thai = 'Đã thanh toán' WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(capNhatTrangThaiSql);
                updateStmt.setInt(1, datPhongId);
                updateStmt.executeUpdate();

                // 4. Ghi nhận vào bảng thanh_toan
                String insertThanhToanSql = "INSERT INTO thanh_toan (dat_phong_id, phuong_thuc, so_tien, ngay_thanh_toan) " +
                        "VALUES (?, ?, (SELECT gia FROM phong WHERE id = ?), CURRENT_DATE())";
                PreparedStatement insertStmt = conn.prepareStatement(insertThanhToanSql);
                insertStmt.setInt(1, datPhongId);
                insertStmt.setString(2, phuongThuc);
                insertStmt.setInt(3, phongId);
                insertStmt.executeUpdate();

                System.out.println("Xác nhận thanh toán thành công!");
            } else {
                System.out.println("Không tìm thấy đặt phòng tương ứng.");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi xác nhận thanh toán: " + e.getMessage());
        }
    }

    public void tinhTongDoanhThuTheoNam(int nam) {
        try {
            String sql = "SELECT SUM(so_tien) AS tong_doanh_thu FROM thanh_toan " +
                    "WHERE YEAR(ngay_thanh_toan) = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, nam);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double tongDoanhThu = rs.getDouble("tong_doanh_thu");
                System.out.println(">> Tổng doanh thu năm " + nam + " là: " + tongDoanhThu + " VNĐ");
            } else {
                System.out.println("Không có dữ liệu doanh thu cho năm " + nam);
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi thống kê doanh thu: " + e.getMessage());
        }
    }

}
