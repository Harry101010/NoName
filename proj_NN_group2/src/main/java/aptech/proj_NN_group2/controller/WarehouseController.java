package aptech.proj_NN_group2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.util.List;

import aptech.proj_NN_group2.model.business.repository.warehouse.WarehouseRepository;
import aptech.proj_NN_group2.model.entity.warehouse.IngredientLot;

public class WarehouseController {

    // --- TableView Components ---
    @FXML private TableView<IngredientLot> tblWarehouse;
    @FXML private TableColumn<IngredientLot, Integer> colId;
    @FXML private TableColumn<IngredientLot, String> colMaterialName;
    @FXML private TableColumn<IngredientLot, String> colUnit;
    @FXML private TableColumn<IngredientLot, Double> colQuantity;
    @FXML private TableColumn<IngredientLot, String> colExpiryDate;
    @FXML private TableColumn<IngredientLot, String> colStatus; // Khai báo thêm để khớp FXML

    // --- Form Components ---
    @FXML private ComboBox<String> cbMaterial; // FXML dùng ComboBox nên đổi từ TextField sang ComboBox
    @FXML private TextField txtQuantity;
    @FXML private TextField txtExpiryDate;

    private WarehouseRepository repo = new WarehouseRepository();

    @FXML
    public void initialize() {
        // Ánh xạ dữ liệu cho các cột của bảng
        colId.setCellValueFactory(new PropertyValueFactory<>("lotId"));
        colMaterialName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        // colStatus.setCellValueFactory(new PropertyValueFactory<>("status")); // Nếu model có status thì bỏ comment

        loadData();
        
        // Bạn có thể thêm dữ liệu mẫu cho ComboBox ở đây nếu cần
        // cbMaterial.getItems().addAll("Bột mì", "Đường", "Sữa");
    }

    private void loadData() {
        List<IngredientLot> data = repo.getAllStock();
        if (data != null) {
            tblWarehouse.getItems().setAll(data);
        }
    }

    @FXML
    private void handleAdd() {
        try {
            // Lấy giá trị từ ComboBox (thay vì TextField)
            String name = cbMaterial.getValue(); 
            if (name == null || name.isEmpty()) {
                showAlert("Vui lòng chọn nguyên liệu!");
                return;
            }

            double qty = Double.parseDouble(txtQuantity.getText());
            String expiry = txtExpiryDate.getText();

            int ingredientId = repo.findIngredientIdByName(name);

            if (ingredientId == -1) {
                showAlert("Không tìm thấy nguyên liệu trong hệ thống!");
                return;
            }

            repo.importStock(ingredientId, qty, expiry);
            loadData();
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Số lượng phải là một số hợp lệ!");
        } catch (Exception e) {
            showAlert("Lỗi khi nhập kho: " + e.getMessage());
        }
    }

    @FXML
    private void handleExport() {
        IngredientLot selected = tblWarehouse.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một dòng trong bảng để xuất!");
            return;
        }

        try {
            double qty = Double.parseDouble(txtQuantity.getText());
            boolean ok = repo.exportFIFO(selected.getIngredientId(), qty);

            if (!ok) {
                showAlert("Không đủ hàng trong kho để xuất!");
            }

            loadData();
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Số lượng xuất không hợp lệ!");
        }
    }

    // --- Các hàm FXML yêu cầu nhưng đang thiếu ---

    @FXML
    private void handleUpdate() {
        // Code xử lý cập nhật thông tin dòng đang chọn
        IngredientLot selected = tblWarehouse.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Đang cập nhật lô hàng ID: " + selected.getLotId());
            // Thực hiện logic update của bạn ở đây...
            loadData();
        } else {
            showAlert("Chọn một dòng để cập nhật!");
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
        clearForm();
        System.out.println("Dữ liệu đã được làm mới.");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Logic chuyển màn hình đăng nhập hoặc đóng ứng dụng
        System.out.println("Thực hiện đăng xuất...");
        // Ví dụ: Platform.exit();
    }

    private void clearForm() {
        cbMaterial.getSelectionModel().clearSelection();
        txtQuantity.clear();
        txtExpiryDate.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}