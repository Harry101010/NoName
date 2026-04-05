package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.TestConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class SanXuatController {
    
    @FXML private Label lblBuocHienTai;
    @FXML private Button btnXacNhan;

    // Hàm cập nhật tiến độ sang bước tiếp theo
    public void updateTienDo(int idLenhSX, String buocTiepTheo) {
        String sql = "UPDATE TienDoSanXuat SET buoc_hien_tai = ?, thoi_gian_xac_nhan = GETDATE() WHERE id_lenh_sx = ?";
        
        try (Connection con = new TestConnection().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, buocTiepTheo);
            ps.setInt(2, idLenhSX);
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Đã chuyển sang bước: " + buocTiepTheo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}