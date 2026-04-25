
package aptech.proj_NN_group2.model.entity;

import java.sql.Timestamp;

public class IngredientExportReceipt {

    private int ingredient_export_receipt_id;
    private int ingredient_export_request_id;
    private Integer approved_by;
    private Timestamp created_at;
    private String receipt_status;
    private String note;

    // extra fields for display (JOIN)
    private String ice_cream_name;
    private double planned_output_kg;
    private String request_status;

    public IngredientExportReceipt() {}

    // =========================
    // DB FIELDS
    // =========================
    public int getIngredient_export_receipt_id() {
        return ingredient_export_receipt_id;
    }

    public void setIngredient_export_receipt_id(int v) {
        this.ingredient_export_receipt_id = v;
    }

    public int getIngredient_export_request_id() {
        return ingredient_export_request_id;
    }

    public void setIngredient_export_request_id(int v) {
        this.ingredient_export_request_id = v;
    }

    public Integer getApproved_by() {
        return approved_by;
    }

    public void setApproved_by(Integer v) {
        this.approved_by = v;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp v) {
        this.created_at = v;
    }

    public String getReceipt_status() {
        return receipt_status;
    }

    public void setReceipt_status(String v) {
        this.receipt_status = v;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String v) {
        this.note = v;
    }

    // =========================
    // DISPLAY FIELDS
    // =========================
    public String getIce_cream_name() {
        return ice_cream_name;
    }

    public void setIce_cream_name(String v) {
        this.ice_cream_name = v;
    }

    public double getPlanned_output_kg() {
        return planned_output_kg;
    }

    public void setPlanned_output_kg(double v) {
        this.planned_output_kg = v;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String v) {
        this.request_status = v;
    }

    // =========================
    // ALIAS (🔥 QUAN TRỌNG CHO UI)
    // =========================

    // dùng cho TableView: "id"
    public int getId() {
        return ingredient_export_receipt_id;
    }

    // dùng cho controller
    public int getRequestId() {
        return ingredient_export_request_id;
    }

    // dùng bind "status"
    public String getStatus() {
        return receipt_status;
    }

    // =========================
    // DEBUG / DISPLAY
    // =========================
    @Override
    public String toString() {
        return "XK#" + ingredient_export_receipt_id +
               " | " + ice_cream_name +
               " | " + receipt_status;
    }
}
