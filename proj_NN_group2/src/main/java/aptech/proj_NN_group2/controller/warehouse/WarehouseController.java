package aptech.proj_NN_group2.controller.warehouse;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.IngredientRepository;
import aptech.proj_NN_group2.model.business.repository.WarehouseRepository;
import aptech.proj_NN_group2.model.entity.InventorySummary;
import aptech.proj_NN_group2.model.entity.ingredient.IngredientLot;
import aptech.proj_NN_group2.util.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WarehouseController implements Initializable {

    // ===== BẢNG TRÊN: MASTER =====
    @FXML private TableView<InventorySummary> tblSummary;
    @FXML private TableColumn<InventorySummary, String> colName;
    @FXML private TableColumn<InventorySummary, String> colUnit;
    @FXML private TableColumn<InventorySummary, Double> colTotal;
    @FXML private TableColumn<InventorySummary, LocalDate> colExpiry;
    @FXML private TableColumn<InventorySummary, String> colSummaryStatus;
    @FXML private TableColumn<InventorySummary, String> colStorage;

    // ===== BẢNG DƯỚI: DETAIL =====
    @FXML private TableView<IngredientLot> tblDetailLots; 
    @FXML private TableColumn<IngredientLot, Integer> colLotId;
    @FXML private TableColumn<IngredientLot, String> colLotName;
    @FXML private TableColumn<IngredientLot, String> colSupplier;
    @FXML private TableColumn<IngredientLot, LocalDate> colImportDate;
    @FXML private TableColumn<IngredientLot, LocalDate> colLotExpiry;
    @FXML private TableColumn<IngredientLot, String> colLotStorage;
    @FXML private TableColumn<IngredientLot, Double> colRemain;
    @FXML private TableColumn<IngredientLot, String> colStatus;
    @FXML private TableColumn<IngredientLot, String> colLotUnit;

    // ===== INPUT =====
    @FXML private TextField txtMaterialName;
    @FXML private TextField txtQuantity;
    @FXML private DatePicker dpExpiryDate;
    @FXML private TextField txtSupplierName;
    @FXML private ComboBox<String> cbUnit;
    @FXML private TextField txtSearch;
    
   
    
    
    private ObservableList<InventorySummary> masterData = FXCollections.observableArrayList();
    private final WarehouseRepository repo = new WarehouseRepository();

    // ===== FILTER DATA =====
    private FilteredList<IngredientLot> filteredLots;
    private FilteredList<InventorySummary> filteredSummary;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Cấu hình ban đầu
    	// 1. Nạp danh sách đơn vị cho ComboBox
        ObservableList<String> units = FXCollections.observableArrayList("kg", "l", "pcs", "g", "ml");
        cbUnit.setItems(units);
        
        setupTableColumns();
        setupListeners();
        
        // 2. Load dữ liệu lần đầu
        loadAllTables();
        
        // 3. Load ComboBox đơn vị (nếu cần ở form nhập kho)
        if (cbUnit != null) {
            cbUnit.setItems(FXCollections.observableArrayList(repo.getAllUnits()));
        }

        applyTableStyling();
    }

    // Tách riêng việc cấu hình cột để hàm initialize gọn hơn
    private void setupTableColumns() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Cột bảng Summary
        colName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        colStorage.setCellValueFactory(new PropertyValueFactory<>("storageCondition"));
        
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("nearestExpiry"));
        colExpiry.setCellFactory(column -> new TableCell<InventorySummary, LocalDate>() {
            @Override protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(formatter));
            }
        });

        // Cột bảng Chi tiết lô (FIFO)
        colLotId.setCellValueFactory(new PropertyValueFactory<>("lotId"));
        colLotName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colImportDate.setCellValueFactory(new PropertyValueFactory<>("importDate"));
        colLotExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colLotStorage.setCellValueFactory(new PropertyValueFactory<>("storageCondition"));
        colRemain.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colLotUnit.setCellValueFactory(new PropertyValueFactory<>("unitName"));
    }

    // Tách riêng các sự kiện
   
    private void setupListeners() {
        // 1. Xử lý ô tìm kiếm (Search)
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            FilteredList<InventorySummary> filteredData = new FilteredList<>(masterData, p -> true);
            filteredData.setPredicate(item -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return item.getIngredientName().toLowerCase().contains(newVal.toLowerCase());
            });
            tblSummary.setItems(filteredData);
        });

        // 2. Xử lý khi chọn nguyên liệu ở bảng trên (Bảng Summary)
        tblSummary.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Nạp dữ liệu vào bảng dưới
                tblDetailLots.setItems(FXCollections.observableArrayList(repo.getLotsByIngredientName(newVal.getIngredientName())));
                // Điền tên nguyên liệu vào ô nhập
                txtMaterialName.setText(newVal.getIngredientName());
            } else {
                tblDetailLots.getItems().clear();
                txtMaterialName.clear();
            }
        });

        // 3. Xử lý khi chọn một lô cụ thể ở bảng dưới (Bảng FIFO) để lấy thông tin chi tiết
        tblDetailLots.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Click 1 lần
                IngredientLot selectedLot = tblDetailLots.getSelectionModel().getSelectedItem();
                
                if (selectedLot != null) {
                    // Điền thông tin vào các TextField/DatePicker
                    txtMaterialName.setText(selectedLot.getIngredientName());
                    txtQuantity.setText(String.valueOf(selectedLot.getRemainingQuantity()));
                    
                    // Xử lý null an toàn cho Nhà cung cấp
                    txtSupplierName.setText(selectedLot.getSupplierName() != null ? selectedLot.getSupplierName() : "");
                    
                    // Xử lý an toàn cho Hạn sử dụng
                    if (selectedLot.getExpiryDate() != null) {
                        dpExpiryDate.setValue(selectedLot.getExpiryDate());
                    } else {
                        dpExpiryDate.setValue(null);
                    }
                }
            }
        });
    }

    // Hàm này cực kỳ quan trọng: Gọi mỗi khi thêm mới hoặc nhập kho
    public void loadAllTables() {
        masterData.setAll(repo.getSummary());
        tblSummary.setItems(masterData);
    }

   

    @FXML public void handleRefresh(ActionEvent event) {
        loadAllTables();
        tblDetailLots.getItems().clear();
        clearInputs();
        
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (!isInputValid()) return;

        try {
            String ingredientName = txtMaterialName.getText().trim();
            double quantity = Double.parseDouble(txtQuantity.getText().trim());
            String supplierName = txtSupplierName.getText().trim();
            LocalDate expiry = dpExpiryDate.getValue();
            if (expiry == null) expiry = LocalDate.now().plusYears(1);

            // --- BƯỚC QUAN TRỌNG: LẤY ID TỪ TÊN ---
            // Bạn cần khởi tạo IngredientRepository để tìm ID
            IngredientRepository ingRepo = new IngredientRepository(); 
            var ingredient = ingRepo.findByName(ingredientName); // Giả sử bạn có hàm tìm theo tên

            if (ingredient == null) {
                showError("Không tìm thấy nguyên liệu có tên: " + ingredientName + "\nVui lòng kiểm tra lại tên!");
                return;
            }
            
            String name = txtMaterialName.getText(); // Thay txtMaterialName bằng tên biến TextField của bạn
            System.out.println("DEBUG: Tên nguyên liệu lấy từ UI là: '" + name + "'");
         // Kiểm tra xem bạn đã set tên vào newLot chưa
            IngredientLot newLot = new IngredientLot();
//            newLot.setIngredientName(name); // <-- DÒNG NÀY CÓ KHÔNG?
//            newLot.setIngredientName(txtMaterialName.getText().trim());
            
            if (name == null || name.trim().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Tên nguyên liệu không được để trống!").show();
                return; // Dừng lại không cho lưu
            }

            newLot.setIngredientName(name.trim());
            System.out.println("DEBUG: Tên trong object newLot là: '" + newLot.getIngredientName() + "'");
            
            // --- TẠO ĐỐI TƯỢNG VỚI ID ĐÚNG ---
           
            newLot.setIngredientId(ingredient.getIngredient_id()); // LẤY ID CHUẨN TỪ OBJECT TÌM ĐƯỢC
            newLot.setRemainingQuantity(quantity);
            newLot.setReceivedQuantity(quantity); // Nhập mới nên tồn = nhận
            newLot.setExpiryDate(expiry);
            newLot.setSupplierName(supplierName);
            newLot.setImportDate(LocalDate.now());
            
            
            // --- GỌI REPO LƯU VÀO DB ---
            repo.importStock(newLot); 

            // Xử lý sau khi thành công
            clearInputs();
            loadAllTables();
            new Alert(Alert.AlertType.INFORMATION, "Nhập kho thành công").showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate(ActionEvent event) {
    	if (!isInputValid()) return;
        IngredientLot selected = tblDetailLots.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a lot to update");
            return;
        }

        try {
            // Cập nhật giá trị từ giao diện vào đối tượng selected
            selected.setRemainingQuantity(Double.parseDouble(txtQuantity.getText()));
            selected.setExpiryDate(dpExpiryDate.getValue());
            selected.setSupplierName(txtSupplierName.getText());

            // Gọi hàm mới trong Repository
            if (repo.updateLot(selected)) {
                loadAllTables(); // Tải lại bảng để thấy dữ liệu mới
                clearInputs();
                new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!").show();
            } else {
                showError("Cập nhật thất bại, hãy kiểm tra lại dữ liệu.");
            }
        } catch (NumberFormatException e) {
            showError("Số lượng phải là số!");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        IngredientLot selected = tblDetailLots.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a lot to delete");
            return;
        }

        repo.deleteLot(selected.getLotId());
        handleRefresh(null);
        new Alert(Alert.AlertType.INFORMATION, "Deleted successfully").showAndWait();
    }

       
    // Các hàm khác như handleLogout...
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
        tblDetailLots.getSelectionModel().clearSelection();
    }
   
    
 // Hàm kiểm tra dữ liệu đầu vào
    private boolean isInputValid() {
        String errorMessage = "";

        if (txtMaterialName.getText() == null || txtMaterialName.getText().trim().isEmpty()) {
            errorMessage += "Tên nguyên liệu không được để trống!\n";
        }
        
        if (cbUnit.getValue() == null) {
            errorMessage += "Vui lòng chọn đơn vị tính!\n";
        }
        
        try {
            Double.parseDouble(txtQuantity.getText().trim());
        } catch (NumberFormatException e) {
            errorMessage += "Số lượng phải là một con số hợp lệ!\n";
        }
        
        if (dpExpiryDate.getValue() == null) {
            errorMessage += "Vui lòng chọn hạn sử dụng!\n";
        }

        if (errorMessage.isEmpty()) {
            return true; // Dữ liệu hợp lệ
        } else {
            // Hiện thông báo lỗi cho người dùng
            showWarning("Lỗi nhập liệu:\n" + errorMessage);
            return false;
        }
    }
    private void applyTableStyling() {
        tblSummary.setRowFactory(tv -> new TableRow<InventorySummary>() {
            @Override
            protected void updateItem(InventorySummary item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getTotalStock() < 10) { 
                    // Nếu tồn dưới 10 -> Tô đỏ nhạt
                    setStyle("-fx-background-color: #ffcccc;"); 
                } else {
                    setStyle("");
                }
            }
        });
    }
   
   
    @FXML
    private void openNewIngredientDialog() {
        try {
           
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/warehouse/AddIngredient.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Thêm nguyên liệu mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Sau khi đóng cửa sổ thêm mới, load lại bảng chính
            loadAllTables(); 
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở cửa sổ thêm nguyên liệu!");
        }
    }
   
    @FXML
    private void handleExport(ActionEvent event) {
        InventorySummary selected = tblSummary.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Vui lòng chọn nguyên liệu cần xuất ở bảng trên!");
            return;
        }

        try {
            // 1. Load FXML Dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/export_dialog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Phiếu xuất kho");
            stage.setScene(new Scene(root));
            
            ExportDialogController controller = loader.getController();
            controller.initData(selected.getIngredientName(), selected.getTotalStock());
            controller.setDialogStage(stage);
            
            stage.showAndWait(); // Đợi người dùng đóng dialog
            
            // 2. Lấy kết quả từ Dialog
            double qtyToExport = controller.getResultQuantity();
            
            // 3. Chỉ xử lý nếu số lượng hợp lệ ( > 0 )
            if (qtyToExport > 0) {
                
                // Validate: Kiểm tra tồn kho trước khi xuất
                if (qtyToExport > selected.getTotalStock()) {
                    showError("Số lượng xuất vượt quá tồn kho hiện tại!");
                    return;
                }
                
                // Thực hiện xuất kho
                int id = repo.findIngredientIdByName(selected.getIngredientName());
                
                // Gọi FIFO xuất kho duy nhất 1 lần tại đây
                if (repo.exportFIFO(id, qtyToExport)) {
                    loadAllTables(); // Tải lại bảng để cập nhật số tồn mới
                    showInfo("Xuất kho thành công!");
                } else {
                    showError("Xuất kho thất bại (Có thể do dữ liệu tồn kho đã thay đổi).");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Có lỗi xảy ra: " + e.getMessage());
        }
    }
 // --- CÁC HÀM TIỆN ÍCH HIỂN THÔNG BÁO ---

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}