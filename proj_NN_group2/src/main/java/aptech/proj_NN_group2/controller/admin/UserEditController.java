package aptech.proj_NN_group2.controller.admin;

import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.model.business.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UserEditController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRole;

    private User user;
    private UserRepository userRepo = new UserRepository();
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        // Nạp danh sách quyền (giống bên AdminController nhưng không có Admin)
        cbRole.setItems(FXCollections.observableArrayList(
            "Trưởng sản xuất", "Quản lý kho", "Nhân viên kinh doanh", "Staff"
        ));
    }

    // Hàm nhận dữ liệu từ AdminController truyền sang
    public void setUserData(User user) {
        this.user = user;
        txtUsername.setText(user.getUsername());
        cbRole.setValue(user.getRoleName());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        String newUsername = txtUsername.getText().trim();
        String newPass = txtPassword.getText().trim();
        String roleName = cbRole.getValue();

        if (newUsername.isEmpty() || roleName == null) {
            new Alert(Alert.AlertType.ERROR, "Tên đăng nhập và Quyền không được để trống!").show();
            return;
        }

        try {
            boolean success;
            int roleId = convertRoleToId(roleName);

            if (!newPass.isEmpty()) {
                // Cập nhật cả mật khẩu mới
                String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(newPass, org.mindrot.jbcrypt.BCrypt.gensalt());
                String sql = "UPDATE users SET username = ?, role_id = ?, password_hash = ? WHERE user_id = ?";
                success = userRepo.executeUpdate(sql, ps -> {
                    ps.setString(1, newUsername);
                    ps.setInt(2, roleId);
                    ps.setString(3, hashed);
                    ps.setInt(4, user.getUserId());
                });
            } else {
                // Chỉ cập nhật tên và quyền
                user.setUsername(newUsername);
                user.setRoleId(roleId);
                success = userRepo.update(user);
            }

            if (success) {
                saveClicked = true;
                closeStage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }

    private int convertRoleToId(String roleName) {
        if (roleName == null) return 5;
        switch (roleName) {
            case "Trưởng sản xuất": return 2;
            case "Quản lý kho": return 3;
            case "Nhân viên kinh doanh": return 4;
            default: return 5; // Staff
        }
    }
}