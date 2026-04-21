package aptech.proj_NN_group2.model.entity;

import java.time.LocalDate;

public class IngredientLot {

    private int lotId;
    private int ingredientId;
    private String ingredientName;
    private String unitName;
    private double remainingQuantity;
    private double receivedQuantity;
    private LocalDate importDate;
    private LocalDate expiryDate;
    private String storageCondition;
    private String supplierName;
    
    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }
    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public double getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(double remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public double getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(double receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDate importDate) {
        this.importDate = importDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getStatus() {
        if (expiryDate == null) {
            return "Không rõ";
        }

        LocalDate today = LocalDate.now();

        if (expiryDate.isBefore(today)) {
            return "Hết hạn";
        } else if (!expiryDate.isAfter(today.plusDays(7))) {
            return "Sắp hết hạn";
        } else {
            return "Còn hạn";
        }
    }
}