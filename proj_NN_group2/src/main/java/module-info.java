module aptech.proj_NN_group2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;
    requires jbcrypt;
    requires jakarta.mail;
	requires java.prefs;
	requires org.apache.poi.poi;
	requires org.apache.poi.ooxml;

    opens aptech.proj_NN_group2 to javafx.fxml;

    opens aptech.proj_NN_group2.controller.admin to javafx.fxml;
    opens aptech.proj_NN_group2.controller.auth to javafx.fxml;
    opens aptech.proj_NN_group2.controller.production to javafx.fxml;
    opens aptech.proj_NN_group2.controller.sales to javafx.fxml;
    opens aptech.proj_NN_group2.controller.warehouse to javafx.fxml;

    opens aptech.proj_NN_group2.model.entity to javafx.base;

    exports aptech.proj_NN_group2;
    exports aptech.proj_NN_group2.controller.admin;
    exports aptech.proj_NN_group2.controller.auth;
    exports aptech.proj_NN_group2.controller.production;
    exports aptech.proj_NN_group2.controller.sales;
    exports aptech.proj_NN_group2.controller.warehouse;

    exports aptech.proj_NN_group2.model.entity;
    exports aptech.proj_NN_group2.model.business.repository;
    exports aptech.proj_NN_group2.model.mapper;
    exports aptech.proj_NN_group2.util;
}