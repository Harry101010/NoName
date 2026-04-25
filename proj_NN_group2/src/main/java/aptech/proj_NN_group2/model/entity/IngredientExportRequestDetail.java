package aptech.proj_NN_group2.model.entity;

import java.math.BigDecimal;

public class IngredientExportRequestDetail {
    private int ingredient_export_request_detail_id;
    private int ingredient_export_request_id;
    private int ingredient_id;
    private BigDecimal required_quantity;

    // extra fields for display
    private String ingredient_name;
    private String unit_name;

    public IngredientExportRequestDetail() {}

    public int getIngredient_export_request_detail_id() { return ingredient_export_request_detail_id; }
    public void setIngredient_export_request_detail_id(int v) { this.ingredient_export_request_detail_id = v; }

    public int getIngredient_export_request_id() { return ingredient_export_request_id; }
    public void setIngredient_export_request_id(int v) { this.ingredient_export_request_id = v; }

    public int getIngredient_id() { return ingredient_id; }
    public void setIngredient_id(int v) { this.ingredient_id = v; }

    public BigDecimal getRequired_quantity() { return required_quantity; }
    public void setRequired_quantity(BigDecimal v) { this.required_quantity = v; }

    public String getIngredient_name() { return ingredient_name; }
    public void setIngredient_name(String v) { this.ingredient_name = v; }

    public String getUnit_name() { return unit_name; }
    public void setUnit_name(String v) { this.unit_name = v; }
}
