package aptech.proj_NN_group2.controller;

import java.io.IOException;

import aptech.proj_NN_group2.App;
import aptech.proj_NN_group2.util.StringValue;
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
}
