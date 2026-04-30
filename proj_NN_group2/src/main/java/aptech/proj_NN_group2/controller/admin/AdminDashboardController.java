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
        	
        	String path = "/aptech/proj_NN_group2/production/ProductionDashboard.fxml";
        	java.net.URL url = getClass().getResource(path);
        	System.out.println("Debug Path: " + path);
        	System.out.println("Resource found? " + (url != null));

        	if (url == null) {
        	    System.out.println("LỖI: Java không tìm thấy file FXML tại đường dẫn trên!");
        	}
        	
            FXMLLoader loader = SceneManager.createLoader(fxmlPath);
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
        	// IN RA LỖI CHI TIẾT VÀO CONSOLE
            System.err.println("LỖI CHI TIẾT TẠI ĐÂY:");
            e.printStackTrace(); // Dòng này cực quan trọng
            
            // Nếu có nguyên nhân sâu xa
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
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