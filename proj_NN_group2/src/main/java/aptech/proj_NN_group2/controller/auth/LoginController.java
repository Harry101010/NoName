package aptech.proj_NN_group2.controller.auth;


import java.io.IOException;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
//<<<<<<< HEAD
//
//    @FXML
//    private TextField txtUsername;
//
//    @FXML
//    private PasswordField txtPassword;
//
//    private final UserRepository userRepo = new UserRepository();
//
//    @FXML
//    public void handleLogin() {
//        String username = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
//        String password = txtPassword.getText() != null ? txtPassword.getText() : "";
//
//        if (username.isEmpty() || password.isEmpty()) {
//            DialogUtil.warning(txtUsername, "Đăng nhập", "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
//            return;
//        }
//
//        User authenticatedUser = userRepo.login(username);
//
//        if (authenticatedUser == null) {
//            DialogUtil.error(txtUsername, "Đăng nhập thất bại", "Tài khoản không tồn tại!");
//            return;
//        }
//
//        if (!authenticatedUser.isActive()) {
//            DialogUtil.error(txtUsername, "Đăng nhập thất bại", "Tài khoản của bạn đã bị khóa!");
//            return;
//        }
//
//        try {
//        	String dbHash = authenticatedUser.getPasswordHash();
//
//            if (!org.mindrot.jbcrypt.BCrypt.checkpw(password, dbHash)) {
//                DialogUtil.error(txtUsername, "Đăng nhập thất bại", "Mật khẩu không chính xác!");
//                return;
//            }
//
//            String homeView = resolveHomeView(authenticatedUser);
//            if (homeView == null) {
//                DialogUtil.error(
//                        txtUsername,
//                        "Đăng nhập thất bại",
//                        "Vai trò '" + authenticatedUser.getRoleName() + "' chưa được cấu hình màn hình chính."
//                );
//                return;
//            }
//
//            CurrentUser.setUser(authenticatedUser);
//
//            String roleLabel = authenticatedUser.getRoleName() != null && !authenticatedUser.getRoleName().isBlank()
//                    ? authenticatedUser.getRoleName().trim()
//                    : "User";
//
//            String title = "Hệ thống Quản lý - " + roleLabel + ": " + authenticatedUser.getUsername();
//            NavigationUtil.goTo(txtUsername, homeView, title);
//        } catch (Exception e) {
//            DialogUtil.error(
//                    txtUsername,
//                    "Đăng nhập thất bại",
//                    "Lỗi xác thực dữ liệu. Vui lòng liên hệ Admin để reset mật khẩu!"
//            );
//        }
//    }
//
//    @FXML
//    public void handleForgotPassword() {
//        NavigationUtil.goTo(txtUsername, StringValue.VIEW_FORGOT_PASSWORD, "Quên mật khẩu");
//    }
//
//    private String resolveHomeView(User user) {
//        if (user == null) {
//            return null;
//        }
//
//        int roleId = user.getRoleId();
//        String roleName = user.getRoleName() != null ? user.getRoleName().trim() : "";
//
//        if (roleId == UserRoleUtil.ROLE_ADMIN || UserRoleUtil.ADMIN.equalsIgnoreCase(roleName)) {
//            return StringValue.VIEW_USER_MANAGEMENT;
//        }
//
//        if (roleId == UserRoleUtil.ROLE_PRODUCTION_MANAGER
//                || UserRoleUtil.PRODUCTION_MANAGER.equalsIgnoreCase(roleName)) {
//            return StringValue.VIEW_MAIN_MENU;
//        }
//
//        if (roleId == UserRoleUtil.ROLE_WAREHOUSE_MANAGER
//                || UserRoleUtil.WAREHOUSE_MANAGER.equalsIgnoreCase(roleName)) {
//            return StringValue.VIEW_WAREHOUSE_DASHBOARD;
//        }
//
//        if (roleId == UserRoleUtil.ROLE_SALESMAN
//                || UserRoleUtil.SALESMAN.equalsIgnoreCase(roleName)) {
//            return StringValue.VIEW_SALEMAN_WAREHOUSE_DASHBOARD;
//        }
//
//        return null;
//    }
//=======
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
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin").show();
            return;
        }

        User authenticatedUser = userRepo.login(user);

        if (authenticatedUser == null) {
            new Alert(Alert.AlertType.ERROR, "Tài khoản không tồn tại.").show();
            return;
        }

        if (!authenticatedUser.isActive()) {
            new Alert(Alert.AlertType.ERROR, "Tài khoản của bạn đã bị khóa!").show();
            return;
        }

        try {
            String dbHash = authenticatedUser.getPasswordHash() != null
                    ? authenticatedUser.getPasswordHash().trim()
                    : "";

            if (!org.mindrot.jbcrypt.BCrypt.checkpw(pass, dbHash)) {
                new Alert(Alert.AlertType.ERROR, "Tài khoản hoặc mật khẩu không chính xác!").show();
                return;
            }

            CurrentUser.setUser(authenticatedUser);
            System.out.println("Đăng nhập thành công với quyền: " + authenticatedUser.getRoleName());

            if (authenticatedUser.isMustChangePassword()) {
                openMustChangePasswordScreen(authenticatedUser);
            } else {
                switchScene(authenticatedUser);
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Lỗi xác thực dữ liệu. Vui lòng liên hệ Admin để reset mật khẩu!").show();
        }
    }

    private void switchScene(User authenticatedUser) {
        int roleId = authenticatedUser.getRoleId();
        String fxmlPath = "";
        String title = "";

        switch (roleId) {
            case 1:
                fxmlPath = "/aptech/proj_NN_group2/admin/user_management.fxml";
                title = "Hệ thống quản lý - Admin " + authenticatedUser.getUsername();
                break;

            case 2:
                fxmlPath = "/aptech/proj_NN_group2/production/main_menu.fxml";
                title = "Giao diện sản xuất " + authenticatedUser.getUsername();
                break;

            case 3:
                fxmlPath = "/aptech/proj_NN_group2/warehouse/warehouse_dashboard.fxml";
                title = "Giao diện kho " + authenticatedUser.getUsername();
                break;

            case 4:
                fxmlPath = "/aptech/proj_NN_group2/sales/saleman_warehouse_dashboard.fxml";
                title = "Giao diện kinh doanh " + authenticatedUser.getUsername();
                break;

            default:
                new Alert(Alert.AlertType.ERROR,
                        "Quyền truy cập không hợp lệ: " + authenticatedUser.getRoleName()).show();
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi chuyển màn hình: " + e.getMessage()).show();
        }
    }

    private void openChangePasswordScreen(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/aptech/proj_NN_group2/change_password.fxml"));
            Parent root = loader.load();

            ChangePasswordController controller = loader.getController();
            controller.setCurrentUser(user);
            controller.setMode(ChangePasswordController.Mode.NORMAL);

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Đổi mật khẩu");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Không thể mở màn hình đổi mật khẩu: " + e.getMessage()).show();
        }
    }
    
    private void openMustChangePasswordScreen(User authenticatedUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/auth/change_password.fxml"));
            Parent root = loader.load();

            ChangePasswordController controller = loader.getController();
            controller.setCurrentUser(authenticatedUser);
            controller.setMode(ChangePasswordController.Mode.FORCE_AFTER_FORGOT);

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đổi mật khẩu");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể mở màn hình đổi mật khẩu!").show();
        }
    }
    
    //gọi hàm quên mk bên navigation util
    @FXML
    public void handleForgotPassword(ActionEvent event) {
        NavigationUtil.toForgotPassword(event);
    }
}