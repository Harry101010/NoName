package aptech.proj_NN_group2.controller.production;

import aptech.proj_NN_group2.model.business.repository.IngredientRepository;
import aptech.proj_NN_group2.model.entity.ingredient.Ingredient;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AddIngredientDialogController {
    @FXML private TextField txtSearch, txtQuantity;
    @FXML private TableView<Ingredient> tblIngredients;
    @FXML private TableColumn<Ingredient, String> colCode, colName, colUnit;

    private Ingredient selectedIngredient;
    private double quantity;

    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("ingredient_id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("ingredient_name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit_name"));

        FilteredList<Ingredient> filteredData = new FilteredList<>(FXCollections.observableArrayList(new IngredientRepository().findAll()), p -> true);
        tblIngredients.setItems(filteredData);

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(i -> newVal == null || newVal.isEmpty() || 
                i.getIngredient_name().toLowerCase().contains(newVal.toLowerCase()) || 
                String.valueOf(i.getIngredient_id()).contains(newVal));
        });
    }

    @FXML private void handleConfirm() {
        selectedIngredient = tblIngredients.getSelectionModel().getSelectedItem();
        if (selectedIngredient == null || txtQuantity.getText().isEmpty()) return;
        try {
            quantity = Double.parseDouble(txtQuantity.getText());
            ((Stage) txtSearch.getScene().getWindow()).close();
        } catch (NumberFormatException e) { new Alert(Alert.AlertType.ERROR, "Số lượng phải là số!").show(); }
    }

    public Ingredient getSelectedIngredient() { return selectedIngredient; }
    public double getQuantity() { return quantity; }
}