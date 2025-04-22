package controller;

import model.NguoiDung;
import model.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthController {
    private Connection conn;

    public AuthController() {
        this.conn = Database.getConnection();
    }

    public boolean dangKy(String hoTen, String email, String matKhau, String sdt, String diaChi) {
        try {
            // Kiểm tra email đã tồn tại chưa
            if (emailTonTai(email)) {
                System.out.println("Email này đã được sử dụng.");
                return false;
            }

            // Thêm người dùng mới với vai trò mặc định là khách hàng
            String sql = "INSERT INTO nguoi_dung (ho_ten, email, mat_khau, vai_tro, sdt, dia_chi) VALUES (?, ?, ?, 'Khách hàng', ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hoTen);
            stmt.setString(2, email);
            stmt.setString(3, matKhau);
            stmt.setString(4, sdt);
            stmt.setString(5, diaChi);

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi đăng ký: " + e.getMessage());
            return false;
        }
    }

    private boolean emailTonTai(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM nguoi_dung WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    public NguoiDung dangNhap(String email, String matKhau) {
        try {
            String sql = "SELECT * FROM nguoi_dung WHERE email = ? AND mat_khau = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, matKhau);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String hoTen = rs.getString("ho_ten");
                String vaiTro = rs.getString("vai_tro");
                String sdt = rs.getString("sdt");
                String diaChi = rs.getString("dia_chi");

                return new NguoiDung(id, hoTen, email, matKhau, vaiTro, sdt, diaChi);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi đăng nhập: " + e.getMessage());
        }
        return null;
    }

    public boolean capNhatThongTinCaNhan(int id, String hoTen, String sdt, String diaChi) {
        try {
            String sql = "UPDATE nguoi_dung SET ho_ten = ?, sdt = ?, dia_chi = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hoTen);
            stmt.setString(2, sdt);
            stmt.setString(3, diaChi);
            stmt.setInt(4, id);

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật thông tin: " + e.getMessage());
            return false;
        }
    }

    public boolean doiMatKhau(int id, String matKhauCu, String matKhauMoi) {
        try {
            // Kiểm tra mật khẩu cũ
            String checkSql = "SELECT COUNT(*) FROM nguoi_dung WHERE id = ? AND mat_khau = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            checkStmt.setString(2, matKhauCu);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Mật khẩu cũ chính xác, cập nhật mật khẩu mới
                String updateSql = "UPDATE nguoi_dung SET mat_khau = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, matKhauMoi);
                updateStmt.setInt(2, id);

                int result = updateStmt.executeUpdate();
                return result > 0;
            } else {
                System.out.println("Mật khẩu cũ không chính xác.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi đổi mật khẩu: " + e.getMessage());
            return false;
        }
    }

    public NguoiDung timNguoiDungTheoEmail(String email) {
        try {
            String sql = "SELECT * FROM nguoi_dung WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String hoTen = rs.getString("ho_ten");
                String matKhau = rs.getString("mat_khau");
                String vaiTro = rs.getString("vai_tro");
                String sdt = rs.getString("sdt") != null ? rs.getString("sdt") : "";
                String diaChi = rs.getString("dia_chi") != null ? rs.getString("dia_chi") : "";

                return new NguoiDung(id, hoTen, email, matKhau, vaiTro, sdt, diaChi);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tìm người dùng: " + e.getMessage());
        }
        return null;
    }
    public List<NguoiDung> layDanhSachNguoiDung() {
        List<NguoiDung> danhSach = new ArrayList<>();

        try {
            String sql = "SELECT * FROM nguoi_dung ORDER BY id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String hoTen = rs.getString("ho_ten");
                String email = rs.getString("email");
                String matKhau = rs.getString("mat_khau"); // Trong thực tế không nên lấy mật khẩu
                String vaiTro = rs.getString("vai_tro");
                String sdt = rs.getString("sdt") != null ? rs.getString("sdt") : "";
                String diaChi = rs.getString("dia_chi") != null ? rs.getString("dia_chi") : "";

                danhSach.add(new NguoiDung(id, hoTen, email, matKhau, vaiTro, sdt, diaChi));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách người dùng: " + e.getMessage());
        }

        return danhSach;
    }
}