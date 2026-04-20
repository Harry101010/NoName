package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.model.business.repository.WarehouseRepository;
import aptech.proj_NN_group2.model.entity.IngredientLot;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ResourceBundle;

public class WarehouseController implements Initializable {

    @FXML private TableView<IngredientLot> tblWarehouse;

    @FXML private TableColumn<IngredientLot, Integer> colId;
    @FXML private TableColumn<IngredientLot, String> colMaterialName;
    @FXML private TableColumn<IngredientLot, String> colUnit;
    @FXML private TableColumn<IngredientLot, Double> colQuantity;
    @FXML private TableColumn<IngredientLot, String> colExpiryDate;
    @FXML private TableColumn<IngredientLot, String> colStatus;
    @FXML private TableColumn<IngredientLot, String> colSupplier;

    @FXML private TextField txtMaterialName;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtExpiryDate;
    @FXML private TextField txtSupplierName;

    private final WarehouseRepository repo = new WarehouseRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	
        colId.setCellValueFactory(new PropertyValueFactory<>("lotId"));
        colMaterialName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
       
        loadData();
    }

    private void loadData() {
        var list = repo.getAllStock();

        System.out.println("Warehouse rows = " + list.size());

        tblWarehouse.setItems(FXCollections.observableArrayList(list));
        tblWarehouse.refresh();
    }
    @FXML
    private void handleAdd(ActionEvent event) {

        try {
            String ingredientName = txtMaterialName.getText().trim();
            String supplierName = txtSupplierName.getText().trim();
            double quantity = Double.parseDouble(txtQuantity.getText().trim());
            String expiryDate = txtExpiryDate.getText().trim();

            if (ingredientName.isEmpty() || supplierName.isEmpty() || expiryDate.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Thiếu dữ liệu");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập đầy đủ nguyên liệu, nhà cung cấp và hạn sử dụng.");
                alert.showAndWait();
                return;
            }

            int ingredientId = repo.findIngredientIdByName(ingredientName);

            if (ingredientId == -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Không tìm thấy");
                alert.setHeaderText(null);
                alert.setContentText("Không tìm thấy nguyên liệu: " + ingredientName);
                alert.showAndWait();
                return;
            }

            repo.importStock(ingredientId, quantity, expiryDate, supplierName);

            txtMaterialName.clear();
            txtQuantity.clear();
            txtExpiryDate.clear();
            txtSupplierName.clear();

            loadData();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi dữ liệu");
            alert.setHeaderText(null);
            alert.setContentText("Số lượng phải là số hợp lệ.");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleUpdate(ActionEvent event) {
        // TODO: xử lý cập nhật kho
        loadData();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
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
    public void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }
    @FXML
    private void handleDelete(ActionEvent event) {

        IngredientLot selected = tblWarehouse.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn một nguyên liệu trong bảng để xóa.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Bạn có chắc muốn xóa lô nguyên liệu ID = " + selected.getLotId() + " ?"
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

            	try {

            	    boolean deleted = repo.deleteLot(selected.getLotId());

            	    if (deleted) {
            	        loadData();

            	        Alert success = new Alert(Alert.AlertType.INFORMATION);
            	        success.setTitle("Thành công");
            	        success.setHeaderText(null);
            	        success.setContentText("Đã xóa nguyên liệu khỏi kho.");
            	        success.showAndWait();
            	    } else {
            	        Alert error = new Alert(Alert.AlertType.ERROR);
            	        error.setTitle("Lỗi");
            	        error.setHeaderText(null);
            	        error.setContentText("Không thể xóa nguyên liệu.");
            	        error.showAndWait();
            	    }

            	} catch (RuntimeException ex) {

            	    Alert error = new Alert(Alert.AlertType.ERROR);
            	    error.setTitle("Không thể xóa");
            	    error.setHeaderText(null);
            	    error.setContentText(ex.getMessage());
            	    error.showAndWait();
            	}
            }
        });
    }
}