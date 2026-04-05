package aptech.proj_NN_group2.util;

import java.sql.*;

public class KhoThanhPhamDAO {

    /**
     * Hàm nhập kho thành phẩm sau khi đóng gói hoàn tất.
     * Tự động tính hạn sử dụng là 180 ngày (6 tháng) kể từ ngày sản xuất.
     */
    public boolean nhapKho(String tenKem, int soLuong, int idLenhSX) {
        String sql = "INSERT INTO KhoThanhPham (ten_kem, so_luong_hop, han_su_dung_kem, id_lenh_sx_goc) " +
                     "VALUES (?, ?, DATEADD(day, 180, GETDATE()), ?)";
        
        try (Connection con = new TestConnection().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, tenKem);
            ps.setInt(2, soLuong);
            ps.setInt(3, idLenhSX);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi khi nhập kho thành phẩm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}