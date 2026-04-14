package aptech.proj_NN_group2.controller.saleman;

import aptech.proj_NN_group2.model.entity.saleman.IssueNote;
import aptech.proj_NN_group2.model.entity.saleman.ProductIssueDetail;
import aptech.proj_NN_group2.model.business.repository.saleman.SalemanRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalemanController {

    // --- PHẦN TẠO PHIẾU ---
    @FXML private TextField txtCustomerName, txtOrderCode, txtQuantity;
    @FXML private DatePicker dpDeliveryDate;
    @FXML private ComboBox<String> cbProducts;
    
    @FXML private TableView<ProductIssueDetail> tableTempDetails;
    @FXML private TableColumn<ProductIssueDetail, String> colProductName;
    @FXML private TableColumn<ProductIssueDetail, Double> colQuantity;
    @FXML private TableColumn<ProductIssueDetail, Void> colAction;

    // --- PHẦN LỊCH SỬ ---
    @FXML private TableView<IssueNote> tableHistory;
    @FXML private TableColumn<IssueNote, Integer> colHistoryId;
    @FXML private TableColumn<IssueNote, String> colHistoryOrderCode, colHistorySaleman, colHistoryCustomer, colHistoryStatus;
    @FXML private TableColumn<IssueNote, String> colHistoryProductName; 
    @FXML private TableColumn<IssueNote, Double> colHistoryProductQty;
    @FXML private TableColumn<IssueNote, LocalDateTime> colHistoryCreateDate, colHistoryDeliveryDate;
    @FXML private TableColumn<IssueNote, Void> colHistoryAction; 

    private SalemanRepository repo = new SalemanRepository();
    private ObservableList<ProductIssueDetail> tempDetails = FXCollections.observableArrayList();
    private ObservableList<IssueNote> historyList = FXCollections.observableArrayList();
    
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Áp dụng định dạng ngày Việt Nam cho DatePicker Tạo phiếu
        dpDeliveryDate.setConverter(createVietnameseDateConverter());

        cbProducts.setItems(FXCollections.observableArrayList(repo.getActiveIceCreams()));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tableTempDetails.setItems(tempDetails);
        setupActionColumn(); 

        colHistoryId.setCellValueFactory(new PropertyValueFactory<>("noteId"));
        colHistoryOrderCode.setCellValueFactory(new PropertyValueFactory<>("customerOrderCode"));
        colHistorySaleman.setCellValueFactory(new PropertyValueFactory<>("salemanName")); 
        colHistoryCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colHistoryProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colHistoryProductQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colHistoryCreateDate.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        colHistoryDeliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        colHistoryStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        formatDateColumn(colHistoryCreateDate, dateTimeFormatter);
        formatDateColumn(colHistoryDeliveryDate, dateFormatter);
        setupStatusColoring(colHistoryStatus);
        setupHistoryActionColumn(); 
        
        tableHistory.setItems(historyList);
        loadHistory();
    }

    /**
     * Hàm hỗ trợ chuyển đổi DatePicker sang định dạng dd/MM/yyyy
     */
    private StringConverter<LocalDate> createVietnameseDateConverter() {
        return new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
            }
        };
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

    private void setupHistoryActionColumn() {
        colHistoryAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

                btnDelete.setOnAction(event -> {
                    IssueNote note = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Xóa phiếu #" + note.getNoteId() + "?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(type -> {
                        if (type == ButtonType.YES && repo.deleteNote(note.getNoteId())) loadHistory();
                    });
                });

                btnEdit.setOnAction(event -> showEditHistoryDialog(getTableView().getItems().get(getIndex())));
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    IssueNote note = getTableView().getItems().get(getIndex());
                    // ĐÃ CẬP NHẬT: Chỉ hiển thị nút Sửa/Xóa khi trạng thái là "Chờ duyệt"
                    boolean isActionable = "Chờ duyệt".equals(note.getStatus());
                    pane.setVisible(isActionable);
                    setGraphic(pane);
                }
            }
        });
    }

    private void formatDateColumn(TableColumn<IssueNote, LocalDateTime> column, DateTimeFormatter dtf) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : dtf.format(item));
            }
        });
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);
            {
                btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setOnAction(e -> tempDetails.remove(getTableView().getItems().get(getIndex())));
                btnEdit.setOnAction(e -> showEditDialog(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void showEditDialog(ProductIssueDetail detail) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(detail.getQuantity()));
        dialog.setTitle("Sửa số lượng");
        dialog.setHeaderText("Sản phẩm: " + detail.getProductName());
        dialog.setContentText("Nhập số lượng mới (kg):");
        dialog.showAndWait().ifPresent(r -> {
            try { 
                detail.setQuantity(Double.parseDouble(r)); 
                tableTempDetails.refresh(); 
            } catch (Exception e) { 
                showAlert("Lỗi", "Số lượng không hợp lệ!"); 
            }
        });
    }

    private void showEditHistoryDialog(IssueNote note) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin phiếu #" + note.getNoteId());
        
        ButtonType saveBtn = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        
        TextField editOrder = new TextField(note.getCustomerOrderCode());
        
        TextField editSaleman = new TextField(note.getSalemanName());
        editSaleman.setDisable(true); 
        
        TextField editCustomer = new TextField(note.getCustomerName());
        
        TextField editProduct = new TextField(note.getProductName());
        editProduct.setDisable(true); 
        
        TextField editQuantity = new TextField(String.valueOf(note.getQuantity()));
        
        DatePicker editDelivery = new DatePicker(note.getDeliveryDate() != null ? note.getDeliveryDate().toLocalDate() : null);
        editDelivery.setConverter(createVietnameseDateConverter()); 

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 10, 20));
        
        grid.add(new Label("Mã đơn khách:"), 0, 0); 
        grid.add(editOrder, 1, 0);
        
        grid.add(new Label("Nhân viên:"), 0, 1); 
        grid.add(editSaleman, 1, 1);
        
        grid.add(new Label("Tên khách hàng:"), 0, 2); 
        grid.add(editCustomer, 1, 2);
        
        grid.add(new Label("Sản phẩm:"), 0, 3); 
        grid.add(editProduct, 1, 3);
        
        grid.add(new Label("Số lượng (kg):"), 0, 4); 
        grid.add(editQuantity, 1, 4);
        
        grid.add(new Label("Ngày giao:"), 0, 5); 
        grid.add(editDelivery, 1, 5);

        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    note.setCustomerOrderCode(editOrder.getText().trim());
                    note.setCustomerName(editCustomer.getText().trim());
                    note.setQuantity(Double.parseDouble(editQuantity.getText().trim()));
                    
                    if (editDelivery.getValue() != null) {
                        note.setDeliveryDate(editDelivery.getValue().atStartOfDay());
                    }
                    
                    if (repo.updateIssueNote(note)) { 
                        showAlert("Thành công", "Đã cập nhật phiếu thành công!"); 
                        loadHistory(); 
                    } else {
                        showAlert("Lỗi", "Không thể cập nhật phiếu, vui lòng kiểm tra lại hệ thống!"); 
                    }
                } catch (NumberFormatException e) {
                    showAlert("Lỗi nhập liệu", "Số lượng sản phẩm phải là một số hợp lệ!");
                }
            }
        });
    }

    @FXML 
    public void loadHistory() { 
        historyList.setAll(repo.findAllNotes()); 
    }

    @FXML 
    private void addItemToTable() {
        try {
            String selected = cbProducts.getValue();
            if (selected == null) {
                showAlert("Cảnh báo", "Vui lòng chọn sản phẩm!");
                return;
            }
            double qty = Double.parseDouble(txtQuantity.getText());
            if (qty <= 0) throw new Exception();

            int id = Integer.parseInt(selected.split(" - ")[0]);
            String name = selected.split(" - ")[1];
            
            tempDetails.add(new ProductIssueDetail(id, name, qty));
            txtQuantity.clear();
        } catch (Exception e) { 
            showAlert("Lỗi", "Số lượng không hợp lệ!"); 
        }
    }

    @FXML 
    private void handleSaveNote() {
        if (txtCustomerName.getText().isEmpty() || dpDeliveryDate.getValue() == null || tempDetails.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin khách hàng và ít nhất một sản phẩm!");
            return;
        }
        IssueNote note = new IssueNote();
        note.setCustomerName(txtCustomerName.getText().trim());
        note.setCustomerOrderCode(txtOrderCode.getText().trim());
        note.setDeliveryDate(dpDeliveryDate.getValue().atStartOfDay());
        
        if (repo.createFullIssueNote(note, tempDetails)) { 
            showAlert("Thành công", "Đã gửi yêu cầu xuất kho!"); 
            handleClear(); 
            loadHistory(); 
        } else {
            showAlert("Lỗi", "Không thể lưu phiếu!");
        }
    }

    @FXML 
    private void handleClear() {
        txtCustomerName.clear(); 
        txtOrderCode.clear(); 
        txtQuantity.clear();
        dpDeliveryDate.setValue(null); 
        tempDetails.clear();
        cbProducts.getSelectionModel().clearSelection();
    }

    private void showAlert(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); 
        a.setTitle(t); 
        a.setHeaderText(null);
        a.setContentText(c); 
        a.showAndWait();
    }

    public void refreshHistory() {
        loadHistory();
    }
}