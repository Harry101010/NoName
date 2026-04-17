package aptech.proj_NN_group2.model.entity;

public class IngredientLot {
    private int lotId;
    private int ingredientId;
    private String ingredientName;
    private String unitName;
    private double remainingQuantity;
    private double receivedQuantity;
    private String importDate;
    private String expiryDate;
    private String supplierName;
    public int getLotId() { return lotId; }
    public void setLotId(int lotId) { this.lotId = lotId; }

    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public double getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(double remainingQuantity) { this.remainingQuantity = remainingQuantity; }

    public double getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(double receivedQuantity) { this.receivedQuantity = receivedQuantity; }

    public String getImportDate() { return importDate; }
    public void setImportDate(String importDate) { this.importDate = importDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    public String getStatus() {
        if (expiryDate == null || expiryDate.isEmpty()) return "Không rõ";

        try {
            if (java.time.LocalDate.parse(expiryDate)
                    .isBefore(java.time.LocalDate.now())) {
                return "Hết hạn";
            }
        } catch (Exception e) {
            return "Sai định dạng";
        }

        return "Còn hạn";
    }
}