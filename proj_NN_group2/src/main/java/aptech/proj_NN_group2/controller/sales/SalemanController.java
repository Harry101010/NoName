package aptech.proj_NN_group2.controller.sales;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import aptech.proj_NN_group2.model.business.repository.SalemanRepository;
import aptech.proj_NN_group2.model.entity.IssueNote;
import aptech.proj_NN_group2.model.entity.ProductIssueDetail;
import aptech.proj_NN_group2.util.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class SalemanController {

    @FXML private TextField txtCustomerName, txtOrderCode, txtQuantity;
    @FXML private DatePicker dpDeliveryDate;
    @FXML private ComboBox<String> cbProducts;

    @FXML private TableView<ProductIssueDetail> tableTempDetails;
    @FXML private TableColumn<ProductIssueDetail, String> colProductName;
    @FXML private TableColumn<ProductIssueDetail, Double> colQuantity;
    @FXML private TableColumn<ProductIssueDetail, Void> colAction;

    @FXML private TableView<IssueNote> tableHistory;
    @FXML private TableColumn<IssueNote, Integer> colHistoryId;
    @FXML private TableColumn<IssueNote, String> colHistoryOrderCode, colHistorySaleman, colHistoryCustomer, colHistoryStatus;
    @FXML private TableColumn<IssueNote, String> colHistoryProductName;
    @FXML private TableColumn<IssueNote, Double> colHistoryProductQty;
    @FXML private TableColumn<IssueNote, LocalDateTime> colHistoryCreateDate, colHistoryDeliveryDate;
    @FXML private TableColumn<IssueNote, Void> colHistoryAction;

    private final SalemanRepository repo = new SalemanRepository();
    private final ObservableList<ProductIssueDetail> tempDetails = FXCollections.observableArrayList();
    private final ObservableList<IssueNote> historyList = FXCollections.observableArrayList();

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
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

    private StringConverter<LocalDate> createVietnameseDateConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
            }
        };
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
                    if (DialogUtil.confirmYesNo(tableHistory, "Xác nhận", "Xóa phiếu #" + note.getNoteId() + "?")) {
                        if (repo.deleteNote(note.getNoteId())) {
                            loadHistory();
                        } else {
                            DialogUtil.error(tableHistory, "Lỗi", "Không thể xóa phiếu.");
                        }
                    }
                });

                btnEdit.setOnAction(event -> showEditHistoryDialog(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    IssueNote note = getTableView().getItems().get(getIndex());
                    boolean isActionable = "Chờ duyệt".equals(note.getStatus());
                    pane.setVisible(isActionable);
                    setGraphic(pane);
                }
            }
        });
    }

    private void formatDateColumn(TableColumn<IssueNote, LocalDateTime> column, DateTimeFormatter dtf) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
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

                btnDelete.setOnAction(e -> {
                    ProductIssueDetail detail = getTableView().getItems().get(getIndex());
                    tempDetails.remove(detail);
                });

                btnEdit.setOnAction(e -> showEditDialog(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
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
                DialogUtil.error(tableTempDetails, "Lỗi", "Số lượng không hợp lệ!");
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
                        DialogUtil.info(tableHistory, "Thành công", "Đã cập nhật phiếu thành công!");
                        loadHistory();
                    } else {
                        DialogUtil.error(tableHistory, "Lỗi", "Không thể cập nhật phiếu.");
                    }
                } catch (NumberFormatException e) {
                    DialogUtil.error(tableHistory, "Lỗi nhập liệu", "Số lượng sản phẩm phải là một số hợp lệ!");
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
                DialogUtil.warning(cbProducts, "Cảnh báo", "Vui lòng chọn sản phẩm!");
                return;
            }

            String qtyText = txtQuantity.getText() == null ? "" : txtQuantity.getText().trim();
            double qty = Double.parseDouble(qtyText);
            if (qty <= 0) {
                throw new NumberFormatException();
            }

            int id = Integer.parseInt(selected.split(" - ")[0]);
            String name = selected.split(" - ")[1];

            tempDetails.add(new ProductIssueDetail(id, name, qty));
            txtQuantity.clear();
        } catch (Exception e) {
            DialogUtil.error(txtQuantity, "Lỗi", "Số lượng không hợp lệ!");
        }
    }

    @FXML
    private void handleSaveNote() {
        String customer = txtCustomerName.getText() == null ? "" : txtCustomerName.getText().trim();
        String orderCode = txtOrderCode.getText() == null ? "" : txtOrderCode.getText().trim();

        if (customer.isEmpty() || dpDeliveryDate.getValue() == null || tempDetails.isEmpty()) {
            DialogUtil.warning(tableHistory, "Lỗi", "Vui lòng nhập đầy đủ thông tin khách hàng và ít nhất một sản phẩm!");
            return;
        }

        IssueNote note = new IssueNote();
        note.setCustomerName(customer);
        note.setCustomerOrderCode(orderCode);
        note.setDeliveryDate(dpDeliveryDate.getValue().atStartOfDay());

        if (repo.createFullIssueNote(note, tempDetails)) {
            DialogUtil.info(tableHistory, "Thành công", "Đã gửi yêu cầu xuất kho!");
            handleClear();
            loadHistory();
        } else {
            DialogUtil.error(tableHistory, "Lỗi", "Không thể lưu phiếu!");
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

    public void refreshHistory() {
        loadHistory();
    }
}