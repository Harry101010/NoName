module aptech.proj_NN_group2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens aptech.proj_NN_group2 to javafx.fxml;
    exports aptech.proj_NN_group2;
    requires java.sql;
}
