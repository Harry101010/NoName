package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private void goToCreateBatch(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_CREATE_BATCH, "Tạo mẻ sản xuất");
    }

    @FXML
    private void goToProductionProcess(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_PRODUCTION_PROCESS, "Quản lý quy trình sản xuất");
    }
}