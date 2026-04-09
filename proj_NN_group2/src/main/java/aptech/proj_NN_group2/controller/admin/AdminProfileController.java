package aptech.proj_NN_group2.controller.admin;

import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.model.business.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminProfileController {
    @FXML private TextField txtUsername, txtRole;
    @FXML private PasswordField txtNewPassword, txtConfirmPassword;

    private User currentAdmin;
    private UserRepository userRepo = new UserRepository();

    // Hàm này để AdminController truyền dữ liệu Admin đang đăng nhập sang
    public void setAdminData(User admin) {
        this.currentAdmin = admin;
        txtUsername.setText(admin.getUsername());
        txtRole.setText(admin.getRoleName());
    }

    @FXML
    private void handleSave() {
        String newUsername = txtUsername.getText().trim();
        String pass = txtNewPassword.getText().trim();
        String confirm = txtConfirmPassword.getText().trim();

        if (newUsername.isEmpty()) {
            showAlert("Lỗi", "Tên đăng nhập không được để trống!");
            return;
        }

        if (!pass.isEmpty() && !pass.equals(confirm)) {
            showAlert("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            boolean success;
            if (!pass.isEmpty()) {
                String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(pass, org.mindrot.jbcrypt.BCrypt.gensalt());
                String sql = "UPDATE users SET username = ?, password_hash = ? WHERE user_id = ?";
                success = userRepo.executeUpdate(sql, ps -> {
                    ps.setString(1, newUsername);
                    ps.setString(2, hashed);
                    ps.setInt(3, currentAdmin.getUserId());
                });
            } else {
                String sql = "UPDATE users SET username = ? WHERE user_id = ?";
                success = userRepo.executeUpdate(sql, ps -> {
                    ps.setString(1, newUsername);
                    ps.setInt(2, currentAdmin.getUserId());
                });
            }

            if (success) {
                showAlert("Thành công", "Đã cập nhật hồ sơ cá nhân!");
                handleClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleClose() {
        ((Stage) txtUsername.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        new Alert(Alert.AlertType.INFORMATION, content).showAndWait();
    }
}