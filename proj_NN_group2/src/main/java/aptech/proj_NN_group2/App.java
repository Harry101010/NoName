package aptech.proj_NN_group2;

import java.io.IOException;

import aptech.proj_NN_group2.util.SceneManager;
import aptech.proj_NN_group2.util.StringValue;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        SceneManager.show(primaryStage, StringValue.VIEW_LOGIN, "Hệ thống Quản lý Sản xuất & Xuất kho");
    }

    public static void setRoot(String fxmlPath) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been initialized yet.");
        }
        SceneManager.switchScene(primaryStage, fxmlPath, primaryStage.getTitle());
    }

    public static void setRoot(String fxmlPath, String title) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been initialized yet.");
        }
        SceneManager.switchScene(primaryStage, fxmlPath, title);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}