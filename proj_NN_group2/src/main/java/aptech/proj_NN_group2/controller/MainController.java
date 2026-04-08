package aptech.proj_NN_group2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    
    @FXML private Button btnDashboard;
    @FXML private Button btnSanXuat;
    @FXML private Button btnThanhPham; // Thêm biến cho nút mới
    @FXML private Button btnKho;

    @FXML
    public void initialize() {
        // Mặc định hiện Dashboard khi mở app
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/aptech/proj_NN_group2/DashboardView.fxml");
        setButtonActive(btnDashboard);
    }

    @FXML
    private void showSanXuat() {
        loadView("/aptech/proj_NN_group2/SanXuatView.fxml");
        setButtonActive(btnSanXuat);
    }

    // Hàm mới để nạp trang Kho Thành Phẩm
    @FXML
    private void showKhoThanhPham() {
        loadView("/aptech/proj_NN_group2/ThanhPhamView.fxml");
        setButtonActive(btnThanhPham);
    }

    @FXML
    private void showKhoNguyenLieu() {
        loadView("/aptech/proj_NN_group2/NguyenLieuView.fxml");
        setButtonActive(btnKho);
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Lỗi nạp FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setButtonActive(Button activeBtn) {
        String activeStyle = "-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 8; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;";
        String normalStyle = "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;";

        // Trả tất cả nút về trạng thái bình thường
        if (btnDashboard != null) btnDashboard.setStyle(normalStyle);
        if (btnSanXuat != null) btnSanXuat.setStyle(normalStyle);
        if (btnThanhPham != null) btnThanhPham.setStyle(normalStyle); // Thêm dòng này
        if (btnKho != null) btnKho.setStyle(normalStyle);

        // Làm nổi bật nút vừa bấm
        if (activeBtn != null) {
            activeBtn.setStyle(activeStyle);
        }
    }
    
    @FXML
    private void handleLogout() {
        System.exit(0);
    }
}