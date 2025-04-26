package view;

import controller.*;

//import java.sql.Date;
//import java.sql.Connection;
import java.util.*;
import model.*;

public class MainView {
    Scanner scanner = new Scanner(System.in);
    AuthController auth = new AuthController();
    PhongController phongCtrl = new PhongController();
    NguoiDung nguoiDung = null;
    List<Phong> dsPhongChon;

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
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Mật khẩu: ");
        String matKhau = scanner.nextLine();
        if (auth.dangKy(hoTen, email, matKhau)) {
            System.out.println("Đăng ký thành công.");
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
                menuAdmin();
            } else {
                dsPhongChon = new ArrayList<>();
                menuKhachHang();
            }
        } else {
            System.out.println("Sai tài khoản hoặc mật khẩu.");
        }
    }

    void menuKhachHang() {
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
                nguoiDung = null;
                break;
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    void menuAdmin() {
        while (true) {
            System.out.println("\n--- MENU ADMIN ---");
            System.out.println("1. Tìm và xem chi tiết phòng");
            System.out.println("2. Quản lý người dùng");
            System.out.println("3. Cập nhật thông tin cá nhân");
            System.out.println("4. Đổi mật khẩu");
            System.out.println("0. Đăng xuất");
            System.out.print("Chọn: ");
            int chon = scanner.nextInt(); scanner.nextLine();
            if (chon == 1) {
                timVaXemChiTietPhong();
            } else if (chon == 2) {
                quanLyNguoiDung();
            } else if (chon == 3) {
                capNhatThongTinCaNhan();
            } else if (chon == 4) {
                doiMatKhau();
            } else if (chon == 0) {
                System.out.println("Đã đăng xuất.");
                nguoiDung = null;
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

        // Nếu là khách hàng và phòng còn trống, hiển thị lựa chọn đặt phòng
        if (!nguoiDung.getVaiTro().equals("admin") && p.getTrangThai().equalsIgnoreCase("Trống")) {
            System.out.println("\n1. Đặt phòng này");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            int chon = scanner.nextInt(); scanner.nextLine();
            if (chon == 1) {
                boolean success = phongCtrl.datPhong(p.getId());
                if (success) {
                    dsPhongChon.add(p);
                    System.out.println("Chọn phòng thành công!");
                }
                System.out.println("Bạn có tiếp tục đặt thêm phòng không? (y/n)");
                String chonTiepTuc = scanner.nextLine();
                if (chonTiepTuc.equalsIgnoreCase("y")) {
                    return;
                } else {
                    System.out.println("Đặt tất cả các phòng đã chọn? (y/n)");
                    String tieptuc = scanner.nextLine();
                    if(tieptuc.equalsIgnoreCase("y")) {
                        System.out.println("Nhập ngày nhận");
                        String ngayNhan = scanner.nextLine();
                        System.out.println("Nhập ngày trả");
                        String ngayTra = scanner.nextLine();
                        phongCtrl.datNhieuPhong(nguoiDung.getId(), dsPhongChon,ngayNhan, ngayTra);
                        System.out.println("Đặt tất cả các phòng đã chọn thành công!");
                    } else {
                        return;
                    }
                    //
                }
                System.out.println("\nNhấn 'enter' để quay lại.");
                scanner.nextLine();
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

        // Không cho phép thay đổi vai trò ở đây
        // Gọi phương thức cập nhật thông tin từ AuthController
        boolean success = auth.capNhatThongTinCaNhan(nguoiDung.getId(), hoTen, sdt, diaChi);
        if (success) {
            System.out.println("Cập nhật thông tin thành công!");
            // Cập nhật lại thông tin người dùng hiện tại
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

        System.out.print("Nhập mật khẩu mới: ");
        String matKhauMoi = scanner.nextLine();

        System.out.print("Xác nhận mật khẩu mới: ");
        String xacNhan = scanner.nextLine();

        if (!matKhauMoi.equals(xacNhan)) {
            System.out.println("Mật khẩu mới và xác nhận không khớp!");
        } else if (matKhauMoi.length() < 6) {
            System.out.println("Mật khẩu mới phải có ít nhất 6 ký tự!");
        } else {
            boolean success = auth.doiMatKhau(nguoiDung.getId(), matKhauCu, matKhauMoi);
            if (success) {
                System.out.println("Đổi mật khẩu thành công!");
                nguoiDung.setMatKhau(matKhauMoi);
            } else {
                System.out.println("Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu hiện tại.");
            }
        }

        System.out.println("\nNhấn 'enter' để quay lại menu.");
        scanner.nextLine();
    }

    void quanLyNguoiDung() {
        if (!nguoiDung.getVaiTro().equals("admin")) {
            System.out.println("Bạn không có quyền truy cập chức năng này.");
            return;
        }

        while (true) {
            System.out.println("\n--- QUẢN LÝ NGƯỜI DÙNG ---");
            System.out.println("1. Danh sách người dùng");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            int chon = scanner.nextInt(); scanner.nextLine();
            if (chon == 1) {
                xemDanhSachNguoiDung();
            } else if (chon == 0) {
                break;
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    void xemDanhSachNguoiDung() {
        System.out.println("\n--- DANH SÁCH NGƯỜI DÙNG ---");
        List<NguoiDung> danhSach = auth.layDanhSachNguoiDung();

        if (danhSach.isEmpty()) {
            System.out.println("Không có người dùng nào.");
        } else {
            System.out.printf("%-5s | %-25s | %-25s | %-15s\n", "ID", "Họ tên", "Email", "Vai trò");
            System.out.println("----------------------------------------------------------------------");
            for (NguoiDung nd : danhSach) {
                System.out.printf("%-5d | %-25s | %-25s | %-15s\n",
                        nd.getId(), nd.getHoTen(), nd.getEmail(), nd.getVaiTro());
            }
        }

        System.out.println("\nNhấn 'enter' để quay lại menu.");
        scanner.nextLine();
    }
}