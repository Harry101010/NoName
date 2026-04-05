package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.DashboardDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class DashboardController {
    @FXML private Label lblTongMeKem, lblCanhBaoKho;
    @FXML private ListView<String> lvNguyenLieuSapHet;

    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        DashboardDAO dao = new DashboardDAO();
        
        // Cập nhật số mẻ kem
        int tongMe = dao.getSlanMeKemHomNay();
        lblTongMeKem.setText(String.valueOf(tongMe));
        
        // Cập nhật danh sách nguyên liệu sắp hết
        List<String> list = dao.getNguyenLieuSapHet();
        lvNguyenLieuSapHet.getItems().clear();
        lvNguyenLieuSapHet.getItems().addAll(list);
        
        // Hiển thị số lượng cảnh báo
        lblCanhBaoKho.setText(String.valueOf(list.size()));
    }
}