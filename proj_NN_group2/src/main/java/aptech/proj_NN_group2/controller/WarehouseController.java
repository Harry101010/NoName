package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WarehouseController {
@FXML
public void handleLogout(ActionEvent event) {
	NavigationUtil.logout(event);
}
}
