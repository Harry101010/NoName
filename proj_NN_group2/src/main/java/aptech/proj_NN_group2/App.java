package aptech.proj_NN_group2;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/aptech/proj_NN_group2/warehouse_dashboard.fxml"));
        scene = new Scene(root, 900, 700);
        stage.setTitle("Hệ thống Quản lý Sản xuất & Xuất kho");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Hỗ trợ cả absolute path (bắt đầu bằng /) và relative path
        String path = fxml.startsWith("/") ? fxml + ".fxml"
                : "/aptech/proj_NN_group2/" + fxml + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(path));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}