package aptech.proj_NN_group2.controller.admin;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.DialogUtil;
import aptech.proj_NN_group2.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AdminProfileController {

    @FXML private TextField txtUsername, txtRole;
    @FXML private PasswordField txtNewPassword, txtConfirmPassword;

    private User currentAdmin;
    private final UserRepository userRepo = new UserRepository();

    public void setAdminData(User admin) {
        this.currentAdmin = admin;
        txtUsername.setText(admin.getUsername());
        txtRole.setText(admin.getRoleName());
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    @FXML
    private void handleSave() {
        if (currentAdmin == null) {
            DialogUtil.error(txtUsername, "Lỗi", "Không có dữ liệu Admin để cập nhật!");
            return;
        }

        String newUsername = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
        String pass = txtNewPassword.getText() != null ? txtNewPassword.getText().trim() : "";
        String confirm = txtConfirmPassword.getText() != null ? txtConfirmPassword.getText().trim() : "";

        if (newUsername.isEmpty()) {
            DialogUtil.error(txtUsername, "Lỗi", "Tên đăng nhập không được để trống!");
            return;
        }

        if (!pass.isEmpty() && !pass.equals(confirm)) {
            DialogUtil.error(txtUsername, "Lỗi", "Mật khẩu xác nhận không khớp!");
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

                if (success) {
                    currentAdmin.setPasswordHash(hashed);
                }
            } else {
                String sql = "UPDATE users SET username = ? WHERE user_id = ?";
                success = userRepo.executeUpdate(sql, ps -> {
                    ps.setString(1, newUsername);
                    ps.setInt(2, currentAdmin.getUserId());
                });
            }

            if (success) {
                currentAdmin.setUsername(newUsername);
                CurrentUser.setUser(currentAdmin);
                DialogUtil.info(txtUsername, "Thành công", "Đã cập nhật hồ sơ cá nhân!");
                SceneManager.closeWindow(txtUsername);
            } else {
                DialogUtil.error(txtUsername, "Lỗi", "Không thể cập nhật hồ sơ!");
            }
        } catch (Exception e) {
            DialogUtil.error(txtUsername, "Lỗi", "Đã xảy ra lỗi khi cập nhật!");
        }
    }

    @FXML
    private void handleClose() {
        SceneManager.closeWindow(txtUsername);
    }
}