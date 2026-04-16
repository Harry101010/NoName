package aptech.proj_NN_group2.controller.admin;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.DialogUtil;
import aptech.proj_NN_group2.util.SceneManager;
import aptech.proj_NN_group2.util.UserRoleUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserEditController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRole;

    private User user;
    private final UserRepository userRepo = new UserRepository();
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        cbRole.setItems(FXCollections.observableArrayList(UserRoleUtil.editableRoles()));
    }

    public void setUserData(User user) {
        this.user = user;
        txtUsername.setText(user.getUsername());
        cbRole.setValue(user.getRoleName());
        txtPassword.clear();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (user == null) {
            DialogUtil.error(txtUsername, "Lỗi", "Không có dữ liệu nhân viên để cập nhật!");
            return;
        }

        String newUsername = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
        String newPass = txtPassword.getText() != null ? txtPassword.getText().trim() : "";
        String roleName = cbRole.getValue();

        if (newUsername.isEmpty() || roleName == null) {
            DialogUtil.error(txtUsername, "Lỗi", "Tên đăng nhập và Quyền không được để trống!");
            return;
        }

        try {
            int roleId = UserRoleUtil.toRoleId(roleName);
            boolean success;

            if (!newPass.isEmpty()) {
                String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(newPass, org.mindrot.jbcrypt.BCrypt.gensalt());
                String sql = "UPDATE users SET username = ?, role_id = ?, password_hash = ? WHERE user_id = ?";

                success = userRepo.executeUpdate(sql, ps -> {
                    ps.setString(1, newUsername);
                    ps.setInt(2, roleId);
                    ps.setString(3, hashed);
                    ps.setInt(4, user.getUserId());
                });

                if (success) {
                    user.setPasswordHash(hashed);
                }
            } else {
                user.setUsername(newUsername);
                user.setRoleId(roleId);
                success = userRepo.update(user);
            }

            if (success) {
                user.setUsername(newUsername);
                user.setRoleId(roleId);
                saveClicked = true;
                DialogUtil.info(txtUsername, "Thành công", "Đã cập nhật nhân viên!");
                SceneManager.closeWindow(txtUsername);
            } else {
                DialogUtil.error(txtUsername, "Lỗi", "Không thể cập nhật nhân viên!");
            }
        } catch (Exception e) {
            DialogUtil.error(txtUsername, "Lỗi", "Đã xảy ra lỗi khi cập nhật!");
        }
    }

    @FXML
    private void handleCancel() {
        SceneManager.closeWindow(txtUsername);
    }
}