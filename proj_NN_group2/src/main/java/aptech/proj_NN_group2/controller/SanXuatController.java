package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.NguyenLieuDAO;
import aptech.proj_NN_group2.util.TestConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class SanXuatController {

    @FXML private Label lblIdLenh, lblTenMon;
    @FXML private TextField txtDungTichDongHoa, txtSoKhuon;
    @FXML private Label status1, status3;

    private int currentIdLenh = 1; // Giả sử đang làm lệnh ID số 1

    // Hàm cập nhật trạng thái chung
    private void updateStatus(String stepName, String extraInfo, double numericValue) {
        String sql = "UPDATE TienDoSanXuat SET buoc_hien_tai = ?, thoi_gian_xac_nhan = GETDATE()";
        
        // Nếu có giá trị số (dung tích hoặc số khuôn), cập nhật thêm cột tương ứng
        if (stepName.equals("DONG_HOA") || stepName.equals("THANH_TRUNG")) {
            sql += ", dung_tich_thuc = " + numericValue;
        } else if (stepName.equals("CHIET_ROT")) {
            sql += ", so_luong_khuon = " + (int)numericValue;
        }
        
        sql += " WHERE id_lenh_sx = ?";

        try (Connection con = new TestConnection().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, stepName);
            ps.setInt(2, currentIdLenh);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Thành công: " + stepName);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã xác nhận bước: " + extraInfo);
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Các hàm xử lý sự kiện cho từng nút (onAction)
    @FXML 
    private void handleStep1() { 
        NguyenLieuDAO dao = new NguyenLieuDAO();
        // Giả sử mẻ kem này cần 10 lít Sữa tươi tiệt trùng
        boolean checkKho = dao.xuatKhoFIFO("Sữa tươi tiệt trùng", 10.0);
        
        if (checkKho) {
            updateStatus("TRON", "Xử lý & Trộn (Đã trừ kho 10 lít sữa)", 0);
        } else {
            showAlert("Lỗi kho", "Không đủ nguyên liệu Sữa tươi trong kho!");
        }
    }
    
    @FXML private void handleStep2() { 
        double dt = Double.parseDouble(txtDungTichDongHoa.getText());
        updateStatus("DONG_HOA", "Đồng hóa", dt); 
    }
    
    @FXML private void handleStep3() { updateStatus("THANH_TRUNG", "Thanh trùng", 0); }
    
    @FXML private void handleStep4() { updateStatus("U_KEM", "Bắt đầu ủ (4°C)", 0); }
    
    @FXML private void handleStep5() { updateStatus("DANH_KEM", "Đánh kem", 0); }
    
    @FXML private void handleStep6() { 
        int khuôn = Integer.parseInt(txtSoKhuon.getText());
        updateStatus("CHIET_ROT", "Chiết rót", khuôn); 
    }
    
    @FXML private void handleStep7() { updateStatus("LAM_CUNG", "Làm cứng", 0); }
    
    @FXML private void handleStep8() { 
        updateStatus("DONG_GOI", "Đóng gói hoàn tất", 0);
        // Sau bước này sẽ gọi hàm nhập kho thành phẩm (sẽ viết sau)
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}