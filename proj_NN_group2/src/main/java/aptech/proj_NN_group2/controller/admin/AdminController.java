package aptech.proj_NN_group2.controller.admin;

import java.io.IOException;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.DialogUtil;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.SceneManager;
import aptech.proj_NN_group2.util.StringValue;
import aptech.proj_NN_group2.util.UserRoleUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Boolean> colStatus;

    @FXML private TextField txtNewUser;
    @FXML private PasswordField txtNewPass;
    @FXML private ComboBox<String> cbRole;
    @FXML private Label lblWelcome;

    private final UserRepository userRepo = new UserRepository();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("roleName"));

        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colStatus.setCellFactory(column -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                if (item) {
                    setText("Hoạt động");
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                } else {
                    setText("Đã khóa");
                    setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });

        cbRole.setItems(FXCollections.observableArrayList(UserRoleUtil.editableRoles()));

        if (CurrentUser.isLoggedIn()) {
            lblWelcome.setText("Xin chào, " + CurrentUser.getUsername());
        }

        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNewUser.setText(newSelection.getUsername());
                cbRole.setValue(newSelection.getRoleName());
                txtNewPass.clear();
            }
        });

        loadUserData();
    }

    private void loadUserData() {
        userList.setAll(userRepo.findAll());
        tableUsers.setItems(userList);
    }

    @FXML
    public void handleLogout() {
        NavigationUtil.logout(lblWelcome);
    }

    @FXML
    public void addUser() {
        String username = txtNewUser.getText() != null ? txtNewUser.getText().trim() : "";
        String password = txtNewPass.getText() != null ? txtNewPass.getText().trim() : "";
        String roleName = cbRole.getValue();

        if (username.isEmpty() || password.isEmpty() || roleName == null) {
            DialogUtil.warning(tableUsers, "Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRoleId(UserRoleUtil.toRoleId(roleName));

        if (userRepo.create(newUser)) {
            DialogUtil.info(tableUsers, "Thành công", "Đã tạo tài khoản cho: " + username);
            loadUserData();
            clearFields();
        } else {
            DialogUtil.error(tableUsers, "Lỗi", "Không thể tạo tài khoản. Có thể tên đăng nhập bị trùng!");
        }
    }

    @FXML
    public void toggleStatus() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            DialogUtil.warning(tableUsers, "Thông báo", "Vui lòng chọn một nhân viên!");
            return;
        }

        if (isProtectedAccount(selectedUser)) {
            DialogUtil.warning(tableUsers, "Cảnh báo", "Bạn không thể khóa tài khoản quản trị hệ thống!");
            return;
        }

        boolean success = userRepo.toggleActive(selectedUser.getUserId(), !selectedUser.isActive());
        if (success) {
            loadUserData();
            clearFields();
        } else {
            DialogUtil.error(tableUsers, "Lỗi", "Không thể cập nhật trạng thái!");
        }
    }

    @FXML
    public void handleDeleteUser() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtil.warning(tableUsers, "Thông báo", "Vui lòng chọn nhân viên cần xóa!");
            return;
        }

        if (isProtectedAccount(selected)) {
            DialogUtil.warning(tableUsers, "Cảnh báo", "Không được phép xóa tài khoản quản trị!");
            return;
        }

        if (DialogUtil.confirmYesNo(tableUsers, "Xác nhận", "Bạn có chắc muốn xóa tài khoản " + selected.getUsername() + "?")) {
            if (userRepo.delete(selected.getUserId())) {
                loadUserData();
                clearFields();
                DialogUtil.info(tableUsers, "Thành công", "Đã xóa người dùng!");
            } else {
                DialogUtil.error(tableUsers, "Lỗi", "Không thể xóa người dùng!");
            }
        }
    }

    @FXML
    public void handleUpdateUser() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtil.warning(tableUsers, "Thông báo", "Vui lòng chọn nhân viên cần sửa từ danh sách!");
            return;
        }

        if (isProtectedAccount(selected)) {
            DialogUtil.warning(tableUsers, "Cảnh báo", "Vui lòng vào phần 'Hồ sơ' để cập nhật thông tin Admin!");
            return;
        }

        try {
            SceneManager.openModal(
                tableUsers,
                StringValue.VIEW_USER_EDIT,
                "Chỉnh sửa nhân viên: " + selected.getUsername(),
                UserEditController.class,
                controller -> controller.setUserData(selected)
            );

            loadUserData();
            clearFields();
        } catch (IOException e) {
            DialogUtil.error(tableUsers, "Lỗi", "Không thể mở cửa sổ chỉnh sửa! Kiểm tra lại đường dẫn FXML.");
        }
    }

    @FXML
    public void handleOpenProfile() {
        try {
            SceneManager.openModal(
                lblWelcome,
                StringValue.VIEW_ADMIN_PROFILE,
                "Hồ sơ Admin",
                AdminProfileController.class,
                controller -> controller.setAdminData(CurrentUser.requireUser())
            );

            loadUserData();
        } catch (IllegalStateException ex) {
            DialogUtil.error(lblWelcome, "Lỗi", ex.getMessage());
        } catch (IOException ex) {
            DialogUtil.error(lblWelcome, "Lỗi", "Không thể mở hồ sơ Admin!");
        }
    }

    private boolean isProtectedAccount(User user) {
        if (user == null) {
            return false;
        }

        return CurrentUser.hasUserId(user.getUserId())
                || UserRoleUtil.isAdminRole(user.getRoleName())
                || "admin_user".equalsIgnoreCase(user.getUsername());
    }

    private void clearFields() {
        txtNewUser.clear();
        txtNewPass.clear();
        cbRole.getSelectionModel().clearSelection();
        tableUsers.getSelectionModel().clearSelection();
    }
}