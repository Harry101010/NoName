package aptech.proj_NN_group2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    
    // Các biến này phải trùng tên với fx:id bạn đặt trong Scene Builder
    @FXML private Button btnDashboard;
    @FXML private Button btnSanXuat;
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

    @FXML
    private void showKhoNguyenLieu() {
        // Nạp file view mới thay vì dùng chung với Dashboard
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

    // Hàm xử lý đổi màu nút bấm
    private void setButtonActive(Button activeBtn) {
        // Style cho nút đang chọn (Nền xanh đậm, chữ trắng)
        String activeStyle = "-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 8; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;";
        // Style cho nút bình thường (Nền trong suốt, chữ xám xanh)
        String normalStyle = "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;";

        // Trả tất cả về bình thường
        if (btnDashboard != null) btnDashboard.setStyle(normalStyle);
        if (btnSanXuat != null) btnSanXuat.setStyle(normalStyle);
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