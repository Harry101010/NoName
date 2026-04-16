package aptech.proj_NN_group2.controller.production;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.IngredientExportRequestRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportRequest;
import aptech.proj_NN_group2.model.entity.IngredientExportRequestDetail;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.util.CurrentUser;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class IngredientIssueRequestController implements Initializable {

    // --- Phần tạo phiếu ---
    @FXML private ComboBox<ProductionOrder> cbOrder;
    @FXML private Label lblOrderInfo;
    @FXML private TextArea taNote;
    @FXML private Label lblMessage;

    // --- Bảng chi tiết nguyên liệu preview ---
    @FXML private TableView<IngredientExportRequestDetail> tablePreview;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colIngredient;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colUnit;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colQty;

    // --- Bảng danh sách phiếu đã tạo ---
    @FXML private TableView<IngredientExportRequest> tableRequests;
    @FXML private TableColumn<IngredientExportRequest, Integer> colReqId;
    @FXML private TableColumn<IngredientExportRequest, String> colReqIceCream;
    @FXML private TableColumn<IngredientExportRequest, String> colReqKg;
    @FXML private TableColumn<IngredientExportRequest, String> colReqStatus;
    @FXML private TableColumn<IngredientExportRequest, String> colReqDate;
    @FXML private TableColumn<IngredientExportRequest, String> colReqNote;

    private final ProductionOrderRepository orderRepo = new ProductionOrderRepository();
    private final IngredientExportRequestRepository requestRepo = new IngredientExportRequestRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup bảng preview nguyên liệu
        colIngredient.setCellValueFactory(new PropertyValueFactory<>("ingredient_name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit_name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("required_quantity"));

        // Setup bảng danh sách phiếu
        colReqId.setCellValueFactory(new PropertyValueFactory<>("ingredient_export_request_id"));
        colReqIceCream.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        colReqKg.setCellValueFactory(new PropertyValueFactory<>("planned_output_kg"));
        colReqStatus.setCellValueFactory(new PropertyValueFactory<>("request_status"));
        colReqDate.setCellValueFactory(new PropertyValueFactory<>("requested_at"));
        colReqNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Load lệnh sản xuất vào ComboBox (chỉ lấy lệnh ở trạng thái draft)
        List<ProductionOrder> orders = orderRepo.findByStatus("draft");
        cbOrder.setItems(FXCollections.observableArrayList(orders));
        cbOrder.setConverter(new javafx.util.StringConverter<ProductionOrder>() {
            @Override public String toString(ProductionOrder o) {
                return o == null ? "" : "ID " + o.getProduction_order_id() + " - " + o.getIce_cream_name()
                        + " (" + o.getPlanned_output_kg() + " kg)";
            }
            @Override public ProductionOrder fromString(String s) { return null; }
        });

        // Khi chọn lệnh → hiển thị thông tin + preview nguyên liệu
        cbOrder.setOnAction(e -> handleOrderSelected());

        loadRequestTable();
    }

    @FXML
    private void handleOrderSelected() {
        ProductionOrder order = cbOrder.getValue();
        if (order == null) {
            lblOrderInfo.setText("");
            tablePreview.getItems().clear();
            return;
        }

        lblOrderInfo.setText("Kem: " + order.getIce_cream_name()
                + "  |  Số kg: " + order.getPlanned_output_kg()
                + "  |  Trạng thái: " + order.getOrder_status());

        // Kiểm tra đã có phiếu chưa
        if (requestRepo.existsByOrderId(order.getProduction_order_id())) {
            lblMessage.setStyle("-fx-text-fill: orange;");
            lblMessage.setText("Lệnh sản xuất này đã có phiếu yêu cầu xuất kho.");
        } else {
            lblMessage.setText("");
        }

        // Preview nguyên liệu cần xuất (tính từ công thức * số kg)
        loadPreview(order);
    }

    private void loadPreview(ProductionOrder order) {
        // Tạo request tạm để tính preview (chưa lưu DB)
        IngredientExportRequest temp = new IngredientExportRequest();
        temp.setProduction_order_id(order.getProduction_order_id());
        temp.setPlanned_output_kg(order.getPlanned_output_kg().doubleValue());

        // Dùng query trực tiếp để preview
        List<IngredientExportRequestDetail> preview = requestRepo.previewDetails(
                order.getProduction_order_id(),
                order.getPlanned_output_kg());
        tablePreview.setItems(FXCollections.observableArrayList(preview));
    }

    @FXML
    private void handleCreate() {
        lblMessage.setStyle("-fx-text-fill: red;");

        ProductionOrder order = cbOrder.getValue();
        if (order == null) {
            lblMessage.setText("Vui lòng chọn lệnh sản xuất.");
            return;
        }

        if (requestRepo.existsByOrderId(order.getProduction_order_id())) {
            lblMessage.setText("Lệnh sản xuất này đã có phiếu yêu cầu xuất kho rồi.");
            return;
        }

        IngredientExportRequest request = new IngredientExportRequest();
        request.setProduction_order_id(order.getProduction_order_id());
        request.setPlanned_output_kg(order.getPlanned_output_kg().doubleValue());
        request.setNote(taNote.getText().trim());

        // Gán người tạo từ session
        if (CurrentUser.isLoggedIn()) {
            request.setRequested_by(CurrentUser.getUser().getUserId());
        }

        int newId = requestRepo.createWithDetails(request);
        if (newId > 0) {
            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setText("Tạo phiếu yêu cầu xuất kho thành công! ID: " + newId);
            handleReset();
            loadRequestTable();
        } else {
            lblMessage.setText("Tạo phiếu thất bại, vui lòng thử lại.");
        }
    }

    @FXML
    private void handleReset() {
        cbOrder.setValue(null);
        taNote.clear();
        lblMessage.setText("");
        lblOrderInfo.setText("");
        tablePreview.getItems().clear();
        // Reload lại danh sách lệnh draft
        cbOrder.setItems(FXCollections.observableArrayList(orderRepo.findByStatus("draft")));
    }

    @FXML
    private void handleViewDetails() {
        IngredientExportRequest selected = tableRequests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblMessage.setStyle("-fx-text-fill: orange;");
            lblMessage.setText("Vui lòng chọn một phiếu để xem chi tiết.");
            return;
        }
        List<IngredientExportRequestDetail> details =
                requestRepo.findDetailsByRequestId(selected.getIngredient_export_request_id());
        tablePreview.setItems(FXCollections.observableArrayList(details));
        lblMessage.setStyle("-fx-text-fill: blue;");
        lblMessage.setText("Chi tiết phiếu ID: " + selected.getIngredient_export_request_id()
                + " - " + selected.getIce_cream_name());
    }

    @FXML
    private void goBack() throws java.io.IOException {
        aptech.proj_NN_group2.App.setRoot(aptech.proj_NN_group2.util.StringValue.VIEW_MAIN_MENU);
    }

    private void loadRequestTable() {
        tableRequests.setItems(FXCollections.observableArrayList(requestRepo.findAll()));
    }
}
