package aptech.proj_NN_group2.model.entity;

import java.sql.Timestamp;

public class IngredientExportRequest {
    private int ingredient_export_request_id;
    private int production_order_id;
    private Integer requested_by;
    private Timestamp requested_at;
    private String request_status;
    private String note;

    // extra fields for display
    private String ice_cream_name;
    private double planned_output_kg;
    private String order_status;

    public IngredientExportRequest() {}

    public int getIngredient_export_request_id() { return ingredient_export_request_id; }
    public void setIngredient_export_request_id(int v) { this.ingredient_export_request_id = v; }

    public int getProduction_order_id() { return production_order_id; }
    public void setProduction_order_id(int v) { this.production_order_id = v; }

    public Integer getRequested_by() { return requested_by; }
    public void setRequested_by(Integer v) { this.requested_by = v; }

    public Timestamp getRequested_at() { return requested_at; }
    public void setRequested_at(Timestamp v) { this.requested_at = v; }

    public String getRequest_status() { return request_status; }
    public void setRequest_status(String v) { this.request_status = v; }

    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }

    public String getIce_cream_name() { return ice_cream_name; }
    public void setIce_cream_name(String v) { this.ice_cream_name = v; }

    public double getPlanned_output_kg() { return planned_output_kg; }
    public void setPlanned_output_kg(double v) { this.planned_output_kg = v; }

    public String getOrder_status() { return order_status; }
    public void setOrder_status(String v) { this.order_status = v; }

    @Override
    public String toString() {
        return "ID " + production_order_id + " - " + ice_cream_name + " (" + planned_output_kg + " kg)";
    }
}

