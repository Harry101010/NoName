package aptech.proj_NN_group2.controller.production;

import aptech.proj_NN_group2.model.business.repository.RecipeRepository;
import aptech.proj_NN_group2.model.entity.Recipe;
import aptech.proj_NN_group2.model.entity.RecipeRow;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class IngredientDialogController {
    @FXML private TextField txtName, txtUnit, txtQuantity;
    
    private RecipeRow currentRow; // Chế độ Sửa
    private int iceCreamId;
    private int addIngredientId; // Chế độ Thêm
    private final RecipeRepository recipeRepo = new RecipeRepository();

    // Dùng cho chế độ SỬA
    public void initData(RecipeRow row, int iceCreamId) {
        this.currentRow = row;
        this.iceCreamId = iceCreamId;
        
        if (row != null) {
            txtName.setText(row.getIngredient_name());
            txtUnit.setText(row.getUnit_name());
            txtQuantity.setText(String.valueOf(row.getQuantity_per_kg()));
            
            // Khóa các trường không được sửa
            txtName.setEditable(false);
            txtUnit.setEditable(false);
            txtName.setStyle("-fx-background-color: #e0e0e0;"); 
        }
    }

    // Dùng cho chế độ THÊM MỚI
    public void initAddMode(int ingredientId, String name, String unit, int iceCreamId) {
        this.currentRow = null; // Chế độ thêm
        this.addIngredientId = ingredientId;
        this.iceCreamId = iceCreamId;
        
        txtName.setText(name);
        txtUnit.setText(unit);
        txtName.setEditable(false);
        txtUnit.setEditable(false);
        txtName.setStyle("-fx-background-color: #e0e0e0;");
    }

    @FXML
    private void handleSave() {
        try {
            double quantity = Double.parseDouble(txtQuantity.getText());
         // DÙNG PRINT ĐỂ KIỂM TRA XEM CÓ VÀO ĐÂY KHÔNG
            System.out.println("Đang thực hiện lưu...");
            
            if (quantity < 0) throw new NumberFormatException("Số lượng không được âm!");
                       
            if (currentRow == null) {
                // LOGIC THÊM MỚI
                Recipe newRecipe = new Recipe();
                newRecipe.setIce_cream_id(this.iceCreamId);
                newRecipe.setIngredient_id(this.addIngredientId); // Đã có ID từ hàm initAddMode
                newRecipe.setQuantity_per_kg(quantity);
                boolean success = recipeRepo.create(newRecipe);
                System.out.println("Kết quả lưu DB: " + success); // Xem console xem có in ra true không
                recipeRepo.create(newRecipe);
            } else {
                // LOGIC SỬA
                currentRow.setQuantity_per_kg(quantity);
                recipeRepo.update(currentRow);
            }
            
            handleClose();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Số lượng phải là một con số hợp lệ!");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void handleClose() {
        ((Stage)txtQuantity.getScene().getWindow()).close();
    }
}