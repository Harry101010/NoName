package aptech.proj_NN_group2.controller.admin;

import java.io.IOException;
import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AdminController {
    // Các trường tương ứng với fx:id trong FXML
    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Boolean> colStatus;

    @FXML private TextField txtNewUser;
    @FXML private PasswordField txtNewPass;
    @FXML private ComboBox<String> cbRole;
    @FXML private Label lblWelcome;

    private UserRepository userRepo = new UserRepository();
    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Cấu hình các cột hiển thị dữ liệu
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        
        // 2. Tùy biến hiển thị cột Trạng thái
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active")); 
        colStatus.setCellFactory(column -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("Hoạt động");
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); 
                    } else {
                        setText("Đã khóa");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); 
                    }
                }
            }
        });

        // 3. Nạp dữ liệu vào ComboBox (Bỏ Admin theo ý giáo viên)
        cbRole.setItems(FXCollections.observableArrayList(
            "Trưởng sản xuất", "Quản lý kho", "Nhân viên kinh doanh", "Staff"
        ));

        // 4. Lắng nghe sự kiện click dòng trên bảng để điền dữ liệu vào form
        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNewUser.setText(newSelection.getUsername());
                cbRole.setValue(newSelection.getRoleName());
                txtNewPass.clear(); 
            }
        });

        // 5. Tải dữ liệu ban đầu
        loadUserData();
    }

    private void loadUserData() {
        userList.clear();
        userList.addAll(userRepo.findAll());
        tableUsers.setItems(userList);
    }

    // --- CÁC HÀM XỬ LÝ MENU GÓC ---

    @FXML
    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/aptech/proj_NN_group2/auth/login.fxml"));
            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập hệ thống");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // --- CÁC HÀM QUẢN LÝ USER ---

    @FXML
    public void addUser() {
        String username = txtNewUser.getText().trim();
        String password = txtNewPass.getText().trim();
        String roleName = cbRole.getValue();

        if (username.isEmpty() || password.isEmpty() || roleName == null) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
        int roleId = convertRoleToId(roleName); 

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRoleId(roleId);

        if (userRepo.create(newUser)) {
            showAlert("Thành công", "Đã tạo tài khoản cho: " + username);
            loadUserData();
            clearFields();
        } else {
            showAlert("Lỗi", "Không thể tạo tài khoản. Có thể tên đăng nhập bị trùng!");
        }
    }

    @FXML
    public void toggleStatus() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Thông báo", "Vui lòng chọn một nhân viên!");
            return;
        }
        
        // Chống tự khóa Admin
        if (selectedUser.getRoleName().equals("Admin")) {
            showAlert("Cảnh báo", "Bạn không thể khóa tài khoản quản trị hệ thống!");
            return;
        }

        boolean success = userRepo.toggleActive(selectedUser.getUserId(), !selectedUser.isActive());
        if (success) {
            loadUserData();
            clearFields();
        } else {
            showAlert("Lỗi", "Không thể cập nhật trạng thái!");
        }
    }

    @FXML
    public void handleDeleteUser() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn nhân viên cần xóa!");
            return;
        }
        
        // Chống xóa Admin
        if (selected.getRoleName().equals("Admin")) {
            showAlert("Cảnh báo", "Không được phép xóa tài khoản quản trị!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa tài khoản " + selected.getUsername() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (userRepo.delete(selected.getUserId())) {
                    loadUserData();
                    clearFields();
                    showAlert("Thành công", "Đã xóa người dùng!");
                }
            }
        });
    }

    @FXML
    public void handleUpdateUser() {
        // 1. Kiểm tra xem người dùng đã chọn dòng nào trên bảng chưa
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn nhân viên cần sửa từ danh sách!");
            return;
        }
        
     // Chặn không cho mở cửa sổ sửa nếu chọn trúng Admin
        if (selected.getRoleName().equals("Admin") || selected.getUsername().equals("admin_user")) {
            showAlert("Cảnh báo", "Vui lòng vào phần 'Hồ sơ' để cập nhật thông tin Admin!");
            return; 
        }

        
        try {
            // 3. Khởi tạo FXMLLoader trỏ đến file FXML mới trong folder admin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/admin/UserEdit.fxml"));
            Parent root = loader.load();

            // 4. Lấy Controller của cửa sổ sửa và truyền dữ liệu User sang
            UserEditController editController = loader.getController();
            editController.setUserData(selected);

            // 5. Tạo Stage (Cửa sổ) mới
            Stage editStage = new Stage();
            editStage.setTitle("Chỉnh sửa nhân viên: " + selected.getUsername());
            editStage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Bắt buộc xử lý xong mới được quay lại
            editStage.setScene(new Scene(root));
            
            // 6. Hiển thị và đợi người dùng đóng cửa sổ
            editStage.showAndWait();

            // 7. Sau khi cửa sổ đóng, kiểm tra xem họ có nhấn "Lưu" không để load lại bảng
            if (editController.isSaveClicked()) {
                loadUserData(); // Tải lại bảng TableView
                clearFields();  // Xóa trắng các ô nhập liệu (nếu cần)
                // Không cần showAlert ở đây vì trong UserEditController đã báo thành công rồi
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở cửa sổ chỉnh sửa! Kiểm tra lại đường dẫn FXML.");
        }
    }

    private int convertRoleToId(String roleName) {
        if (roleName == null) return 5;
        switch (roleName) {
            case "Admin": return 1;
            case "Trưởng sản xuất": return 2;
            case "Quản lý kho": return 3;
            case "Nhân viên kinh doanh": return 4;
            default: return 5;
        }
    }

    private void clearFields() {
        txtNewUser.clear();
        txtNewPass.clear();
        cbRole.getSelectionModel().clearSelection();
        tableUsers.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    public void handleOpenProfile() {
        try {
            User admin = null;
            for (User u : tableUsers.getItems()) {
                // Kiểm tra: Nếu là ID 1 HOẶC nếu tên đúng là admin_user
                if (u.getRoleId() == 1 || u.getUsername().equals("admin_user")) { 
                    admin = u;
                    break;
                }
            }

            if (admin != null) {
                // Mở form AdminProfile như bình thường...
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/admin/AdminProfile.fxml"));
                Parent root = loader.load();
                AdminProfileController controller = loader.getController();
                controller.setAdminData(admin);

                Stage stage = new Stage();
                stage.setTitle("Hồ sơ Admin");
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}