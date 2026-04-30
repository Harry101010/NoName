package aptech.proj_NN_group2.controller.production_stage;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import aptech.proj_NN_group2.model.business.repository.production_stage.ProductionTrackingRepository;
import aptech.proj_NN_group2.model.entity.production_stage.ProductionPerformance;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PerformanceReportController {
    @FXML private TableView<ProductionPerformance> tblPerformance;
    @FXML private TableColumn<ProductionPerformance, String> colStage;
    @FXML private TableColumn<ProductionPerformance, Float> colQty;
    
    // Chuyển String thành Timestamp
    @FXML private TableColumn<ProductionPerformance, Timestamp> colStart;
    @FXML private TableColumn<ProductionPerformance, Timestamp> colEnd;
    
    @FXML private TableColumn<ProductionPerformance, Integer> colDuration;
    
    private final ProductionTrackingRepository trackingRepo = new ProductionTrackingRepository();

    @FXML
    public void initialize() {
        // Thay đổi pattern tại đây để hiện cả ngày tháng
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        colStage.setCellValueFactory(new PropertyValueFactory<>("stageName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("actualQuantity"));
        
        // Format cột Start
        colStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colStart.setCellFactory(column -> new TableCell<ProductionPerformance, Timestamp>() {
            @Override protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toLocalDateTime().format(timeFormatter));
            }
        });

        // Format cột End
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colEnd.setCellFactory(column -> new TableCell<ProductionPerformance, Timestamp>() {
            @Override protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toLocalDateTime().format(timeFormatter));
            }
        });

        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
    }

    public void loadData(int orderId) {
        var data = trackingRepo.getPerformanceReport(orderId);
        tblPerformance.setItems(FXCollections.observableArrayList(data));
    }
}