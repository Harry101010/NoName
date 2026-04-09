package aptech.proj_NN_group2.controller;

import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {
	// Các trường tương ứng với fx:id trong FXML
	@FXML
	private TableView<User> tableUsers;
	@FXML
	private TableColumn<User, Integer> colId;
	@FXML
	private TableColumn<User, String> colUsername;
	@FXML
	private TableColumn<User, String> colRole;
	@FXML
	private TableColumn<User, Boolean> colStatus;

	@FXML
	private TextField txtNewUser;
	@FXML
	private PasswordField txtNewPass;
	@FXML
	private ComboBox<String> cbRole;

	private UserRepository userRepo = new UserRepository();
	private ObservableList<User> userList = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		// 1. Cấu hình các cột để hiển thị dữ liệu từ object User
		colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
		colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
		colRole.setCellValueFactory(new PropertyValueFactory<>("roleName"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));

		// 2. Nạp dữ liệu vào ComboBox Role (Tạm thời fix cứng hoặc lấy từ DB)
		cbRole.setItems(FXCollections.observableArrayList("Admin", "Manager", "Staff"));

		// 3. Tải dữ liệu từ SQL lên bảng
		loadUserData();
	}

	private void loadUserData() {
		userList.clear();
		userList.addAll(userRepo.findAll());
		tableUsers.setItems(userList);
	}

	@FXML
	public void addUser() {
		String username = txtNewUser.getText();
		String password = txtNewPass.getText();
		String role = cbRole.getValue();

		if (username.isEmpty() || password.isEmpty() || role == null) {
			new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin nhân viên mới!").show();
			return;
		}

		// Logic thêm User vào DB sẽ viết ở đây
		System.out.println("Đang thêm user: " + username);
		new Alert(Alert.AlertType.INFORMATION, "Tính năng Thêm mới đang được phát triển!").show();
	}

	@FXML
	public void toggleStatus() {
		User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
		if (selectedUser == null) {
			new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một nhân viên từ bảng!").show();
			return;
		}

		// Gọi Repository để cập nhật trạng thái trong SQL
		boolean newStatus = !selectedUser.isActive();
		if (userRepo.toggleActive(selectedUser.getUserId(), newStatus)) {
			loadUserData(); // Tải lại bảng
			new Alert(Alert.AlertType.INFORMATION, "Đã cập nhật trạng thái cho " + selectedUser.getUsername()).show();
		}
	}
	@FXML
	public void handleLogout(ActionEvent event) {
	    NavigationUtil.logout(event);
	}
}