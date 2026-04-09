package aptech.proj_NN_group2.model.entity;

public class RecipeRow {
    private int recipe_id;
    private int ice_cream_id;
    private String ice_cream_name;
    private int ingredient_id;
    private String ingredient_name;
    private String unit_name;
    private double quantity_per_kg;

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public int getIce_cream_id() {
        return ice_cream_id;
    }

    public void setIce_cream_id(int ice_cream_id) {
        this.ice_cream_id = ice_cream_id;
    }

    public String getIce_cream_name() {
        return ice_cream_name;
    }

    public void setIce_cream_name(String ice_cream_name) {
        this.ice_cream_name = ice_cream_name;
    }

    public int getIngredient_id() {
        return ingredient_id;
    }

    public void setIngredient_id(int ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public String getIngredient_name() {
        return ingredient_name;
    }

    public void setIngredient_name(String ingredient_name) {
        this.ingredient_name = ingredient_name;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public double getQuantity_per_kg() {
        return quantity_per_kg;
    }

    public void setQuantity_per_kg(double quantity_per_kg) {
        this.quantity_per_kg = quantity_per_kg;
    }
}