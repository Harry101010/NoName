package aptech.proj_NN_group2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX Appdfasdlkfjadslkfj;asldádsadsadsa
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
    	// Cách viết chuẩn xác dựa trên cây thư mục của bạn
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/MainShell.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hệ Thống Quản Lý Sản Xuất Kem v1.0");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
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
