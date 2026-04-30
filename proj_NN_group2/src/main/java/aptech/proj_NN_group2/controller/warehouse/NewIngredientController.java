package aptech.proj_NN_group2.controller.warehouse;

import java.util.List;

import aptech.proj_NN_group2.model.business.repository.WarehouseRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewIngredientController {
   
 // Khớp với fx:id trong FXML
    @FXML private TextField txtNewName;
    @FXML private ComboBox<String> cbUnit;
    @FXML private TextField txtStorage;

    private WarehouseRepository repo = new WarehouseRepository();

   
    @FXML
    private void handleSave() {
        String name = txtNewName.getText().trim();
        String unitName = cbUnit.getValue();
        String storage = txtStorage.getText().trim();

        if (name.isEmpty() || unitName == null) {
            showError("Vui lòng nhập đủ tên và đơn vị!");
            return;
        }

        if (repo.exists(name)) {
            showError("Nguyên liệu này đã tồn tại!");
            return;
        }

        int unitId = repo.findUnitIdByName(unitName);
        int newId = repo.createIngredient(name, unitId, storage);

        if (newId != -1) {
            showInfo("Thêm nguyên liệu thành công!");
            closeWindow();
        } else {
            showError("Có lỗi xảy ra khi lưu vào Database!");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

        
    @FXML
    public void initialize() {
        List<String> units = repo.getAllUnits();
        cbUnit.setItems(FXCollections.observableArrayList(units));
    }
    
   

    // --- CÁC HÀM TIỆN ÍCH (BỊ THIẾU Ở CODE CŨ) ---
    
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

    private void closeWindow() {
        Stage stage = (Stage) txtNewName.getScene().getWindow();
        stage.close();
    }
}