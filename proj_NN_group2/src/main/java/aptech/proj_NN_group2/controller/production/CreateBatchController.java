package aptech.proj_NN_group2.controller.production;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.IceCreamRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

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

    // Chỉ giữ lại một repository duy nhất
    private final IceCreamRepository iceCreamRepository = new IceCreamRepository();
    private final ProductionOrderRepository orderRepo = new ProductionOrderRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupConverter();
        
        colId.setCellValueFactory(new PropertyValueFactory<>("production_order_id"));
        colIceCream.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        colKg.setCellValueFactory(new PropertyValueFactory<>("planned_output_kg"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("order_status"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        refreshData();
        loadTable();
    }

    private void setupConverter() {
        cbIceCream.setConverter(new StringConverter<IceCream>() {
            @Override
            public String toString(IceCream ic) {
                return ic == null ? "" : ic.getIce_cream_name();
            }
            @Override
            public IceCream fromString(String string) { return null; }
        });
    }

    public void refreshData() {
        System.out.println("DEBUG: Đang chạy vào refreshData ở CreateBatchController");
        List<IceCream> iceCreams = iceCreamRepository.findAllActive();
        System.out.println("DEBUG: Số lượng sản phẩm lấy được từ DB: " + (iceCreams != null ? iceCreams.size() : "null"));
        
        cbIceCream.setItems(FXCollections.observableArrayList(iceCreams));
    }

    @FXML
    private void handleCreate() {
        lblMessage.setText("");

        IceCream selected = cbIceCream.getValue();
        if (selected == null) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Vui lòng chọn loại kem.");
            return;
        }

        String kgText = tfOutputKg.getText() == null ? "" : tfOutputKg.getText().trim();
        if (kgText.isEmpty()) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Vui lòng nhập số kg đầu ra.");
            return;
        }

        BigDecimal kg;
        try {
            kg = new BigDecimal(kgText);
            if (kg.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Số kg phải là số dương hợp lệ.");
            return;
        }

        ProductionOrder order = new ProductionOrder();
        order.setIce_cream_id(selected.getIce_cream_id());
        order.setPlanned_output_kg(kg);
        order.setNote(taNote.getText() == null ? "" : taNote.getText().trim());

        if (CurrentUser.isLoggedIn()) {
            order.setCreated_by(CurrentUser.getUserId());
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
    private void goBack(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_MAIN_MENU, "Hệ thống Quản lý Sản xuất & Xuất kho");
    }

    private void loadTable() {
        tableOrders.setItems(FXCollections.observableArrayList(orderRepo.findAll()));
    }
}