package aptech.proj_NN_group2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import aptech.proj_NN_group2.util.StringValue;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Tải giao diện đăng nhập
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("auth/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hệ thống Quản lý Sản xuất Kem");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}