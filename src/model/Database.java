package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/quan_ly_datphong";
    private static final String USER = "root";
    private static final String PASSWORD = "123456789"; // Thay đổi mật khẩu nếu cần

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Đăng ký driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Thiết lập kết nối
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối đến database thành công!");
            } catch (ClassNotFoundException e) {
                System.out.println("Không tìm thấy driver MySQL: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Lỗi kết nối database: " + e.getMessage());
            }
        }

        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Đã đóng kết nối database.");
            } catch (SQLException e) {
                System.out.println("Lỗi khi đóng kết nối database: " + e.getMessage());
            }
        }
    }
}