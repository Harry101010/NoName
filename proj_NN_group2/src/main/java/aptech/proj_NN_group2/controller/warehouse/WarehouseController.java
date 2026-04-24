package aptech.proj_NN_group2.controller.warehouse;

import aptech.proj_NN_group2.model.business.repository.WarehouseRepository;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import aptech.proj_NN_group2.model.entity.InventorySummary;
import aptech.proj_NN_group2.model.entity.IngredientLot;
import aptech.proj_NN_group2.util.NavigationUtil;
import aptech.proj_NN_group2.util.StringValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class WarehouseController implements Initializable {

    // ===== SUMMARY TABLE =====
    @FXML private TableView<InventorySummary> tblSummary;
    @FXML private TableColumn<InventorySummary, String> colName;
    @FXML private TableColumn<InventorySummary, String> colUnit;
    @FXML private TableColumn<InventorySummary, Double> colTotal;
    @FXML private TableColumn<InventorySummary, LocalDate> colExpiry;
    @FXML private TableColumn<InventorySummary, String> colSummaryStatus;
    @FXML private TableColumn<InventorySummary, String> colStorage;

    // ===== LOT TABLE =====
    @FXML private TableView<IngredientLot> tblLots;
    @FXML private TableColumn<IngredientLot, Integer> colLotId;
    @FXML private TableColumn<IngredientLot, String> colLotName;
    @FXML private TableColumn<IngredientLot, String> colSupplier;
    @FXML private TableColumn<IngredientLot, LocalDate> colImport;
    @FXML private TableColumn<IngredientLot, LocalDate> colLotExpiry;
    @FXML private TableColumn<IngredientLot, Double> colRemain;
    @FXML private TableColumn<IngredientLot, String> colStatus;
    @FXML private TableColumn<IngredientLot, String> colLotStorage;

    // ===== INPUT =====
    @FXML private TextField txtMaterialName;
    @FXML private TextField txtQuantity;
    @FXML private DatePicker dpExpiryDate;
    @FXML private TextField txtSupplierName;
    @FXML private ComboBox<String> cbUnit;
    @FXML private TextField txtSearch;

    private final WarehouseRepository repo = new WarehouseRepository();

    // ===== FILTER DATA =====
    private FilteredList<IngredientLot> filteredLots;
    private FilteredList<InventorySummary> filteredSummary;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cbUnit.setItems(FXCollections.observableArrayList(repo.getAllUnits()));

        // ===== SUMMARY TABLE =====
        colName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("nearestExpiry"));
        colStorage.setCellValueFactory(new PropertyValueFactory<>("storageCondition"));

        colSummaryStatus.setCellValueFactory(cell -> {
            InventorySummary item = cell.getValue();
            return new SimpleStringProperty(item != null ? item.getStatus() : "");
        });

        colSummaryStatus.setCellFactory(column -> createStatusCell());

        // ===== LOT TABLE =====
        colLotId.setCellValueFactory(new PropertyValueFactory<>("lotId"));
        colLotName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colImport.setCellValueFactory(new PropertyValueFactory<>("importDate"));
        colLotExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colRemain.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));
        colLotStorage.setCellValueFactory(new PropertyValueFactory<>("storageCondition"));

        colStatus.setCellValueFactory(cell -> {
            IngredientLot item = cell.getValue();
            return new SimpleStringProperty(item != null ? item.getStatus() : "");
        });

        colStatus.setCellFactory(column -> createStatusCell());

        // ===== LOAD DATA WITH FILTER =====
        filteredLots = new FilteredList<>(FXCollections.observableArrayList(repo.getLots()), b -> true);
        SortedList<IngredientLot> sortedLots = new SortedList<>(filteredLots);
        sortedLots.comparatorProperty().bind(tblLots.comparatorProperty());
        tblLots.setItems(sortedLots);

        filteredSummary = new FilteredList<>(FXCollections.observableArrayList(repo.getSummary()), b -> true);
        SortedList<InventorySummary> sortedSummary = new SortedList<>(filteredSummary);
        sortedSummary.comparatorProperty().bind(tblSummary.comparatorProperty());
        tblSummary.setItems(sortedSummary);

        // ===== SEARCH =====
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {

            String keyword = newValue == null ? "" : newValue.toLowerCase();

            filteredLots.setPredicate(lot -> {
                if (keyword.isBlank()) return true;

                return lot.getIngredientName().toLowerCase().contains(keyword)
                        || (lot.getSupplierName() != null &&
                            lot.getSupplierName().toLowerCase().contains(keyword))
                        || lot.getStatus().toLowerCase().contains(keyword);
            });

            filteredSummary.setPredicate(item -> {
                if (keyword.isBlank()) return true;

                return item.getIngredientName().toLowerCase().contains(keyword)
                        || item.getUnitName().toLowerCase().contains(keyword)
                        || item.getStatus().toLowerCase().contains(keyword);
            });
        });

        // ===== CLICK ROW → LOAD FORM =====
        tblLots.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, selected) -> {
            if (selected != null) {
                txtMaterialName.setText(selected.getIngredientName());
                txtQuantity.setText(String.valueOf(selected.getRemainingQuantity()));
                txtSupplierName.setText(selected.getSupplierName() != null ? selected.getSupplierName() : "");
                dpExpiryDate.setValue(selected.getExpiryDate());

                String unitName = selected.getUnitName();
                if (unitName != null && !unitName.isBlank()) {
                    cbUnit.setValue(unitName);
                } else {
                    cbUnit.getSelectionModel().clearSelection();
                }
            }
        });
    }

    // ===== STATUS UI =====
    private <T> TableCell<T, String> createStatusCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null || status.isBlank()) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label dot = new Label();
                dot.setMinSize(10, 10);
                dot.setMaxSize(10, 10);

                Label text = new Label(status);
                HBox box = new HBox(8, dot, text);

                String lower = status.toLowerCase();

                if (lower.contains("hết hạn") || lower.contains("expired")) {
                    dot.setStyle("-fx-background-color: #ff4d4f; -fx-background-radius: 50%;");
                } 
                else if (lower.contains("sắp") || lower.contains("soon")) {
                    dot.setStyle("-fx-background-color: #faad14; -fx-background-radius: 50%;");
                } 
                else if (lower.contains("không rõ") || lower.contains("unknown")) {
                    dot.setStyle("-fx-background-color: gray; -fx-background-radius: 50%;");
                }
                else {
                    dot.setStyle("-fx-background-color: #52c41a; -fx-background-radius: 50%;");
                }
                setGraphic(box);
                setText(null);
            }
        };
    }

    // ===== ACTIONS =====

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            String ingredientName = txtMaterialName.getText().trim();
            String quantityText = txtQuantity.getText().trim();
            String supplierName = txtSupplierName.getText().trim();
            String selectedUnit = cbUnit.getValue();

            if (ingredientName.isEmpty()) {
                showWarning("Please enter ingredient name");
                return;
            }

            if (selectedUnit == null || selectedUnit.isBlank()) {
                showWarning("Please select unit");
                return;
            }

            if (quantityText.isEmpty()) {
                showWarning("Please enter quantity");
                return;
            }

            double quantity = Double.parseDouble(quantityText);

            int ingredientId = repo.findIngredientIdByName(ingredientName);

            if (ingredientId == -1) {
                int unitId = repo.findUnitIdByName(selectedUnit);
                ingredientId = repo.createIngredient(ingredientName, unitId);
            }

            LocalDate expiry = dpExpiryDate.getValue();
            if (expiry == null) expiry = LocalDate.now().plusYears(1);

            repo.importStock(ingredientId, quantity, expiry.toString(), supplierName);

            handleRefresh(null);
            new Alert(Alert.AlertType.INFORMATION, "Added successfully").showAndWait();

        } catch (Exception e) {
            showError("Invalid data");
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        IngredientLot selected = tblLots.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a lot to update");
            return;
        }

        try {
            double quantity = Double.parseDouble(txtQuantity.getText());
            int ingredientId = repo.findIngredientIdByName(txtMaterialName.getText());

            LocalDate expiry = dpExpiryDate.getValue();
            if (expiry == null) expiry = selected.getExpiryDate();

            repo.deleteLot(selected.getLotId());
            repo.importStock(ingredientId, quantity, expiry.toString(), txtSupplierName.getText());

            handleRefresh(null);
            new Alert(Alert.AlertType.INFORMATION, "Updated successfully").showAndWait();

        } catch (Exception e) {
            showError("Update failed");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        IngredientLot selected = tblLots.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a lot to delete");
            return;
        }

        repo.deleteLot(selected.getLotId());
        handleRefresh(null);
        new Alert(Alert.AlertType.INFORMATION, "Deleted successfully").showAndWait();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        initialize(null, null);
    }

    @FXML
    private void handleViewExportRequests(ActionEvent event) {
        NavigationUtil.goTo(event, StringValue.VIEW_EXPORT_REQUESTS, "Export Requests");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }
    
    @FXML
    private void goToProfile(ActionEvent event) {
    	NavigationUtil.toAccountProfile(event);
    }

    private void showWarning(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
    @FXML
    private void handleExport(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Excel File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialFileName("inventory.xlsx");

            File file = fileChooser.showSaveDialog(null);
            if (file == null) return;

            Workbook workbook = new XSSFWorkbook();

            // ===== SHEET 1: SUMMARY =====
            Sheet summarySheet = workbook.createSheet("Summary");

            Row header1 = summarySheet.createRow(0);
            String[] summaryHeaders = {
                    "Ingredient", "Unit", "Total Stock",
                    "Nearest Expiry", "Storage", "Status"
            };

            for (int i = 0; i < summaryHeaders.length; i++) {
                header1.createCell(i).setCellValue(summaryHeaders[i]);
            }

            int rowIndex = 1;
            for (InventorySummary item : tblSummary.getItems()) {
                Row row = summarySheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(item.getIngredientName());
                row.createCell(1).setCellValue(item.getUnitName());
                row.createCell(2).setCellValue(item.getTotalStock());
                row.createCell(3).setCellValue(
                        item.getNearestExpiry() != null ? item.getNearestExpiry().toString() : ""
                );
                row.createCell(4).setCellValue(item.getStorageCondition());
                row.createCell(5).setCellValue(item.getStatus());
            }

            // ===== SHEET 2: LOTS =====
            Sheet lotSheet = workbook.createSheet("Lots");

            Row header2 = lotSheet.createRow(0);
            String[] lotHeaders = {
                    "Lot ID", "Ingredient", "Supplier",
                    "Import Date", "Expiry Date",
                    "Remaining", "Storage", "Status"
            };

            for (int i = 0; i < lotHeaders.length; i++) {
                header2.createCell(i).setCellValue(lotHeaders[i]);
            }

            rowIndex = 1;
            for (IngredientLot lot : tblLots.getItems()) {
                Row row = lotSheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(lot.getLotId());
                row.createCell(1).setCellValue(lot.getIngredientName());
                row.createCell(2).setCellValue(lot.getSupplierName());

                row.createCell(3).setCellValue(
                        lot.getImportDate() != null ? lot.getImportDate().toString() : ""
                );
                row.createCell(4).setCellValue(
                        lot.getExpiryDate() != null ? lot.getExpiryDate().toString() : ""
                );

                row.createCell(5).setCellValue(lot.getRemainingQuantity());
                row.createCell(6).setCellValue(lot.getStorageCondition());
                row.createCell(7).setCellValue(lot.getStatus());
            }

            // ===== AUTO SIZE =====
            for (int i = 0; i < summaryHeaders.length; i++) {
                summarySheet.autoSizeColumn(i);
            }
            for (int i = 0; i < lotHeaders.length; i++) {
                lotSheet.autoSizeColumn(i);
            }

            // ===== SAVE FILE =====
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();

            new Alert(Alert.AlertType.INFORMATION, "Export successful!").showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Export failed!").showAndWait();
        }
    }
}