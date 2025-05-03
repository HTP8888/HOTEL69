package controller;

import model.Database;
import model.Phong;
import model.DatPhong;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class   DatPhongController {
    private Connection conn;
    public DatPhongController() {
        this.conn = Database.getConnection();
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

    public String layDanhSachPhongThanhToan(int nguoiDungId) {

        try {
            String sql = "SELECT p.*, dp.trang_thai AS trang_thai_dat_phong,ct.dat_phong_id " +
                    "FROM dat_phong dp " +
                    "JOIN chi_tiet_dat_phong ct ON dp.id = ct.dat_phong_id " +
                    "JOIN phong p ON ct.phong_id = p.id " +
                    "WHERE dp.nguoi_dung_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, nguoiDungId);

            ResultSet rs = stmt.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("dat_phong_id");
                String tenPhong = rs.getString("ten_phong");
                String loaiPhong = rs.getString("loai_phong");
                double gia = rs.getDouble("gia");
                String trangThai = rs.getString("trang_thai_dat_phong"); // lấy trạng thái ĐƠN ĐẶT PHÒNG
                String tienNghi = rs.getString("tien_nghi");
                System.out.println("Đặt phòng id: " + id + "    |Tên phòng: " + tenPhong + "    |Loại phòng: " + loaiPhong + "    |Giá: " + gia + "   |Trạng thái: " + trangThai);
            }
            if(!found){
                return "Không có phòng nào được đặt";
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi lấy danh sách phòng đã đặt: " + e.getMessage());

        }
        return "";
    }

    public Date layNgayNhan(int datPhongId) {
        String sql = "SELECT ngay_nhan FROM dat_phong WHERE id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, datPhongId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("ngay_nhan");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy ngày nhận phòng");
        }
        return null; // hoặc có thể trả về giá trị mặc định khác nếu cần
    }

    public boolean kiemTraCach1Tuan(int datPhongId) {
        Date sqlNgayNhan = layNgayNhan(datPhongId);
        if (sqlNgayNhan != null) {
            LocalDate ngayNhan = sqlNgayNhan.toLocalDate();
            LocalDate homNay = LocalDate.now();

            long daysBetween = ChronoUnit.DAYS.between(homNay, ngayNhan);

            return daysBetween > 7;
        }
        return false; // Nếu không có ngày nhận hoặc lỗi
    }

    public void huyDatPhong(int dat_phong_id) {
        PreparedStatement stmtUpdateDatPhong = null;
        PreparedStatement stmtUpdatePhong = null;
        PreparedStatement stmtDeleteChiTiet = null;
        PreparedStatement stmtSelectPhongIds = null;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

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

    public void xacNhanThanhToan(int datPhongId, String phuongThuc) {
        try {
            // 1. Kiểm tra trạng thái đơn đặt phòng
            String trangThaiSql = "SELECT trang_thai FROM dat_phong WHERE id = ?";
            PreparedStatement stmtTrangThai = conn.prepareStatement(trangThaiSql);
            stmtTrangThai.setInt(1, datPhongId);
            ResultSet rs = stmtTrangThai.executeQuery();

            if (rs.next()) {
                String trangThai = rs.getString("trang_thai");

                if ("Đã thanh toán".equalsIgnoreCase(trangThai)) {
                    System.out.println("Đơn đặt phòng này đã được thanh toán. Không thể thanh toán lại.");
                    return;
                }
                if ("Đã hủy".equalsIgnoreCase(trangThai)) {
                    System.out.println("Đơn đặt phòng này đã bị hủy. Không thể thanh toán.");
                    return;
                }

                // 2. Cập nhật trạng thái
                String updateSql = "UPDATE dat_phong SET trang_thai = 'Đã thanh toán' WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, datPhongId);
                updateStmt.executeUpdate();
            } else {
                System.out.println("Không tìm thấy đơn đặt phòng với ID đã cho.");
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi xác nhận thanh toán: " + e.getMessage());
        }
    }

    public void ghiNhanThanhToan(int datPhongId, String phuongThuc, int soTien) {
        try {
            String insertSql = "INSERT INTO thanh_toan (dat_phong_id, phuong_thuc, so_tien, ngay_thanh_toan) " +
                    "VALUES (?, ?, ?, CURRENT_DATE())";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, datPhongId);
            insertStmt.setString(2, phuongThuc);
            insertStmt.setInt(3, soTien);
            insertStmt.executeUpdate();

            System.out.println("Xác nhận thanh toán thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi khi thanh toán: " + e.getMessage());
        }
    }


    public int getTongTien(int datPhongId) {
        String tongTienSql = "SELECT SUM(p.gia) AS tong_tien FROM chi_tiet_dat_phong ct " +
                "JOIN phong p ON ct.phong_id = p.id WHERE ct.dat_phong_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(tongTienSql)) {
            stmt.setInt(1, datPhongId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("tong_tien");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tính tổng tiền: " + e.getMessage());
        }
        return 0;
    }

    public String kiemTraTrangThaiDon(int maDon) {
        String sql = "SELECT trang_thai FROM dat_phong WHERE id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maDon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("trang_thai");
            } else {
                return null; // Không tìm thấy đơn
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra trạng thái đơn: " + e.getMessage());
            return null;
        }
    }

    public List<Phong> layPhongTheoDon(int maDon) {
        List<Phong> danhSachPhong = new ArrayList<>();
        String sql = "SELECT p.id, p.ten_phong, p.loai_phong, p.gia, p.trang_thai, p.tien_nghi, p.mo_ta " +
                "FROM phong p " +
                "JOIN chi_tiet_dat_phong cdp ON p.id = cdp.phong_id " +
                "JOIN dat_phong dp ON dp.id = cdp.dat_phong_id " +
                "WHERE dp.id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDon);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten_phong");
                String loai = rs.getString("loai_phong");
                double gia = rs.getDouble("gia");
                String trangThai = rs.getString("trang_thai");
                String tienNghi = rs.getString("tien_nghi");
                String moTa = rs.getString("mo_ta");

                Phong phong = new Phong(id, ten, loai, gia, trangThai, tienNghi, moTa);
                danhSachPhong.add(phong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachPhong;
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