package aptech.proj_NN_group2.controller.production;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.FinishedStockRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionStageRepository;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.model.entity.ProductionStage;
import aptech.proj_NN_group2.model.entity.ProductionStageDetail;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class StageDetailController implements Initializable {

    @FXML private javafx.scene.control.ComboBox<ProductionOrder> cbOrder;
    @FXML private Label lblOrderInfo;
    @FXML private TableView<ProductionStage> tableStages;
    @FXML private TableColumn<ProductionStage, Integer> colNo;
    @FXML private TableColumn<ProductionStage, String> colName;
    @FXML private TableColumn<ProductionStage, String> colStatus;
    @FXML private TableColumn<ProductionStage, String> colStart;
    @FXML private TableColumn<ProductionStage, String> colEnd;
    @FXML private TableColumn<ProductionStage, Integer> colDuration;
    @FXML private TableColumn<ProductionStage, BigDecimal> colVolume;
    @FXML private TableColumn<ProductionStage, String> colNote;

    @FXML private VBox panelDetail;
    @FXML private Label lblStageTitle;
    @FXML private TextField tfDuration;
    @FXML private TextField tfVolume;
    @FXML private TextField tfMold;
    @FXML private TextField tfNote;

    @FXML private VBox panelMixing;
    @FXML private TextField tfMixingTemp;
    @FXML private TextArea taMixingRatio;

    @FXML private Label lblMessage;

    private final ProductionOrderRepository orderRepo = new ProductionOrderRepository();
    private final ProductionStageRepository stageRepo = new ProductionStageRepository();
    private final FinishedStockRepository finishedStockRepo = new FinishedStockRepository();

    private ProductionStage selectedStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNo.setCellValueFactory(new PropertyValueFactory<>("stage_no"));
        colName.setCellValueFactory(new PropertyValueFactory<>("stage_name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("stage_status"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("start_time"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("end_time"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("actual_duration_min"));
        colVolume.setCellValueFactory(new PropertyValueFactory<>("actual_volume"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        List<ProductionOrder> orders = orderRepo.findAll();
        cbOrder.setItems(FXCollections.observableArrayList(orders));
        cbOrder.setConverter(new javafx.util.StringConverter<ProductionOrder>() {
            @Override
            public String toString(ProductionOrder o) {
                return o == null ? "" : "ID " + o.getProduction_order_id() + " - " + o.getIce_cream_name()
                        + " [" + o.getOrder_status() + "]";
            }

            @Override
            public ProductionOrder fromString(String s) {
                return null;
            }
        });

        panelDetail.setVisible(false);
        panelDetail.setManaged(false);

        tableStages.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, stage) -> {
            if (stage != null) {
                handleStageSelected(stage);
            }
        });
    }

    @FXML
    private void handleLoadStages() {
        ProductionOrder order = cbOrder.getValue();
        if (order == null) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Vui lòng chọn lệnh sản xuất.");
            return;
        }

        lblMessage.setText("");
        lblOrderInfo.setText("Kem: " + order.getIce_cream_name()
                + "  |  Số kg: " + order.getPlanned_output_kg()
                + "  |  Trạng thái: " + order.getOrder_status());

        List<ProductionStage> stages = stageRepo.findByOrderId(order.getProduction_order_id());
        if (stages.isEmpty()) {
            stageRepo.initStages(order.getProduction_order_id());
            stages = stageRepo.findByOrderId(order.getProduction_order_id());
        }

        tableStages.setItems(FXCollections.observableArrayList(stages));
        panelDetail.setVisible(false);
        panelDetail.setManaged(false);
        selectedStage = null;
    }

    private void handleStageSelected(ProductionStage stage) {
        selectedStage = stage;
        lblMessage.setText("");

        panelDetail.setVisible(true);
        panelDetail.setManaged(true);

        lblStageTitle.setText("Công đoạn " + stage.getStage_no() + ": " + stage.getStage_name()
                + "  [" + stage.getStage_status() + "]");

        tfDuration.setText(stage.getActual_duration_min() != null ? stage.getActual_duration_min().toString() : "");
        tfVolume.setText(stage.getActual_volume() != null ? stage.getActual_volume().toPlainString() : "");
        tfMold.setText(stage.getMold_count() != null ? stage.getMold_count().toString() : "");
        tfNote.setText(stage.getNote() != null ? stage.getNote() : "");

        boolean isMixing = stage.getStage_no() == 1;
        panelMixing.setVisible(isMixing);
        panelMixing.setManaged(isMixing);

        if (isMixing) {
            tfMixingTemp.clear();
            taMixingRatio.clear();
        }

        boolean editable = "open".equals(stage.getStage_status());
        tfDuration.setDisable(!editable);
        tfVolume.setDisable(!editable);
        tfMold.setDisable(!editable);
        tfNote.setDisable(!editable);
        tfMixingTemp.setDisable(!editable);
        taMixingRatio.setDisable(!editable);
    }

    @FXML
    private void handleSave() {
        lblMessage.setStyle("-fx-text-fill: red;");

        if (selectedStage == null) {
            lblMessage.setText("Vui lòng chọn một công đoạn.");
            return;
        }

        ProductionOrder order = cbOrder.getValue();
        if (order == null) {
            lblMessage.setText("Vui lòng chọn lệnh sản xuất.");
            return;
        }

        if (!"open".equals(selectedStage.getStage_status())) {
            lblMessage.setText("Chỉ có thể ghi nhận công đoạn đang ở trạng thái 'open'.");
            return;
        }

        Integer duration = null;
        String durationText = tfDuration.getText() == null ? "" : tfDuration.getText().trim();
        if (!durationText.isEmpty()) {
            try {
                duration = Integer.parseInt(durationText);
            } catch (NumberFormatException e) {
                lblMessage.setText("Thời gian phải là số nguyên.");
                return;
            }
        }

        BigDecimal volume = null;
        String volumeText = tfVolume.getText() == null ? "" : tfVolume.getText().trim();
        if (!volumeText.isEmpty()) {
            try {
                volume = new BigDecimal(volumeText);
            } catch (NumberFormatException e) {
                lblMessage.setText("Dung tích phải là số hợp lệ.");
                return;
            }
        }

        Integer mold = null;
        String moldText = tfMold.getText() == null ? "" : tfMold.getText().trim();
        if (!moldText.isEmpty()) {
            try {
                mold = Integer.parseInt(moldText);
            } catch (NumberFormatException e) {
                lblMessage.setText("Số khuôn phải là số nguyên.");
                return;
            }
        }

        ProductionStageDetail detail = new ProductionStageDetail();
        detail.setProduction_stage_id(selectedStage.getProduction_stage_id());
        detail.setProduction_order_id(selectedStage.getProduction_order_id());
        detail.setStage_no(selectedStage.getStage_no());
        detail.setActual_duration_min(duration);
        detail.setActual_volume(volume);
        detail.setMold_count(mold);
        detail.setNote(tfNote.getText() == null ? "" : tfNote.getText().trim());

        if (detail.isMixingStage()) {
            String tempText = tfMixingTemp.getText() == null ? "" : tfMixingTemp.getText().trim();
            if (!tempText.isEmpty()) {
                try {
                    detail.setMixing_temperature_c(new BigDecimal(tempText));
                } catch (NumberFormatException e) {
                    lblMessage.setText("Nhiệt độ trộn phải là số hợp lệ.");
                    return;
                }
            }
            detail.setMixing_ratio_note(taMixingRatio.getText() == null ? "" : taMixingRatio.getText().trim());
        }

        boolean ok = stageRepo.completeStageWithDetail(detail);
        if (!ok) {
            lblMessage.setText("Lưu thất bại, vui lòng thử lại.");
            return;
        }

        boolean finalStage = selectedStage.getStage_no() >= ProductionStageRepository.STAGE_NAMES.length;
        boolean imported = true;
        if (finalStage) {
            double finishedQty = detail.getActual_volume() != null
                    ? detail.getActual_volume().doubleValue()
                    : (order.getPlanned_output_kg() != null ? order.getPlanned_output_kg().doubleValue() : 0d);
            if (finishedQty > 0) {
                imported = finishedStockRepo.importFinishedStock(selectedStage.getProduction_order_id(), finishedQty);
            }
        } else {
            stageRepo.unlockNextStage(selectedStage.getProduction_order_id(), selectedStage.getStage_no() + 1);
        }

        lblMessage.setStyle(imported ? "-fx-text-fill: green;" : "-fx-text-fill: orange;");
        lblMessage.setText(finalStage
                ? (imported
                    ? "Ghi nhận công đoạn cuối thành công và đã nhập kho thành phẩm."
                    : "Ghi nhận công đoạn cuối thành công nhưng nhập kho thành phẩm thất bại.")
                : "Ghi nhận công đoạn " + selectedStage.getStage_no()
                + " thành công! Công đoạn tiếp theo đã được mở.");

        handleLoadStages();
    }

    @FXML
    private void handleReset() {
        tfDuration.clear();
        tfVolume.clear();
        tfMold.clear();
        tfNote.clear();
        tfMixingTemp.clear();
        taMixingRatio.clear();
        lblMessage.setText("");
    }

    @FXML
    private void goBack(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_MAIN_MENU, "Hệ thống Quản lý Sản xuất & Xuất kho");
    }
}