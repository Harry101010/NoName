package aptech.proj_NN_group2.controller;

import java.io.IOException;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.CurrentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
	@FXML
	private TextField txtUsername;
	@FXML
	private PasswordField txtPassword;

	private UserRepository userRepo = new UserRepository();

	@FXML
	public void handleLogin() {
		String user = txtUsername.getText();
		String pass = txtPassword.getText();

		// Thêm exception khi ko ghi đủ thông tin
		if (user.isEmpty() || pass.isEmpty()) {
			new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin").show();
			return;
		}

		User authenticatedUser = userRepo.login(user);

		// Thêm exception khi tk ko tồn tại
		if (authenticatedUser == null) {
			new Alert(Alert.AlertType.ERROR, "Tài khoản không tồn tại.").show();
			return;
		}

		if (authenticatedUser != null
				&& org.mindrot.jbcrypt.BCrypt.checkpw(pass, authenticatedUser.getPasswordHash().trim())) {
			//Đánh dấu người dùng hiện tại sau khi login thành công = CurrentUser
			CurrentUser.setUser(authenticatedUser);
			System.out.println("Đăng nhập thành công với quyền: " + authenticatedUser.getRoleName());
			int roleId = authenticatedUser.getRoleId();
			// Phân quyền --Start--
			String fxmlPath = "";
			String title = "";

			switch (roleId) {
			case 1:
				fxmlPath = "/aptech/proj_NN_group2/user_management.fxml";
				title = "Hệ thống quản lý - Admin " + authenticatedUser.getUsername();
				break;
				
			case 4:
				fxmlPath = "/aptech/proj_NN_group2/staff_dashboard.fxml";
				title = "Giao diện kinh doanh " + authenticatedUser.getUsername();
				break;
				
			case 3:
				fxmlPath = "/aptech/proj_NN_group2/warehouse_dashboard.fxml";
				title = "Giao diện kho " + authenticatedUser.getUsername();
				break;

			case 2:
				fxmlPath = "/aptech/proj_NN_group2/produce_dashboard.fxml";
				title = "Giao diện sản xuất " + authenticatedUser.getUsername();
				break;
				
			default:
				new Alert(Alert.AlertType.ERROR, "Quyền truy cập không hợp lệ: " + authenticatedUser.getRoleName()).show();
				return;
			}
			// Phân quyền --End--
			try {
				// 1. Tải file giao diện quản lý
				FXMLLoader loader = new FXMLLoader(
						getClass().getResource(fxmlPath));
				Parent root = loader.load();
				System.out.println("Sau load FXML: " + (System.currentTimeMillis()) + " ms");
				// 2. Lấy Stage (cửa sổ) hiện tại
				Stage stage = (Stage) txtUsername.getScene().getWindow();

				// 3. Đặt Scene mới vào Stage
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.setTitle(title);
				stage.centerOnScreen(); // Đưa cửa sổ ra giữa màn hình
				stage.show();
				System.out.println("Hoàn tất login: " + (System.currentTimeMillis()) + " ms");

			} catch (IOException e) {
				e.printStackTrace();
				new Alert(Alert.AlertType.ERROR, "Lỗi khi chuyển màn hình: " + e.getMessage()).show();
			}
		} else {
			new Alert(Alert.AlertType.ERROR, "Tài khoản hoặc mật khẩu không chính xác!").show();
		}
	}

	@FXML
	public void handleForgotPassword() {
		new Alert(Alert.AlertType.INFORMATION, "Vui lòng liên hệ Admin để cấp lại mật khẩu!").show();
	}
}