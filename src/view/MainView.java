package view;

import controller.*;
import java.util.*;
import model.*;

public class MainView {
    Scanner scanner = new Scanner(System.in);
    AuthController auth = new AuthController();
    PhongController phongCtrl = new PhongController();
    NguoiDung nguoiDung = null;

    public void menu() {
        while (true) {
            System.out.println("\n1. Đăng ký\n2. Đăng nhập\n0. Thoát");
            System.out.print("Chọn: ");
            int c = scanner.nextInt(); scanner.nextLine();
            if (c == 1) dangKy();
            else if (c == 2) dangNhap();
            else break;
        }
    }

    void dangKy() {
        System.out.print("Họ tên: ");
        String hoTen = scanner.nextLine();
        while (hoTen.trim().isEmpty()) {
            System.out.println("Họ tên không được để trống. Vui lòng nhập lại.");
            System.out.print("Nhập lại họ tên: ");
            hoTen = scanner.nextLine();
        }
        // Kiểm tra định dạng email
        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine();
            if (email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                break;
            } else {
                System.out.println("Email không hợp lệ. Vui lòng nhập lại.");
            }
        }

        // Kiểm tra định dạng mật khẩu
        String matKhau;
        while (true) {
            System.out.print("Mật khẩu: ");
            matKhau = scanner.nextLine();
            if (matKhau.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
                break;
            } else {
                System.out.println("Mật khẩu phải có ít nhất 6 ký tự, gồm chữ hoa, chữ thường và số. Vui lòng nhập lại.");
            }
        }

        // Kiểm tra định dạng số điện thoại
        String sdt;
        while (true) {
            System.out.print("Số điện thoại: ");
            sdt = scanner.nextLine();
            if (sdt.matches("^\\+?\\d{1,12}$")) {
                break;
            } else {
                System.out.println("Số điện thoại chỉ được chứa số và không vượt quá 12 ký tự. Vui lòng nhập lại.");
            }
        }

        System.out.print("Địa chỉ: ");
        String diaChi = scanner.nextLine();

        if (auth.dangKy(hoTen, email, matKhau, sdt, diaChi)) {
            System.out.println("Đăng ký thành công.");
        } else {
            System.out.println("Đăng ký thất bại.");
        }
    }

    void dangNhap() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Mật khẩu: ");
        String matKhau = scanner.nextLine();
        nguoiDung = auth.dangNhap(email, matKhau);
        if (nguoiDung != null) {
            System.out.println("Xin chào, " + nguoiDung.getHoTen());
            if (nguoiDung.getVaiTro().equals("admin")) {
                AdminView adminView = new AdminView(scanner, auth, phongCtrl, nguoiDung);
                adminView.menuAdmin();
            } else {
                UserView userView = new UserView(scanner, auth, phongCtrl, nguoiDung);
                userView.menuKhachHang();
            }
            nguoiDung = null; // Đặt lại người dùng sau khi đăng xuất
        } else {
            System.out.println("Sai tài khoản hoặc mật khẩu.");
        }
    }
}
