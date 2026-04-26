package aptech.proj_NN_group2;

import aptech.proj_NN_group2.model.entity.User;
import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.SceneManager;
import aptech.proj_NN_group2.util.StringValue;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Giả lập đăng nhập (Bỏ qua bước login để test nhanh)
        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("dev_tester");
        mockUser.setRoleId(1); // Admin
        mockUser.setRoleName("Admin");
        CurrentUser.setUser(mockUser);
        
        String viewToTest = StringValue.VIEW_ADMIN_DASHBOARD;

        // 2. CHỌN MÀN HÌNH CẦN TEST
        // Bạn hãy đổi tham số thứ 2 thành view bạn muốn:
        
        // --- CHỌN 1 TRONG CÁC DÒNG DƯỚI ĐÂY ---
        
        // Để test Kho:
//        String viewToTest = StringValue.VIEW_WAREHOUSE_DASHBOARD; 
        
        // Để test Sản xuất (Menu chính):
//         String viewToTest = StringValue.VIEW_MAIN_MENU;
        
        // Để test Tạo mẻ sản xuất:
        // String viewToTest = StringValue.VIEW_CREATE_BATCH;

        // ----------------------------------------

        SceneManager.show(stage, viewToTest, "Test Module: " + viewToTest);
        stage.setWidth(1200);   // Chiều rộng mong muốn
        stage.setHeight(800);   // Chiều cao mong muốn
        stage.centerOnScreen(); // Đưa cửa sổ ra giữa màn hình
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}