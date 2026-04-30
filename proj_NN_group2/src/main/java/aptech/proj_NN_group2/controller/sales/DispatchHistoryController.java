package aptech.proj_NN_group2.controller.sales;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import aptech.proj_NN_group2.model.business.repository.SalemanRepository;
import aptech.proj_NN_group2.model.entity.sales.DispatchHistory;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DispatchHistoryController {

    @FXML private TableView<DispatchHistory> tableHistory;
    
    // Khai báo các cột khớp với fx:id trong FXML
    @FXML private TableColumn<DispatchHistory, LocalDateTime> colDate; // Cột Dispatch Date (VN)
    @FXML private TableColumn<DispatchHistory, Integer> colOrderId;
    @FXML private TableColumn<DispatchHistory, String> colProduct;
    @FXML private TableColumn<DispatchHistory, Double> colQty;
    @FXML private TableColumn<DispatchHistory, String> colStatus;
    @FXML private TableColumn<DispatchHistory, LocalDateTime> colMfg;
    @FXML private TableColumn<DispatchHistory, String> colCustomer;
    @FXML private TableColumn<DispatchHistory, String> colNotes;

    private final SalemanRepository repo = new SalemanRepository();
    private final DateTimeFormatter vnFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        // 1. Ánh xạ dữ liệu cho từng cột (Tên trong "" phải khớp với biến trong DispatchHistory.java)
        colDate.setCellValueFactory(new PropertyValueFactory<>("dispatchDate"));
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("qualityStatus"));
        colMfg.setCellValueFactory(new PropertyValueFactory<>("mfgDate"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // 2. Định dạng ngày tháng hiển thị tiếng Việt cho các cột LocalDateTime
        setupDateColumnFormat(colDate);
        setupDateColumnFormat(colMfg);

        // 3. Tải dữ liệu lần đầu
        loadDispatchHistory();
    }

    /**
     * Hàm làm mới dữ liệu khi nhấn nút Refresh
     */
    @FXML
    private void handleRefresh() {
        loadDispatchHistory();
        System.out.println("Dispatch history refreshed!");
    }

    /**
     * Hàm nạp dữ liệu từ Repository vào bảng
     */
    private void loadDispatchHistory() {
        try {
            List<DispatchHistory> data = repo.getHistory();
            if (data != null) {
                tableHistory.getItems().setAll(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hàm tiện ích để định dạng hiển thị ngày tháng
     */
    private void setupDateColumnFormat(TableColumn<DispatchHistory, LocalDateTime> col) {
        col.setCellFactory(column -> new TableCell<DispatchHistory, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(vnFormat.format(item));
                }
            }
        });
    }
}