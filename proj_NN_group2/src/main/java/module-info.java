module aptech.proj_NN_group2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;
    requires jbcrypt;
    requires jakarta.mail;

    // Cho phép JavaFX FXML loader truy cập các Controller
    opens aptech.proj_NN_group2 to javafx.fxml;
    opens aptech.proj_NN_group2.controller to javafx.fxml;
    opens aptech.proj_NN_group2.controller.admin to javafx.fxml;
    opens aptech.proj_NN_group2.controller.auth to javafx.fxml;
    opens aptech.proj_NN_group2.controller.saleman to javafx.fxml;
    opens aptech.proj_NN_group2.controller.production to javafx.fxml;

    // Cho phép TableView đọc properties của Entity (reflection)
    opens aptech.proj_NN_group2.model.entity to javafx.base;
    opens aptech.proj_NN_group2.model.entity.saleman to javafx.base;

    // Export các package cần thiết
    exports aptech.proj_NN_group2;
    exports aptech.proj_NN_group2.model.entity;
    exports aptech.proj_NN_group2.model.entity.saleman;
    exports aptech.proj_NN_group2.model.business.repository;
    exports aptech.proj_NN_group2.model.business.repository.saleman;
    exports aptech.proj_NN_group2.model.mapper;
    exports aptech.proj_NN_group2.util;
    exports aptech.proj_NN_group2.controller.saleman;
}
