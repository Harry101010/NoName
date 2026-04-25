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

import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import javafx.scene.control.cell.PropertyValueFactory;

public class IngredientIssueRequestController implements Initializable {


    @FXML private ComboBox<ProductionOrder> cbOrder;
    @FXML private Label lblOrderInfo;
    @FXML private TextArea taNote;
    @FXML private Label lblMessage;

    @FXML private TableView<IngredientExportRequestDetail> tablePreview;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colIngredient;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colUnit;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colQty;

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

        colIngredient.setCellValueFactory(new PropertyValueFactory<>("ingredient_name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit_name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("required_quantity"));

        colReqId.setCellValueFactory(new PropertyValueFactory<>("ingredient_export_request_id"));
        colReqIceCream.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        colReqKg.setCellValueFactory(new PropertyValueFactory<>("planned_output_kg"));
        colReqStatus.setCellValueFactory(new PropertyValueFactory<>("request_status"));
        colReqDate.setCellValueFactory(new PropertyValueFactory<>("requested_at"));
        colReqNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        cbOrder.setItems(FXCollections.observableArrayList(orderRepo.findByStatus("draft")));
        cbOrder.setConverter(new javafx.util.StringConverter<ProductionOrder>() {
            @Override
            public String toString(ProductionOrder o) {
                return o == null ? "" : "ID " + o.getProduction_order_id() + " - " + o.getIce_cream_name()
                        + " (" + o.getPlanned_output_kg() + " kg)";
            }

            @Override
            public ProductionOrder fromString(String s) {
                return null;
            }
        });

        cbOrder.setOnAction(e -> handleOrderSelected());loadRequestTable();
    }

    @FXML
    private void handleOrderSelected() {
        ProductionOrder order = cbOrder.getValue();
        if (order == null) {
            lblOrderInfo.setText("");
            tablePreview.getItems().clear();
            lblMessage.setText("");
            return;
        }

        lblOrderInfo.setText("Kem: " + order.getIce_cream_name()
                + "  |  Số kg: " + order.getPlanned_output_kg()
                + "  |  Trạng thái: " + order.getOrder_status());


        if (requestRepo.existsByOrderId(order.getProduction_order_id())) {
            lblMessage.setStyle("-fx-text-fill: orange;");
            lblMessage.setText("Lệnh sản xuất này đã có phiếu yêu cầu xuất kho.");
        } else {
            lblMessage.setText("");
        }
        loadPreview(order);
    }
    
    private void loadPreview(ProductionOrder order) {

        List<IngredientExportRequestDetail> preview = requestRepo.previewDetails(
                order.getProduction_order_id(),
                order.getPlanned_output_kg()
        ); 
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
        request.setNote(taNote.getText() == null ? "" : taNote.getText().trim());

        try {
            request.setRequested_by(CurrentUser.requireUser().getUserId());
        } catch (IllegalStateException ex) {
            lblMessage.setText(ex.getMessage());
            return; }

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
    private void goBack(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_MAIN_MENU, "Hệ thống Quản lý Sản xuất & Xuất kho");
    }

    private void loadRequestTable() {
        tableRequests.setItems(FXCollections.observableArrayList(requestRepo.findAll()));
    }
 // Sửa hàm initialize hoặc thêm hàm này
    public void refreshData() {
        // Nạp lại danh sách lệnh sản xuất 'draft'
        cbOrder.setItems(FXCollections.observableArrayList(orderRepo.findByStatus("draft")));
        
        // Nạp lại danh sách phiếu yêu cầu (nếu cần)
        loadRequestTable();
        
        // Reset các thông tin cũ
        lblOrderInfo.setText("");
        tablePreview.getItems().clear();
    }
}
