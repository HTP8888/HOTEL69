package view;

import controller.*;
import java.util.*;
import model.*;

public class UserView {
    private Scanner scanner;
    private AuthController auth;
    private PhongController phongCtrl;
    private NguoiDung nguoiDung;
    private List<Phong> dsPhongChon;
    private DatPhongController bookingController;
    public UserView(Scanner scanner, AuthController auth, PhongController phongCtrl, NguoiDung nguoiDung, DatPhongController bookingController) {
        this.scanner = scanner;
        this.auth = auth;
        this.phongCtrl = phongCtrl;
        this.nguoiDung = nguoiDung;
        this.bookingController= bookingController;
        this.dsPhongChon = new ArrayList<>();
    }

    void menuKhachHang() {
        while (true) {
            System.out.println("\n--- MENU KHÁCH HÀNG ---");
            System.out.println("1. Tìm và xem chi tiết phòng");
            System.out.println("2. Cập nhật thông tin cá nhân");
            System.out.println("3. Đổi mật khẩu");
            System.out.println("4. Đặt phòng");
            System.out.println("5. Xem phòng đã đặt");
            System.out.println("6. Hủy phòng đã thanh toán");
            System.out.println("0. Đăng xuất");
            System.out.print("Chọn: ");
            int chon = scanner.nextInt(); scanner.nextLine();
            if (chon == 1) {
                timVaXemChiTietPhong();
            } else if (chon == 2) {
                capNhatThongTinCaNhan();
            } else if (chon == 3) {
                doiMatKhau();
            }else if(chon ==4){
                datPhong();
            }else if(chon ==5){
                xemPhongDaDat(nguoiDung);
            }else if(chon ==6){
                huyPhong();
            }
            else if (chon == 0) {
                phongCtrl.setPhong(dsPhongChon);
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
            System.out.println("\n1. Chọn phòng này");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            int chon = scanner.nextInt(); scanner.nextLine();
            if (chon == 1) {
                boolean success = phongCtrl.datPhong(p.getId());
                if (success) {
                    dsPhongChon.add(p);
                    System.out.println("Chọn phòng thành công!");
                }
                System.out.println("\nNhấn 'enter' để quay lại.");
                scanner.nextLine();
            }
        } else {
            System.out.println("\nNhấn 'enter' để quay lại.");
            scanner.nextLine();
        }
    }

    void datPhong(){
        if(dsPhongChon.isEmpty()){
            System.out.println("Bạn chưa chọn phòng nào (Nhấn enter để quay lại)");
            scanner.nextLine();
        }
        else {
            System.out.println("Danh sách các phòng bạn đã chọn:");
            for(Phong phong:dsPhongChon){
                System.out.println(String.format("id: %d |Tên Phòng: %s  |Loại phòng: %s  |Giá:  %f  |Tiện nghi:  %s  |Trạng thái:  Đang được chọn",phong.getId(),phong.getTenPhong(),phong.getLoaiPhong(),phong.getGia(),phong.getTienNghi()));
            }
            System.out.println("Đặt tất cả các phòng đã chọn? (y/n)");
            String tieptuc = scanner.nextLine();
            if (tieptuc.equalsIgnoreCase("y")) {
                try {
                    System.out.print("Nhập ngày nhận (YY-MM-DD): ");
                    String ngayNhan = scanner.nextLine();
                    System.out.print("Nhập ngày trả (YY-MM-DD): ");
                    String ngayTra = scanner.nextLine();
                    phongCtrl.datNhieuPhong(nguoiDung.getId(), dsPhongChon, ngayNhan, ngayTra);
                    System.out.println("Đặt tất cả các phòng đã chọn thành công!");
                    System.out.print("Nhấn 'enter' để quay lại.");
                    scanner.nextLine();
                }catch (Exception e){
                    System.out.println("Lỗi đặt phòng");
                }
            } else {
                return;
            }
        }
    }

    void huyPhong() {
        System.out.println("Danh sách phòng đã thanh toán: ");
        String tmp= bookingController.layDanhSachPhongThanhToan(nguoiDung.getId());
        if(tmp.equals("Không có phòng nào được đặt")){
            System.out.println("Bạn chưa đặt phòng nào (Nhấn enter để quay lại): ");
            scanner.nextLine();
        }else {
            System.out.println(tmp);
            System.out.print("Bạn muốn hủy phòng nào (Chọn mã đặt phòng) hoặc nhấn 0 để quay lại: ");
            int chon = scanner.nextInt();
            scanner.nextLine();
            if(chon != 0) {
                bookingController.huyDatPhong(chon);
                dsPhongChon.clear();
            }
            else{
                return;
            }
        }
    }

    private void xemPhongDaDat(NguoiDung nguoiDung) {
        List<Phong> danhSach = bookingController.layDanhSachPhongDaDat(nguoiDung.getId());
        if (danhSach.isEmpty()) {
            System.out.println("Bạn chưa đặt phòng nào (Nhấn enter để quay lại).");
            scanner.nextLine();
        } else {
            System.out.println("Danh sách phòng bạn đã đặt:");
            for (Phong p : danhSach) {
                System.out.println("ID: " + p.getId() + " - Tên phòng: " + p.getTenPhong() +
                        " - Loại: " + p.getLoaiPhong() + " - Giá: " + p.getGia() +
                        " - Trạng thái: " + p.getTrangThai());
            }

            Scanner scanner = new Scanner(System.in);
            System.out.print("Bạn có muốn thanh toán phòng này không? (y/n): ");
            String chon = scanner.nextLine();

            if (chon.equalsIgnoreCase("y")) {
                System.out.print("Nhập ID phòng muốn thanh toán: ");
                int idPhong = scanner.nextInt();
                scanner.nextLine(); // clear buffer

                System.out.println("Chọn phương thức thanh toán:");
                System.out.println("1. Thanh toán qua thẻ");
                System.out.println("2. Chuyển khoản MB Bank");
                int luaChon = scanner.nextInt();
                scanner.nextLine(); // clear buffer

                if (luaChon == 1) {
                    System.out.print("Nhập số thẻ: ");
                    String soThe = scanner.nextLine();
                    System.out.print("Nhập tên ngân hàng: ");
                    String tenNganHang = scanner.nextLine();

                    System.out.println("Xác nhận thanh toán...");
                    System.out.println("Thanh toán bằng thẻ " + soThe + " (" + tenNganHang + ") thành công!");
                    bookingController.xacNhanThanhToan(nguoiDung.getId(), idPhong, "the");

                } else if (luaChon == 2) {
                    System.out.println("Vui lòng chuyển khoản tới tài khoản MB Bank 123789.");
                    System.out.println("Xác nhận đã chuyển khoản? (y/n): ");
                    String xacNhan = scanner.nextLine();

                    if (xacNhan.equalsIgnoreCase("y")) {
                        System.out.println("Thanh toán chuyển khoản thành công!");
                        bookingController.xacNhanThanhToan(nguoiDung.getId(), idPhong, "chuyen_khoan");
                    } else {
                        System.out.println("Hủy thanh toán.");
                    }
                } else {
                    System.out.println("Lựa chọn không hợp lệ.");
                }
            }
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
}
