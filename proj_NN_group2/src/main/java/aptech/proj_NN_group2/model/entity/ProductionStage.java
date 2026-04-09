package aptech.proj_NN_group2.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ProductionStage {
    private int production_stage_id;
    private int production_order_id;
    private int stage_no;
    private String stage_name;
    private Integer planned_duration_min;
    private Integer actual_duration_min;
    private BigDecimal actual_volume;
    private Integer mold_count;
    private Timestamp start_time;
    private Timestamp end_time;
    private String stage_status;
    private Integer recorded_by;
    private String note;

    public ProductionStage() {}

    public int getProduction_stage_id() { return production_stage_id; }
    public void setProduction_stage_id(int v) { this.production_stage_id = v; }

    public int getProduction_order_id() { return production_order_id; }
    public void setProduction_order_id(int v) { this.production_order_id = v; }

    public int getStage_no() { return stage_no; }
    public void setStage_no(int v) { this.stage_no = v; }

    public String getStage_name() { return stage_name; }
    public void setStage_name(String v) { this.stage_name = v; }

    public Integer getPlanned_duration_min() { return planned_duration_min; }
    public void setPlanned_duration_min(Integer v) { this.planned_duration_min = v; }

    public Integer getActual_duration_min() { return actual_duration_min; }
    public void setActual_duration_min(Integer v) { this.actual_duration_min = v; }

    public BigDecimal getActual_volume() { return actual_volume; }
    public void setActual_volume(BigDecimal v) { this.actual_volume = v; }

    public Integer getMold_count() { return mold_count; }
    public void setMold_count(Integer v) { this.mold_count = v; }

    public Timestamp getStart_time() { return start_time; }
    public void setStart_time(Timestamp v) { this.start_time = v; }

    public Timestamp getEnd_time() { return end_time; }
    public void setEnd_time(Timestamp v) { this.end_time = v; }

    public String getStage_status() { return stage_status; }
    public void setStage_status(String v) { this.stage_status = v; }

    public Integer getRecorded_by() { return recorded_by; }
    public void setRecorded_by(Integer v) { this.recorded_by = v; }

    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
}
