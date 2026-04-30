package aptech.proj_NN_group2.controller.sales;

import aptech.proj_NN_group2.model.business.repository.sales.FinishedStockRepository;
import aptech.proj_NN_group2.model.entity.sales.FinishedStock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class StockLookupController {

    @FXML private TableView<FinishedStock> tblStock;
    @FXML private TableColumn<FinishedStock, Void> colIndex;
    @FXML private TableColumn<FinishedStock, Integer> colOrderId; // Hiển thị số 16, 17...
    @FXML private TableColumn<FinishedStock, String> colName;
    @FXML private TableColumn<FinishedStock, Double> colQty;
    @FXML private TableColumn<FinishedStock, String> colMfgDate;
    @FXML private TableColumn<FinishedStock, String> colExpiry;
    @FXML private TableColumn<FinishedStock, String> colLocation;

    @FXML private TextField txtSearch;
    @FXML private Label lblMessage;

    private final FinishedStockRepository stockRepo = new FinishedStockRepository();
    
    // Sử dụng ObservableList để quản lý dữ liệu gốc và FilteredList để tìm kiếm
    private final ObservableList<FinishedStock> masterData = FXCollections.observableArrayList();
    private FilteredList<FinishedStock> filteredData;

    @FXML
    public void initialize() {
        // 1. Cấu hình cột Số thứ tự tự động (1, 2, 3...)
        colIndex.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        // 2. Ánh xạ dữ liệu từ Model FinishedStock vào các cột
        // Lưu ý: Tên trong ngoặc kép phải khớp chính xác với tên biến trong file FinishedStock.java
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMfgDate.setCellValueFactory(new PropertyValueFactory<>("mfgDate"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("expDate"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        // 3. Thiết lập FilteredList
        filteredData = new FilteredList<>(masterData, p -> true);
        tblStock.setItems(filteredData);

        // 4. Lắng nghe ô tìm kiếm (Search Bar)
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(stock -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                
                // Tìm kiếm theo tên sản phẩm hoặc mã đơn hàng
                if (stock.getProductName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(stock.getOrderId()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        // 5. Tải dữ liệu lần đầu
        loadData();
    }

    /**
     * Tải dữ liệu từ Database và cập nhật lên bảng
     */
    public void loadData() {
        try {
            var listFromDb = stockRepo.getAllStock();
            masterData.setAll(listFromDb);
            System.out.println("DEBUG: Đã tải " + listFromDb.size() + " dòng từ kho thành phẩm.");
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Lỗi khi tải dữ liệu từ kho!");
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Sự kiện khi nhấn nút Làm mới (Refresh)
     */
    @FXML
    private void refreshInventory() {
        loadData();
        lblMessage.setText("Dữ liệu đã được cập nhật mới nhất.");
        lblMessage.setStyle("-fx-text-fill: #2ecc71;"); // Màu xanh lá nhẹ
    }

    /**
     * Sự kiện đồng bộ các đơn hàng đã 'Completed' nhưng chưa có trong kho
     */
    @FXML
    private void handleSync() {
        try {
            stockRepo.syncMissingOrders();
            loadData();
            lblMessage.setText("Đồng bộ kho thành công!");
            lblMessage.setStyle("-fx-text-fill: #3498db;"); // Màu xanh dương
        } catch (Exception e) {
            lblMessage.setText("Lỗi đồng bộ dữ liệu!");
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }
}