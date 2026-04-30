package aptech.proj_NN_group2.controller.production_stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import aptech.proj_NN_group2.model.business.repository.sales.FinishedStockRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionStageRepository;
import aptech.proj_NN_group2.model.business.repository.production_stage.ProductionTrackingRepository;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.model.entity.production_stage.ProductionStageTemplate;
import aptech.proj_NN_group2.model.entity.production_stage.ProductionTracking;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ProductionTrackingController {

    @FXML private TableView<ProductionOrder> tblProductionMatrix;
    @FXML private TableColumn<ProductionOrder, Integer> colOrderId;
    @FXML private TableColumn<ProductionOrder, String> colProductName;

    private final ProductionTrackingRepository trackingRepo = new ProductionTrackingRepository();
    private final ProductionStageRepository stageRepo = new ProductionStageRepository();
    private final FinishedStockRepository finishedStockRepo = new FinishedStockRepository();
    
    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("production_order_id"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        
        var stages = stageRepo.getAllStages();
        setupDynamicColumns(stages);
        refreshMatrix();
    }

    private void setupDynamicColumns(List<ProductionStageTemplate> stages) {
    	colOrderId.setPrefWidth(70); // Cột ID chỉ cần 70px
        colProductName.setPrefWidth(150); // Cột tên sp rộng hơn chút
        for (int i = 0; i < stages.size(); i++) {
            ProductionStageTemplate stage = stages.get(i);
            Integer previousStageId = (i == 0) ? null : stages.get(i - 1).getStageId();
            TableColumn<ProductionOrder, String> col = new TableColumn<>(stage.getStageName());
         // CHỈNH KÍCH THƯỚC CỘT ĐỘNG TẠI ĐÂY
            col.setPrefWidth(130); // Độ rộng ưu tiên
            col.setMinWidth(100);  // Độ rộng tối thiểu để không bị mất chữ
            setupStageColumn(col, stage.getStageId(), previousStageId);
            tblProductionMatrix.getColumns().add(col);
        }
    }

    private void setupStageColumn(TableColumn<ProductionOrder, String> col, int stageId, Integer previousStageId) {
        col.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button();
            private final VBox vbox = new VBox(btn); // VBox chỉ chứa Button

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }

                ProductionOrder order = getTableView().getItems().get(getIndex());
                int orderId = order.getProduction_order_id();
                
                ProductionTracking tracking = trackingRepo.getTrackingData(orderId, stageId); 
                String status = (tracking != null) ? tracking.getStatus() : "pending";
                
                // Logic kiểm tra khóa
                String prevStatus = (previousStageId != null) ? trackingRepo.getStatusByOrderAndStage(orderId, previousStageId) : "completed";
                boolean isLocked = (previousStageId != null && !"completed".equals(prevStatus));

                // Cấu hình chung cho nút bấm
                btn.setMinWidth(90);
                btn.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 5 10 5 10; -fx-background-radius: 5; -fx-cursor: hand;");

                if (isLocked) {
                    btn.setText("🔒 Locked"); 
                    btn.setDisable(true); 
                    btn.setStyle(btn.getStyle() + "-fx-background-color: #6c757d;");
                } else if ("pending".equals(status)) {
                    btn.setText("▶ Play"); 
                    btn.setDisable(false); 
                    btn.setStyle(btn.getStyle() + "-fx-background-color: #007bff;");
                } else if ("in_progress".equals(status)) {
                    btn.setText("⏹ Stop"); 
                    btn.setDisable(false); 
                    btn.setStyle(btn.getStyle() + "-fx-background-color: #fd7e14;");
                } else {
                    btn.setText("✔ Done"); 
                    btn.setDisable(true); 
                    btn.setStyle(btn.getStyle() + "-fx-background-color: #28a745;");
                }

                // Xử lý sự kiện nhấn
                btn.setOnAction(e -> {
                    if ("pending".equals(status)) {
                        trackingRepo.updateStatusByOrderAndStage(orderId, stageId, "in_progress");
                    } else if ("in_progress".equals(status)) {
                        showEntryDialog(orderId, stageId, col.getText());
                    }
                    refreshMatrix();
                });
                
                setGraphic(vbox);
            }
        });
    }

    public void refreshMatrix() {
        var activeOrders = new ProductionOrderRepository().findActiveOrders(); 
        tblProductionMatrix.setItems(FXCollections.observableArrayList(activeOrders));
    }

    private void showEntryDialog(int orderId, int stageId, String stageName) {
        Dialog<ProductionData> dialog = new Dialog<>();
        dialog.setTitle("Record Stage: " + stageName);
        dialog.setHeaderText("Please enter production details for: " + stageName);

        // Tạo các nút
        ButtonType btnFail = new ButtonType("Fail", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, btnFail, ButtonType.CANCEL);

        // 1. Tự động lấy số lượng
        String initialQty = "";
        if (stageId == 1) {
            // Công đoạn 1: Lấy từ lệnh sản xuất
            ProductionOrder order = new ProductionOrderRepository().getOrderById(orderId);
            if (order != null && order.getPlanned_output_kg() != null) {
                initialQty = order.getPlanned_output_kg().toPlainString();
            }
        } else {
            // Các công đoạn sau: Lấy từ công đoạn trước
            ProductionTracking prevStage = trackingRepo.getPreviousStageData(orderId, stageId);
            if (prevStage != null) {
                initialQty = String.valueOf(prevStage.getActual_quantity());
            }
        }

        // 2. Thiết lập UI
        TextField txtQuantity = new TextField(initialQty);
        txtQuantity.setPromptText("Quantity");
        
        TextField txtNote = new TextField();
        txtNote.setPromptText("Note (Mandatory if Failed)");

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        Label lblTime = new Label(currentTime);
        lblTime.setStyle("-fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Completion Time:"), 0, 0); grid.add(lblTime, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);       grid.add(txtQuantity, 1, 1);
        grid.add(new Label("Note:"), 0, 2);           grid.add(txtNote, 1, 2);
        dialog.getDialogPane().setContent(grid);

        // 3. Xử lý Logic khi nhấn nút
        dialog.setResultConverter(dialogButton -> {
            String qty = txtQuantity.getText();
            String note = txtNote.getText();
            
            if (dialogButton == ButtonType.OK) {
                return new ProductionData(qty, note, "completed");
            } else if (dialogButton == btnFail) {
                if (note == null || note.trim().isEmpty()) {
                    showError("Validation Error", "Note is required for Failed status!");
                    return null;
                }
                return new ProductionData("0", note, "failed");
            }
            return null;
        });

        // 4. Lưu vào Database
        dialog.showAndWait().ifPresent(data -> {
            trackingRepo.updateStageStatus(orderId, stageId, data.quantity, data.note, data.status);
            
            // Kiểm tra nhập kho tự động (nếu là bước cuối)
            if ("completed".equals(data.status)) {
                int currentStageNo = stageRepo.getStageNoById(stageId);
                int maxStage = trackingRepo.getMaxStageNo(orderId);
                
                if (currentStageNo == maxStage) {
                    finishedStockRepo.importFinishedStock(orderId, Double.parseDouble(data.quantity));
                    System.out.println("Auto-inventory: Import success for Order " + orderId);
                }
            }
            refreshMatrix();
        });
    }

    private static class ProductionData {
        String quantity, note, status;
        ProductionData(String q, String n, String s) { this.quantity = q; this.note = n; this.status = s; }
    }
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}