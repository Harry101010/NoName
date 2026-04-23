package aptech.proj_NN_group2.controller.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import org.mindrot.jbcrypt.BCrypt;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.EmailUtil;

public class ForgotPasswordController implements Initializable {
	@FXML
	private TextField txtInput;

	@FXML
	private Label lblMessage;

	private final UserRepository userRepository = new UserRepository();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblMessage.setText("");
	}

	@FXML
	private void handleForgotPassword() {
		try {
			lblMessage.setText("");

			String input = txtInput.getText().trim();

			if (input.isEmpty()) {
				lblMessage.setText("Vui lòng nhập username hoặc email");
				return;
			}

			User user = userRepository.findByEmailOrUsername(input);
			if (user == null) {
				lblMessage.setText("Tài khoản không tồn tại.");
				return;
			}

			if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
				lblMessage.setText("Tài khoản chưa có email để khôi phục mật khẩu.");
				return;
			}

			String tempPassword = generateTempPassword();
			String hashedPassword = BCrypt.hashpw(tempPassword, BCrypt.gensalt());

			boolean updated = userRepository.updatePasswordAndSetMustChange(user.getUserId(), hashedPassword);
			if (!updated) {
				lblMessage.setText("Không thể cập nhật mất khẩu tạm.");
				return;
			}

			String subject = "Khôi phục mật khẩu";
			String content = "Xin chào " + user.getUsername() + ". Mật khẩu tạm thời của bạn là: \n" + tempPassword
					+ "\n. Vui lòng đăng nhập và đổi mật khẩu ngay sau đó.";
			boolean sent = EmailUtil.sendEmail(user.getEmail(), subject, content);

			if (sent) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Thông báo");
				alert.setHeaderText(null);
				alert.setContentText("Mật khẩu tạm đã được gửi qua email.");
				alert.showAndWait();
				
				backtoLogin();
			} else {
				lblMessage.setText("Cập nhật mật khẩu thành công nhưng gửi email thất bại.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			lblMessage.setText("Đã xảy ra lỗi hệ thống");
		}
	}

	private String generateTempPassword() {
		String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < 8; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}

		return sb.toString();
	}

	private void backtoLogin() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/auth/login.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) txtInput.getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.setTitle("Đăng nhập hệ thống");
			stage.centerOnScreen();
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			lblMessage.setText("Gửi mail thành công nhưng không thể quay lại màn hình đăng nhập.");
		}

	}
}
