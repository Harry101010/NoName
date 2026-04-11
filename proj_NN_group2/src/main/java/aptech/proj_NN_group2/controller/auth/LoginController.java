package aptech.proj_NN_group2.controller.auth;

import java.io.IOException;
import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
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
		String user = txtUsername.getText().trim();
		String pass = txtPassword.getText();

		if (user.isEmpty() || pass.isEmpty()) {
			new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!").show();
			return;
		}

		User authenticatedUser = userRepo.login(user);

		if (authenticatedUser == null) {
			new Alert(Alert.AlertType.ERROR, "Tài khoản không tồn tại!").show();
			return;
		}

		if (!authenticatedUser.isActive()) {
			new Alert(Alert.AlertType.ERROR, "Tài khoản của bạn đã bị khóa!").show();
			return;
		}

		// --- ĐOẠN SỬA LẠI CHỈ DÙNG BCRYPT ---
		try {
			String dbHash = authenticatedUser.getPasswordHash() != null ? authenticatedUser.getPasswordHash().trim()
					: "";

			// CHỈ kiểm tra bằng BCrypt, không chấp nhận pass.equals("123456") nữa
			if (org.mindrot.jbcrypt.BCrypt.checkpw(pass, dbHash)) {
				System.out.println("Đăng nhập thành công!");
				switchScene(authenticatedUser);
			} else {
				new Alert(Alert.AlertType.ERROR, "Mật khẩu không chính xác!").show();
			}
		} catch (Exception e) {
			// Lỗi này xảy ra nếu chuỗi trong DB không đúng định dạng BCrypt
			new Alert(Alert.AlertType.ERROR, "Lỗi xác thực dữ liệu. Vui lòng liên hệ Admin để reset mật khẩu!").show();
		}
	}

//    @FXML
//    public void handleLogin() {
//        String user = txtUsername.getText().trim();
//        String pass = txtPassword.getText();
//
//        if (user.isEmpty() || pass.isEmpty()) {
//            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!").show();
//            return;
//        }
//
//        User authenticatedUser = userRepo.login(user);
//        
//        // 1. Kiểm tra tài khoản tồn tại
//        if (authenticatedUser == null) {
//            new Alert(Alert.AlertType.ERROR, "Tài khoản không tồn tại!").show();
//            return;
//        }
//
//        // 2. Kiểm tra trạng thái khóa
//        if (!authenticatedUser.isActive()) {
//            new Alert(Alert.AlertType.ERROR, "Tài khoản của bạn đã bị khóa!").show();
//            return;
//        }
//
//        // 3. LOGIC ĐĂNG NHẬP CƯỠNG BỨC
//        try {
//            String dbHash = authenticatedUser.getPasswordHash() != null ? authenticatedUser.getPasswordHash().trim() : "";
//            
//            // CHẤP NHẬN: Nếu pass gõ vào là 123456 HOẶC khớp BCrypt
//            boolean isPasswordMatch = pass.equals("123456") || 
//                                     (dbHash.startsWith("$2a$") && org.mindrot.jbcrypt.BCrypt.checkpw(pass, dbHash));
//
//            if (isPasswordMatch) {
//                System.out.println("ĐĂNG NHẬP THÀNH CÔNG!");
//                switchScene(authenticatedUser);
//            } else {
//                new Alert(Alert.AlertType.ERROR, "Mật khẩu không chính xác!").show();
//            }
//        } catch (Exception e) {
//            // Nếu lỗi BCrypt xảy ra, vẫn cho phép kiểm tra bằng pass thô để cứu vãn
//            if (pass.equals("123456")) {
//                switchScene(authenticatedUser);
//            } else {
//                new Alert(Alert.AlertType.ERROR, "Lỗi xác thực mật khẩu!").show();
//            }
//        }
//    }

	private void switchScene(User user) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/aptech/proj_NN_group2/admin/user_management.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) txtUsername.getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.setTitle("Hệ thống Quản lý - " + (user.getRoleName() != null ? user.getRoleName() : "User") + ": "
					+ user.getUsername());
			stage.centerOnScreen();
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Không thể tải giao diện quản lý!").show();
		}
	}

	@FXML
	public void handleForgotPassword() {
		 try {
		        FXMLLoader loader = new FXMLLoader(
		                getClass().getResource("/aptech/proj_NN_group2/auth/forgot_password.fxml"));
		        Parent root = loader.load();

		        Stage stage = (Stage) txtUsername.getScene().getWindow();

		        Scene scene = new Scene(root);
		        stage.setScene(scene);
		        stage.setTitle("Quên mật khẩu");
		        stage.centerOnScreen();
		        stage.show();

		    } catch (IOException e) {
		        e.printStackTrace();
		        new Alert(Alert.AlertType.ERROR, "Lỗi khi chuyển sang màn hình quên mật khẩu: " + e.getMessage()).show();
		    }
	}
}

//package aptech.proj_NN_group2.controller;
//
//import java.io.IOException;
//
//import aptech.proj_NN_group2.model.business.repository.UserRepository;
//import aptech.proj_NN_group2.model.entity.User;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//
//public class LoginController {
//    @FXML private TextField txtUsername;
//    @FXML private PasswordField txtPassword;
//    
//    private UserRepository userRepo = new UserRepository();
//
//   
//    @FXML
//    public void handleLogin() {
//    	
//    	System.out.println("MÃ HÓA CHUẨN TRÊN MÁY NÀY: " + org.mindrot.jbcrypt.BCrypt.hashpw("123456", org.mindrot.jbcrypt.BCrypt.gensalt()));
//        
//    	String user = txtUsername.getText().trim();
//        String pass = txtPassword.getText();
//
//        if (user.isEmpty() || pass.isEmpty()) {
//            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!").show();
//            return;
//        }
//
//        User authenticatedUser = userRepo.login(user);
//        
//        if (authenticatedUser != null) {
//            // IN RA ĐỂ KIỂM TRA
//            System.out.println("--- THÔNG TIN TÀI KHOẢN TỪ DB ---");
//            System.out.println("Username: " + authenticatedUser.getUsername());
//            System.out.println("Hash trong DB: [" + authenticatedUser.getPasswordHash() + "]");
//            System.out.println("Độ dài chuỗi Hash: " + (authenticatedUser.getPasswordHash() != null ? authenticatedUser.getPasswordHash().length() : "NULL"));
//            System.out.println("Quyền: " + authenticatedUser.getRoleName());
//            System.out.println("---------------------------------");
//         // Trong LoginController.java
//            System.out.println("Mật khẩu bạn vừa gõ: [" + pass + "]");
//            System.out.println("Hash dùng để so sánh: [" + authenticatedUser.getPasswordHash() + "]");
//
//            boolean isPasswordMatch = org.mindrot.jbcrypt.BCrypt.checkpw(pass, authenticatedUser.getPasswordHash().trim());
//        }
//
//        if (authenticatedUser == null) {
//            new Alert(Alert.AlertType.ERROR, "Tài khoản không tồn tại!").show();
//            return;
//        }
//
//        // 1. Kiểm tra trạng thái khóa trước
//        if (!authenticatedUser.isActive()) {
//            new Alert(Alert.AlertType.ERROR, "Tài khoản của bạn đã bị khóa!").show();
//            return;
//        }
//
//        // 2. Kiểm tra mật khẩu bằng BCrypt bên trong khối try-catch
//        try {
//            boolean isPasswordMatch = org.mindrot.jbcrypt.BCrypt.checkpw(pass, authenticatedUser.getPasswordHash().trim());
//
//            if (isPasswordMatch) {
//                System.out.println("Đăng nhập thành công!");
//                switchScene(authenticatedUser); // Hàm phụ để chuyển màn hình cho gọn code
//            } else {
//                new Alert(Alert.AlertType.ERROR, "Mật khẩu không chính xác!").show();
//            }
//        } catch (IllegalArgumentException e) {
//            // Nếu mật khẩu trong DB là "123456" (chưa băm), BCrypt sẽ nhảy vào đây
//            new Alert(Alert.AlertType.ERROR, "Mật khẩu cũ chưa được mã hóa an toàn. Hãy báo Admin reset mật khẩu!").show();
//        }
//    }
//
//    // Hàm phụ giúp code handleLogin sạch sẽ hơn
//    private void switchScene(User user) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/user_management.fxml"));
//            Parent root = loader.load();
//            Stage stage = (Stage) txtUsername.getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Hệ thống Quản lý - " + user.getRoleName() + ": " + user.getUsername());
//            stage.centerOnScreen();
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    public void handleForgotPassword() {
//        new Alert(Alert.AlertType.INFORMATION, "Vui lòng liên hệ Admin để cấp lại mật khẩu!").show();
//    }
//}