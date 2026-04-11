module aptech.proj_NN_group2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;
    requires jbcrypt; 
    requires jakarta.mail;

    // Cho phép JavaFX truy cập vào các folder chứa file FXML và Controller
    opens aptech.proj_NN_group2 to javafx.fxml;
    opens aptech.proj_NN_group2.controller to javafx.fxml;
    opens aptech.proj_NN_group2.controller.admin to javafx.fxml;
    opens aptech.proj_NN_group2.controller.auth to javafx.fxml;
    
    // Rất quan trọng: Cho phép TableView đọc dữ liệu từ các thuộc tính của User/Role
    opens aptech.proj_NN_group2.model.entity to javafx.base;

    // PHẦN SỬA LỖI: Public các package để các Controller có thể nhận diện được Model và Repository
    exports aptech.proj_NN_group2;
    exports aptech.proj_NN_group2.model.entity;
    exports aptech.proj_NN_group2.model.business.repository;
    exports aptech.proj_NN_group2.model.mapper;
    exports aptech.proj_NN_group2.util;
}