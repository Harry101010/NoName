package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.controller.production.RecipeManagementController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class WarehouseDashboardController {

    @FXML private TabPane mainTabPane;
    
    // Inject 3 controller con (lưu ý fx:id trong FXML phải trùng tên biến + "Controller")
    @FXML private WarehouseController mainWarehouseController; 
    @FXML private ExportRequestController exportRequestsController;
    @FXML private RecipeManagementController recipeManagementController;

    @FXML
    public void initialize() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                refreshActiveTab(newTab);
            }
        });
    }

    private void refreshActiveTab(Tab tab) {
        String title = tab.getText();
        if (title.contains("TỒN KHO")) {
            if (mainWarehouseController != null) mainWarehouseController.handleRefresh(null);
        } else if (title.contains("XUẤT KHO")) {
            if (exportRequestsController != null) exportRequestsController.handleRefresh(null);
        } else if (title.contains("ĐỊNH MỨC")) {
            if (recipeManagementController != null) recipeManagementController.refreshAll(null);
        }
    }
}