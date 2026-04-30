package aptech.proj_NN_group2.controller.sales;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import aptech.proj_NN_group2.model.business.repository.SalemanRepository;
import aptech.proj_NN_group2.model.entity.IssueNote;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class WarehouseApprovalController {
    @FXML private TableView<IssueNote> tblPending;
    @FXML private TableColumn<IssueNote, Integer> colId;
    @FXML private TableColumn<IssueNote, String> colCustomer, colProduct, colStatus;
    @FXML private TableColumn<IssueNote, Double> colQty;
    @FXML private TableColumn<IssueNote, LocalDateTime> colDate;
    @FXML private ComboBox<String> cbQuality;

    private final SalemanRepository repo = new SalemanRepository();

    @FXML
    public void initialize() {
        // Cấu hình các cột
        colId.setCellValueFactory(new PropertyValueFactory<>("noteId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Định dạng ngày hiển thị
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        // Thiết lập ComboBox chất lượng
        cbQuality.setItems(FXCollections.observableArrayList("Qualified", "Damaged", "Near Expiry"));
        cbQuality.getSelectionModel().selectFirst();

        loadPendingRequests();
    }

    @FXML
    public void loadPendingRequests() {
        tblPending.getItems().setAll(repo.getPendingRequests());
    }

    @FXML
    private void handleApprove() {
        IssueNote selected = tblPending.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a request to approve!");
            return;
        }

        try {
            boolean success = repo.processApprovalFIFO(selected.getNoteId(), cbQuality.getValue(), "Approved by Warehouse");
            if (success) {
                showAlert("Success", "Order dispatched successfully using FIFO!");
                loadPendingRequests();
            }
        } catch (Exception e) {
            showAlert("Error", "Error during FIFO processing: " + e.getMessage());
        }
    }

    
    @FXML
    private void handleReject() {
        IssueNote selected = tblPending.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Gọi hàm đã tạo ở Bước 1
            repo.updateRequestStatus(selected.getNoteId(), "Rejected");
            loadPendingRequests();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}