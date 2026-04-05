module aptech.proj_NN_group2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens aptech.proj_NN_group2 to javafx.fxml;
    exports aptech.proj_NN_group2;
    requires java.sql;
    
 // Cho phép FXML đọc các file trong package controller
    opens aptech.proj_NN_group2.controller to javafx.fxml;
    
    // Xuất package để các phần khác có thể sử dụng
    exports aptech.proj_NN_group2.controller;
    exports aptech.proj_NN_group2.util;
    
    opens aptech.proj_NN_group2.model to javafx.base;
    
}
