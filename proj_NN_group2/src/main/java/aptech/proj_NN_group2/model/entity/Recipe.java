package aptech.proj_NN_group2.model.entity;

public class Recipe {
    private int recipe_id;
    private int ice_cream_id;
    private int ingredient_id;
    private String ingredient_name;
    private double quantity_per_kg; 

    public Recipe() {}

    public Recipe(int recipe_id, int ice_cream_id, int ingredient_id, double quantity_per_kg) {
        this.recipe_id = recipe_id;
        this.ice_cream_id = ice_cream_id;
        this.ingredient_id = ingredient_id;
        this.quantity_per_kg = quantity_per_kg;
    }

    // --- Getters và Setters ---
    public int getRecipe_id() { return recipe_id; }
    public void setRecipe_id(int recipe_id) { this.recipe_id = recipe_id; }

    public int getIce_cream_id() { return ice_cream_id; }
    public void setIce_cream_id(int ice_cream_id) { this.ice_cream_id = ice_cream_id; }

    public int getIngredient_id() { return ingredient_id; }
    public void setIngredient_id(int ingredient_id) { this.ingredient_id = ingredient_id; }

    public double getQuantity_per_kg() { return quantity_per_kg; }
    public void setQuantity_per_kg(double quantity_per_kg) { this.quantity_per_kg = quantity_per_kg; }

    // --- Dành cho logic tính toán (BOM) ---
    public String getIngredientName() { return ingredient_name; }
    public void setIngredientName(String name) { this.ingredient_name = name; }

    public double getQuantityPerUnit() { return this.quantity_per_kg; }

    @Override
    public String toString() {
        return "Recipe{id=" + recipe_id + ", name='" + ingredient_name + "', qty=" + quantity_per_kg + "}";
    }
}