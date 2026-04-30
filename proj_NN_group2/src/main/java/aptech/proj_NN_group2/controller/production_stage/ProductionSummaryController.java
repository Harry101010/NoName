package aptech.proj_NN_group2.controller.production_stage;

import java.util.List;
import aptech.proj_NN_group2.model.business.repository.production_stage.ProductionTrackingRepository;
import aptech.proj_NN_group2.model.entity.production_stage.ProductionSummary;
// Giả sử bạn có class này, nếu không có hãy bỏ dòng dưới và tự định nghĩa ProgressBar
import aptech.proj_NN_group2.view.ui.ProgressTableCell; 
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductionSummaryController {
    
    @FXML private TableView<ProductionSummary> tblSummary;
    @FXML private TableColumn<ProductionSummary, Integer> colOrderId;
    @FXML private TableColumn<ProductionSummary, String> colProductName;
    @FXML private TableColumn<ProductionSummary, Double> colProgress;
    @FXML private TableColumn<ProductionSummary, String> colCurrentStage;
    @FXML private TableColumn<ProductionSummary, String> colFailedStage;
    @FXML private Button btnRefresh;
    
    
    // ĐÃ KHAI BÁO BIẾN NÀY ĐỂ HẾT LỖI "cannot be resolved"
    private final ProductionTrackingRepository trackingRepo = new ProductionTrackingRepository();

    
    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));

        // --- CỘT PROGRESS BAR MÀU XANH LÁ + HIỆN % ---
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progressPercent"));
        colProgress.setCellFactory(column -> new TableCell<>() {
            private final ProgressBar pb = new ProgressBar();
            private final Label label = new Label();
            private final javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10, pb, label);

            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    pb.setProgress(item / 100.0);
                    pb.setPrefWidth(100);
                    // Đổi màu thanh tiến trình sang xanh lá
                    pb.setStyle("-fx-accent: #28a745;"); 
                    
//                    label.setText(String.format("%.0f%%", item));
                    label.setText((int)item.doubleValue() + "%");
                    
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });

        // --- CỘT CURRENT STAGE (Màu xanh dương đậm) ---
        colCurrentStage.setCellValueFactory(new PropertyValueFactory<>("currentStage"));
        colCurrentStage.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #007bff; -fx-font-weight: bold;"); // Màu xanh dương chuyên nghiệp
                }
            }
        });

        // --- CỘT FAILED STAGE (Màu đỏ cảnh báo) ---
        colFailedStage.setCellValueFactory(new PropertyValueFactory<>("failedStage"));
        colFailedStage.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;"); // Màu đỏ lỗi
                }
            }
        });

        loadData();
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }
    
    public void loadData() {
        // Gọi hàm từ repo
        List<ProductionSummary> data = trackingRepo.getProductionSummary();
        tblSummary.setItems(FXCollections.observableArrayList(data)); 
    }
}