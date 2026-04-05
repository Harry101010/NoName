package aptech.proj_NN_group2.util;

import java.sql.*;
import java.util.ArrayList;

public class NguyenLieuDAO {
    // Hàm lấy danh sách nguyên liệu ưu tiên hàng cũ (FIFO)
	public void getNguyenLieuFIFO() {
	    String sql = "SELECT * FROM NguyenLieu ORDER BY han_su_dung ASC, ngay_nhap_kho ASC";
	    // Thêm dấu ngoặc nhọn sau try-with-resources
	    try (Connection con = new TestConnection().getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) { 
	        
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            System.out.println("Tên: " + rs.getString("ten_nguyen_lieu") + " - HSD: " + rs.getDate("han_su_dung"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}