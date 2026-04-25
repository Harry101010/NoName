package aptech.proj_NN_group2.controller.warehouse;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

import aptech.proj_NN_group2.model.business.repository.IngredientExportRequestRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportReceipt;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ExportHistoryController implements Initializable {

    @FXML private TableView<IngredientExportReceipt> tblHistory;
    @FXML private TableColumn<IngredientExportReceipt, Integer> colId;
    @FXML private TableColumn<IngredientExportReceipt, String> colStatus;
    @FXML private TableColumn<IngredientExportReceipt, Timestamp> colDate;

    private final IngredientExportRequestRepository repo = new IngredientExportRequestRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cấu hình cột
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        
        // Load dữ liệu
        loadHistory();
    }

    private void loadHistory() {
    	List<IngredientExportReceipt> list = repo.getAllReceipts();
        // Giả sử repo của bạn có hàm getAllReceipts()
    	System.out.println("DEBUG: Số lượng phiếu trong danh sách là: " + list.size());
        tblHistory.setItems(FXCollections.observableArrayList(repo.getAllReceipts()));
    }
}