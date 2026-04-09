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

    opens aptech.proj_NN_group2.controller.admin to javafx.fxml;
    opens aptech.proj_NN_group2.controller.auth to javafx.fxml;
//    opens aptech.proj_NN_group2.controller.ingredient to javafx.fxml;
//    opens aptech.proj_NN_group2.controller.production to javafx.fxml;
//    opens aptech.proj_NN_group2.controller.production_stage to javafx.fxml;
//    opens aptech.proj_NN_group2.controller.recipe to javafx.fxml;
//    opens aptech.proj_NN_group2.controller.saleman to javafx.fxml;
//    opens aptech.proj_NN_group2.controller.warehouse to javafx.fxml;
    
    exports aptech.proj_NN_group2;
}