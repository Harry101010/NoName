package aptech.proj_NN_group2.controller.production;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionStageRepository;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.model.entity.ProductionStage;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductionProcessController implements Initializable {

    @FXML private ComboBox<ProductionOrder> cbOrder;
    @FXML private Label lblOrderInfo;
    @FXML private TableView<ProductionStage> tableStages;
    @FXML private TableColumn<ProductionStage, Integer> colNo;
    @FXML private TableColumn<ProductionStage, String> colName;
    @FXML private TableColumn<ProductionStage, String> colStatus;
    @FXML private TableColumn<ProductionStage, String> colStart;
    @FXML private TableColumn<ProductionStage, String> colEnd;
    @FXML private TableColumn<ProductionStage, Integer> colDuration;
    @FXML private TableColumn<ProductionStage, BigDecimal> colVolume;
    @FXML private TableColumn<ProductionStage, Integer> colMold;

    @FXML private TextField tfDuration;
    @FXML private TextField tfVolume;
    @FXML private TextField tfMold;
    @FXML private TextField tfNote;
    @FXML private Label lblMessage;

    private final ProductionOrderRepository orderRepo = new ProductionOrderRepository();
    private final ProductionStageRepository stageRepo = new ProductionStageRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNo.setCellValueFactory(new PropertyValueFactory<>("stage_no"));
        colName.setCellValueFactory(new PropertyValueFactory<>("stage_name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("stage_status"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("start_time"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("end_time"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("actual_duration_min"));
        colVolume.setCellValueFactory(new PropertyValueFactory<>("actual_volume"));
        colMold.setCellValueFactory(new PropertyValueFactory<>("mold_count"));

        List<ProductionOrder> orders = orderRepo.findAll();
        cbOrder.setItems(FXCollections.observableArrayList(orders));
        cbOrder.setConverter(new javafx.util.StringConverter<ProductionOrder>() {
            @Override
            public String toString(ProductionOrder o) {
                return o == null ? "" : "ID " + o.getProduction_order_id() + " - " + o.getIce_cream_name();
            }

            @Override
            public ProductionOrder fromString(String s) {
                return null;
            }
        });
    }

    @FXML
    private void handleLoadStages() {
        ProductionOrder order = cbOrder.getValue();
        if (order == null) {
            lblMessage.setText("Vui lòng chọn lệnh sản xuất.");
            return;
        }

        lblMessage.setText("");
        lblOrderInfo.setText("Kem: " + order.getIce_cream_name() +
                " | Số kg: " + order.getPlanned_output_kg() +
                " | Trạng thái: " + order.getOrder_status());

        List<ProductionStage> stages = stageRepo.findByOrderId(order.getProduction_order_id());

        if (stages.isEmpty()) {
            stageRepo.initStages(order.getProduction_order_id());
            stages = stageRepo.findByOrderId(order.getProduction_order_id());
        }

        tableStages.setItems(FXCollections.observableArrayList(stages));
    }

    @FXML
    private void handleComplete() {
        lblMessage.setStyle("-fx-text-fill: red;");
        ProductionOrder order = cbOrder.getValue();
        if (order == null) {
            lblMessage.setText("Chưa chọn lệnh sản xuất.");
            return;
        }

        ProductionStage openStage = tableStages.getItems().stream()
                .filter(s -> "open".equals(s.getStage_status()))
                .findFirst()
                .orElse(null);

        if (openStage == null) {
            lblMessage.setText("Không có công đoạn nào đang mở.");
            return;
        }

        Integer duration = null;
        String durationText = tfDuration.getText() != null ? tfDuration.getText().trim() : "";
        if (!durationText.isEmpty()) {
            try {
                duration = Integer.parseInt(durationText);
            } catch (NumberFormatException e) {
                lblMessage.setText("Thời gian phải là số nguyên.");
                return;
            }
        }

        BigDecimal volume = null;
        String volumeText = tfVolume.getText() != null ? tfVolume.getText().trim() : "";
        if (!volumeText.isEmpty()) {
            try {
                volume = new BigDecimal(volumeText);
            } catch (NumberFormatException e) {
                lblMessage.setText("Dung tích phải là số hợp lệ.");
                return;
            }
        }

        Integer mold = null;
        String moldText = tfMold.getText() != null ? tfMold.getText().trim() : "";
        if (!moldText.isEmpty()) {
            try {
                mold = Integer.parseInt(moldText);
            } catch (NumberFormatException e) {
                lblMessage.setText("Số khuôn phải là số nguyên.");
                return;
            }
        }

        openStage.setActual_duration_min(duration);
        openStage.setActual_volume(volume);
        openStage.setMold_count(mold);
        openStage.setNote(tfNote.getText() != null ? tfNote.getText().trim() : "");

        boolean ok = stageRepo.completeStage(openStage);
        if (!ok) {
            lblMessage.setText("Lỗi khi cập nhật công đoạn.");
            return;
        }

        if (openStage.getStage_no() < 8) {
            stageRepo.unlockNextStage(order.getProduction_order_id(), openStage.getStage_no() + 1);
        }

        lblMessage.setStyle("-fx-text-fill: green;");
        lblMessage.setText("Hoàn thành công đoạn " + openStage.getStage_no() + ": " + openStage.getStage_name());
        handleReset();
        handleLoadStages();
    }

    @FXML
    private void handleReset() {
        tfDuration.clear();
        tfVolume.clear();
        tfMold.clear();
        tfNote.clear();
    }

    @FXML
    private void goBack(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_MAIN_MENU, "Hệ thống Quản lý Sản xuất & Xuất kho");
    }
}