package aptech.proj_NN_group2.controller.production;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.IngredientExportReceiptRepository;
import aptech.proj_NN_group2.model.business.repository.IngredientExportRequestRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportReceipt;
import aptech.proj_NN_group2.model.entity.IngredientExportRequestDetail;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ConfirmReceivedIngredientController implements Initializable {

    // --- Bảng phiếu xuất kho chờ xác nhận ---
    @FXML private TableView<IngredientExportReceipt> tableReceipts;
    @FXML private TableColumn<IngredientExportReceipt, Integer> colReceiptId;
    @FXML private TableColumn<IngredientExportReceipt, String>  colIceCream;
    @FXML private TableColumn<IngredientExportReceipt, String>  colKg;
    @FXML private TableColumn<IngredientExportReceipt, String>  colReceiptStatus;
    @FXML private TableColumn<IngredientExportReceipt, String>  colCreatedAt;
    @FXML private TableColumn<IngredientExportReceipt, String>  colNote;

    // --- Bảng chi tiết nguyên liệu ---
    @FXML private TableView<IngredientExportRequestDetail> tableDetails;
    @FXML private TableColumn<IngredientExportRequestDetail, String>  colIngredient;
    @FXML private TableColumn<IngredientExportRequestDetail, String>  colUnit;
    @FXML private TableColumn<IngredientExportRequestDetail, String>  colQty;

    @FXML private Label lblSelectedInfo;
    @FXML private Label lblMessage;

    private final IngredientExportReceiptRepository receiptRepo = new IngredientExportReceiptRepository();
    private final IngredientExportRequestRepository requestRepo = new IngredientExportRequestRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup bảng phiếu xuất kho
        colReceiptId.setCellValueFactory(new PropertyValueFactory<>("ingredient_export_receipt_id"));
        colIceCream.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        colKg.setCellValueFactory(new PropertyValueFactory<>("planned_output_kg"));
        colReceiptStatus.setCellValueFactory(new PropertyValueFactory<>("receipt_status"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Setup bảng chi tiết nguyên liệu
        colIngredient.setCellValueFactory(new PropertyValueFactory<>("ingredient_name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit_name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("required_quantity"));

        // Khi chọn phiếu → load chi tiết nguyên liệu
        tableReceipts.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, selected) -> {
                if (selected != null) handleReceiptSelected(selected);
            });

        loadReceiptTable();
    }

    private void handleReceiptSelected(IngredientExportReceipt receipt) {
        lblSelectedInfo.setText(
            "Phiếu XK #" + receipt.getIngredient_export_receipt_id()
            + "  |  Kem: " + receipt.getIce_cream_name()
            + "  |  Số kg: " + receipt.getPlanned_output_kg()
            + "  |  Trạng thái: " + receipt.getReceipt_status());
        lblMessage.setText("");

        List<IngredientExportRequestDetail> details =
            requestRepo.findDetailsByRequestId(receipt.getIngredient_export_request_id());
        tableDetails.setItems(FXCollections.observableArrayList(details));
    }

    @FXML
    private void handleConfirm() {
        lblMessage.setStyle("-fx-text-fill: red;");

        IngredientExportReceipt selected = tableReceipts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblMessage.setText("Vui lòng chọn phiếu xuất kho cần xác nhận.");
            return;
        }

        if (!"approved".equals(selected.getReceipt_status())) {
            lblMessage.setText("Chỉ có thể xác nhận phiếu ở trạng thái 'approved'.");
            return;
        }

        int productionOrderId = requestRepo.findProductionOrderId(selected.getIngredient_export_request_id());
        if (productionOrderId <= 0) {
            lblMessage.setText("Không tìm thấy lệnh sản xuất liên quan.");
            return;
        }

        boolean ok = receiptRepo.confirmReceived(
            selected.getIngredient_export_receipt_id(),
            selected.getIngredient_export_request_id(),
            productionOrderId);

        if (ok) {
            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setText("Xác nhận nhận nguyên liệu thành công! Lệnh sản xuất chuyển sang 'in_progress'.");
            tableDetails.getItems().clear();
            lblSelectedInfo.setText("");
            loadReceiptTable();
        } else {
            lblMessage.setText("Xác nhận thất bại, vui lòng thử lại.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadReceiptTable();
        tableDetails.getItems().clear();
        lblSelectedInfo.setText("");
        lblMessage.setText("");
    }

    @FXML
    private void goBack() throws java.io.IOException {
        aptech.proj_NN_group2.App.setRoot(aptech.proj_NN_group2.util.StringValue.VIEW_MAIN_MENU);
    }

    private void loadReceiptTable() {
        tableReceipts.setItems(FXCollections.observableArrayList(receiptRepo.findApproved()));
    }
}