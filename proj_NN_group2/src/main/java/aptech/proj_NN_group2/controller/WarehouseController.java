package aptech.proj_NN_group2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.util.List;

import aptech.proj_NN_group2.model.business.repository.warehouse.WarehouseRepository;
import aptech.proj_NN_group2.model.entity.warehouse.IngredientLot;
import aptech.proj_NN_group2.util.NavigationUtil;

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
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        loadData();
        
        // Bạn có thể thêm dữ liệu mẫu cho ComboBox ở đây nếu cần
        // cbMaterial.getItems().addAll("Bột mì", "Đường", "Sữa");
        cbMaterial.getItems().addAll(repo.getAllIngredientNames());
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
            String name = cbMaterial.getValue(); 
            if (name == null || name.isEmpty()) {
                showAlert("Vui lòng chọn nguyên liệu!");
                return;
            }

            String supplierName = txtSupplier.getText();
            if (supplierName == null || supplierName.isEmpty()) {
                showAlert("Vui lòng nhập nhà cung cấp!");
                return;
            }

            double qty = Double.parseDouble(txtQuantity.getText());
            String expiry = txtExpiryDate.getText();

            int ingredientId = repo.findIngredientIdByName(name);

            if (ingredientId == -1) {
                showAlert("Không tìm thấy nguyên liệu!");
                return;
            }

            // ✅ ĐÚNG Ở ĐÂY
            repo.importStock(ingredientId, qty, expiry, supplierName);

            loadData();
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Số lượng phải là số!");
        } catch (Exception e) {
            showAlert("Lỗi: " + e.getMessage());
        }
    }
    @FXML
    private void handleExport() {
        try {
            String name = cbMaterial.getValue();
            double qty = Double.parseDouble(txtQuantity.getText());

            int ingredientId = repo.findIngredientIdByName(name);

            boolean ok = repo.exportWithReceipt(ingredientId, qty, 1);

            if (ok) {
                showAlert("Xuất kho thành công (đã tạo phiếu)");
            } else {
                showAlert("Không đủ hàng!");
            }

            loadData();
            clearForm();

        } catch (Exception e) {
            showAlert("Lỗi xuất kho!");
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
    	NavigationUtil.logout(event);
    }
    
    @FXML
    private void goToProfile(ActionEvent event) {
    	NavigationUtil.toAccountProfile(event);
    }

    private void clearForm() {
        cbMaterial.getSelectionModel().clearSelection();
        txtQuantity.clear();
        txtExpiryDate.clear();
        txtSupplier.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML private TableColumn<IngredientLot, String> colSupplier;
    @FXML private TextField txtSupplier;
}