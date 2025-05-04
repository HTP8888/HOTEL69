package view;

import controller.*;
import java.util.*;
import model.*;

public class UserView {
    private Scanner scanner;
    private AuthController auth;
    private PhongController phongCtrl;
    private NguoiDung nguoiDung;

    public UserView(Scanner scanner, AuthController auth, PhongController phongCtrl, NguoiDung nguoiDung) {
        this.scanner = scanner;
        this.auth = auth;
        this.phongCtrl = phongCtrl;
        this.nguoiDung = nguoiDung;
    }

    public void menuKhachHang() {
        while (true) {
            System.out.println("\n--- MENU KHÁCH HÀNG ---");
            System.out.println("1. Tìm và xem chi tiết phòng");
            System.out.println("2. Cập nhật thông tin cá nhân");
            System.out.println("3. Đổi mật khẩu");
            System.out.println("0. Đăng xuất");
            System.out.print("Chọn: ");
            int chon = scanner.nextInt(); scanner.nextLine();
            if (chon == 1) {
                timVaXemChiTietPhong();
            } else if (chon == 2) {
                capNhatThongTinCaNhan();
            } else if (chon == 3) {
                doiMatKhau();
            } else if (chon == 0) {
                System.out.println("Đã đăng xuất.");
                break;
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    void timVaXemChiTietPhong() {
        while (true) {
            System.out.print("Nhập loại phòng cần tìm (Đơn/Đôi/VIP) hoặc nhập 'back' để quay lại: ");
            String loai = scanner.nextLine();

            if (loai.equalsIgnoreCase("back")) {
                break;  // Quay lại menu trước đó
            }

            List<Phong> ds = phongCtrl.timKiemPhongTheoLoai(loai);
            if (ds.isEmpty()) {
                System.out.println("Không có phòng trống thuộc loại: " + loai);
            } else {
                System.out.println("\n--- DANH SÁCH PHÒNG LOẠI " + loai.toUpperCase() + " ---");
                System.out.printf("%-5s | %-15s | %-10s | %-15s\n", "STT", "Tên phòng", "Giá", "Trạng thái");
                System.out.println("--------------------------------------------------");
                for (int i = 0; i < ds.size(); i++) {
                    Phong p = ds.get(i);
                    System.out.printf("%-5d | %-15s | %-10.0f | %-15s\n",
                            i + 1, p.getTenPhong(), p.getGia(), p.getTrangThai());
                }

                // Cho phép người dùng chọn phòng để xem chi tiết
                System.out.print("\nNhập số thứ tự phòng để xem chi tiết (hoặc 0 để quay lại): ");
                try {
                    int chon = Integer.parseInt(scanner.nextLine());
                    if (chon > 0 && chon <= ds.size()) {
                        // Hiển thị chi tiết phòng đã chọn
                        Phong p = ds.get(chon - 1);
                        hienThiChiTietPhong(p);
                    } else if (chon != 0) {
                        System.out.println("Lựa chọn không hợp lệ.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Vui lòng nhập số.");
                }
            }
        }
    }

    void hienThiChiTietPhong(Phong p) {
        System.out.println("\n--- CHI TIẾT PHÒNG ---");
        System.out.println("ID: " + p.getId());
        System.out.println("Tên phòng: " + p.getTenPhong());
        System.out.println("Loại phòng: " + p.getLoaiPhong());
        System.out.println("Giá: " + p.getGia());
        System.out.println("Mô tả: " + p.getMoTa());
        System.out.println("Trạng thái: " + p.getTrangThai());

        if (p.getTienNghi() != null && !p.getTienNghi().isEmpty()) {
            System.out.println("Tiện nghi:");
            String[] dsTienNghi = p.getTienNghi().split(",");
            for (String tn : dsTienNghi) {
                System.out.println("- " + tn.trim());
            }
        } else {
            System.out.println("Không có tiện nghi.");
        }

        // Hiển thị lựa chọn đặt phòng nếu phòng còn trống
        if (p.getTrangThai().equalsIgnoreCase("Trống")) {
            System.out.println("\n1. Đặt phòng này");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            int chon = scanner.nextInt(); scanner.nextLine();
            if (chon == 1) {
                // Code đặt phòng sẽ được thêm sau
                System.out.println("Chức năng đặt phòng sẽ được thêm sau.");
            }
        } else {
            System.out.println("\nNhấn 'enter' để quay lại.");
            scanner.nextLine();
        }
    }

    void capNhatThongTinCaNhan() {
        System.out.println("\n--- CẬP NHẬT THÔNG TIN CÁ NHÂN ---");
        System.out.println("Thông tin hiện tại:");
        System.out.println("Họ tên: " + nguoiDung.getHoTen());
        System.out.println("Email: " + nguoiDung.getEmail());
        System.out.println("Số điện thoại: " + (nguoiDung.getSdt() != null && !nguoiDung.getSdt().isEmpty() ? nguoiDung.getSdt() : "Chưa cập nhật"));
        System.out.println("Địa chỉ: " + (nguoiDung.getDiaChi() != null && !nguoiDung.getDiaChi().isEmpty() ? nguoiDung.getDiaChi() : "Chưa cập nhật"));

        System.out.print("\nHọ tên mới (Enter để giữ nguyên): ");
        String hoTen = scanner.nextLine();
        if (hoTen.isEmpty()) {
            hoTen = nguoiDung.getHoTen();
        }

        System.out.print("Số điện thoại mới (Enter để giữ nguyên): ");
        String sdt = scanner.nextLine();
        if (sdt.isEmpty()) {
            sdt = nguoiDung.getSdt();
        }

        System.out.print("Địa chỉ mới (Enter để giữ nguyên): ");
        String diaChi = scanner.nextLine();
        if (diaChi.isEmpty()) {
            diaChi = nguoiDung.getDiaChi();
        }

        boolean success = auth.capNhatThongTinCaNhan(nguoiDung.getId(), hoTen, sdt, diaChi);
        if (success) {
            System.out.println("Cập nhật thông tin thành công!");
            nguoiDung.setHoTen(hoTen);
            nguoiDung.setSdt(sdt);
            nguoiDung.setDiaChi(diaChi);
        } else {
            System.out.println("Cập nhật thông tin thất bại.");
        }

        System.out.println("\nNhấn 'enter' để quay lại menu.");
        scanner.nextLine();
    }

    void doiMatKhau() {
        System.out.println("\n--- ĐỔI MẬT KHẨU ---");
        System.out.print("Nhập mật khẩu hiện tại: ");
        String matKhauCu = scanner.nextLine();

        String matKhauMoi, xacNhan;
        while (true) {
            System.out.print("Nhập mật khẩu mới: ");
            matKhauMoi = scanner.nextLine();
            System.out.print("Xác nhận mật khẩu mới: ");
            xacNhan = scanner.nextLine();

            if (!matKhauMoi.equals(xacNhan)) {
                System.out.println("Mật khẩu mới và xác nhận không khớp. Vui lòng nhập lại.\n");
            } else if (!matKhauMoi.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
                System.out.println("Mật khẩu phải có ít nhất 6 ký tự, gồm chữ hoa, chữ thường và số. Vui lòng nhập lại.\n");
            } else {
                break;
            }
        }

        boolean success = auth.doiMatKhau(nguoiDung.getId(), matKhauCu, matKhauMoi);
        if (success) {
            System.out.println("Đổi mật khẩu thành công!");
            nguoiDung.setMatKhau(matKhauMoi);
        } else {
            System.out.println("Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu hiện tại.");
        }

        System.out.println("\nNhấn 'enter' để quay lại menu.");
        scanner.nextLine();
    }

}
