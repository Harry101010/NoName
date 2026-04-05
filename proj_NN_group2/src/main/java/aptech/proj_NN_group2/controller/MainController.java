package aptech.proj_NN_group2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        showDashboard(); 
    }

    // Viết các hàm @FXML một lần duy nhất ở đây
    @FXML
    private void showDashboard() {
        loadView("/aptech/proj_NN_group2/DashboardView.fxml");
    }

    @FXML
    private void showSanXuat() {
        loadView("/aptech/proj_NN_group2/SanXuatView.fxml");
    }
    @FXML
    private void showKhoNguyenLieu() {
        // Hiện tại chưa có file KhoView thì ta nạp tạm Dashboard hoặc để trống
        loadView("/aptech/proj_NN_group2/DashboardView.fxml"); 
    }

    @FXML
    private void showKhoThanhPham() {
        // Tương tự cho nút Kho Thành Phẩm nếu trong FXML có gọi
        loadView("/aptech/proj_NN_group2/DashboardView.fxml");
    }

    // Hàm bổ trợ loadView
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 