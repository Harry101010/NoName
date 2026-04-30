package aptech.proj_NN_group2.model.entity.ingredient;

import java.util.Objects;

public class Ingredient {
    private int ingredient_id;
    private String ingredient_name;
    private String origin;
    private String storage_condition;
    private int unit_id;
    private String unit_name; // Trường mới thêm
    private double price_per_unit;
    private boolean is_active;

    public Ingredient() {
    }

    // Constructor cũ vẫn giữ nguyên để không làm lỗi code ở các file khác
    public Ingredient(int ingredient_id, String ingredient_name, String origin, String storage_condition,
                      int unit_id, double price_per_unit, boolean is_active) {
        this.ingredient_id = ingredient_id;
        this.ingredient_name = ingredient_name;
        this.origin = origin;
        this.storage_condition = storage_condition;
        this.unit_id = unit_id;
        this.price_per_unit = price_per_unit;
        this.is_active = is_active;
    }

    // --- Getter và Setter cho các trường cũ ---
    public int getIngredient_id() { return ingredient_id; }
    public void setIngredient_id(int ingredient_id) { this.ingredient_id = ingredient_id; }

    public String getIngredient_name() { return ingredient_name; }
    public void setIngredient_name(String ingredient_name) { this.ingredient_name = ingredient_name; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getStorage_condition() { return storage_condition; }
    public void setStorage_condition(String storage_condition) { this.storage_condition = storage_condition; }

    public int getUnit_id() { return unit_id; }
    public void setUnit_id(int unit_id) { this.unit_id = unit_id; }

    public double getPrice_per_unit() { return price_per_unit; }
    public void setPrice_per_unit(double price_per_unit) { this.price_per_unit = price_per_unit; }

    public boolean isIs_active() { return is_active; }
    public boolean getIs_active() { return is_active; }
    public void setIs_active(boolean is_active) { this.is_active = is_active; }

    // --- Getter và Setter cho unit_name (MỚI THÊM) ---
    public String getUnit_name() { return unit_name; }
    public void setUnit_name(String unit_name) { this.unit_name = unit_name; }

    @Override
    public String toString() {
        return ingredient_name == null ? "" : ingredient_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;
        Ingredient that = (Ingredient) o;
        return ingredient_id == that.ingredient_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient_id);
    }
    public int getIngredientId() {
        return this.ingredient_id;
    }
}