package aptech.proj_NN_group2.controller.saleman;

import aptech.proj_NN_group2.model.entity.saleman.IssueNote;
import aptech.proj_NN_group2.model.entity.saleman.FinishedProductInventory;
import aptech.proj_NN_group2.model.business.repository.saleman.SalemanRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FinishedProductWarehouseController {
    // Table Phê duyệt
    @FXML private TableView<IssueNote> tableApproval;
    @FXML private TableColumn<IssueNote, String> colOrderCode, colCustomer, colProductName, colStatus, colSaleman;
    @FXML private TableColumn<IssueNote, Integer> colId;
    @FXML private TableColumn<IssueNote, Double> colQty;
    @FXML private TableColumn<IssueNote, Void> colAction;

    // Table Tồn kho
    @FXML private TableView<FinishedProductInventory> tableInventory;
    @FXML private TableColumn<FinishedProductInventory, String> colInvPO, colInvName, colInvLocation;
    @FXML private TableColumn<FinishedProductInventory, Double> colInvQty;
    @FXML private TableColumn<FinishedProductInventory, LocalDateTime> colInvMfg, colInvExp;
    @FXML private TextField txtSearchInventory;

    private SalemanRepository repo = new SalemanRepository();
    private ObservableList<IssueNote> pendingList = FXCollections.observableArrayList();
    private ObservableList<FinishedProductInventory> inventoryMasterData = FXCollections.observableArrayList();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // --- 1. Khởi tạo Bảng Phê Duyệt ---
        colId.setCellValueFactory(new PropertyValueFactory<>("noteId"));
        colOrderCode.setCellValueFactory(new PropertyValueFactory<>("customerOrderCode"));
        colSaleman.setCellValueFactory(new PropertyValueFactory<>("salemanName"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        setupStatusColoring(colStatus); 
        setupApprovalActions();
        loadPendingNotes();

        // --- 2. Khởi tạo Bảng Tồn Kho ---
        colInvPO.setCellValueFactory(new PropertyValueFactory<>("poCode"));
        colInvName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colInvQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colInvMfg.setCellValueFactory(new PropertyValueFactory<>("mfgDate"));
        colInvExp.setCellValueFactory(new PropertyValueFactory<>("expDate"));
        colInvLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        formatDateColumnInv(colInvMfg);
        formatDateColumnInv(colInvExp);
        setupSearchFilter();
        loadInventoryData();
    }

    private void setupStatusColoring(TableColumn<IssueNote, String> col) {
        col.setCellFactory(column -> new TableCell<IssueNote, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Đã duyệt")) setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    else if (item.equals("Từ chối")) setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void setupApprovalActions() {
        colAction.setCellFactory(param -> new TableCell<IssueNote, Void>() {
            private final Button btnApprove = new Button("Duyệt");
            private final Button btnReject = new Button("Từ chối");
            private final HBox pane = new HBox(10, btnApprove, btnReject);
            {
                btnApprove.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                btnReject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                
                btnApprove.setOnAction(e -> {
                    IssueNote n = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Xác nhận xuất kho cho: " + n.getProductName() + "?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(type -> {
                        if (type == ButtonType.YES) {
                            if (repo.approveIssueNote(n.getNoteId())) {
                                // Cập nhật cả 2 bảng để thấy số lượng tồn kho giảm ngay lập tức
                                loadPendingNotes();
                                loadInventoryData();
                            }
                        }
                    });
                });
                
                btnReject.setOnAction(e -> {
                    IssueNote n = getTableView().getItems().get(getIndex());
                    if (repo.rejectIssueNote(n.getNoteId())) {
                        loadPendingNotes();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupSearchFilter() {
        FilteredList<FinishedProductInventory> filteredData = new FilteredList<>(inventoryMasterData, p -> true);
        txtSearchInventory.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(item -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lowerFilter = newVal.toLowerCase();
                if (item.getProductName().toLowerCase().contains(lowerFilter)) return true;
                if (item.getPoCode() != null && item.getPoCode().toLowerCase().contains(lowerFilter)) return true;
                return false;
            });
        });
        tableInventory.setItems(filteredData);
    }

    @FXML 
    private void loadPendingNotes() { 
        // Thủ kho chỉ xem các phiếu có trạng thái "Chờ duyệt"
        pendingList.setAll(repo.findAllNotes().stream()
                .filter(n -> "Chờ duyệt".equals(n.getStatus()))
                .toList()); 
        tableApproval.setItems(pendingList); 
    }
    
    @FXML
    private void loadInventoryData() { 
        // Lấy dữ liệu tồn kho mới nhất từ DB
        inventoryMasterData.setAll(repo.getFinishedInventory()); 
    }

    private void formatDateColumnInv(TableColumn<FinishedProductInventory, LocalDateTime> col) {
        col.setCellFactory(c -> new TableCell<FinishedProductInventory, LocalDateTime>() {
            @Override protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dtf.format(item));
            }
        });
    }
 // Thêm hàm này để bên ngoài có thể gọi làm mới bảng
    public void refreshData() {
        loadPendingNotes();
        loadInventoryData();
    }
}