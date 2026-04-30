package aptech.proj_NN_group2.model.entity;

import aptech.proj_NN_group2.model.entity.ingredient.Ingredient;

public class IngredientRow {
    private int ingredient_id;
    private String ingredient_name;
    private String origin;
    private String storage_condition;
    private int unit_id;
    private String unit_name;
    private double price_per_unit;
    private boolean is_active;

    // 1. Constructor rỗng (cần thiết cho một số thao tác JavaFX)
    public IngredientRow() {
    }

    // 2. Constructor nhận vào đối tượng Ingredient để map dữ liệu (Đây là cái bạn cần!)
    public IngredientRow(Ingredient ing) {
        this.ingredient_id = ing.getIngredient_id();
        this.ingredient_name = ing.getIngredient_name();
        this.origin = ing.getOrigin();
        this.storage_condition = ing.getStorage_condition();
        this.unit_id = ing.getUnit_id();
        this.unit_name = ing.getUnit_name();
        this.price_per_unit = ing.getPrice_per_unit();
        this.is_active = ing.getIs_active();
    }

    // --- Các Getter và Setter ---

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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getStorage_condition() {
        return storage_condition;
    }

    public void setStorage_condition(String storage_condition) {
        this.storage_condition = storage_condition;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public double getPrice_per_unit() {
        return price_per_unit;
    }

    public void setPrice_per_unit(double price_per_unit) {
        this.price_per_unit = price_per_unit;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}