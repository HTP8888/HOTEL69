package view;

import controller.*;
import java.util.*;
import model.*;

public class AdminView {
    private Scanner scanner;
    private AuthController auth;
    private PhongController phongCtrl;
    private DatPhongController bookingController;
    private NguoiDung nguoiDung;
    private KhuyenMaiController KMC;
    private DanhGiaController DGC;

    public AdminView(Scanner scanner, AuthController auth, PhongController phongCtrl, NguoiDung nguoiDung,DatPhongController bookingController, KhuyenMaiController KMC, DanhGiaController DGC) {
        this.scanner = scanner;
        this.auth = auth;
        this.phongCtrl = phongCtrl;
        this.nguoiDung = nguoiDung;
        this.KMC = KMC;
        this.DGC = DGC;
        this.bookingController = bookingController;
    }

    public void menuAdmin() {
        while (true) {
            System.out.println("\n--- MENU ADMIN ---");
            System.out.println("1. Quản lý phòng");
            System.out.println("2. Quản lý người dùng");
            System.out.println("3. Quản lý KM");
            System.out.println("4. Quản lý đánh giá");
            System.out.println("5. Thống kê doanh thu");
            System.out.println("6. Cập nhật thông tin cá nhân");
            System.out.println("7. Đổi mật khẩu");
            System.out.println("0. Đăng xuất");
            System.out.print("Chọn: ");
            int chon = scanner.nextInt();
            scanner.nextLine();
            switch (chon) {
                case 1:
                    quanLyPhong();
                    break;
                case 2:
                    quanLyNguoiDung();
                    break;
                case 3:
                    quanLyKM();
                    break;
                case 4:
                    quanLyDanhGia();
                    break;
                case 5:
                    thongKeDoanhThu();
                    break;
                case 6:
                    capNhatThongTinCaNhan();
                    break;
                case 7:
                    doiMatKhau();
                    break;
                case 0:
                    System.out.println("Đã đăng xuất.");
                    nguoiDung = null; // Đặt nguoiDung thành null để đăng xuất
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
            if (chon == 0) {
                break; // Thoát khỏi vòng lặp khi đăng xuất
            }
        }
    }


    void quanLyPhong() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ PHÒNG ---");
            System.out.println("1. Thêm phòng");
            System.out.println("2. Sửa phòng");
            System.out.println("3. Xóa phòng");
            System.out.println("4. Xem phòng");
            System.out.println("0. Quay lại menu chính");
            System.out.print("Chọn chức năng: ");
            int chon = scanner.nextInt();
            scanner.nextLine();
            switch (chon) {
                case 1:
                    System.out.println("Bạn đã chọn: Thêm phòng");
                    themPhong();
                    break;
                case 2:
                    System.out.println("Bạn đã chọn: Sửa phòng");
                    suaPhong();
                    break;
                case 3:
                    System.out.println("Bạn đã chọn: Xóa phòng");
                    xoaPhong();
                    break;
                case 4:
                    System.out.println("Bạn đã chọn: Xem phòng");
                    xemPhong();
                    break;
                case 0:
                    System.out.println("Quay lại menu chính...");
                    return; // Thoát khỏi quanLyPhong(), quay lại menu chính
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    public void themPhong() {
        System.out.println("\n--- THÊM PHÒNG ---");

        String tenPhong = "";
        boolean valid = false;
        while (!valid) {
            System.out.print("Nhập tên phòng: ");
            tenPhong = scanner.nextLine();

            // Kiểm tra tên phòng có trùng hay không
            if (phongCtrl.checkTenPhong(tenPhong)) {
                valid = true;  // Nếu tên hợp lệ (không trùng), thoát khỏi vòng lặp
            } else {
                System.out.println("Tên phòng đã tồn tại. Vui lòng nhập tên khác.");
            }
        }

        System.out.print("Nhập loại phòng: ");
        String loaiPhong = scanner.nextLine();

        System.out.print("Nhập giá phòng: ");
        double gia = Double.parseDouble(scanner.nextLine());

        System.out.print("Nhập mô tả phòng: ");
        String moTa = scanner.nextLine();

        System.out.print("Nhập tiện nghi phòng: ");
        String tienNghi = scanner.nextLine();

        // Gọi phương thức thêm phòng
        boolean success = phongCtrl.themPhongVaoHeThong(tenPhong, loaiPhong, gia, moTa, tienNghi);

        if (success) {
            System.out.println("Thêm phòng thành công!");
        } else {
            System.out.println("Thêm phòng thất bại.");
            themPhong();
        }

        System.out.println("\nNhấn 'Enter' để quay lại menu.");
        scanner.nextLine();
    }

    public void xoaPhong() {
        System.out.println("\n--- XÓA PHÒNG ---");

        System.out.print("Nhập tên phòng cần xóa: ");
        String tenPhong = scanner.nextLine();

        // Gọi phương thức xóa phòng
        boolean success = phongCtrl.xoaPhongKhoiHeThong(tenPhong);

        if (success) {
            System.out.println("Xóa phòng thành công!");
        } else {
            System.out.println("Xóa phòng thất bại.");
            xoaPhong();
        }

        System.out.println("\nNhấn 'Enter' để quay lại menu.");
        scanner.nextLine();
    }

    void suaPhong() {
        while (true){
            System.out.printf("Nhập tên phòng hoặc nhấn 'Enter' để quay lại: ");
            String p = scanner.nextLine();
            if(p.equalsIgnoreCase("")){
                break;
            }
            List<Phong> ds = phongCtrl.timKiemPhongTheoTen(p);
            Phong phong = ds.get(0);

            System.out.println("\n--- CẬP NHẬT THÔNG TIN PHÒNG ---");

            String tenPhongCu = phong.getTenPhong();  // tên phòng cũ để tìm và cập nhật

            boolean valid = false;
            String tenPhongMoi = "";
            while (!valid) {
                System.out.print("\nTên phòng mới (Enter để giữ nguyên): ");
                tenPhongMoi = scanner.nextLine();
                if (tenPhongMoi.isEmpty()) {
                    tenPhongMoi = tenPhongCu;
                    valid = true;
                } else {
                    // Kiểm tra tên phòng có trùng hay không
                    if (phongCtrl.checkTenPhong(tenPhongMoi)) {
                        valid = true;  // Nếu tên hợp lệ (không trùng), thoát khỏi vòng lặp
                    } else {
                        System.out.println("Tên phòng đã tồn tại. Vui lòng nhập tên khác.");
                    }
                }
            }

            System.out.print("Loại phòng mới (Enter để giữ nguyên): ");
            String loaiPhong = scanner.nextLine();
            if (loaiPhong.isEmpty()) {
                loaiPhong = phong.getLoaiPhong();
            }

            System.out.print("Giá mới (Enter để giữ nguyên): ");
            String giaInput = scanner.nextLine();
            double gia = phong.getGia();
            if (!giaInput.isEmpty()) {
                try {
                    gia = Double.parseDouble(giaInput);
                } catch (NumberFormatException e) {
                    System.out.println("Giá không hợp lệ. Giữ nguyên giá cũ.");
                }
            }

            System.out.print("Mô tả mới (Enter để giữ nguyên): ");
            String moTa = scanner.nextLine();
            if (moTa.isEmpty()) {
                moTa = phong.getMoTa();
            }

            System.out.print("Tiện nghi mới (Enter để giữ nguyên): ");
            String tienNghi = scanner.nextLine();
            if (tienNghi.isEmpty()) {
                tienNghi = phong.getTienNghi();
            }

            System.out.print("Trạng thái mới (Enter để giữ nguyên): ");
            String trangThai = scanner.nextLine();
            if (trangThai.isEmpty()) {
                trangThai = phong.getTrangThai();
            }

            // Gọi phương thức controller để cập nhật theo tên phòng cũ
            boolean success = phongCtrl.capNhatPhong(tenPhongCu, tenPhongMoi, loaiPhong, gia, moTa, tienNghi, trangThai);
            if (success) {
                System.out.println("---Cập nhật thành công!---");
            } else {
                System.out.println("-----Thất bại-----");
            }
        }
    }

    void xemPhong() {
        // Lấy danh sách các phòng
        List<Phong> phongs = phongCtrl.layDanhSachPhong();

        // In tiêu đề bảng
        System.out.println("| STT | Tên Phòng    | Loại Phòng   | Giá        | Trạng Thái |");
        System.out.println("|-----|--------------|--------------|------------|------------|");

        // In thông tin các phòng
        int stt = 1;  // Số thứ tự
        for (Phong p : phongs) {
            System.out.printf("| %-3d | %-12s | %-12s | %-10.2f | %-10s |\n",
                    stt,
                    p.getTenPhong(),
                    p.getLoaiPhong(),
                    p.getGia(),
                    p.getTrangThai());
            stt++;  // Tăng số thứ tự
        }

        System.out.println("\nNhập 1 để tìm phòng, nhập 0 để quay lại:");
        int chon = scanner.nextInt();
        scanner.nextLine();

        if (chon == 1) {
            timPhong(); // Gọi phương thức tìm phòng
        } else if (chon == 0) {
            System.out.println("Quay lại...");
            quanLyPhong();
        } else {
            System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại.");
            xemPhong();
        }
    }

    void timPhong() {
        while (true) {
            System.out.print("Nhập phòng muốn xem hoặc nhập 'back' để quay lại: ");
            String tenPhong = scanner.nextLine();

            if (tenPhong.equalsIgnoreCase("back")) {
                break;  // Quay lại menu trước đó
            }

            List<Phong> ds = phongCtrl.timKiemPhongTheoTen(tenPhong);
            if (ds.isEmpty()) {
                System.out.println("Không có phòng " + tenPhong);
            } else {
                Phong p = ds.get(0);
                hienThiChiTietPhong(p);
            }
        }
    }

    void hienThiChiTietPhong(Phong p) {
        // Độ rộng từng cột (trừ mô tả)
        int w_id = 2, w_ten = 10, w_loai = 10, w_gia = 11, w_tienNghi = 25, w_trangThai = 10; // Thay đổi tiện nghi thành 25

        // Gói nội dung các cột
        List<String> ids = wrap(String.valueOf(p.getId()), w_id);
        List<String> tenPhongs = wrap(p.getTenPhong(), w_ten);
        List<String> loaiPhongs = wrap(p.getLoaiPhong(), w_loai);
        List<String> gias = wrap(String.format("%.2f", p.getGia()), w_gia);
        List<String> tienNghis = wrap(p.getTienNghi(), w_tienNghi); // Cập nhật độ rộng cột tiện nghi
        List<String> trangThais = wrap(p.getTrangThai(), w_trangThai);

        // Tìm số dòng lớn nhất cần in
        int maxRows = Collections.max(Arrays.asList(
                ids.size(), tenPhongs.size(), loaiPhongs.size(), gias.size(), tienNghis.size(), trangThais.size()
        ));

        // In tiêu đề bảng chính (không có mô tả)
        System.out.printf("| %-2s | %-10s | %-10s | %-11s | %-25s | %-10s |\n", // Cập nhật cột tiện nghi thành 25
                "id", "ten_phong", "loai_phong", "gia", "tien_nghi", "trang_thai");
        System.out.println("|----|------------|------------|-------------|---------------------------|------------|");

        // In từng dòng bảng chính
        for (int i = 0; i < maxRows; i++) {
            System.out.printf("| %-2s | %-10s | %-10s | %-11s | %-25s | %-10s |\n", // Cập nhật cột tiện nghi thành 25
                    getSafe(ids, i),
                    getSafe(tenPhongs, i),
                    getSafe(loaiPhongs, i),
                    getSafe(gias, i),
                    getSafe(tienNghis, i),
                    getSafe(trangThais, i)
            );
        }

        // In mô tả ở dưới bảng
        System.out.println("\nGhi chú - Mô tả phòng:");
        List<String> moTas = wrap(p.getMoTa(), 100); // wrap mô tả theo chiều rộng dòng (có thể thay đổi)

        for (String line : moTas) {
            System.out.println(line);
        }
    }

    // Hàm chia nhỏ chuỗi thành từng dòng không vượt quá độ dài max
    List<String> wrap(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null) {
            lines.add("");
            return lines;
        }
        while (text.length() > maxWidth) {
            lines.add(text.substring(0, maxWidth));
            text = text.substring(maxWidth);
        }
        lines.add(text); // dòng cuối
        return lines;
    }

    // Tránh IndexOutOfBounds
    String getSafe(List<String> list, int index) {
        return index < list.size() ? list.get(index) : "";
    }

    // Hàm rút gọn chuỗi nếu quá dài
    String rutGon(String chuoi, int doDaiToiDa) {
        if (chuoi == null) return "";
        return chuoi.length() > doDaiToiDa ? chuoi.substring(0, doDaiToiDa - 1) + "…" : chuoi;
    }



    // --- QuanLyUser ---
    void quanLyNguoiDung() {
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
            // In tiêu đề của bảng với thêm cột "Số điện thoại" và "Địa chỉ"
            System.out.printf("%-5s | %-25s | %-25s | %-15s | %-15s | %-30s\n",
                    "ID", "Họ tên", "Email", "Vai trò", "Số điện thoại", "Địa chỉ");
            System.out.println("-------------------------------------------------------------------------");

            // Lặp qua danh sách người dùng và in ra thông tin từng người
            for (NguoiDung nd : danhSach) {
                System.out.printf("%-5d | %-25s | %-25s | %-15s | %-15s | %-30s\n",
                        nd.getId(), nd.getHoTen(), nd.getEmail(), nd.getVaiTro(),
                        nd.getSdt(), nd.getDiaChi());
            }
        }

        System.out.println("\nNhấn 'enter' để quay lại menu.");
        scanner.nextLine();
    }



    // --- QLKM ---
    void quanLyKM() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ KHUYẾN MÃI ---");
            System.out.println("1. Thêm khuyến mãi");
            System.out.println("2. Sửa khuyến mãi");
            System.out.println("3. Xóa khuyến mãi");
            System.out.println("4. Xem danh sách khuyến mãi");
            System.out.println("0. Quay lại menu chính");
            System.out.print("Chọn chức năng: ");

            int chon = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            switch (chon) {
                case 1:
                    System.out.println("Bạn đã chọn: Thêm khuyến mãi");
                    themKhuyenMaiView();
                    break;
                case 2:
                    System.out.println("Bạn đã chọn: Sửa khuyến mãi");
                    suaKhuyenMaiView();
                    break;
                case 3:
                    System.out.println("Bạn đã chọn: Xóa khuyến mãi");
                    xoaKhuyenMaiView();
                    break;
                case 4:
                    System.out.println("Bạn đã chọn: Xem danh sách khuyến mãi");
                    xemDanhSachKhuyenMaiView();
                    break;
                case 0:
                    System.out.println("Quay lại menu chính...");
                    return; // Thoát khỏi menu quản lý khuyến mãi, quay lại menu chính
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    void themKhuyenMaiView() {
        System.out.println("\n--- THÊM KHUYẾN MÃI ---");

        System.out.print("Nhập mã khuyến mãi: ");
        String ma = scanner.nextLine();

        System.out.print("Nhập tên khuyến mãi: ");
        String tenKhuyenMai = scanner.nextLine();

        System.out.print("Nhập mô tả khuyến mãi: ");
        String moTa = scanner.nextLine();

        System.out.print("Nhập phần trăm giảm giá: ");
        int phanTramGiam = scanner.nextInt();
        scanner.nextLine(); // clear buffer

        System.out.print("Nhập ngày bắt đầu (yyyy-MM-dd): ");
        String ngayBatDau = scanner.nextLine();

        System.out.print("Nhập ngày kết thúc (yyyy-MM-dd): ");
        String ngayKetThuc = scanner.nextLine();

        boolean success = KMC.themKhuyenMai(ma, tenKhuyenMai, moTa, phanTramGiam, ngayBatDau, ngayKetThuc);

        if (success) {
            System.out.println("Thêm khuyến mãi thành công!");
        } else {
            System.out.println("Thêm khuyến mãi thất bại.");
        }

        System.out.println("\nNhấn 'Enter' để quay lại menu.");
        scanner.nextLine();
    }

    void suaKhuyenMaiView() {
        System.out.println("\n--- SỬA KHUYẾN MÃI ---");

        System.out.print("Nhập mã khuyến mãi cần sửa: ");
        String ma = scanner.nextLine();

        KhuyenMai km = KMC.timKhuyenMaiTheoMa(ma); // Cần có trong controller
        if (km == null) {
            System.out.println("Không tìm thấy khuyến mãi với mã: " + ma);
            return;
        }

        System.out.println("Nhấn Enter nếu muốn giữ nguyên giá trị hiện tại.");

        System.out.print("Tên khuyến mãi (" + km.getTenKhuyenMai() + "): ");
        String tenKhuyenMai = scanner.nextLine();
        if (tenKhuyenMai.isEmpty()) tenKhuyenMai = km.getTenKhuyenMai();

        System.out.print("Mô tả (" + km.getMoTa() + "): ");
        String moTa = scanner.nextLine();
        if (moTa.isEmpty()) moTa = km.getMoTa();

        System.out.print("Phần trăm giảm (" + km.getPhanTramGiam() + "): ");
        String giamStr = scanner.nextLine();
        int phanTramGiam = giamStr.isEmpty() ? km.getPhanTramGiam() : Integer.parseInt(giamStr);

        String ngayBatDauStrGoc = (km.getNgayBatDau() != null) ? km.getNgayBatDau().toString() : "null";
        System.out.print("Ngày bắt đầu (" + ngayBatDauStrGoc + "): ");
        String batDauStr = scanner.nextLine();
        String ngayBatDau = batDauStr.isEmpty() ? ngayBatDauStrGoc : batDauStr;

        String ngayKetThucStrGoc = (km.getNgayKetThuc() != null) ? km.getNgayKetThuc().toString() : "null";
        System.out.print("Ngày kết thúc (" + ngayKetThucStrGoc + "): ");
        String ketThucStr = scanner.nextLine();
        String ngayKetThuc = ketThucStr.isEmpty() ? ngayKetThucStrGoc : ketThucStr;

        // Nếu giá trị cũ là "null" (chuỗi) và không nhập mới, set thành null trong DB
        boolean success = KMC.capNhatKhuyenMai(
                ma,
                tenKhuyenMai,
                moTa,
                phanTramGiam,
                "null".equals(ngayBatDau) ? null : ngayBatDau,
                "null".equals(ngayKetThuc) ? null : ngayKetThuc
        );

        if (success) {
            System.out.println("Cập nhật khuyến mãi thành công!");
        } else {
            System.out.println("Cập nhật khuyến mãi thất bại.");
        }

        System.out.println("\nNhấn 'Enter' để quay lại menu.");
        scanner.nextLine();
    }


    void xoaKhuyenMaiView() {
        System.out.println("\n--- XÓA KHUYẾN MÃI ---");

        System.out.print("Nhập mã khuyến mãi cần xóa: ");
        String ma = scanner.nextLine();

        boolean success = KMC.xoaKhuyenMai(ma);

        if (success) {
            System.out.println("Xóa khuyến mãi thành công!");
        } else {
            System.out.println("Xóa khuyến mãi thất bại.");
        }

        System.out.println("\nNhấn 'Enter' để quay lại menu.");
        scanner.nextLine();
    }

    void xemDanhSachKhuyenMaiView() {
        System.out.println("\n--- DANH SÁCH KHUYẾN MÃI ---");

        List<KhuyenMai> khuyenMais = KMC.layDanhSachKhuyenMai();
        if (khuyenMais.isEmpty()) {
            System.out.println("Không có khuyến mãi nào.");
        } else {
            for (KhuyenMai khuyenMai : khuyenMais) {
                System.out.println("Mã: " + khuyenMai.getMa() + ", Tên: " + khuyenMai.getTenKhuyenMai() +
                        ", Mô tả: " + khuyenMai.getMoTa() + ", Giảm: " + khuyenMai.getPhanTramGiam() +
                        "%, Ngày bắt đầu: " + khuyenMai.getNgayBatDau() + ", Ngày kết thúc: " + khuyenMai.getNgayKetThuc());System.out.println("\n--- DANH SÁCH KHUYẾN MÃI ---");
                System.out.printf("%-10s %-20s %-25s %-7s %-15s %-15s\n",
                        "Mã", "Tên khuyến mãi", "Mô tả", "Giảm", "Bắt đầu", "Kết thúc");
                System.out.println("------------------------------------------------------------------------------------------");

                for (KhuyenMai km : khuyenMais) {
                    System.out.printf("%-10s %-20s %-25s %-7s %-15s %-15s\n",
                            km.getMa(),
                            km.getTenKhuyenMai(),
                            km.getMoTa(),
                            km.getPhanTramGiam() + "%",
                            km.getNgayBatDau(),
                            km.getNgayKetThuc());
                }

            }
        }

        System.out.println("\nNhấn 'Enter' để quay lại menu.");
        scanner.nextLine();
    }

    void quanLyDanhGia () {
        while (true) {
            System.out.println("\n--- QUẢN LÝ ĐÁNH GIÁ ---");
            System.out.println("1. Xem đánh giá");
            System.out.println("2. Xóa đánh giá");
            System.out.println("0. Quay lại menu chính");
            System.out.print("Chọn chức năng: ");
            int chon = scanner.nextInt();
            scanner.nextLine();
            switch (chon) {
                case 1:
                    System.out.println("Bạn đã chọn: Xem đánh giá");
                    xemDanhGia();
                    break;
                case 2:
                    System.out.println("Bạn đã chọn: Xóa đánh giá");
                    xoaDanhGia();
                    break;
                case 0:
                    System.out.println("Quay lại menu chính...");
                    return; // Thoát khỏi quanLyPhong(), quay lại menu chính
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    void xemDanhGia () {
        System.out.println("\n--- DANH SÁCH ĐÁNH GIÁ ---");
        List<DanhGia> danhGiaList = DGC.layTatCaDanhGia();

        if (danhGiaList.isEmpty()) {
            System.out.println("Không có đánh giá nào.");
            return;
        }

        System.out.printf("%-5s | %-20s | %-10s | %-5s | %-40s | %-15s\n",
                "ID", "Tên người đánh giá", "Phòng ID", "Điểm", "Nội dung", "Ngày đánh giá");
        System.out.println("-----------------------------------------------------------------------------------------------");

        for (DanhGia dg : danhGiaList) {
            System.out.printf("%-5d | %-20s | %-10d | %-5d | %-40s | %-15s\n",
                    dg.getId(), dg.getTenNguoiDanhGia(), dg.getPhongId(),
                    dg.getDiemDanhGia(), dg.getNoiDung(), dg.getNgayDanhGia());
        }

        System.out.println("\nNhấn 'Enter' để quay lại menu.");
        scanner.nextLine();
    }

    public void xoaDanhGia() {
        System.out.print("Nhập ID đánh giá bạn muốn xóa: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        boolean ketQua = DGC.xoaDanhGia(id);

        if (ketQua) {
            System.out.println("Xóa đánh giá thành công!");
        } else {
            System.out.println("Có lỗi xảy ra khi xóa đánh giá.");
        }

        System.out.println("\nNhấn 'Enter' để quay lại menu.");
        scanner.nextLine();
    }



    void thongKeDoanhThu() {
        System.out.println("\n--- THỐNG KÊ DOANH THU ---");
        System.out.print("Nhập năm cần thống kê (ví dụ 2025): ");
        int nam = scanner.nextInt();
        scanner.nextLine();
        bookingController.tinhTongDoanhThuTheoNam(nam);
        System.out.println("\nNhấn 'enter' để quay lại menu.");
        scanner.nextLine();
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