package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.model.business.repository.WarehouseRepository;
import aptech.proj_NN_group2.model.entity.InventorySummary;
import aptech.proj_NN_group2.model.entity.IngredientLot;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class WarehouseController implements Initializable {

    // ===== SUMMARY TABLE =====
    @FXML private TableView<InventorySummary> tblSummary;
    @FXML private TableColumn<InventorySummary, String> colName;
    @FXML private TableColumn<InventorySummary, String> colUnit;
    @FXML private TableColumn<InventorySummary, Double> colTotal;
    @FXML private TableColumn<InventorySummary, LocalDate> colExpiry;
    @FXML private TableColumn<InventorySummary, String> colSummaryStatus;
    @FXML private TableColumn<InventorySummary, String> colStorage;


    // ===== LOT TABLE =====
    @FXML private TableView<IngredientLot> tblLots;
    @FXML private TableColumn<IngredientLot, Integer> colLotId;
    @FXML private TableColumn<IngredientLot, String> colLotName;
    @FXML private TableColumn<IngredientLot, String> colSupplier;
    @FXML private TableColumn<IngredientLot, LocalDate> colImport;
    @FXML private TableColumn<IngredientLot, LocalDate> colLotExpiry;
    @FXML private TableColumn<IngredientLot, Double> colRemain;
    @FXML private TableColumn<IngredientLot, String> colStatus;
    @FXML private TableColumn<IngredientLot, String> colLotStorage;
    
    

    
    // ===== INPUT =====
    @FXML private TextField txtMaterialName;
    @FXML private TextField txtQuantity;
    @FXML private DatePicker dpExpiryDate;
    @FXML private TextField txtSupplierName;
    @FXML private ComboBox<String> cbUnit;

    private final WarehouseRepository repo = new WarehouseRepository();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cbUnit.setItems(FXCollections.observableArrayList(repo.getAllUnits()));

        // ===== SUMMARY TABLE =====
        colName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("nearestExpiry"));
        colStorage.setCellValueFactory(
                new PropertyValueFactory<>("storageCondition")
        );

        colSummaryStatus.setCellValueFactory(cell -> {
            InventorySummary item = cell.getValue();
            return new SimpleStringProperty(item != null ? item.getStatus() : "");
        });

        colSummaryStatus.setCellFactory(column -> createStatusCell());

        // ===== LOT TABLE =====
        colLotId.setCellValueFactory(new PropertyValueFactory<>("lotId"));
        colLotName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colImport.setCellValueFactory(new PropertyValueFactory<>("importDate"));
        colLotExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colRemain.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));
        colLotStorage.setCellValueFactory(new PropertyValueFactory<>("storageCondition"));

        colStatus.setCellValueFactory(cell -> {
            IngredientLot item = cell.getValue();
            return new SimpleStringProperty(item != null ? item.getStatus() : "");
        });

        colStatus.setCellFactory(column -> createStatusCell());

        loadAllTables();

        // Khi chọn 1 dòng trong bảng thì đổ dữ liệu lên form
        tblLots.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, selected) -> {
            if (selected != null) {
                txtMaterialName.setText(selected.getIngredientName());
                txtQuantity.setText(String.valueOf(selected.getRemainingQuantity()));

                txtSupplierName.setText(
                        selected.getSupplierName() != null
                                ? selected.getSupplierName()
                                : ""
                );

                dpExpiryDate.setValue(selected.getExpiryDate());

                String unitName = selected.getUnitName();
                if (unitName != null && !unitName.isBlank()) {
                    cbUnit.setValue(unitName);
                } else {
                    cbUnit.getSelectionModel().clearSelection();
                }
            }
        });
    }

    private <T> TableCell<T, String> createStatusCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null || status.isBlank()) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label dot = new Label("●");
                dot.setStyle("-fx-font-size: 14px;");

                String lower = status.toLowerCase();

                if (lower.contains("hết hạn") || lower.contains("expired")) {
                    dot.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                } else if (lower.contains("sắp") || lower.contains("soon")) {
                    dot.setStyle("-fx-text-fill: orange; -fx-font-size: 14px;");
                } else {
                    dot.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                }

                Label text = new Label(status);
                HBox box = new HBox(6, dot, text);

                setGraphic(box);
                setText(null);
            }
        };
    }

    private void loadAllTables() {
        tblSummary.setItems(FXCollections.observableArrayList(repo.getSummary()));
        tblLots.setItems(FXCollections.observableArrayList(repo.getLots()));
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            String ingredientName = txtMaterialName.getText().trim();
            String quantityText = txtQuantity.getText().trim();
            String supplierName = txtSupplierName.getText().trim();
            String selectedUnit = cbUnit.getValue();

            if (ingredientName.isEmpty()) {
                showWarning("Vui lòng nhập tên nguyên liệu");
                return;
            }

            if (selectedUnit == null || selectedUnit.isBlank()) {
                showWarning("Vui lòng chọn đơn vị");
                return;
            }

            if (quantityText.isEmpty()) {
                showWarning("Vui lòng nhập số lượng");
                return;
            }

            double quantity;
            try {
                quantity = Double.parseDouble(quantityText);
            } catch (NumberFormatException e) {
                showWarning("Số lượng phải là số");
                return;
            }

            int ingredientId = repo.findIngredientIdByName(ingredientName);

            // Nếu nguyên liệu chưa tồn tại thì tạo mới
            if (ingredientId == -1) {
                int unitId = repo.findUnitIdByName(selectedUnit);

                if (unitId == -1) {
                    showError("Không tìm thấy đơn vị trong database");
                    return;
                }

                ingredientId = repo.createIngredient(ingredientName, unitId);

                if (ingredientId == -1) {
                    showError("Không thể tạo nguyên liệu mới");
                    return;
                }
            }

            LocalDate expiry = dpExpiryDate.getValue();

            // Nếu không nhập hạn dùng thì mặc định 1 năm sau
            if (expiry == null) {
                expiry = LocalDate.now().plusYears(1);
            }

            repo.importStock(
                    ingredientId,
                    quantity,
                    expiry.toString(),
                    supplierName
            );

            clearInputs();
            loadAllTables();

            new Alert(Alert.AlertType.INFORMATION, "Nhập kho thành công").showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Dữ liệu không hợp lệ");
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {

        IngredientLot selected = tblLots.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Hãy chọn lô cần cập nhật");
            return;
        }

        try {
            String ingredientName = txtMaterialName.getText().trim();
            String quantityText = txtQuantity.getText().trim();
            String supplierName = txtSupplierName.getText().trim();

            if (ingredientName.isEmpty()) {
                showWarning("Vui lòng nhập tên nguyên liệu");
                return;
            }

            if (quantityText.isEmpty()) {
                showWarning("Vui lòng nhập số lượng");
                return;
            }

            double quantity;
            try {
                quantity = Double.parseDouble(quantityText);
            } catch (NumberFormatException e) {
                showWarning("Số lượng phải là số");
                return;
            }

            int ingredientId = repo.findIngredientIdByName(ingredientName);

            if (ingredientId == -1) {
                showError("Không tìm thấy nguyên liệu");
                return;
            }

            LocalDate expiry = dpExpiryDate.getValue();

            // Nếu không chọn hạn dùng mới thì giữ hạn cũ
            if (expiry == null) {
                expiry = selected.getExpiryDate();
            }

            // Nếu vẫn null thì gán mặc định
            if (expiry == null) {
                expiry = LocalDate.now().plusYears(1);
            }

            repo.deleteLot(selected.getLotId());

            repo.importStock(
                    ingredientId,
                    quantity,
                    expiry.toString(),
                    supplierName
            );

            clearInputs();
            loadAllTables();

            new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công").showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi cập nhật dữ liệu");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {

        IngredientLot selected = tblLots.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Hãy chọn lô cần xóa");
            return;
        }

        if (repo.deleteLot(selected.getLotId())) {
            clearInputs();
            loadAllTables();
            new Alert(Alert.AlertType.INFORMATION, "Xóa thành công").showAndWait();
        } else {
            showError("Không thể xóa");
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        clearInputs();
        loadAllTables();
    }

    @FXML
    private void handleViewExportRequests(ActionEvent event) {
        NavigationUtil.goTo(
                event,
                StringValue.VIEW_EXPORT_REQUESTS,
                "Danh sách yêu cầu xuất kho"
        );
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }

    private void clearInputs() {
        txtMaterialName.clear();
        txtQuantity.clear();
        txtSupplierName.clear();
        dpExpiryDate.setValue(null);
        cbUnit.getSelectionModel().clearSelection();
        tblLots.getSelectionModel().clearSelection();
    }

    private void showWarning(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}