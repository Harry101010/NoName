package aptech.proj_NN_group2.controller.production;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.IceCreamRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CreateBatchController implements Initializable {

    @FXML private ComboBox<IceCream> cbIceCream;
    @FXML private TextField tfOutputKg;
    @FXML private TextArea taNote;
    @FXML private Label lblMessage;

    @FXML private TableView<ProductionOrder> tableOrders;
    @FXML private TableColumn<ProductionOrder, Integer> colId;
    @FXML private TableColumn<ProductionOrder, String> colIceCream;
    @FXML private TableColumn<ProductionOrder, BigDecimal> colKg;
    @FXML private TableColumn<ProductionOrder, String> colStatus;
    @FXML private TableColumn<ProductionOrder, String> colCreatedAt;
    @FXML private TableColumn<ProductionOrder, String> colNote;

    private final IceCreamRepository iceCreamRepo = new IceCreamRepository();
    private final ProductionOrderRepository orderRepo = new ProductionOrderRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load danh sách kem vào ComboBox
        cbIceCream.setItems(FXCollections.observableArrayList(iceCreamRepo.findAllActive()));

        // Setup cột bảng
        colId.setCellValueFactory(new PropertyValueFactory<>("production_order_id"));
        colIceCream.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        colKg.setCellValueFactory(new PropertyValueFactory<>("planned_output_kg"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("order_status"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        loadTable();
    }

    @FXML
    private void handleCreate() {
        lblMessage.setText("");

        IceCream selected = cbIceCream.getValue();
        if (selected == null) {
            lblMessage.setText("Vui lòng chọn loại kem.");
            return;
        }

        String kgText = tfOutputKg.getText().trim();
        if (kgText.isEmpty()) {
            lblMessage.setText("Vui lòng nhập số kg đầu ra.");
            return;
        }

        BigDecimal kg;
        try {
            kg = new BigDecimal(kgText);
            if (kg.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblMessage.setText("Số kg phải là số dương hợp lệ.");
            return;
        }

        ProductionOrder order = new ProductionOrder();
        order.setIce_cream_id(selected.getIce_cream_id());
        order.setPlanned_output_kg(kg);
        order.setNote(taNote.getText().trim());
        if (aptech.proj_NN_group2.util.CurrentUser.isLoggedIn()) {
            order.setCreated_by(aptech.proj_NN_group2.util.CurrentUser.getUser().getUserId());
        }

        int newId = orderRepo.create(order);
        if (newId > 0) {
            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setText("Tạo mẻ sản xuất thành công! ID: " + newId);
            handleReset();
            loadTable();
        } else {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Tạo thất bại, vui lòng thử lại.");
        }
    }

    @FXML
    private void handleReset() {
        cbIceCream.setValue(null);
        tfOutputKg.clear();
        taNote.clear();
        lblMessage.setText("");
    }

    @FXML
    private void goBack() throws java.io.IOException {
        aptech.proj_NN_group2.App.setRoot(aptech.proj_NN_group2.util.StringValue.VIEW_MAIN_MENU);
    }

    private void loadTable() {
        tableOrders.setItems(FXCollections.observableArrayList(orderRepo.findAll()));
    }
}