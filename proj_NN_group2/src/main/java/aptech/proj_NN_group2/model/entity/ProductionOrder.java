package aptech.proj_NN_group2.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ProductionOrder {
    private int production_order_id;
    private int ice_cream_id;
    private BigDecimal planned_output_kg;
    private Integer created_by;
    private Timestamp created_at;
    private String order_status;
    private String note;

    // extra field for display
    private String ice_cream_name;

    public ProductionOrder() {}

    public int getProduction_order_id() { return production_order_id; }
    public void setProduction_order_id(int production_order_id) { this.production_order_id = production_order_id; }

    public int getIce_cream_id() { return ice_cream_id; }
    public void setIce_cream_id(int ice_cream_id) { this.ice_cream_id = ice_cream_id; }

    public BigDecimal getPlanned_output_kg() { return planned_output_kg; }
    public void setPlanned_output_kg(BigDecimal planned_output_kg) { this.planned_output_kg = planned_output_kg; }

    public Integer getCreated_by() { return created_by; }
    public void setCreated_by(Integer created_by) { this.created_by = created_by; }

    public Timestamp getCreated_at() { return created_at; }
    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }

    public String getOrder_status() { return order_status; }
    public void setOrder_status(String order_status) { this.order_status = order_status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getIce_cream_name() { return ice_cream_name; }
    public void setIce_cream_name(String ice_cream_name) { this.ice_cream_name = ice_cream_name; }
}
