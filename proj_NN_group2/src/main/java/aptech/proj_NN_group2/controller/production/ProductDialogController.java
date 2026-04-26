package aptech.proj_NN_group2.controller.production;

import aptech.proj_NN_group2.model.business.repository.IceCreamRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProductDialogController {
    @FXML private TextField txtProductName;
    @FXML private CheckBox chkActive;
    
    private IceCream currentProduct;
    private final IceCreamRepository iceCreamRepo = new IceCreamRepository();

    // Dùng để nạp dữ liệu cũ khi sửa sản phẩm
    public void initData(IceCream product) {
        this.currentProduct = product;
        if (product != null) {
            txtProductName.setText(product.getIce_cream_name());
            chkActive.setSelected(product.getIs_active());
        } else {
            // Mặc định cho sản phẩm mới
            chkActive.setSelected(true);
        }
    }

    @FXML
    private void handleSave() {
        String name = txtProductName.getText();
        if (name == null || name.trim().isEmpty()) return;

        if (currentProduct == null) {
            // --- THÊM MỚI ---
            IceCream newProduct = new IceCream();
            newProduct.setIce_cream_name(name.trim());
            newProduct.setIs_active(chkActive.isSelected()); // Lấy từ CheckBox
            iceCreamRepo.create(newProduct);
        } else {
            // --- CẬP NHẬT ---
            currentProduct.setIce_cream_name(name.trim());
            currentProduct.setIs_active(chkActive.isSelected()); // Lấy từ CheckBox
            iceCreamRepo.update(currentProduct);
        }
        handleClose();
    }

    @FXML
    private void handleClose() {
        ((Stage)txtProductName.getScene().getWindow()).close();
    }
}