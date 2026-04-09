package aptech.proj_NN_group2.controller;

import java.text.DecimalFormat;
import java.util.List;

import aptech.proj_NN_group2.model.business.repository.IceCreamRepository;
import aptech.proj_NN_group2.model.business.repository.IngredientRepository;
import aptech.proj_NN_group2.model.business.repository.RecipeRepository;
import aptech.proj_NN_group2.model.business.repository.UnitRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.entity.Ingredient;
import aptech.proj_NN_group2.model.entity.IngredientRow;
import aptech.proj_NN_group2.model.entity.Recipe;
import aptech.proj_NN_group2.model.entity.RecipeRow;
import aptech.proj_NN_group2.model.entity.Unit;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class RecipeManagementController {

    private final IceCreamRepository iceCreamRepository = new IceCreamRepository();
    private final IngredientRepository ingredientRepository = new IngredientRepository();
    private final RecipeRepository recipeRepository = new RecipeRepository();
    private final UnitRepository unitRepository = new UnitRepository();

    private final DecimalFormat df = new DecimalFormat("#0.####");

    @FXML private TextField txtIceCreamName;
    @FXML private CheckBox chkIceCreamActive;
    @FXML private TableView<IceCream> tblIceCream;
    @FXML private TableColumn<IceCream, Integer> colIceCreamId;
    @FXML private TableColumn<IceCream, String> colIceCreamName;
    @FXML private TableColumn<IceCream, Boolean> colIceCreamActive;

    @FXML private TextField txtIngredientName;
    @FXML private TextField txtIngredientOrigin;
    @FXML private TextField txtStorageCondition;
    @FXML private ComboBox<Unit> cboUnit;
    @FXML private TextField txtPricePerUnit;
    @FXML private CheckBox chkIngredientActive;
    @FXML private TableView<IngredientRow> tblIngredient;
    @FXML private TableColumn<IngredientRow, Integer> colIngredientId;
    @FXML private TableColumn<IngredientRow, String> colIngredientName;
    @FXML private TableColumn<IngredientRow, String> colIngredientOrigin;
    @FXML private TableColumn<IngredientRow, String> colIngredientStorage;
    @FXML private TableColumn<IngredientRow, Integer> colIngredientUnitId;
    @FXML private TableColumn<IngredientRow, String> colIngredientUnitName;
    @FXML private TableColumn<IngredientRow, Double> colIngredientPrice;
    @FXML private TableColumn<IngredientRow, Boolean> colIngredientActive;

    @FXML private ComboBox<IceCream> cboRecipeIceCream;
    @FXML private ComboBox<Ingredient> cboRecipeIngredient;
    @FXML private TextField txtQuantityPerKg;
    @FXML private TableView<RecipeRow> tblRecipe;
    @FXML private TableColumn<RecipeRow, Integer> colRecipeId;
    @FXML private TableColumn<RecipeRow, String> colRecipeIceCreamName;
    @FXML private TableColumn<RecipeRow, String> colRecipeIngredientName;
    @FXML private TableColumn<RecipeRow, String> colRecipeUnitName;
    @FXML private TableColumn<RecipeRow, Double> colRecipeQtyPerKg;

    @FXML private ComboBox<IceCream> cboCalcIceCream;
    @FXML private TextField txtCalcOutputKg;
    @FXML private TextArea txtCalcResult;

    private IceCream currentIceCream;
    private Ingredient currentIngredient;
    private Recipe currentRecipe;

    @FXML
    public void initialize() {
        setupTables();
        loadCombos();
        refreshAllTables();

        tblIceCream.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                currentIceCream = newV;
                fillIceCreamForm(newV);
            }
        });

        tblIngredient.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                currentIngredient = ingredientRepository.findById(newV.getIngredient_id());
                fillIngredientForm(currentIngredient);
            }
        });

        tblRecipe.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                currentRecipe = recipeRepository.findById(newV.getRecipe_id());
                fillRecipeForm(currentRecipe);
            }
        });

        cboRecipeIceCream.valueProperty().addListener((obs, oldV, newV) -> refreshRecipeTable());
        cboCalcIceCream.valueProperty().addListener((obs, oldV, newV) -> clearCalcResult());
    }

    private void setupTables() {
        colIceCreamId.setCellValueFactory(new PropertyValueFactory<>("ice_cream_id"));
        colIceCreamName.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        colIceCreamActive.setCellValueFactory(new PropertyValueFactory<>("is_active"));

        colIngredientId.setCellValueFactory(new PropertyValueFactory<>("ingredient_id"));
        colIngredientName.setCellValueFactory(new PropertyValueFactory<>("ingredient_name"));
        colIngredientOrigin.setCellValueFactory(new PropertyValueFactory<>("origin"));
        colIngredientStorage.setCellValueFactory(new PropertyValueFactory<>("storage_condition"));
        colIngredientUnitId.setCellValueFactory(new PropertyValueFactory<>("unit_id"));
        colIngredientUnitName.setCellValueFactory(new PropertyValueFactory<>("unit_name"));
        colIngredientPrice.setCellValueFactory(new PropertyValueFactory<>("price_per_unit"));
        colIngredientActive.setCellValueFactory(new PropertyValueFactory<>("is_active"));

        colRecipeId.setCellValueFactory(new PropertyValueFactory<>("recipe_id"));
        colRecipeIceCreamName.setCellValueFactory(new PropertyValueFactory<>("ice_cream_name"));
        colRecipeIngredientName.setCellValueFactory(new PropertyValueFactory<>("ingredient_name"));
        colRecipeUnitName.setCellValueFactory(new PropertyValueFactory<>("unit_name"));
        colRecipeQtyPerKg.setCellValueFactory(new PropertyValueFactory<>("quantity_per_kg"));
    }

    private void loadCombos() {
        List<Unit> units = unitRepository.findAll();
        cboUnit.setItems(FXCollections.observableArrayList(units));

        List<IceCream> iceCreams = iceCreamRepository.findAll();
        cboRecipeIceCream.setItems(FXCollections.observableArrayList(iceCreams));
        cboCalcIceCream.setItems(FXCollections.observableArrayList(iceCreams));

        List<Ingredient> ingredients = ingredientRepository.findAll();
        cboRecipeIngredient.setItems(FXCollections.observableArrayList(ingredients));

        if (!iceCreams.isEmpty()) {
            cboRecipeIceCream.getSelectionModel().selectFirst();
            cboCalcIceCream.getSelectionModel().selectFirst();
        }
        if (!units.isEmpty()) {
            cboUnit.getSelectionModel().selectFirst();
        }
        if (!ingredients.isEmpty()) {
            cboRecipeIngredient.getSelectionModel().selectFirst();
        }
    }

    private void refreshAllTables() {
        tblIceCream.setItems(FXCollections.observableArrayList(iceCreamRepository.findAll()));
        tblIngredient.setItems(FXCollections.observableArrayList(ingredientRepository.findAllRows()));
        refreshRecipeTable();
    }

    private void refreshRecipeTable() {
        IceCream selected = cboRecipeIceCream.getValue();
        if (selected == null) {
            tblRecipe.setItems(FXCollections.observableArrayList(recipeRepository.findAllRows()));
        } else {
            tblRecipe.setItems(FXCollections.observableArrayList(recipeRepository.findRowsByIceCreamId(selected.getIce_cream_id())));
        }
    }

    private void fillIceCreamForm(IceCream iceCream) {
        txtIceCreamName.setText(iceCream.getIce_cream_name());
        chkIceCreamActive.setSelected(iceCream.getIs_active());
    }

    private void fillIngredientForm(Ingredient ingredient) {
        txtIngredientName.setText(ingredient.getIngredient_name());
        txtIngredientOrigin.setText(ingredient.getOrigin());
        txtStorageCondition.setText(ingredient.getStorage_condition());
        txtPricePerUnit.setText(df.format(ingredient.getPrice_per_unit()));
        chkIngredientActive.setSelected(ingredient.getIs_active());

        Unit unit = unitRepository.findById(ingredient.getUnit_id());
        if (unit != null) {
            cboUnit.getSelectionModel().select(unit);
        }
    }

    private void fillRecipeForm(Recipe recipe) {
        IceCream iceCream = iceCreamRepository.findById(recipe.getIce_cream_id());
        Ingredient ingredient = ingredientRepository.findById(recipe.getIngredient_id());

        if (iceCream != null) cboRecipeIceCream.getSelectionModel().select(iceCream);
        if (ingredient != null) cboRecipeIngredient.getSelectionModel().select(ingredient);
        txtQuantityPerKg.setText(df.format(recipe.getQuantity_per_kg()));
    }

    private void clearIceCreamForm() {
        currentIceCream = null;
        txtIceCreamName.clear();
        chkIceCreamActive.setSelected(true);
        tblIceCream.getSelectionModel().clearSelection();
    }

    private void clearIngredientForm() {
        currentIngredient = null;
        txtIngredientName.clear();
        txtIngredientOrigin.clear();
        txtStorageCondition.clear();
        txtPricePerUnit.clear();
        chkIngredientActive.setSelected(true);
        cboUnit.getSelectionModel().clearSelection();
        tblIngredient.getSelectionModel().clearSelection();
    }

    private void clearRecipeForm() {
        currentRecipe = null;
        txtQuantityPerKg.clear();
        cboRecipeIngredient.getSelectionModel().clearSelection();
        tblRecipe.getSelectionModel().clearSelection();
    }

    private void clearCalcResult() {
        txtCalcResult.clear();
    }

    @FXML
    private void newIceCream() {
        clearIceCreamForm();
    }

    @FXML
    private void saveIceCream() {
        String name = txtIceCreamName.getText() == null ? "" : txtIceCreamName.getText().trim();
        if (name.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Tên kem không được để trống.");
            return;
        }

        IceCream iceCream = currentIceCream != null ? currentIceCream : new IceCream();
        iceCream.setIce_cream_name(name);
        iceCream.setIs_active(chkIceCreamActive.isSelected());

        boolean ok = iceCream.getIce_cream_id() == 0
                ? iceCreamRepository.save(iceCream)
                : iceCreamRepository.update(iceCream);

        if (ok) {
            loadCombos();
            refreshAllTables();
            selectIceCream(iceCream.getIce_cream_id());
            alert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu thành phẩm kem.");
        } else {
            alert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu thành phẩm kem.");
        }
    }

    @FXML
    private void deleteIceCream() {
        IceCream selected = tblIceCream.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Hãy chọn một thành phẩm kem để xóa.");
            return;
        }

        recipeRepository.deleteByIceCreamId(selected.getIce_cream_id());
        if (iceCreamRepository.delete(selected.getIce_cream_id())) {
            loadCombos();
            refreshAllTables();
            clearIceCreamForm();
            alert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa thành phẩm kem.");
        } else {
            alert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa thành phẩm kem.");
        }
    }

    @FXML
    private void newIngredient() {
        clearIngredientForm();
    }

    @FXML
    private void saveIngredient() {
        try {
            String name = txtIngredientName.getText() == null ? "" : txtIngredientName.getText().trim();
            if (name.isEmpty()) {
                alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Tên nguyên liệu không được để trống.");
                return;
            }

            Unit unit = cboUnit.getValue();
            if (unit == null) {
                alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Bạn phải chọn đơn vị.");
                return;
            }

            double price = parseDouble(txtPricePerUnit.getText(), "Giá thành / đơn vị");

            Ingredient ingredient = currentIngredient != null ? currentIngredient : new Ingredient();
            ingredient.setIngredient_name(name);
            ingredient.setOrigin(txtIngredientOrigin.getText());
            ingredient.setStorage_condition(txtStorageCondition.getText());
            ingredient.setUnit_id(unit.getUnit_id());
            ingredient.setPrice_per_unit(price);
            ingredient.setIs_active(chkIngredientActive.isSelected());

            boolean ok = ingredient.getIngredient_id() == 0
                    ? ingredientRepository.save(ingredient)
                    : ingredientRepository.update(ingredient);

            if (ok) {
                loadCombos();
                refreshAllTables();
                selectIngredient(ingredient.getIngredient_id());
                alert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu nguyên liệu.");
            } else {
                alert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu nguyên liệu.");
            }
        } catch (Exception ex) {
            alert(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
        }
    }

    @FXML
    private void deleteIngredient() {
        Ingredient selected = tblIngredient.getSelectionModel().getSelectedItem() != null
                ? ingredientRepository.findById(tblIngredient.getSelectionModel().getSelectedItem().getIngredient_id())
                : null;

        if (selected == null) {
            alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Hãy chọn một nguyên liệu để xóa.");
            return;
        }

        recipeRepository.deleteByIngredientId(selected.getIngredient_id());
        if (ingredientRepository.delete(selected.getIngredient_id())) {
            loadCombos();
            refreshAllTables();
            clearIngredientForm();
            alert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa nguyên liệu.");
        } else {
            alert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa nguyên liệu.");
        }
    }

    @FXML
    private void newRecipe() {
        clearRecipeForm();
    }

    @FXML
    private void saveRecipe() {
        try {
            IceCream iceCream = cboRecipeIceCream.getValue();
            Ingredient ingredient = cboRecipeIngredient.getValue();

            if (iceCream == null) {
                alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Bạn phải chọn một thành phẩm kem.");
                return;
            }
            if (ingredient == null) {
                alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Bạn phải chọn nguyên liệu.");
                return;
            }

            double qtyPerKg = parseDouble(txtQuantityPerKg.getText(), "Định mức / 1kg");

            Recipe recipe = currentRecipe != null ? currentRecipe : new Recipe();
            recipe.setIce_cream_id(iceCream.getIce_cream_id());
            recipe.setIngredient_id(ingredient.getIngredient_id());
            recipe.setQuantity_per_kg(qtyPerKg);

            if (recipe.getRecipe_id() == 0) {
                Recipe existing = recipeRepository.findByIceCreamAndIngredient(
                        iceCream.getIce_cream_id(),
                        ingredient.getIngredient_id()
                );
                if (existing != null) {
                    recipe.setRecipe_id(existing.getRecipe_id());
                    boolean okUpdate = recipeRepository.update(recipe);
                    if (okUpdate) {
                        refreshRecipeTable();
                        alert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật định mức hiện có.");
                    } else {
                        alert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật định mức.");
                    }
                    return;
                }
            }

            boolean ok = recipe.getRecipe_id() == 0
                    ? recipeRepository.save(recipe)
                    : recipeRepository.update(recipe);

            if (ok) {
                refreshRecipeTable();
                selectRecipe(recipe.getRecipe_id());
                alert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu công thức.");
            } else {
                alert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu công thức.");
            }
        } catch (Exception ex) {
            alert(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
        }
    }

    @FXML
    private void deleteRecipe() {
        RecipeRow selected = tblRecipe.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Hãy chọn một dòng công thức để xóa.");
            return;
        }

        if (recipeRepository.delete(selected.getRecipe_id())) {
            refreshRecipeTable();
            clearRecipeForm();
            alert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa công thức.");
        } else {
            alert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa công thức.");
        }
    }

    @FXML
    private void calculateRecipe() {
        try {
            IceCream iceCream = cboCalcIceCream.getValue();
            if (iceCream == null) {
                alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Bạn phải chọn thành phẩm kem cần tính.");
                return;
            }

            double outputKg = parseDouble(txtCalcOutputKg.getText(), "Sản lượng đầu ra (kg)");
            List<RecipeRow> rows = recipeRepository.findRowsByIceCreamId(iceCream.getIce_cream_id());

            if (rows.isEmpty()) {
                alert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Kem này chưa có định mức nguyên liệu.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Kem: ").append(iceCream.getIce_cream_name()).append("\n");
            sb.append("Sản lượng cần sản xuất: ").append(df.format(outputKg)).append(" kg\n");
            sb.append("--------------------------------------------------\n");

            for (RecipeRow row : rows) {
                double total = row.getQuantity_per_kg() * outputKg;
                sb.append(row.getIngredient_name())
                  .append(" = ")
                  .append(df.format(total))
                  .append(" ")
                  .append(row.getUnit_name())
                  .append("  [")
                  .append(df.format(row.getQuantity_per_kg()))
                  .append(" / kg]")
                  .append("\n");
            }

            txtCalcResult.setText(sb.toString());
        } catch (Exception ex) {
            alert(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
        }
    }

    @FXML
    private void refreshAll() {
        loadCombos();
        refreshAllTables();
        clearCalcResult();
    }

    private void selectIceCream(int id) {
        for (IceCream x : tblIceCream.getItems()) {
            if (x.getIce_cream_id() == id) {
                tblIceCream.getSelectionModel().select(x);
                return;
            }
        }
    }

    private void selectIngredient(int id) {
        for (IngredientRow x : tblIngredient.getItems()) {
            if (x.getIngredient_id() == id) {
                tblIngredient.getSelectionModel().select(x);
                return;
            }
        }
    }

    private void selectRecipe(int id) {
        for (RecipeRow x : tblRecipe.getItems()) {
            if (x.getRecipe_id() == id) {
                tblRecipe.getSelectionModel().select(x);
                return;
            }
        }
    }

    private double parseDouble(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số hợp lệ.");
        }
    }

    private void alert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}