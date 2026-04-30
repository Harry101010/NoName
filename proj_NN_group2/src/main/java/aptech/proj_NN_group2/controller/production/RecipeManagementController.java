package aptech.proj_NN_group2.controller.production;

import aptech.proj_NN_group2.model.business.repository.IceCreamRepository;
import aptech.proj_NN_group2.model.business.repository.RecipeRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.entity.Recipe;
import aptech.proj_NN_group2.model.entity.RecipeRow;
import aptech.proj_NN_group2.model.entity.ingredient.Ingredient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class RecipeManagementController {
	@FXML private TableView<IceCream> tblProducts;
    @FXML private TableColumn<IceCream, Integer> colProductId;
    @FXML private TableColumn<IceCream, String> colProductName;
    @FXML private TableColumn<IceCream, Boolean> colProductActive; // Cột mới
    @FXML private TextField txtProductFilter;

    @FXML private TableView<RecipeRow> tblRecipeDetails;
    @FXML private TableColumn<RecipeRow, String> colSTT;
    @FXML private TableColumn<RecipeRow, String> colIngName;
    @FXML private TableColumn<RecipeRow, Double> colQuantity;
    @FXML private TableColumn<RecipeRow, String> colUnit;
    @FXML private Label lblRecipeTitle;

    private final RecipeRepository recipeRepo = new RecipeRepository();
    private final IceCreamRepository iceCreamRepo = new IceCreamRepository();
    
    private final ObservableList<IceCream> productList = FXCollections.observableArrayList();
    private FilteredList<IceCream> filteredList;

    @FXML
    public void initialize() {
        setupTableColumns();
        
        filteredList = new FilteredList<>(productList, p -> true);
        tblProducts.setItems(filteredList);

        txtProductFilter.textProperty().addListener((o, old, val) -> {
            filteredList.setPredicate(p -> val == null || val.isEmpty() || 
                p.getIce_cream_name().toLowerCase().contains(val.toLowerCase()));
        });
        
        loadProductData();
        
        tblProducts.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if (newVal != null) {
                lblRecipeTitle.setText("Nguyên liệu: " + newVal.getIce_cream_name());
                refreshIngredientTable(newVal.getIce_cream_id());
            }
        });
    }

    private void setupTableColumns() {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("ice_cream_id"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        
        // --- CẤU HÌNH CỘT ACTIVE ---
        colProductActive.setCellValueFactory(new PropertyValueFactory<>("is_active"));
        colProductActive.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "Có" : "Không"));
            }
        });

        colSTT.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(empty ? null : item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colIngName.setCellValueFactory(new PropertyValueFactory<>("ingredient_name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity_per_kg"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit_name"));
    }

    private void loadProductData() {
        var allProducts = iceCreamRepo.findAll();
        productList.setAll(allProducts);
    }
 // Thêm hàm này vào class RecipeManagementController
    public void refreshAll() {
        // Gọi hàm loadProductData() để lấy lại dữ liệu từ DB
        loadProductData();
        System.out.println("Đã refresh định mức sản phẩm!");
    }

    private void refreshIngredientTable(int iceCreamId) {
        var updatedList = recipeRepo.findRowsByIceCreamId(iceCreamId);
        tblRecipeDetails.setItems(FXCollections.observableArrayList(updatedList));
    }

    @FXML 
    private void handleProductAdd() {
        openProductDialog(null, "Thêm Sản Phẩm");
        loadProductData(); 
    }

    @FXML 
    private void handleProductEdit() {
        IceCream selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn sản phẩm cần sửa!").show();
            return;
        }
        openProductDialog(selected, "Sửa Sản Phẩm");
        loadProductData(); 
    }

    @FXML 
    private void handleProductDelete() {
        IceCream selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa sản phẩm " + selected.getIce_cream_name() + "?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            iceCreamRepo.delete(selected.getIce_cream_id());
            loadProductData();
            tblRecipeDetails.getItems().clear();
        }
    }

    private void openProductDialog(IceCream product, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/production/product_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            
            ProductDialogController controller = loader.getController();
            controller.initData(product);
            
            stage.showAndWait();
            loadProductData();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
	
	
	

   

    

   

    // --- CÁC HÀM XỬ LÝ NGUYÊN LIỆU ---
    
    // Thêm nguyên liệu (Mở bảng chọn)
    @FXML 
    private void handleIngredientAdd() {
        IceCream selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Hãy chọn sản phẩm ở danh sách bên trái trước!").show();
            return;
        }

        try {
            // 1. Load FXML thủ công để lấy Controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/aptech/proj_NN_group2/production/add_ingredient_dialog.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thêm Nguyên Liệu");
            stage.setScene(new Scene(loader.load()));

            // 2. Lấy controller của Dialog để chuẩn bị đọc dữ liệu sau khi đóng
            AddIngredientDialogController dialogController = loader.getController();

            // 3. Hiển thị Dialog và đợi người dùng thao tác
            stage.showAndWait();

            // 4. LẤY DỮ LIỆU SAU KHI DIALOG ĐÓNG
            Ingredient ing = dialogController.getSelectedIngredient();
            double quantity = dialogController.getQuantity();

            // 5. NẾU CÓ DỮ LIỆU, THỰC HIỆN LƯU VÀO DB
            if (ing != null) {
                Recipe newRecipe = new Recipe();
                newRecipe.setIce_cream_id(selected.getIce_cream_id());
                newRecipe.setIngredient_id(ing.getIngredient_id());
                newRecipe.setQuantity_per_kg(quantity);
                
                // Gọi repository để lưu
                recipeRepo.create(newRecipe);
                
                // 6. Refresh lại bảng để hiện dữ liệu mới
                refreshIngredientTable(selected.getIce_cream_id());
                
                System.out.println("Đã lưu nguyên liệu: " + ing.getIngredient_name());
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi thêm nguyên liệu: " + e.getMessage()).show();
        }
    }

    // Sửa định mức (Mở form sửa)
    @FXML private void handleIngredientEdit() {
        RecipeRow selected = tblRecipeDetails.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Hãy chọn dòng nguyên liệu cần sửa!").show();
            return;
        }
        openDialog("/aptech/proj_NN_group2/production/edit_ingredient_dialog.fxml", "Sửa Định Mức", selected);
        refreshIngredientTable(tblProducts.getSelectionModel().getSelectedItem().getIce_cream_id());
    }

    // Xóa nguyên liệu
    @FXML private void handleIngredientDelete() {
        RecipeRow selected = tblRecipeDetails.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn chắc chắn muốn xóa?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            recipeRepo.deleteRow(selected); // Đảm bảo hàm này đã có trong RecipeRepository
            refreshIngredientTable(tblProducts.getSelectionModel().getSelectedItem().getIce_cream_id());
        }
    }

    // --- HÀM DÙNG CHUNG ĐỂ MỞ POPUP ---
    private void openDialog(String fxmlPath, String title, RecipeRow dataToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            
            // Nếu có dữ liệu để sửa, truyền vào dialog
            if (dataToEdit != null && loader.getController() instanceof IngredientDialogController) {
                ((IngredientDialogController) loader.getController()).initData(dataToEdit, tblProducts.getSelectionModel().getSelectedItem().getIce_cream_id());
            }
            
            stage.showAndWait();
        } catch (Exception e) { 
            e.printStackTrace(); 
            new Alert(Alert.AlertType.ERROR, "Không thể mở cửa sổ: " + e.getMessage()).show();
        }
    }
 // --- CÁC HÀM XỬ LÝ SẢN PHẨM (BÊN TRÁI) ---

//    @FXML 
//    private void handleProductAdd() {
//        // Mở Dialog với dữ liệu null (Chế độ thêm mới)
//        openProductDialog(null, "Thêm Sản Phẩm");
//        loadProductData(); // Refresh lại danh sách sản phẩm
//    }
//
//    
//
//    @FXML 
//    private void handleProductDelete() {
//        IceCream selected = tblProducts.getSelectionModel().getSelectedItem();
//        if (selected == null) return;
//
//        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa sản phẩm " + selected.getIce_cream_name() + "?");
//        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
//            iceCreamRepo.delete(selected.getIce_cream_id()); // Đảm bảo hàm này có trong IceCreamRepository
//            loadProductData(); // Refresh danh sách
//            tblRecipeDetails.getItems().clear(); // Xóa công thức hiển thị nếu có
//        }
//    }
//
//    // Hàm mở Dialog riêng cho Sản phẩm
//    private void openProductDialog(IceCream product, String title) {
//        String fxmlPath = "/aptech/proj_NN_group2/production/product_form.fxml"; // Kiểm tra kỹ đường dẫn này!
//        try {
//            java.net.URL url = getClass().getResource(fxmlPath);
//            
//            // Debug: In ra đường dẫn thực tế để kiểm tra
//            if (url == null) {
//                System.err.println("LỖI: KHÔNG TÌM THẤY FILE FXML tại đường dẫn: " + fxmlPath);
//                System.err.println("Hãy chắc chắn file nằm trong /aptech/proj_NN_group2/production/product_form.fxml");
//                return; // Dừng lại không chạy tiếp để tránh crash
//            }
//
//            FXMLLoader loader = new FXMLLoader(url);
//            Stage stage = new Stage();
//            stage.setTitle(title);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.setScene(new Scene(loader.load()));
//            
//        
//         // Truyền đối tượng đã chọn (có ID) sang Controller của Dialog
//            ProductDialogController controller = loader.getController();
//            controller.initData(product); // Nếu product là null, nó hiểu là chế độ Thêm
//            
//            stage.showAndWait();
//            loadProductData();
//        } catch (Exception e) { 
//            e.printStackTrace(); 
//        }
//    }
//   
//    @FXML 
//    private void handleProductEdit() {
//        IceCream selected = tblProducts.getSelectionModel().getSelectedItem();
//        if (selected == null) {
//            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn sản phẩm cần sửa!").show();
//            return;
//        }
//        // Mở Dialog với dữ liệu sản phẩm đang chọn (Chế độ sửa)
//        openProductDialog(selected, "Sửa Sản Phẩm");
//        loadProductData(); // Refresh lại danh sách
//    }
    @FXML
    private void newIceCream(ActionEvent event) {
        // Logic của bạn để thêm kem mới ở đây
        System.out.println("Nút Thêm kem mới đã được nhấn!");
        // Ví dụ: NavigationUtil.goTo(event, ..., ...);
    }
}