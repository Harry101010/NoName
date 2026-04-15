package aptech.proj_NN_group2.controller.production;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionStageRepository;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.model.entity.ProductionStage;
import aptech.proj_NN_group2.model.entity.ProductionStageDetail;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class StageDetailController implements Initializable {

    // --- Chọn lệnh sản xuất ---
    @FXML private ComboBox<ProductionOrder> cbOrder;
    @FXML private Label lblOrderInfo;

    // --- Bảng công đoạn ---
    @FXML private TableView<ProductionStage> tableStages;
    @FXML private TableColumn<ProductionStage, Integer> colNo;
    @FXML private TableColumn<ProductionStage, String>  colName;
    @FXML private TableColumn<ProductionStage, String>  colStatus;
    @FXML private TableColumn<ProductionStage, String>  colStart;
    @FXML private TableColumn<ProductionStage, String>  colEnd;
    @FXML private TableColumn<ProductionStage, Integer> colDuration;
    @FXML private TableColumn<ProductionStage, BigDecimal> colVolume;
    @FXML private TableColumn<ProductionStage, String>  colNote;

    // --- Form ghi nhận chung (tất cả công đoạn) ---
    @FXML private VBox panelDetail;
    @FXML private Label lblStageTitle;
    @FXML private TextField tfDuration;
    @FXML private TextField tfVolume;
    @FXML private TextField tfMold;
    @FXML private TextField tfNote;

    // --- Panel đặc thù công đoạn 1: Material Preparation & Mixing ---
    @FXML private VBox panelMixing;
    @FXML private TextField tfMixingTemp;
    @FXML private TextArea taMixingRatio;

    @FXML private Label lblMessage;

    private final ProductionOrderRepository orderRepo = new ProductionOrderRepository();
    private final ProductionStageRepository stageRepo = new ProductionStageRepository();

    private ProductionStage selectedStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup bảng công đoạn
        colNo.setCellValueFactory(new PropertyValueFactory<>("stage_no"));
        colName.setCellValueFactory(new PropertyValueFactory<>("stage_name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("stage_status"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("start_time"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("end_time"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("actual_duration_min"));
        colVolume.setCellValueFactory(new PropertyValueFactory<>("actual_volume"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Load lệnh sản xuất đang in_progress hoặc waiting_ingredient
        List<ProductionOrder> orders = orderRepo.findAll();
        cbOrder.setItems(FXCollections.observableArrayList(orders));
        cbOrder.setConverter(new javafx.util.StringConverter<ProductionOrder>() {
            @Override public String toString(ProductionOrder o) {
                return o == null ? "" : "ID " + o.getProduction_order_id() + " - " + o.getIce_cream_name()
                        + " [" + o.getOrder_status() + "]";
            }
            @Override public ProductionOrder fromString(String s) { return null; }
        });

        // Ẩn panel detail ban đầu
        panelDetail.setVisible(false);
        panelDetail.setManaged(false);

        // Khi click vào công đoạn → hiện form chi tiết
        tableStages.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, stage) -> {
                if (stage != null) handleStageSelected(stage);
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

        // Ẩn form detail khi load lại
        panelDetail.setVisible(false);
        panelDetail.setManaged(false);
        selectedStage = null;
    }

    private void handleStageSelected(ProductionStage stage) {
        selectedStage = stage;
        lblMessage.setText("");

        // Hiện panel detail
        panelDetail.setVisible(true);
        panelDetail.setManaged(true);

        lblStageTitle.setText("Công đoạn " + stage.getStage_no() + ": " + stage.getStage_name()
                + "  [" + stage.getStage_status() + "]");

        // Điền dữ liệu đã có (nếu đã ghi nhận trước đó)
        tfDuration.setText(stage.getActual_duration_min() != null ? stage.getActual_duration_min().toString() : "");
        tfVolume.setText(stage.getActual_volume() != null ? stage.getActual_volume().toPlainString() : "");
        tfMold.setText(stage.getMold_count() != null ? stage.getMold_count().toString() : "");
        tfNote.setText(stage.getNote() != null ? stage.getNote() : "");

        // Hiện/ẩn panel Mixing tùy công đoạn
        boolean isMixing = stage.getStage_no() == 1;
        panelMixing.setVisible(isMixing);
        panelMixing.setManaged(isMixing);

        if (isMixing) {
            tfMixingTemp.clear();
            taMixingRatio.clear();
        }

        // Disable form nếu công đoạn đã completed hoặc pending
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
        if (!"open".equals(selectedStage.getStage_status())) {
            lblMessage.setText("Chỉ có thể ghi nhận công đoạn đang ở trạng thái 'open'.");
            return;
        }

        // Validate và parse dữ liệu
        Integer duration = null;
        if (!tfDuration.getText().trim().isEmpty()) {
            try { duration = Integer.parseInt(tfDuration.getText().trim()); }
            catch (NumberFormatException e) { lblMessage.setText("Thời gian phải là số nguyên."); return; }
        }

        BigDecimal volume = null;
        if (!tfVolume.getText().trim().isEmpty()) {
            try { volume = new BigDecimal(tfVolume.getText().trim()); }
            catch (NumberFormatException e) { lblMessage.setText("Dung tích phải là số hợp lệ."); return; }
        }

        Integer mold = null;
        if (!tfMold.getText().trim().isEmpty()) {
            try { mold = Integer.parseInt(tfMold.getText().trim()); }
            catch (NumberFormatException e) { lblMessage.setText("Số khuôn phải là số nguyên."); return; }
        }

        // Build ProductionStageDetail
        ProductionStageDetail detail = new ProductionStageDetail();
        detail.setProduction_stage_id(selectedStage.getProduction_stage_id());
        detail.setProduction_order_id(selectedStage.getProduction_order_id());
        detail.setStage_no(selectedStage.getStage_no());
        detail.setActual_duration_min(duration);
        detail.setActual_volume(volume);
        detail.setMold_count(mold);
        detail.setNote(tfNote.getText().trim());

        // Trường đặc thù công đoạn 1
        if (detail.isMixingStage()) {
            if (!tfMixingTemp.getText().trim().isEmpty()) {
                try {
                    detail.setMixing_temperature_c(new BigDecimal(tfMixingTemp.getText().trim()));
                } catch (NumberFormatException e) {
                    lblMessage.setText("Nhiệt độ trộn phải là số hợp lệ.");
                    return;
                }
            }
            detail.setMixing_ratio_note(taMixingRatio.getText().trim());
        }

        boolean ok = stageRepo.completeStageWithDetail(detail);
        if (!ok) { lblMessage.setText("Lưu thất bại, vui lòng thử lại."); return; }

        // Mở công đoạn tiếp theo nếu còn
        if (selectedStage.getStage_no() < 8) {
            stageRepo.unlockNextStage(selectedStage.getProduction_order_id(), selectedStage.getStage_no() + 1);
        }

        lblMessage.setStyle("-fx-text-fill: green;");
        lblMessage.setText("Ghi nhận công đoạn " + selectedStage.getStage_no()
                + " thành công! Công đoạn tiếp theo đã được mở.");

        // Reload bảng
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
    private void goBack() throws java.io.IOException {
        aptech.proj_NN_group2.App.setRoot(aptech.proj_NN_group2.util.StringValue.VIEW_MAIN_MENU);
    }
}