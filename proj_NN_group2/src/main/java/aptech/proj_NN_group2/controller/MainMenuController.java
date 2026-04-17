package aptech.proj_NN_group2.controller;


import java.io.IOException;

import aptech.proj_NN_group2.App;

import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class MainMenuController {

    @FXML
    private void goToCreateBatch() throws IOException {
        App.setRoot(StringValue.VIEW_CREATE_BATCH);
    }

    @FXML
    private void goToProductionProcess() throws IOException {
        App.setRoot(StringValue.VIEW_PRODUCTION_PROCESS);
    }

    @FXML
    private void goToIngredientIssueRequest() throws IOException {
        App.setRoot(StringValue.VIEW_INGREDIENT_ISSUE_REQUEST);
    }

    @FXML
    private void goToConfirmReceivedIngredient() throws IOException {
        App.setRoot(StringValue.VIEW_CONFIRM_RECEIVED_INGREDIENT);
    }

    @FXML
    private void goToStageDetail() throws IOException {
        App.setRoot(StringValue.VIEW_STAGE_DETAIL);
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
    	NavigationUtil.logout(event);
    }
}
