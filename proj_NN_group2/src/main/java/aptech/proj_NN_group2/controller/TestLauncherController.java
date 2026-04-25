package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class TestLauncherController {
    
    @FXML 
    private void toKho(ActionEvent e) { 
        NavigationUtil.goTo(e, StringValue.VIEW_WAREHOUSE_DASHBOARD, "Kho"); 
    }
    
    @FXML 
    private void toSanXuat(ActionEvent e) { 
        NavigationUtil.goTo(e, StringValue.VIEW_MAIN_MENU, "Sản xuất"); 
    }
}