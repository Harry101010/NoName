package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.controller.production.RecipeManagementController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class WarehouseDashboardController {

    @FXML private TabPane mainTabPane;

    @FXML private Tab tabRecipeManagement;
    @FXML private Tab tabExportRequests;
    @FXML private Tab tabExportHistory;
    @FXML private Tab tabWarehouse;

    // Included controllers from tabbed_warehouse.fxml
    @FXML private WarehouseController mainWarehouseController;
    @FXML private ExportRequestController exportRequestsController;
    @FXML private RecipeManagementController recipeManagementController;
    @FXML private ExportHistoryController exportHistoryController;

    @FXML
    public void initialize() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> refreshActiveTab(newTab));

        // Ensure the first visible tab has fresh data after the whole scene graph is ready.
        Platform.runLater(() -> refreshActiveTab(mainTabPane.getSelectionModel().getSelectedItem()));
    }

    private void refreshActiveTab(Tab tab) {
        if (tab == null) {
            return;
        }

        if (tab == tabWarehouse) {
            if (mainWarehouseController != null) {
                mainWarehouseController.handleRefresh(null);
            }
            return;
        }

        if (tab == tabExportRequests) {
            if (exportRequestsController != null) {
                exportRequestsController.handleRefresh(null);
            }
            return;
        }

        if (tab == tabRecipeManagement) {
            if (recipeManagementController != null) {
                recipeManagementController.refreshAll(null);
            }
            return;
        }

        if (tab == tabExportHistory && exportHistoryController != null) {
            exportHistoryController.refreshHistory();
        }
    }
}