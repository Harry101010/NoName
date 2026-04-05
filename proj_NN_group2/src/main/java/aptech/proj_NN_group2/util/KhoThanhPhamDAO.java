package aptech.proj_NN_group2.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class KhoThanhPhamDAO {

    public boolean nhapKho(String tenKem, int soLuong, int idLenh) {
        // SQL đã khớp hoàn toàn với cột trong Database của bạn
        String sql = "INSERT INTO KhoThanhPham (ten_kem, so_luong_hop, id_lenh_sx_goc, ngay_san_xuat, han_su_dung_kem) " +
                     "VALUES (?, ?, ?, GETDATE(), DATEADD(month, 6, GETDATE()))";

        // Dùng Exception chung để xử lý lỗi từ TestConnection().getConnection()
        try (Connection con = new TestConnection().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, tenKem);
            ps.setInt(2, soLuong);
            ps.setInt(3, idLenh);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}