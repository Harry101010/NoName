package aptech.proj_NN_group2.controller.production;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.IceCreamRepository;
import aptech.proj_NN_group2.model.business.repository.IngredientExportReceiptRepository;
import aptech.proj_NN_group2.model.business.repository.IngredientExportRequestRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionStageRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.entity.IngredientExportReceipt;
import aptech.proj_NN_group2.model.entity.IngredientExportRequest;
import aptech.proj_NN_group2.model.entity.IngredientExportRequestDetail;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.model.entity.ProductionStage;
import aptech.proj_NN_group2.model.entity.ProductionStageDetail;
import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * Màn hình quản lý sản xuất tổng hợp — 1 màn hình duy nhất theo workflow:
 * Tạo lệnh SX → Yêu cầu xuất kho NL → Xác nhận nhận NL → 8 công đoạn → Hoàn thành
 */
public class ProductionDashboardController implements Initializable {

    // ── Bảng danh sách lệnh SX (trái) ──────────────────────────────────────
    @FXML private TableView<ProductionOrder> tableOrders;
    @FXML private TableColumn<ProductionOrder, Integer> colOrderId;
    @FXML private TableColumn<ProductionOrder, String>  colIceCream;
    @FXML private TableColumn<ProductionOrder, String>  colKg;
    @FXML private TableColumn<ProductionOrder, String>  colOrderStatus;
    @FXML private TableColumn<ProductionOrder, String>  colCreatedAt;

    // ── Panel chi tiết (phải) ───────────────────────────────────────────────
    @FXML private VBox panelDetail;
    @FXML private Label lblOrderTitle;
    @FXML private Label lblCurrentStep;
    @FXML private Button btnAction;          // nút hành động thay đổi theo trạng thái
    @FXML private Label lblActionMessage;

    // ── Panel tạo lệnh mới ─────────────────────────────────────────────────
    @FXML private VBox panelCreateOrder;
    @FXML private ComboBox<IceCream> cbIceCream;
    @FXML private TextField tfOutputKg;
    @FXML private TextArea taOrderNote;

    // ── Panel preview nguyên liệu ──────────────────────────────────────────
    @FXML private VBox panelIngredients;
    @FXML private TableView<IngredientExportRequestDetail> tableIngredients;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colIngName;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colIngUnit;
    @FXML private TableColumn<IngredientExportRequestDetail, String> colIngQty;

    // ── Panel công đoạn ────────────────────────────────────────────────────
    @FXML private VBox panelStages;
    @FXML private TableView<ProductionStage> tableStages;
    @FXML private TableColumn<ProductionStage, Integer> colStageNo;
    @FXML private TableColumn<ProductionStage, String>  colStageName;
    @FXML private TableColumn<ProductionStage, String>  colStageStatus;
    @FXML private TableColumn<ProductionStage, String>  colStageStart;
    @FXML private TableColumn<ProductionStage, String>  colStageEnd;
    @FXML private TableColumn<ProductionStage, String>  colStageDuration;
    @FXML private TableColumn<ProductionStage, String>  colStageVolume;
    @FXML private TableColumn<ProductionStage, String>  colStageMold;

    // ── Panel ghi nhận công đoạn ───────────────────────────────────────────
    @FXML private VBox panelStageInput;
    @FXML private Label lblStageTitle;
    @FXML private TextField tfDuration;
    @FXML private TextField tfVolume;
    @FXML private TextField tfMold;
    @FXML private TextField tfStageNote;
    @FXML private VBox panelMixing;          // chỉ hiện ở công đoạn 1
    @FXML private TextField tfMixingTemp;
    @FXML private TextArea taMixingRatio;

    // ── Repositories ───────────────────────────────────────────────────────
    private final ProductionOrderRepository orderRepo         = new ProductionOrderRepository();
    private final ProductionStageRepository stageRepo         = new ProductionStageRepository();
    private final IceCreamRepository        iceCreamRepo      = new IceCreamRepository();
    private final IngredientExportRequestRepository reqRepo   = new IngredientExportRequestRepository();
    private final IngredientExportReceiptRepository receiptRepo = new IngredientExportReceiptRepository();

    private ProductionOrder selectedOrder;
    private ProductionStage  selectedStage;

    private static final String STATUS_DRAFT               = "draft";
    private static final String STATUS_WAITING_INGREDIENT  = "waiting_ingredient";
    private static final String STATUS_IN_PROGRESS         = "in_progress";
    private static final String STATUS_FINISHED            = "finished";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupOrderTable();
        setupIngredientTable();
        setupStageTable();
        loadOrders();
        hideAllPanels();

        // Khi chọn lệnh SX → cập nhật panel phải
        tableOrders.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, order) -> { if (order != null) onOrderSelected(order); });

        // Khi chọn công đoạn → hiện form ghi nhận
        tableStages.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, stage) -> { if (stage != null) onStageSelected(stage); });
    }

    // ── Setup bảng ─────────────────────────────────────────────────────────

    private void setupOrderTable() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("production_order_id"));
        colIceCream.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        colKg.setCellValueFactory(new PropertyValueFactory<>("planned_output_kg"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("created_at"));

        // Status column with colored badge
        colOrderStatus.setCellValueFactory(new PropertyValueFactory<>("order_status"));
        colOrderStatus.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setText(null); setStyle(""); return; }
                switch (status) {
                    case "draft"               -> { setText("● New");                setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;"); }
                    case "waiting_ingredient"  -> { setText("● Pending Ingredients"); setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;"); }
                    case "in_progress"         -> { setText("● In Progress");         setStyle("-fx-text-fill: #7c3aed; -fx-font-weight: bold;"); }
                    case "finished"            -> { setText("● Completed");           setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;"); }
                    case "cancelled"           -> { setText("● Cancelled");           setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;"); }
                    default                    -> { setText(status);                  setStyle(""); }
                }
            }
        });
    }

    private void setupIngredientTable() {
        colIngName.setCellValueFactory(new PropertyValueFactory<>("ingredient_name"));
        colIngUnit.setCellValueFactory(new PropertyValueFactory<>("unit_name"));
        colIngQty.setCellValueFactory(new PropertyValueFactory<>("required_quantity"));
    }

    private void setupStageTable() {
        colStageNo.setCellValueFactory(new PropertyValueFactory<>("stage_no"));
        colStageName.setCellValueFactory(new PropertyValueFactory<>("stage_name"));
        colStageStart.setCellValueFactory(new PropertyValueFactory<>("start_time"));
        colStageEnd.setCellValueFactory(new PropertyValueFactory<>("end_time"));
        colStageDuration.setCellValueFactory(new PropertyValueFactory<>("actual_duration_min"));
        colStageVolume.setCellValueFactory(new PropertyValueFactory<>("actual_volume"));
        colStageMold.setCellValueFactory(new PropertyValueFactory<>("mold_count"));

        // Stage status with colored badge
        colStageStatus.setCellValueFactory(new PropertyValueFactory<>("stage_status"));
        colStageStatus.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setText(null); setStyle(""); return; }
                switch (status) {
                    case "pending"   -> { setText("○ Pending");   setStyle("-fx-text-fill: #9ca3af; -fx-font-weight: bold;"); }
                    case "open"      -> { setText("▶ Open");      setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;"); }
                    case "completed" -> { setText("✓ Done");      setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;"); }
                    case "blocked"   -> { setText("✕ Blocked");   setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;"); }
                    default          -> { setText(status);         setStyle(""); }
                }
            }
        });
    }

    // ── Load dữ liệu ───────────────────────────────────────────────────────

    private void loadOrders() {
        tableOrders.setItems(FXCollections.observableArrayList(orderRepo.findAll()));
    }

    private void loadStages(int orderId) {
        List<ProductionStage> stages = stageRepo.findByOrderId(orderId);
        if (stages.isEmpty()) {
            stageRepo.initStages(orderId);
            stages = stageRepo.findByOrderId(orderId);
        }
        tableStages.setItems(FXCollections.observableArrayList(stages));
    }

    // ── Xử lý chọn lệnh SX ────────────────────────────────────────────────

    private void onOrderSelected(ProductionOrder order) {
        selectedOrder = order;
        selectedStage = null;
        lblActionMessage.setText("");
        panelStageInput.setVisible(false);
        panelStageInput.setManaged(false);

        lblOrderTitle.setText("Order #" + order.getProduction_order_id()
                + " — " + order.getIce_cream_name()
                + " (" + order.getPlanned_output_kg() + " kg)");

        updatePanelByStatus(order);
    }

    /**
     * Cập nhật panel phải theo trạng thái lệnh SX — trái tim của màn hình này.
     */
    private void updatePanelByStatus(ProductionOrder order) {
        hideAllPanels();
        panelDetail.setVisible(true);
        panelDetail.setManaged(true);

        switch (order.getOrder_status()) {
            case STATUS_DRAFT -> showDraftPanel(order);
            case STATUS_WAITING_INGREDIENT -> showWaitingIngredientPanel(order);
            case STATUS_IN_PROGRESS -> showInProgressPanel(order);
            case STATUS_FINISHED -> showFinishedPanel(order);
            default -> lblCurrentStep.setText("Trạng thái: " + order.getOrder_status());
        }
    }

    /** Step 1: New order → show ingredient preview + "Create Issue Request" button */
    private void showDraftPanel(ProductionOrder order) {
        lblCurrentStep.setText("📋 Step 1: Create ingredient issue request");
        btnAction.setText("Create Ingredient Issue Request");
        btnAction.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAction.setDisable(false);

        panelIngredients.setVisible(true);
        panelIngredients.setManaged(true);
        List<IngredientExportRequestDetail> preview = reqRepo.previewDetails(
                order.getProduction_order_id(), order.getPlanned_output_kg());
        tableIngredients.setItems(FXCollections.observableArrayList(preview));
    }

    /** Step 2: Waiting for warehouse → show "Confirm Received" button */
    private void showWaitingIngredientPanel(ProductionOrder order) {
        lblCurrentStep.setText("⏳ Step 2: Waiting for warehouse → Confirm ingredient receipt");

        IngredientExportReceipt receipt = receiptRepo.findApprovedByOrderId(order.getProduction_order_id());
        if (receipt != null && "approved".equals(receipt.getReceipt_status())) {
            btnAction.setText("✔ Confirm Ingredients Received");
            btnAction.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            btnAction.setDisable(false);
        } else {
            btnAction.setText("⏳ Waiting for warehouse approval...");
            btnAction.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
            btnAction.setDisable(true);
        }

        panelIngredients.setVisible(true);
        panelIngredients.setManaged(true);
        IngredientExportRequest req = reqRepo.findAll().stream()
                .filter(r -> r.getProduction_order_id() == order.getProduction_order_id())
                .findFirst().orElse(null);
        if (req != null) {
            tableIngredients.setItems(FXCollections.observableArrayList(
                    reqRepo.findDetailsByRequestId(req.getIngredient_export_request_id())));
        }
    }

    /** Step 3: In progress → show 8 stages */
    private void showInProgressPanel(ProductionOrder order) {
        lblCurrentStep.setText("🏭 Step 3: Execute 8 production stages");
        btnAction.setText("Complete Current Stage");
        btnAction.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAction.setDisable(false);

        panelStages.setVisible(true);
        panelStages.setManaged(true);
        loadStages(order.getProduction_order_id());
    }

    /** Step 4: Finished */
    private void showFinishedPanel(ProductionOrder order) {
        lblCurrentStep.setText("✅ Production order completed!");
        btnAction.setText("Completed");
        btnAction.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnAction.setDisable(true);

        panelStages.setVisible(true);
        panelStages.setManaged(true);
        loadStages(order.getProduction_order_id());
    }

    // ── Xử lý chọn công đoạn ──────────────────────────────────────────────

    private void onStageSelected(ProductionStage stage) {
        selectedStage = stage;
        lblActionMessage.setText("");

        boolean editable = "open".equals(stage.getStage_status());
        panelStageInput.setVisible(editable);
        panelStageInput.setManaged(editable);

        if (!editable) return;

        lblStageTitle.setText("Stage " + stage.getStage_no() + ": " + stage.getStage_name());
        tfDuration.setText(stage.getActual_duration_min() != null ? stage.getActual_duration_min().toString() : "");
        tfVolume.setText(stage.getActual_volume() != null ? stage.getActual_volume().toPlainString() : "");
        tfMold.setText(stage.getMold_count() != null ? stage.getMold_count().toString() : "");
        tfStageNote.setText(stage.getNote() != null ? stage.getNote() : "");

        boolean isMixing = stage.getStage_no() == 1;
        panelMixing.setVisible(isMixing);
        panelMixing.setManaged(isMixing);
        if (isMixing) { tfMixingTemp.clear(); taMixingRatio.clear(); }
    }

    // ── Nút hành động chính ────────────────────────────────────────────────

    @FXML
    private void handleAction(ActionEvent event) {
        if (selectedOrder == null) return;
        lblActionMessage.setText("");

        switch (selectedOrder.getOrder_status()) {
            case STATUS_DRAFT              -> handleCreateIssueRequest();
            case STATUS_WAITING_INGREDIENT -> handleConfirmReceived();
            case STATUS_IN_PROGRESS        -> handleCompleteStage();
            default -> {}
        }
    }

    /** Create ingredient issue request */
    private void handleCreateIssueRequest() {
        if (reqRepo.existsByOrderId(selectedOrder.getProduction_order_id())) {
            showMessage("An issue request already exists for this order.", false);
            return;
        }

        IngredientExportRequest req = new IngredientExportRequest();
        req.setProduction_order_id(selectedOrder.getProduction_order_id());
        req.setPlanned_output_kg(selectedOrder.getPlanned_output_kg().doubleValue());
        if (CurrentUser.isLoggedIn()) req.setRequested_by(CurrentUser.getUser().getUserId());

        int newId = reqRepo.createWithDetails(req);
        if (newId > 0) {
            showMessage("✅ Ingredient issue request created successfully! ID: " + newId, true);
            refreshSelectedOrder();
        } else {
            showMessage("❌ Failed to create request. Please try again.", false);
        }
    }

    /** Confirm ingredients received */
    private void handleConfirmReceived() {
        IngredientExportReceipt receipt = receiptRepo.findApprovedByOrderId(selectedOrder.getProduction_order_id());
        if (receipt == null) {
            showMessage("No approved export receipt found.", false);
            return;
        }

        int requestId = receipt.getIngredient_export_request_id();
        int orderId   = selectedOrder.getProduction_order_id();

        boolean ok = receiptRepo.confirmReceived(
                receipt.getIngredient_export_receipt_id(), requestId, orderId);
        if (ok) {
            showMessage("✅ Ingredients received confirmed! Production started.", true);
            refreshSelectedOrder();
        } else {
            showMessage("❌ Confirmation failed. Please try again.", false);
        }
    }

    /** Complete current open stage */
    private void handleCompleteStage() {
        if (selectedStage == null || !"open".equals(selectedStage.getStage_status())) {
            showMessage("Please click on an open stage to record details.", false);
            return;
        }
        handleSaveStage();
    }

    /** Lưu dữ liệu công đoạn */
    @FXML
    private void handleSaveStage() {
        if (selectedStage == null) return;

        Integer duration = parseIntOrNull(tfDuration.getText());
        BigDecimal volume = parseBigDecimalOrNull(tfVolume.getText());
        Integer mold = parseIntOrNull(tfMold.getText());

        if (duration == null && volume == null) {
            showMessage("Please enter at least duration or volume.", false);
            return;
        }

        ProductionStageDetail detail = new ProductionStageDetail();
        detail.setProduction_stage_id(selectedStage.getProduction_stage_id());
        detail.setProduction_order_id(selectedStage.getProduction_order_id());
        detail.setStage_no(selectedStage.getStage_no());
        detail.setActual_duration_min(duration);
        detail.setActual_volume(volume);
        detail.setMold_count(mold);
        detail.setNote(tfStageNote.getText().trim());

        if (detail.isMixingStage()) {
            detail.setMixing_temperature_c(parseBigDecimalOrNull(tfMixingTemp.getText()));
            detail.setMixing_ratio_note(taMixingRatio.getText().trim());
        }

        boolean ok = stageRepo.completeStageWithDetail(detail);
        if (!ok) { showMessage("❌ Failed to save stage. Please try again.", false); return; }

        if (selectedStage.getStage_no() < 8) {
            stageRepo.unlockNextStage(selectedOrder.getProduction_order_id(), selectedStage.getStage_no() + 1);
            showMessage("✅ Stage " + selectedStage.getStage_no()
                    + " completed! Stage " + (selectedStage.getStage_no() + 1) + " is now open.", true);
        } else {
            orderRepo.executeUpdate(
                "UPDATE production_orders SET order_status='finished' WHERE production_order_id=?",
                ps -> ps.setInt(1, selectedOrder.getProduction_order_id()));
            showMessage("🎉 All production stages completed! Order finished.", true);
        }

        panelStageInput.setVisible(false);
        panelStageInput.setManaged(false);
        refreshSelectedOrder();
    }

    // ── Tạo lệnh mới ──────────────────────────────────────────────────────

    @FXML
    private void handleShowCreateOrder() {
        tableOrders.getSelectionModel().clearSelection();
        hideAllPanels();
        panelCreateOrder.setVisible(true);
        panelCreateOrder.setManaged(true);
        cbIceCream.setItems(FXCollections.observableArrayList(iceCreamRepo.findAllActive()));
        cbIceCream.setValue(null);
        tfOutputKg.clear();
        taOrderNote.clear();
    }

    @FXML
    private void handleCreateOrder() {
        IceCream ic = cbIceCream.getValue();
        if (ic == null) { showMessage("Vui lòng chọn loại kem.", false); return; }

        BigDecimal kg = parseBigDecimalOrNull(tfOutputKg.getText());
        if (kg == null || kg.compareTo(BigDecimal.ZERO) <= 0) {
            showMessage("Số kg phải là số dương hợp lệ.", false); return;
        }

        ProductionOrder order = new ProductionOrder();
        order.setIce_cream_id(ic.getIce_cream_id());
        order.setPlanned_output_kg(kg);
        order.setNote(taOrderNote.getText().trim());
        if (CurrentUser.isLoggedIn()) order.setCreated_by(CurrentUser.getUser().getUserId());

        int newId = orderRepo.create(order);
        if (newId > 0) {
            showMessage("✅ Production order created successfully! ID: " + newId, true);
            panelCreateOrder.setVisible(false);
            panelCreateOrder.setManaged(false);
            loadOrders();
        } else {
            showMessage("❌ Failed to create order. Please try again.", false);
        }
    }

    @FXML
    private void handleCancelCreate() {
        panelCreateOrder.setVisible(false);
        panelCreateOrder.setManaged(false);
    }

    // ── Navigation ─────────────────────────────────────────────────────────

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }

    @FXML
    private void goBack(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_MAIN_MENU, "Menu chính");
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void hideAllPanels() {
        panelDetail.setVisible(false);       panelDetail.setManaged(false);
        panelCreateOrder.setVisible(false);  panelCreateOrder.setManaged(false);
        panelIngredients.setVisible(false);  panelIngredients.setManaged(false);
        panelStages.setVisible(false);       panelStages.setManaged(false);
        panelStageInput.setVisible(false);   panelStageInput.setManaged(false);
    }

    private void refreshSelectedOrder() {
        loadOrders();
        if (selectedOrder != null) {
            // Tìm lại order đã cập nhật
            tableOrders.getItems().stream()
                .filter(o -> o.getProduction_order_id() == selectedOrder.getProduction_order_id())
                .findFirst()
                .ifPresent(o -> {
                    tableOrders.getSelectionModel().select(o);
                    onOrderSelected(o);
                });
        }
    }

    private void showMessage(String msg, boolean success) {
        lblActionMessage.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        lblActionMessage.setText(msg);
    }

    private Integer parseIntOrNull(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try { return Integer.parseInt(text.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private BigDecimal parseBigDecimalOrNull(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try { return new BigDecimal(text.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
