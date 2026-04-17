package aptech.proj_NN_group2.util;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class NavigationUtil {
	public static void logout(ActionEvent event) {	
		try {
			// Xóa thông tin user hiện tại khi đăng xuất
			CurrentUser.clear();

			Parent root = FXMLLoader.load(NavigationUtil.class.getResource("/aptech/proj_NN_group2/auth/login.fxml"));

			MenuItem menuItem = (MenuItem) event.getSource();
			Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();
			stage.setScene(new Scene(root));			
			stage.setTitle("Đăng nhập");
			stage.centerOnScreen();
			stage.show();
		} catch (IOException e) {
			   e.printStackTrace();
	            new Alert(Alert.AlertType.ERROR, "Lỗi khi đăng xuất: " + e.getMessage()).show();
	       }
	}
}
