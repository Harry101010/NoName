package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.KhoThanhPhamDAO;
import aptech.proj_NN_group2.util.NguyenLieuDAO;
import aptech.proj_NN_group2.util.TestConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class SanXuatController {

    @FXML private Label lblIdLenh;
    @FXML private ComboBox<String> cbTenMon; 
    @FXML private TextField txtDungTichDongHoa, txtSoKhuon;
    @FXML private Label status1, status3;

    private int currentIdLenh; // Không gán cứng số 4 nữa
    
    @FXML
    public void initialize() {
        // Tự động tìm ID của lệnh đang chờ mới nhất khi mở trang
        this.currentIdLenh = getLatestOrderId();
        lblIdLenh.setText(currentIdLenh > 0 ? String.valueOf(currentIdLenh) : "---");
        
        loadDanhMucMonKem(); 
    }

    private void loadDanhMucMonKem() {
        String sql = "SELECT ten_mon_kem FROM DanhMucMonKem";
        try (Connection con = new TestConnection().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            cbTenMon.getItems().clear();
            while (rs.next()) {
                cbTenMon.getItems().add(rs.getString("ten_mon_kem"));
            }
            if (!cbTenMon.getItems().isEmpty()) {
                cbTenMon.getSelectionModel().selectFirst();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleCreateNewOrder() {
        String monChon = cbTenMon.getValue();
        if (monChon == null) {
            showAlert("Lỗi", "Vui lòng chọn món kem trước!");
            return;
        }

        String sql = "INSERT INTO TienDoSanXuat (ten_mon_kem, buoc_hien_tai, trang_thai) VALUES (?, 'CHO_XU_LY', 'DANG_CHO')";
        try (Connection con = new TestConnection().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, monChon);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.currentIdLenh = rs.getInt(1);
                lblIdLenh.setText(String.valueOf(currentIdLenh));
                showAlert("Thành công", "Đã tạo mẻ kem mới ID: " + currentIdLenh);
                
                // Reset giao diện cho mẻ mới
                status1.setText("Đang chờ...");
                txtDungTichDongHoa.clear();
                txtSoKhuon.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể kết nối cơ sở dữ liệu!");
        }
    }

    private int getLatestOrderId() {
        // Lấy ID lớn nhất đang ở trạng thái DANG_CHO
        String sql = "SELECT TOP 1 id_lenh_sx FROM TienDoSanXuat WHERE trang_thai = 'DANG_CHO' ORDER BY id_lenh_sx DESC";
        try (Connection con = new TestConnection().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0; 
    }

    private void updateStatus(String stepName, String extraInfo, double numericValue) {
        if (currentIdLenh <= 0) {
            showAlert("Lỗi", "Vui lòng nhấn 'Tạo mẻ mới' trước khi xác nhận công đoạn!");
            return;
        }

        String sql = "UPDATE TienDoSanXuat SET buoc_hien_tai = ?, thoi_gian_xac_nhan = GETDATE()";
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
                showAlert("Thành công", "Đã xác nhận bước: " + extraInfo);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML 
    private void handleStep1() { 
        String monChon = cbTenMon.getValue();
        NguyenLieuDAO dao = new NguyenLieuDAO();
        if (monChon != null && monChon.contains("Dâu Tây")) {
            if (dao.xuatKhoVetCan("Dâu tây Đà Lạt (Loại 1)", 0.4)) {
                updateStatus("TRON", "Trộn (đã trừ dâu tây)", 0);
            } else { showAlert("Thất bại", "Kho không đủ dâu tây!"); }
        } else if (monChon != null && monChon.contains("Dừa Non")) {
            if (dao.xuatKhoVetCan("Sữa tươi tiệt trùng", 10.0)) {
                updateStatus("TRON", "Trộn (đã trừ sữa)", 0);
            } else { showAlert("Thất bại", "Kho không đủ sữa!"); }
        }
    }
    
    @FXML private void handleStep2() { 
        try {
            double dt = Double.parseDouble(txtDungTichDongHoa.getText());
            updateStatus("DONG_HOA", "Đồng hóa", dt);
        } catch (Exception e) { showAlert("Lỗi", "Vui lòng nhập số!"); }
    }
    
    @FXML private void handleStep3() { updateStatus("THAN_TRUNG", "Thanh trùng", 0); }
    @FXML private void handleStep4() { updateStatus("U_KEM", "Bắt đầu ủ", 0); }
    @FXML private void handleStep5() { updateStatus("DANH_KEM", "Đánh kem", 0); }
    @FXML private void handleStep6() { 
        try {
            int khuôn = Integer.parseInt(txtSoKhuon.getText());
            updateStatus("CHIET_ROT", "Chiết rót", khuôn);
        } catch (Exception e) { showAlert("Lỗi", "Vui lòng nhập số!"); }
    }
    @FXML private void handleStep7() { updateStatus("LAM_CUNG", "Làm cứng", 0); }
    
    @FXML 
    private void handleStep8() { 
        try {
            int soLuong = Integer.parseInt(txtSoKhuon.getText());
            String tenKem = cbTenMon.getValue();

            String sqlUpdate = "UPDATE TienDoSanXuat SET buoc_hien_tai = 'DONG_GOI', trang_thai = 'HOAN_THANH', thoi_gian_xac_nhan = GETDATE() WHERE id_lenh_sx = ?";
            try (Connection con = new TestConnection().getConnection();
                 PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setInt(1, currentIdLenh);
                ps.executeUpdate();
                
                KhoThanhPhamDAO tpDAO = new KhoThanhPhamDAO();
                if (tpDAO.nhapKho(tenKem, soLuong, currentIdLenh)) {
                    showAlert("Thành công", "Mẻ kem [" + tenKem + "] đã hoàn tất!");
                    this.currentIdLenh = 0; // Xóa ID hiện tại để sẵn sàng cho mẻ tiếp theo
                    lblIdLenh.setText("---");
                }
            }
        } catch (Exception e) { showAlert("Lỗi", "Kiểm tra lại số lượng ở Bước 6!"); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); 
        alert.setTitle(title);
        alert.setHeaderText(null); 
        alert.setContentText(content);
        alert.showAndWait();
    }
}