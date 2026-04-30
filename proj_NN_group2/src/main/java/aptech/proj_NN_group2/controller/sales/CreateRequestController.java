package aptech.proj_NN_group2.controller.sales;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import aptech.proj_NN_group2.model.business.repository.SalemanRepository;
import aptech.proj_NN_group2.model.business.repository.sales.FinishedStockRepository;
import aptech.proj_NN_group2.model.entity.IssueNote;
import aptech.proj_NN_group2.model.entity.sales.FinishedStock;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CreateRequestController {
    // Các trường nhập liệu
    @FXML private TextField txtCustomer;
    @FXML private ComboBox<String> cbProduct;
    @FXML private TextField txtQty;
    @FXML private TextField txtNotes; // Đổi sang TextField cho gọn nếu muốn giao diện nằm ngang
    @FXML private Label lblMsg;

    // Các trường hiển thị bảng lịch sử
    @FXML private TableView<IssueNote> tblMyRequests;
    @FXML private TableColumn<IssueNote, Integer> colId;
    @FXML private TableColumn<IssueNote, String> colCustomer, colProduct, colStatus;
    @FXML private TableColumn<IssueNote, Double> colQty;
    @FXML private TableColumn<IssueNote, LocalDateTime> colDate;

    private final SalemanRepository saleRepo = new SalemanRepository();
    private final FinishedStockRepository stockRepo = new FinishedStockRepository();

    @FXML
    public void initialize() {
        // 1. Khởi tạo các cột cho TableView
        initTableColumns();
        
        // 2. Tải danh sách sản phẩm vào ComboBox
        loadProductList();
        
        // 3. Tải dữ liệu lịch sử yêu cầu vào bảng
        loadRequestHistory();
    }

    private void initTableColumns() {
        // KẾT NỐI DỮ LIỆU (Thiếu dòng này nên cột Status bị trống)
        colDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        colId.setCellValueFactory(new PropertyValueFactory<>("noteId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status")); // DÒNG QUAN TRỌNG NHẤT

        // ĐỊNH DẠNG HIỂN THỊ CỘT NGÀY (Chuẩn dd/MM/yyyy HH:mm)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colDate.setCellFactory(column -> new TableCell<IssueNote, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // ĐỊNH DẠNG MÀU SẮC VÀ TIẾNG ANH CHO CỘT STATUS
        colStatus.setCellFactory(column -> new TableCell<IssueNote, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Ép hiển thị tiếng Anh cho đẹp
                    String statusText = item;
                    if (item.equalsIgnoreCase("Pending") || item.equals("Chờ duyệt")) statusText = "Pending";
                    if (item.equalsIgnoreCase("Approved") || item.equals("Đã duyệt")) statusText = "Approved";
                    if (item.equalsIgnoreCase("Rejected") || item.equals("Từ chối")) statusText = "Rejected";
                    
                    setText(statusText);

                    // Đổi màu sắc
                    if (statusText.equals("Pending")) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Cam
                    } else if (statusText.equals("Approved")) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); // Xanh lá
                    } else if (statusText.equals("Rejected")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Đỏ
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
    }

    private void loadProductList() {
        try {
            List<FinishedStock> stocks = stockRepo.getAllStock();
            List<String> productNames = stocks.stream()
                    .map(FinishedStock::getProductName)
                    .distinct()
                    .collect(Collectors.toList());
            cbProduct.setItems(FXCollections.observableArrayList(productNames));
        } catch (Exception e) {
            lblMsg.setText("Error loading products!");
        }
    }

    private void loadRequestHistory() {
        try {
            // Hàm này lấy toàn bộ danh sách yêu cầu từ Repository (hàm getAllMyRequests ta đã thêm trước đó)
            List<IssueNote> history = saleRepo.getAllMyRequests(); 
            // Lưu ý: Nếu bạn muốn xem cả Approved/Rejected, hãy dùng hàm getAllMyRequests trong Repo
            tblMyRequests.getItems().setAll(history);
        } catch (Exception e) {
            System.err.println("Error loading table history: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        String customer = txtCustomer.getText();
        String product = cbProduct.getValue();
        String qtyStr = txtQty.getText();
        String notes = txtNotes.getText();

        if (customer.isEmpty() || product == null || qtyStr.isEmpty()) {
            lblMsg.setText("Please fill all required fields!");
            lblMsg.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            double qty = Double.parseDouble(qtyStr);
            boolean success = saleRepo.createIssueRequest(customer, product, qty, notes);
            
            if (success) {
                lblMsg.setText("Request submitted successfully!");
                lblMsg.setStyle("-fx-text-fill: green;");
                clearForm();
                loadRequestHistory(); // Cập nhật lại bảng ngay lập tức sau khi thêm thành công
            } else {
                lblMsg.setText("Failed to submit request!");
            }
        } catch (NumberFormatException e) {
            lblMsg.setText("Invalid quantity format!");
        }
    }

    private void clearForm() {
        txtCustomer.clear();
        txtQty.clear();
        txtNotes.clear();
        cbProduct.getSelectionModel().clearSelection();
    }
 // Thêm hàm này vào Controller của bạn
    @FXML
    private void handleRefresh() {
        try {
            loadRequestHistory(); // Gọi lại hàm nạp dữ liệu từ Repo
            lblMsg.setText("Data refreshed!");
            lblMsg.setStyle("-fx-text-fill: blue;");
        } catch (Exception e) {
            lblMsg.setText("Refresh failed!");
            e.printStackTrace();
        }
    }
}