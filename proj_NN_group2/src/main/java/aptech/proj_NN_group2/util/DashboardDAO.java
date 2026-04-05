package aptech.proj_NN_group2.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {
    // 1. Đếm số mẻ kem hoàn thành trong ngày hôm nay
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

    // 2. Lấy danh sách nguyên liệu có tổng tồn dưới 5 đơn vị
    public List<String> getNguyenLieuSapHet() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT ten_nguyen_lieu, SUM(so_luong_ton) as tong FROM NguyenLieu " +
                     "GROUP BY ten_nguyen_lieu HAVING SUM(so_luong_ton) < 5";
        try (Connection con = new TestConnection().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("ten_nguyen_lieu") + " - Tồn: " + rs.getDouble("tong"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}