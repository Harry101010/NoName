package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.model.business.repository.IngredientExportRequestRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportRequest;
import aptech.proj_NN_group2.model.entity.IngredientExportRequestDetail;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExportRequestController implements Initializable {

    @FXML private TableView<IngredientExportRequest> tblRequests;

    @FXML private TableColumn<IngredientExportRequest, Integer> colId;
    @FXML private TableColumn<IngredientExportRequest, Integer> colOrder;
    @FXML private TableColumn<IngredientExportRequest, String> colStatus;
    @FXML private TableColumn<IngredientExportRequest, String> colDate;
    @FXML private TableColumn<IngredientExportRequest, String> colNote;

    private final IngredientExportRequestRepository repo =
            new IngredientExportRequestRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // ===== BIND DATA =====
        colId.setCellValueFactory(new PropertyValueFactory<>("ingredient_export_request_id"));
        colOrder.setCellValueFactory(new PropertyValueFactory<>("production_order_id"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("request_status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("requested_at"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        // ===== STYLE STATUS =====
        setupStatusColor();

        // ===== ROW COLOR (PRO) =====
        setupRowColor();

        // ===== LOAD DATA =====
        loadData();
    }

    // =========================
    // LOAD DATA
    // =========================
    private void loadData() {
        var list = repo.findAll();
        System.out.println("Requests: " + list.size());
        tblRequests.setItems(FXCollections.observableArrayList(list));
    }

    // =========================
    // COLOR STATUS CELL
    // =========================
    private void setupStatusColor() {
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(status);
                setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");

                switch (status.toLowerCase()) {

                    case "pending":
                        setStyle("""
                            -fx-background-color: #f1c40f;
                            -fx-text-fill: black;
                            -fx-font-weight: bold;
                            -fx-alignment: CENTER;
                        """);
                        break;

                    case "approved":
                        setStyle("""
                            -fx-background-color: #2ecc71;
                            -fx-text-fill: white;
                            -fx-font-weight: bold;
                            -fx-alignment: CENTER;
                        """);
                        break;

                    case "rejected":
                        setStyle("""
                            -fx-background-color: #e74c3c;
                            -fx-text-fill: white;
                            -fx-font-weight: bold;
                            -fx-alignment: CENTER;
                        """);
                        break;

                    default:
                        setStyle("""
                            -fx-background-color: #bdc3c7;
                            -fx-text-fill: black;
                            -fx-alignment: CENTER;
                        """);
                }
            }
        });
    }

    // =========================
    // COLOR ROW (PRO)
    // =========================
    private void setupRowColor() {
        tblRequests.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(IngredientExportRequest item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    switch (item.getRequest_status().toLowerCase()) {
                        case "approved":
                            setStyle("-fx-background-color: #d5f5e3;");
                            break;
                        case "rejected":
                            setStyle("-fx-background-color: #fadbd8;");
                            break;
                        case "pending":
                            setStyle("-fx-background-color: #fcf3cf;");
                            break;
                    }
                }
            }
        });
    }

    // =========================
    // VIEW DETAILS
    // =========================
    @FXML
    private void handleViewDetails(ActionEvent event) {

        var selected = tblRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Vui lòng chọn một yêu cầu.");
            return;
        }

        List<IngredientExportRequestDetail> details =
                repo.findDetailsByRequestId(
                        selected.getIngredient_export_request_id()
                );

        if (details.isEmpty()) {
            showWarning("Yêu cầu này chưa có nguyên liệu.");
            return;
        }

        StringBuilder content = new StringBuilder();

        content.append("Lệnh sản xuất: ")
                .append(selected.getProduction_order_id())
                .append("\n");

        content.append("Trạng thái: ")
                .append(selected.getRequest_status())
                .append("\n\n");

        content.append("Danh sách nguyên liệu:\n\n");

        for (var d : details) {
            content.append("• ")
                    .append(d.getIngredient_name())
                    .append(" - ")
                    .append(d.getRequired_quantity())
                    .append(" ")
                    .append(d.getUnit_name())
                    .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết yêu cầu");
        alert.setHeaderText("Yêu cầu #" + selected.getIngredient_export_request_id());
        alert.setContentText(content.toString());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }

    // =========================
    // APPROVE
    // =========================
    @FXML
    private void handleApprove(ActionEvent event) {

        var selected = tblRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Vui lòng chọn yêu cầu.");
            return;
        }

        if (!selected.getRequest_status().equalsIgnoreCase("pending")) {
            showWarning("Yêu cầu đã được xử lý.");
            return;
        }

        try {
            boolean ok = repo.approveRequestWithFIFO(
                    selected.getIngredient_export_request_id()
            );

            if (ok) {
                loadData();
                showInfo("Đã duyệt và trừ kho thành công.");
            }

        } catch (RuntimeException ex) {
            showError("Duyệt thất bại", ex.getMessage());
        }
    }

    // =========================
    // REJECT
    // =========================
    @FXML
    private void handleReject(ActionEvent event) {

        var selected = tblRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Vui lòng chọn yêu cầu.");
            return;
        }

        if (!selected.getRequest_status().equalsIgnoreCase("pending")) {
            showWarning("Yêu cầu đã được xử lý.");
            return;
        }

        boolean ok = repo.updateStatus(
                selected.getIngredient_export_request_id(),
                "rejected"
        );

        if (ok) {
            loadData();
            showInfo("Đã từ chối yêu cầu.");
        } else {
            showWarning("Không thể cập nhật.");
        }
    }

    // =========================
    // REFRESH
    // =========================
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
        showInfo("Đã làm mới dữ liệu.");
    }

    // =========================
    // BACK (FIX CHUẨN)
    // =========================
    @FXML
    private void handleBack(ActionEvent event) {
        NavigationUtil.goTo(
                event,
                StringValue.VIEW_WAREHOUSE_DASHBOARD,
                "Quản lý kho"
        );
    }

    // =========================
    // ALERT HELPERS
    // =========================
    private void showWarning(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}