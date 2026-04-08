module aptech.proj_NN_group2 {
	requires javafx.controls;
    requires javafx.fxml;
	requires java.sql;
	requires com.microsoft.sqlserver.jdbc;
	requires com.zaxxer.hikari;
	requires jbcrypt; // Thêm dòng này để cho phép dùng BCrypt

	opens aptech.proj_NN_group2 to javafx.fxml;
    opens aptech.proj_NN_group2.controller to javafx.fxml;
    opens aptech.proj_NN_group2.model.entity to javafx.base;

    
    exports aptech.proj_NN_group2;
}