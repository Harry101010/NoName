package aptech.proj_NN_group2.controller.warehouse;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ExportDialogController {
    @FXML private Label lblTitle, lblAvailable;
    @FXML private TextField txtExportQty;
    
    private double availableQty;
    private double resultQuantity = -1; // -1 nghĩa là chưa xác nhận
    private Stage dialogStage;

    public void initData(String name, double available) {
        this.availableQty = available;
        lblTitle.setText("Xuất kho: " + name);
        lblAvailable.setText(String.valueOf(available));
    }

    public void setDialogStage(Stage stage) { this.dialogStage = stage; }
    public double getResultQuantity() { return resultQuantity; }

    @FXML private void handleConfirm() {
        try {
            double qty = Double.parseDouble(txtExportQty.getText());
            if (qty <= 0 || qty > availableQty) {
                new Alert(Alert.AlertType.ERROR, "Số lượng không hợp lệ!").show();
                return;
            }
            resultQuantity = qty;
            dialogStage.close();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Vui lòng nhập số!").show();
        }
    }
    @FXML private void handleCancel() { dialogStage.close(); }
}