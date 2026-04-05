package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.KhoThanhPhamDAO;
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
        
        // Yêu cầu xuất 10 lít (Hệ thống sẽ bốc 3 lít Lô A và 7 lít Lô B)
        double soLuongCanXuat = 10.0;
        boolean thanhCong = dao.xuatKhoVetCan("Sữa tươi tiệt trùng", soLuongCanXuat);
        
        if (thanhCong) {
            updateStatus("TRON", "Vét kho thành công: Đã lấy đủ " + soLuongCanXuat + " lít sữa từ nhiều lô.", 0);
            showAlert("Thành công", "Hệ thống đã tự động vét cạn lô cũ và trừ tiếp vào lô mới!");
        } else {
            showAlert("Thất bại", "Tổng kho không đủ " + soLuongCanXuat + " lít để sản xuất!");
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
    
    @FXML 
    private void handleStep8() { 
        try {
            // 1. Lấy dữ liệu thực tế từ giao diện
            int soLuongThucTe = Integer.parseInt(txtSoKhuon.getText());
            String tenKem = "Kem Dừa Non"; // Sau này có thể lấy động từ lblTenMon.getText()

            // 2. Cập nhật trạng thái Lệnh sản xuất thành HOAN_THANH
            String sqlUpdateLenh = "UPDATE TienDoSanXuat SET buoc_hien_tai = 'DONG_GOI', " +
                                   "trang_thai = 'HOAN_THANH', thoi_gian_xac_nhan = GETDATE() " +
                                   "WHERE id_lenh_sx = ?";
            
            try (Connection con = new TestConnection().getConnection();
                 PreparedStatement ps = con.prepareStatement(sqlUpdateLenh)) {
                
                ps.setInt(1, currentIdLenh);
                ps.executeUpdate();
                
                // 3. GỌI DAO MỚI ĐỂ NHẬP KHO THÀNH PHẨM
                KhoThanhPhamDAO tpDAO = new KhoThanhPhamDAO();
                boolean isOk = tpDAO.nhapKho(tenKem, soLuongThucTe, currentIdLenh);
                
                if (isOk) {
                    // Hiển thị thông báo thành công (nhớ dùng INFORMATION thay vì ERROR nhé)
                    showAlert("Thành công", "Mẻ kem đã HOÀN THÀNH và nhập kho " + soLuongThucTe + " hộp!");
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi dữ liệu", "Vui lòng nhập số lượng khuôn ở Bước 6 trước khi đóng gói!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String content) {
        // Sửa ERROR thành INFORMATION để hiện biểu tượng chữ 'i' xanh (hoặc tích xanh tùy bản Java)
        Alert alert = new Alert(Alert.AlertType.INFORMATION); 
        alert.setTitle(title);
        alert.setHeaderText(null); // Để tiêu đề gọn gàng hơn
        alert.setContentText(content);
        alert.showAndWait();
    }
}