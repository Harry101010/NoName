package aptech.proj_NN_group2.controller.sales;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import aptech.proj_NN_group2.model.business.repository.SalemanRepository;
import aptech.proj_NN_group2.model.entity.IssueNote;
import aptech.proj_NN_group2.model.entity.sales.FinishedProductInventory;
import aptech.proj_NN_group2.util.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class FinishedProductWarehouseController {

    @FXML private TableView<IssueNote> tableApproval;
    @FXML private TableColumn<IssueNote, String> colOrderCode;
    @FXML private TableColumn<IssueNote, String> colCustomer;
    @FXML private TableColumn<IssueNote, String> colProductName;
    @FXML private TableColumn<IssueNote, String> colStatus;
    @FXML private TableColumn<IssueNote, String> colSaleman;
    @FXML private TableColumn<IssueNote, Integer> colId;
    @FXML private TableColumn<IssueNote, Double> colQty;
    @FXML private TableColumn<IssueNote, Void> colAction;

    @FXML private TableView<FinishedProductInventory> tableInventory;
    @FXML private TableColumn<FinishedProductInventory, String> colInvPO;
    @FXML private TableColumn<FinishedProductInventory, String> colInvName;
    @FXML private TableColumn<FinishedProductInventory, String> colInvLocation;
    @FXML private TableColumn<FinishedProductInventory, Double> colInvQty;
    @FXML private TableColumn<FinishedProductInventory, LocalDateTime> colInvMfg;
    @FXML private TableColumn<FinishedProductInventory, LocalDateTime> colInvExp;
    @FXML private TextField txtSearchInventory;
    @FXML private TableColumn<FinishedProductInventory, Integer> colInvOrderId;
    

    private final SalemanRepository repo = new SalemanRepository();
    private final ObservableList<IssueNote> pendingList = FXCollections.observableArrayList();
    private final ObservableList<FinishedProductInventory> inventoryMasterData = FXCollections.observableArrayList();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
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
        col.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Đã duyệt")) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (item.equals("Từ chối")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupApprovalActions() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnApprove = new Button("Duyệt");
            private final Button btnReject = new Button("Từ chối");
            private final HBox pane = new HBox(10, btnApprove, btnReject);

            {
                btnApprove.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                btnReject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");

                btnApprove.setOnAction(e -> {
                    IssueNote n = getTableView().getItems().get(getIndex());
                    if (!DialogUtil.confirmYesNo(tableApproval, "Xác nhận", "Xác nhận xuất kho cho: " + n.getProductName() + "?")) {
                        return;
                    }
                    if (repo.approveIssueNote(n.getNoteId())) {
                        loadPendingNotes();
                        loadInventoryData();
                    } else {
                        DialogUtil.error(tableApproval, "Lỗi", "Không thể duyệt phiếu.");
                    }
                });

                btnReject.setOnAction(e -> {
                    IssueNote n = getTableView().getItems().get(getIndex());
                    if (!DialogUtil.confirmYesNo(tableApproval, "Xác nhận", "Từ chối phiếu này?")) {
                        return;
                    }
                    if (repo.rejectIssueNote(n.getNoteId())) {
                        loadPendingNotes();
                    } else {
                        DialogUtil.error(tableApproval, "Lỗi", "Không thể từ chối phiếu.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupSearchFilter() {
        FilteredList<FinishedProductInventory> filteredData = new FilteredList<>(inventoryMasterData, p -> true);
        txtSearchInventory.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(item -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lowerFilter = newVal.toLowerCase();
                return item.getProductName().toLowerCase().contains(lowerFilter)
                        || (item.getPoCode() != null && item.getPoCode().toLowerCase().contains(lowerFilter));
            });
        });
        tableInventory.setItems(filteredData);
    }

    @FXML
    private void loadPendingNotes() {
        pendingList.setAll(repo.findAllNotes().stream()
                .filter(n -> "Chờ duyệt".equals(n.getStatus()))
                .toList());
        tableApproval.setItems(pendingList);
    }

    @FXML
    private void loadInventoryData() {
        inventoryMasterData.setAll(repo.getFinishedInventory());
    }

    private void formatDateColumnInv(TableColumn<FinishedProductInventory, LocalDateTime> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dtf.format(item));
            }
        });
    }

    public void refreshData() {
        loadPendingNotes();
        loadInventoryData();
    }
}