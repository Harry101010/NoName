package aptech.proj_NN_group2.controller.auth;

import org.mindrot.jbcrypt.BCrypt;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.CurrentUser;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountProfileController {

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtRole;
    
    @FXML
    private PasswordField txtCurrentPassword;
    
    @FXML
    private PasswordField txtNewPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    private final UserRepository userRepo = new UserRepository();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = CurrentUser.getUser();

        if (currentUser != null) {
            txtUsername.setText(currentUser.getUsername());
            txtRole.setText(currentUser.getRoleName());
        }
    }

    @FXML
    public void handleSave() {
        if (currentUser == null) {
            showError("Không tìm thấy thông tin tài khoản hiện tại!");
            return;
        }
        
        String currentPassword = txtCurrentPassword.getText().trim();
        String newUsername = txtUsername.getText().trim();
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        if (newUsername.isEmpty()) {
            showError("Tên đăng nhập không được để trống!");
            return;
        }

        if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
        	if (currentPassword.isEmpty()) {
                showError("Vui lòng nhập mật khẩu hiện tại!");
                return;
            }

            String dbHash = currentUser.getPasswordHash() != null
                    ? currentUser.getPasswordHash().trim()
                    : "";

            if (!BCrypt.checkpw(currentPassword, dbHash)) {
                showError("Mật khẩu hiện tại không chính xác!");
                return;
            }

            if (newPassword.length() < 6) {
                showError("Mật khẩu mới phải có ít nhất 6 ký tự!");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showError("Xác nhận mật khẩu không khớp!");
                return;
            }
            
            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            
            boolean updatedPassword = userRepo.updatePasswordAndClearMustChange(currentUser.getUserId(), newHash);
            if (!updatedPassword) {
            	showError("Không thể cập nhật mật khẩu!");
            	return;
            }
            currentUser.setPasswordHash(newHash);
        }

        currentUser.setUsername(newUsername);

        showInfo("Cập nhật thông tin tài khoản thành công!");
        closeWindow();
    }

    @FXML
    public void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }

    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }
}