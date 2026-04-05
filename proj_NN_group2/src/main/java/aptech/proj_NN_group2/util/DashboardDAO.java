package aptech.proj_NN_group2.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {
    // Đếm mẻ kem hoàn thành hôm nay
    public int getSlanMeKemHomNay() {
        String sql = "SELECT COUNT(*) FROM TienDoSanXuat WHERE trang_thai = 'HOAN_THANH' " +
                     "AND CAST(thoi_gian_xac_nhan AS DATE) = CAST(GETDATE() AS DATE)";
        try (Connection con = new TestConnection().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // Lấy nguyên liệu có tổng tồn < 5
    public List<String> getNguyenLieuSapHet() {
        List<String> list = new ArrayList<>();
     // Thay đổi dòng SQL trong DashboardDAO:
        String sql = "SELECT ten_nguyen_lieu, SUM(so_luong_ton) as tong, " +
                     "ISNULL(MAX(don_vi_tinh), '') as dvt " + // Dùng ISNULL để tránh hiện chữ null
                     "FROM NguyenLieu GROUP BY ten_nguyen_lieu HAVING SUM(so_luong_ton) < 5";
        try (Connection con = new TestConnection().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add("⚠️ " + rs.getString("ten_nguyen_lieu") + ": " + rs.getDouble("tong") + " " + rs.getString("dvt"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}