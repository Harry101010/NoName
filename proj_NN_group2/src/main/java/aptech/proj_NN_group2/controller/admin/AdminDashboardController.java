package aptech.proj_NN_group2.controller.admin;

import java.io.IOException;

import aptech.proj_NN_group2.util.DialogUtil;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.SceneManager;
import aptech.proj_NN_group2.util.StringValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class AdminDashboardController {

    @FXML private StackPane contentArea;

    // Phương thức load FXML và đẩy vào StackPane
    private void switchView(String fxmlPath) {
        try {
            FXMLLoader loader = SceneManager.createLoader(fxmlPath);
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            DialogUtil.error("Lỗi hệ thống", "Không thể tải giao diện: " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML private void toUserManagement() { switchView(StringValue.VIEW_USER_MANAGEMENT); }
    
    @FXML private void toWarehouse() { switchView(StringValue.VIEW_WAREHOUSE_DASHBOARD); }
    
    @FXML private void toProduction() { switchView(StringValue.VIEW_MAIN_MENU); }
    
    @FXML private void toSales() { switchView(StringValue.VIEW_SALEMAN_WAREHOUSE_DASHBOARD); }

    @FXML private void handleLogout(ActionEvent e) {
        NavigationUtil.logout(e);
    }
    @FXML
    public void toProduction(ActionEvent event) {
        // Gọi đến file Master này, bỏ qua trang menu nút bấm trung gian
        switchView(StringValue.VIEW_PRODUCTION_DASHBOARD); 
    }
}