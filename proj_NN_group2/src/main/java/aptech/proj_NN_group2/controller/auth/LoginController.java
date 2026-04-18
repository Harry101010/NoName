package aptech.proj_NN_group2.controller.auth;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.DialogUtil;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import aptech.proj_NN_group2.util.ThemeManager;
import aptech.proj_NN_group2.util.UserRoleUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private final UserRepository userRepo = new UserRepository();

    @FXML
    public void handleLogin() {
        String username = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText() : "";

        if (username.isEmpty() || password.isEmpty()) {
            DialogUtil.warning(txtUsername, "Đăng nhập", "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        User authenticatedUser = userRepo.login(username);

        if (authenticatedUser == null) {
            DialogUtil.error(txtUsername, "Đăng nhập thất bại", "Tài khoản không tồn tại!");
            return;
        }

        if (!authenticatedUser.isActive()) {
            DialogUtil.error(txtUsername, "Đăng nhập thất bại", "Tài khoản của bạn đã bị khóa!");
            return;
        }

        try {
            String dbHash = authenticatedUser.getPasswordHash() != null ? authenticatedUser.getPasswordHash().trim() : "";

            if (!org.mindrot.jbcrypt.BCrypt.checkpw(password, dbHash)) {
                DialogUtil.error(txtUsername, "Đăng nhập thất bại", "Mật khẩu không chính xác!");
                return;
            }

            String homeView = resolveHomeView(authenticatedUser);
            if (homeView == null) {
                DialogUtil.error(
                        txtUsername,
                        "Đăng nhập thất bại",
                        "Vai trò '" + authenticatedUser.getRoleName() + "' chưa được cấu hình màn hình chính."
                );
                return;
            }

            CurrentUser.setUser(authenticatedUser);

            String roleLabel = authenticatedUser.getRoleName() != null && !authenticatedUser.getRoleName().isBlank()
                    ? authenticatedUser.getRoleName().trim()
                    : "User";

            String title = "Hệ thống Quản lý - " + roleLabel + ": " + authenticatedUser.getUsername();
            NavigationUtil.goTo(txtUsername, homeView, title);
        } catch (Exception e) {
            DialogUtil.error(
                    txtUsername,
                    "Đăng nhập thất bại",
                    "Lỗi xác thực dữ liệu. Vui lòng liên hệ Admin để reset mật khẩu!"
            );
        }
    }

    @FXML
    public void handleForgotPassword() {
        NavigationUtil.goTo(txtUsername, StringValue.VIEW_FORGOT_PASSWORD, "Quên mật khẩu");
    }

    private String resolveHomeView(User user) {
        if (user == null) {
            return null;
        }

        int roleId = user.getRoleId();
        String roleName = user.getRoleName() != null ? user.getRoleName().trim() : "";

        if (roleId == UserRoleUtil.ROLE_ADMIN || UserRoleUtil.ADMIN.equalsIgnoreCase(roleName)) {
            return StringValue.VIEW_USER_MANAGEMENT;
        }

        if (roleId == UserRoleUtil.ROLE_PRODUCTION_MANAGER
                || UserRoleUtil.PRODUCTION_MANAGER.equalsIgnoreCase(roleName)) {
            return StringValue.VIEW_MAIN_MENU;
        }

        if (roleId == UserRoleUtil.ROLE_WAREHOUSE_MANAGER
                || UserRoleUtil.WAREHOUSE_MANAGER.equalsIgnoreCase(roleName)) {
            return StringValue.VIEW_WAREHOUSE_DASHBOARD;
        }

        if (roleId == UserRoleUtil.ROLE_SALESMAN
                || UserRoleUtil.SALESMAN.equalsIgnoreCase(roleName)) {
            return StringValue.VIEW_SALEMAN_WAREHOUSE_DASHBOARD;
        }

        return null;
    }
}