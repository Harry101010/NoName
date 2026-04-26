package aptech.proj_NN_group2.controller.production;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class ProductionDashboardController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Hiện tại không cần logic gì cả
    }
 // Nối với fx:id trong FXML (bạn nhớ đặt fx:id cho fx:include)
    @FXML private IngredientIssueRequestController issueRequestController; 

    @FXML
    public void onIssueRequestTabSelected(Event event) {
        if (issueRequestController != null) {
            issueRequestController.refreshData();
        }
    }
 // CHÚ Ý: tên biến phải khớp với fx:id trong file FXML
    @FXML private CreateBatchController createBatchController; 

    
    @FXML
    public void onCreateBatchTabSelected(Event event) {
        System.out.println("DEBUG: Đã chọn tab Tạo mẻ");
        if (createBatchController != null) {
            createBatchController.refreshData();
        } else {
            System.out.println("DEBUG: LỖI! createBatchController bị null (Không kết nối được!)");
        }
    }
}