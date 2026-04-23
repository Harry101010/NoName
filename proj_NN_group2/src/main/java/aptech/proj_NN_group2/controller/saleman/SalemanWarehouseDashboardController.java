package aptech.proj_NN_group2.controller.saleman;

import aptech.proj_NN_group2.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class SalemanWarehouseDashboardController {
    @FXML private TabPane mainTabPane;
    
    // Inject các Controller con (tên phải khớp với fx:id trong fxml + "Controller")
    @FXML private SalemanController createIssueController;
    @FXML private FinishedProductWarehouseController warehouseController;

    @FXML
    public void initialize() {
        // Lắng nghe sự kiện chuyển Tab để tự động làm mới dữ liệu
        mainTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            if (newIdx.intValue() == 0) {
                if (createIssueController != null) createIssueController.refreshHistory();
            } else if (newIdx.intValue() == 1) {
                if (warehouseController != null) warehouseController.refreshData();
            }
        });
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
    	NavigationUtil.logout(event);
    }
    
    @FXML
    private void goToProfile(ActionEvent event) {
    	NavigationUtil.toAccountProfile(event);
    }
}