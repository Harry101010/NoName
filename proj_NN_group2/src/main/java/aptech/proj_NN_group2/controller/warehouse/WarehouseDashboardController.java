package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.controller.production.RecipeManagementController;
import aptech.proj_NN_group2.controller.warehouse.WarehouseController;
import aptech.proj_NN_group2.controller.warehouse.ExportRequestController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class WarehouseDashboardController {

    @FXML private TabPane mainTabPane;
    
    // Khai báo đúng tên biến
    @FXML private WarehouseController warehouseController;
    @FXML private ExportRequestController exportRequestController;
    @FXML private RecipeManagementController recipeManagementController;
    @FXML private ExportHistoryController exportHistoryController;

    @FXML
    public void initialize() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> refreshActiveTab(newTab));

        // Ensure the first visible tab has fresh data after the whole scene graph is ready.
        Platform.runLater(() -> refreshActiveTab(mainTabPane.getSelectionModel().getSelectedItem()));
    }

    private void refreshActiveTab(Tab tab) {
        String title = tab.getText();
        
        // Sử dụng đúng tên biến đã khai báo phía trên
        if (title.contains("TỒN KHO")) {
            if (warehouseController != null) warehouseController.handleRefresh(null); 
        } else if (title.contains("XUẤT KHO")) {
            if (exportRequestController != null) exportRequestController.handleRefresh(null);
        } else if (title.contains("ĐỊNH MỨC")) {
            if (recipeManagementController != null) recipeManagementController.refreshAll();
        }
    }
}