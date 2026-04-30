package aptech.proj_NN_group2.model.entity;

import java.time.LocalDate;

public class InventorySummary {

    private String ingredientName;
    private String unitName;
    private double totalStock;
    private LocalDate nearestExpiry;
    private String storageCondition;
    public String getStatus() {
        if (nearestExpiry == null) {
            return "Không rõ";
        }

        LocalDate today = LocalDate.now();

        if (nearestExpiry.isBefore(today)) {
            return "Hết hạn";
        } else if (!nearestExpiry.isAfter(today.plusDays(7))) {
            return "Sắp hết hạn";
        } else {
            return "Còn hạn";
        }
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

    public double getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(double totalStock) {
        this.totalStock = totalStock;
    }

    public LocalDate getNearestExpiry() {
        return nearestExpiry;
    }
    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public void setNearestExpiry(LocalDate nearestExpiry) {
        this.nearestExpiry = nearestExpiry;
    }
}