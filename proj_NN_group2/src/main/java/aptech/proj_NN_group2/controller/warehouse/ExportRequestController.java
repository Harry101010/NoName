package aptech.proj_NN_group2.controller.warehouse;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.IngredientExportRequestRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportRequest;
import aptech.proj_NN_group2.model.entity.IngredientExportRequestDetail;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

public class ExportRequestController implements Initializable {

    @FXML private TableView<IngredientExportRequest> tblRequests;
    @FXML private TableColumn<IngredientExportRequest, Integer> colId;
    @FXML private TableColumn<IngredientExportRequest, Integer> colOrder;
    @FXML private TableColumn<IngredientExportRequest, String> colStatus;
    @FXML private TableColumn<IngredientExportRequest, String> colDate;
    @FXML private TableColumn<IngredientExportRequest, String> colNote;

    private final IngredientExportRequestRepository repo = new IngredientExportRequestRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("ingredient_export_request_id"));
        colOrder.setCellValueFactory(new PropertyValueFactory<>("production_order_id"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("request_status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("requested_at"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        setupStatusColor();
        setupRowColor();
        loadData();
    }

    private void loadData() {
        List<IngredientExportRequest> list = repo.findAll();
        tblRequests.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        IngredientExportRequest selected = tblRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Vui lòng chọn yêu cầu!");
            return;
        }

        if (!selected.getRequest_status().equalsIgnoreCase("pending")) {
            showWarning("Yêu cầu đã được xử lý.");
            return;
        }

        try {
            // Gọi hàm xử lý FIFO trong Repository (Đã chuẩn hóa)
            boolean ok = repo.approveRequestWithFIFO(selected.getIngredient_export_request_id());
            if (ok) {
                loadData();
                showInfo("Duyệt phiếu và trừ kho thành công!");
            } else {
                showError("Lỗi", "Không đủ nguyên liệu hoặc lỗi hệ thống.");
            }
        } catch (RuntimeException ex) {
            showError("Duyệt thất bại", ex.getMessage());
        }
    }

    @FXML
    private void handleReject(ActionEvent event) {
        IngredientExportRequest selected = tblRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Vui lòng chọn yêu cầu.");
            return;
        }

        if (!selected.getRequest_status().equalsIgnoreCase("pending")) {
            showWarning("Yêu cầu đã được xử lý.");
            return;
        }

        boolean ok = repo.updateStatus(selected.getIngredient_export_request_id(), "rejected");

        if (ok) {
            loadData();
            showInfo("Đã từ chối yêu cầu.");
        } else {
            showWarning("Không thể cập nhật trạng thái.");
        }
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        IngredientExportRequest selected = tblRequests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Vui lòng chọn một yêu cầu.");
            return;
        }

        List<IngredientExportRequestDetail> details = repo.findDetailsByRequestId(selected.getIngredient_export_request_id());

        if (details.isEmpty()) {
            showWarning("Yêu cầu này chưa có nguyên liệu.");
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("Lệnh sản xuất: ").append(selected.getProduction_order_id()).append("\n");
        content.append("Danh sách nguyên liệu:\n\n");

        for (IngredientExportRequestDetail d : details) {
            content.append("• ").append(d.getIngredient_name())
                   .append(" - ").append(d.getRequired_quantity())
                   .append(" ").append(d.getUnit_name()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết yêu cầu");
        alert.setContentText(content.toString());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_WAREHOUSE_DASHBOARD, "Quản lý kho");
    }

    // Helpers
    private void showWarning(String msg) { new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }
    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
    private void showError(String title, String msg) { 
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait(); 
    }

    private void setupStatusColor() { /* ... Giữ nguyên code cũ của bạn ... */ }
    private void setupRowColor() { /* ... Giữ nguyên code cũ của bạn ... */ }
}