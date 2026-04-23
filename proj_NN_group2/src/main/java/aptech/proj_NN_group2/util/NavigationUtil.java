package aptech.proj_NN_group2.util;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
	
	public static void toAccountProfile(ActionEvent event) {
	    try {
	        FXMLLoader loader = new FXMLLoader(
	                NavigationUtil.class.getResource("/aptech/proj_NN_group2/auth/account_profile.fxml"));
	        Parent root = loader.load();

	        Stage stage = new Stage();
	        stage.setScene(new Scene(root));
	        stage.setTitle("Thông tin tài khoản");
	        stage.centerOnScreen();
	        stage.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	        new Alert(Alert.AlertType.ERROR, "Không thể mở trang Thông tin tài khoản!").show();
	    }
	}

	public static void toForgotPassword(ActionEvent event) {
		try {
	        FXMLLoader loader = new FXMLLoader(
	                NavigationUtil.class.getResource("/aptech/proj_NN_group2/auth/forgot_password.fxml"));
	        Parent root = loader.load();

	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        stage.setScene(new Scene(root));
	        stage.setTitle("Quên mật khẩu");
	        stage.centerOnScreen();
	        stage.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	        new Alert(Alert.AlertType.ERROR, "Không thể mở màn hình quên mật khẩu!").show();
	    }
	}
}
