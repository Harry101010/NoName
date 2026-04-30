package aptech.proj_NN_group2.controller.auth;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ChangePasswordController {

    public enum Mode {
        NORMAL,
        FORCE_AFTER_FORGOT
    }

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblDescription;

    @FXML
    private PasswordField txtNewPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    private final UserRepository userRepo = new UserRepository();

    private User currentUser;
    private Mode mode = Mode.NORMAL;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        updateUIByMode();
    }

    @FXML
    public void initialize() {
        updateUIByMode();
    }

    private void updateUIByMode() {
        if (lblTitle == null || lblDescription == null) {
            return;
        }

        if (mode == Mode.FORCE_AFTER_FORGOT) {
            lblTitle.setText("Đổi mật khẩu bắt buộc");
            lblDescription.setText("Bạn đang đăng nhập bằng mật khẩu tạm. Vui lòng đổi mật khẩu để tiếp tục.");
        } else {
            lblTitle.setText("Đổi mật khẩu");
            lblDescription.setText("Vui lòng nhập mật khẩu mới.");
        }
    }

    @FXML
    public void handleChangePassword() {
        if (currentUser == null) {
            showError("Không tìm thấy thông tin người dùng hiện tại!");
            return;
        }

        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showWarning("Vui lòng nhập đầy đủ mật khẩu mới và xác nhận mật khẩu!");
            return;
        }

        if (newPassword.length() < 6) {
            showWarning("Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            boolean updated;

            if (mode == Mode.FORCE_AFTER_FORGOT) {
                updated = userRepo.updatePasswordAndClearMustChange(
                        currentUser.getUserId(),
                        newHashedPassword
                );
            } else {
                updated = userRepo.updatePassword(
                        currentUser.getUserId(),
                        newHashedPassword
                );
            }

            if (!updated) {
                showError("Đổi mật khẩu thất bại!");
                return;
            }

            currentUser.setPasswordHash(newHashedPassword);
            currentUser.setMustChangePassword(false);

            showInfo("Đổi mật khẩu thành công!");

            if (mode == Mode.FORCE_AFTER_FORGOT) {
                openDashboardByRole(currentUser);
            } else {
                closeWindow();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Có lỗi xảy ra khi đổi mật khẩu!");
        }
    }

    private void openDashboardByRole(User user) {
        try {
            String fxmlPath = "";
            String title = "";

            switch (user.getRoleId()) {
                case 1:
                    fxmlPath = "/aptech/proj_NN_group2/admin/user_management.fxml";
                    title = "Hệ thống quản lý - Admin " + user.getUsername();
                    break;
                case 2:
                    fxmlPath = "/aptech/proj_NN_group2/production/main_menu.fxml";
                    title = "Giao diện sản xuất " + user.getUsername();
                    break;
                case 3:
                    fxmlPath = "/aptech/proj_NN_group2/warehhouse/warehouse_dashboard.fxml";
                    title = "Giao diện kho " + user.getUsername();
                    break;
                case 4:
                    fxmlPath = "/aptech/proj_NN_group2/sales/saleman_warehouse_dashboard.fxml";
                    title = "Giao diện kinh doanh " + user.getUsername();
                    break;
                default:
                    showError("Không xác định được quyền người dùng!");
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) txtNewPassword.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở màn hình chính!");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) txtNewPassword.getScene().getWindow();
        stage.close();
    }

    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }

    private void showWarning(String message) {
        new Alert(Alert.AlertType.WARNING, message).show();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }
}