package aptech.proj_NN_group2.controller;

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
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    
    private UserRepository userRepo = new UserRepository();

//    @FXML
//    public void handleLogin() {
//        String user = txtUsername.getText();
//        String pass = txtPassword.getText();
//        
//        String hashVuaTao = org.mindrot.jbcrypt.BCrypt.hashpw("123456", org.mindrot.jbcrypt.BCrypt.gensalt());
//        System.out.println("COPY MA NAY: " + hashVuaTao);
//        
//     // THỬ NGHIỆM: Nếu nhập admin/admin thì cho qua luôn
//        if (user.equals("admin") && pass.equals("123")) {
//            System.out.println("Đăng nhập thử nghiệm thành công!");
//            return; 
//        }
//        
//        
//        
//        if (user.isEmpty() || pass.isEmpty()) {
//            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin!").show();
//            return;
//        }
//
//        User authenticatedUser = userRepo.login(user);
//        
//        if (authenticatedUser != null && BCrypt.checkpw(pass, authenticatedUser.getPasswordHash())) {
//            System.out.println("Đăng nhập thành công với quyền: " + authenticatedUser.getRoleName());
//            // Sau này sẽ thêm code chuyển màn hình tại đây
//        } else {
//            new Alert(Alert.AlertType.ERROR, "Sai tài khoản hoặc mật khẩu!").show();
//        }
//    }
    
    @FXML
    public void handleLogin() {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();

        User authenticatedUser = userRepo.login(user);

        if (authenticatedUser != null && org.mindrot.jbcrypt.BCrypt.checkpw(pass, authenticatedUser.getPasswordHash().trim())) {
            System.out.println("Đăng nhập thành công với quyền: " + authenticatedUser.getRoleName());
            
            try {
                // 1. Tải file giao diện quản lý
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/user_management.fxml"));
                Parent root = loader.load();
                
                // 2. Lấy Stage (cửa sổ) hiện tại
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                
                // 3. Đặt Scene mới vào Stage
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Hệ thống Quản lý - Admin: " + authenticatedUser.getUsername());
                stage.centerOnScreen(); // Đưa cửa sổ ra giữa màn hình
                stage.show();
                
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