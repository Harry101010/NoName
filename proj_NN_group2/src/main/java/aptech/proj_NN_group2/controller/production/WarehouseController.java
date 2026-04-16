package aptech.proj_NN_group2.controller.production;

import aptech.proj_NN_group2.util.DialogUtil;
import aptech.proj_NN_group2.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class WarehouseController {

    @FXML
    private void handleAdd(ActionEvent event) {
        DialogUtil.info(owner(event), "Nhập kho", "Màn hình kho đã mở đúng, nhưng nghiệp vụ nhập kho nguyên liệu chưa có backend riêng trong project hiện tại.");
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        DialogUtil.info(owner(event), "Cập nhật tồn kho", "Màn hình kho đã mở đúng, nhưng nghiệp vụ cập nhật tồn kho nguyên liệu chưa có backend riêng trong project hiện tại.");
    }

    @FXML
    private void handleExport(ActionEvent event) {
        DialogUtil.info(owner(event), "Xuất kho", "Màn hình kho đã mở đúng, nhưng nghiệp vụ xuất kho nguyên liệu chưa có backend riêng trong project hiện tại.");
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        DialogUtil.info(owner(event), "Làm mới", "Đã làm mới màn hình kho.");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }

    private Node owner(ActionEvent event) {
        Object source = event != null ? event.getSource() : null;
        return source instanceof Node node ? node : null;
    }
}