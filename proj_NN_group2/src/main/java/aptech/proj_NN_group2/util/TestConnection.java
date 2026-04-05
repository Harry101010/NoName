package aptech.proj_NN_group2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        // 1. Thông tin cấu hình SQL Server
        // Lưu ý: databaseName phải là tên DB bạn đã tạo trong SSMS
        String connectionUrl = 
            "jdbc:sqlserver://localhost:1433;" +
            "databaseName=QuanLySanXuatKem;" + 
            "user=sa;" + 
            "password=123456;" + // Thay bằng mật khẩu của bạn
            "encrypt=true;" + 
            "trustServerCertificate=true;";

        try {
            // 2. Kiểm tra Driver và kết nối
            System.out.println("Đang kết nối đến SQL Server...");
            Connection con = DriverManager.getConnection(connectionUrl);
            
            if (con != null) {
                System.out.println("Chúc mừng! Kết nối thành công.");
                
                // Hiển thị thông tin phiên bản SQL Server
                DatabaseMetaData dm = con.getMetaData();
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                
                con.close(); // Đóng kết nối sau khi test xong
            }
        } catch (Exception e) {
            System.err.println("Kết nối thất bại!");
            e.printStackTrace();
        }
    }
    public Connection getConnection() throws Exception {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=QuanLySanXuatKem;user=sa;password=123456;encrypt=true;trustServerCertificate=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(connectionUrl);
    }
}