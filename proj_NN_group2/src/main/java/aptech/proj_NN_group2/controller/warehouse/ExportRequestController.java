package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.model.business.repository.IngredientExportRequestRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportRequest;
import aptech.proj_NN_group2.model.entity.IngredientExportRequestDetail;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExportRequestController implements Initializable {

    @FXML
    private TableView<IngredientExportRequest> tblRequests;

    @FXML
    private TableColumn<IngredientExportRequest, Integer> colId;

    @FXML
    private TableColumn<IngredientExportRequest, Integer> colOrder;

    @FXML
    private TableColumn<IngredientExportRequest, String> colStatus;

    @FXML
    private TableColumn<IngredientExportRequest, String> colDate;

    @FXML
    private TableColumn<IngredientExportRequest, String> colNote;

    private final IngredientExportRequestRepository repo =
            new IngredientExportRequestRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("ingredient_export_request_id")
        );
        colOrder.setCellValueFactory(
                new PropertyValueFactory<>("production_order_id")
        );
        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("request_status")
        );
        colDate.setCellValueFactory(
                new PropertyValueFactory<>("requested_at")
        );
        colNote.setCellValueFactory(
                new PropertyValueFactory<>("note")
        );

        // tô màu cột trạng thái
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

                    case "approved":
                        setStyle("""
                            -fx-background-color: #f1c40f;
                            -fx-text-fill: black;
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
                        break;
                }
            }
        });

        loadData();
    }

    private void loadData() {
        tblRequests.setItems(
                FXCollections.observableArrayList(repo.findAll())
        );
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {

        IngredientExportRequest selected =
                tblRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Vui lòng chọn một yêu cầu.");
            return;
        }

        List<IngredientExportRequestDetail> details =
                repo.findDetailsByRequestId(
                        selected.getIngredient_export_request_id()
                );

        if (details.isEmpty()) {
            showWarning("Yêu cầu này chưa có nguyên liệu nào từ phía sản xuất.");
            return;
        }

        StringBuilder content = new StringBuilder();

        content.append("Lệnh sản xuất: ")
                .append(selected.getProduction_order_id())
                .append("\n");

        content.append("Trạng thái: ")
                .append(selected.getRequest_status())
                .append("\n\n");

        content.append("Danh sách nguyên liệu cần xuất:\n\n");

        for (IngredientExportRequestDetail d : details) {
            content.append("• ")
                    .append(d.getIngredient_name())
                    .append(" - ")
                    .append(d.getRequired_quantity())
                    .append(" ")
                    .append(d.getUnit_name())
                    .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết yêu cầu xuất kho");
        alert.setHeaderText(
                "Yêu cầu #" + selected.getIngredient_export_request_id()
        );
        alert.setContentText(content.toString());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }

    @FXML
    private void handleApprove(ActionEvent event) {

        IngredientExportRequest selected =
                tblRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Vui lòng chọn yêu cầu cần duyệt.");
            return;
        }

        try {
            boolean ok = repo.approveRequestWithFIFO(
                    selected.getIngredient_export_request_id()
            );

            if (ok) {
                loadData();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đã duyệt yêu cầu và trừ kho thành công.");
                alert.showAndWait();
            }

        } catch (RuntimeException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Không thể duyệt");
            alert.setHeaderText("Duyệt yêu cầu thất bại");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleReject(ActionEvent event) {

        IngredientExportRequest selected =
                tblRequests.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Vui lòng chọn một yêu cầu để từ chối.");
            return;
        }

        boolean ok = repo.updateStatus(
                selected.getIngredient_export_request_id(),
                "rejected"
        );

        if (ok) {
            loadData();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Đã từ chối yêu cầu.");
            alert.showAndWait();
        } else {
            showWarning("Không thể cập nhật trạng thái.");
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Làm mới");
        alert.setHeaderText(null);
        alert.setContentText("Dữ liệu đã được làm mới.");
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/aptech/proj_NN_group2/warehouse/warehouse_dashboard.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) tblRequests.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quản lý kho");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể quay lại giao diện kho.");
            alert.showAndWait();
        }
    }
}