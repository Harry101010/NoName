package aptech.proj_NN_group2.model.entity.production_stage;

import java.sql.Timestamp;

public class ProductionPerformance {
    private String stageName;
    private float actualQuantity;
    private Timestamp startTime;
    private Timestamp endTime;
    private int durationMinutes;

    public ProductionPerformance() {}

    // Getters và Setters
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public float getActualQuantity() { return actualQuantity; }
    public void setActualQuantity(float actualQuantity) { this.actualQuantity = actualQuantity; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}