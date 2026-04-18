package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WarehouseController {

    @FXML
    private void handleAdd(ActionEvent event) {
        goToActualWarehouse(event);
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        goToActualWarehouse(event);
    }

    @FXML
    private void handleExport(ActionEvent event) {
        goToActualWarehouse(event);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        goToActualWarehouse(event);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }

    private void goToActualWarehouse(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_WAREHOUSE_DASHBOARD, "Quản lý kho nguyên liệu");
    }
}