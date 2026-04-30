package aptech.proj_NN_group2.model.entity.production_stage;

import java.math.BigDecimal;

public class ProductionStageTemplate {
    private int stageId;
    private String stageName;
    private int sequenceOrder;
  
    private boolean isProportional;
    private int planned_duration_seconds;


    // Constructor mặc định
    public ProductionStageTemplate() {}

    // Constructor đầy đủ
    public ProductionStageTemplate(int stageId, String stageName, int sequenceOrder, BigDecimal standardTimeMinutes, boolean isProportional) {
        this.stageId = stageId;
        this.stageName = stageName;
        this.sequenceOrder = sequenceOrder;
        
        this.isProportional = isProportional;
        this.planned_duration_seconds = planned_duration_seconds;
    }

    // Các Getter và Setter
    public int getStageId() { return stageId; }
    public void setStageId(int stageId) { this.stageId = stageId; }

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public int getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(int sequenceOrder) { this.sequenceOrder = sequenceOrder; }

    public boolean isProportional() { return isProportional; }
    public void setProportional(boolean proportional) { isProportional = proportional; }
    public int getPlanned_duration_seconds() { return planned_duration_seconds; }
    public void setPlanned_duration_seconds(int v) { this.planned_duration_seconds = v; }
}