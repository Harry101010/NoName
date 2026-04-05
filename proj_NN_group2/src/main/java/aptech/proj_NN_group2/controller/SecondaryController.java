package aptech.proj_NN_group2.controller;

import java.io.IOException;

import aptech.proj_NN_group2.App;
import javafx.fxml.FXML;

public class SecondaryController {
    // ldsjf;saldjfa;sldfkjas;d
    @FXML
    public void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
