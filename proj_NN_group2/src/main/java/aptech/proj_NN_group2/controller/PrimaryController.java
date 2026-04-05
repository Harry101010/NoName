package aptech.proj_NN_group2.controller;

import java.io.IOException;

import aptech.proj_NN_group2.App;
import javafx.fxml.FXML;

public class PrimaryController {
	// abcj
    @FXML
    public void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
