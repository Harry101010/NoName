package aptech.proj_NN_group2.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Chi tiết ghi nhận cho từng công đoạn sản xuất.
 * Công đoạn 1 (Material Preparation & Mixing) có thêm các trường đặc thù:
 * mixing_temperature_c, mixing_ratio_note.
 */
public class ProductionStageDetail {
    private int production_stage_id;
    private int production_order_id;
    private int stage_no;
    private String stage_name;
    private String stage_status;

    // Thông tin ghi nhận chung (tất cả công đoạn)
    private Integer actual_duration_min;
    private BigDecimal actual_volume;
    private Integer mold_count;
    private Timestamp start_time;
    private Timestamp end_time;
    private String note;

    // Trường đặc thù công đoạn 1: Material Preparation & Mixing
    private BigDecimal mixing_temperature_c;   // Nhiệt độ trộn (°C)
    private String mixing_ratio_note;          // Ghi chú tỉ lệ trộn thực tế

    public ProductionStageDetail() {}

    // --- Getters & Setters ---

    public int getProduction_stage_id() { return production_stage_id; }
    public void setProduction_stage_id(int v) { this.production_stage_id = v; }

    public int getProduction_order_id() { return production_order_id; }
    public void setProduction_order_id(int v) { this.production_order_id = v; }

    public int getStage_no() { return stage_no; }
    public void setStage_no(int v) { this.stage_no = v; }

    public String getStage_name() { return stage_name; }
    public void setStage_name(String v) { this.stage_name = v; }

    public String getStage_status() { return stage_status; }
    public void setStage_status(String v) { this.stage_status = v; }

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

    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }

    public BigDecimal getMixing_temperature_c() { return mixing_temperature_c; }
    public void setMixing_temperature_c(BigDecimal v) { this.mixing_temperature_c = v; }

    public String getMixing_ratio_note() { return mixing_ratio_note; }
    public void setMixing_ratio_note(String v) { this.mixing_ratio_note = v; }

    /** Kiểm tra có phải công đoạn Material Preparation & Mixing không */
    public boolean isMixingStage() { return stage_no == 1; }
}