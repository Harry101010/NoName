package aptech.proj_NN_group2.model.entity.ingredient;

public class MaterialRequirement {
    private String ingredientName;
    private double requiredQuantity;
    private double currentStock;
    private String status;

    public MaterialRequirement(String name, double req, double stock, String status) {
        this.ingredientName = name;
        this.requiredQuantity = req;
        this.currentStock = stock;
        this.status = status;
    }

    // Getters
    public String getIngredientName() { return ingredientName; }
    public double getRequiredQuantity() { return requiredQuantity; }
    public double getCurrentStock() { return currentStock; }
    public String getStatus() { return status; }
}