package aptech.proj_NN_group2.model.entity.production_stage;

import java.sql.Timestamp;

public class ProductionTracking {
    private int tracking_id;
    private int order_id;
    private int stage_id;
    private String stage_name; 
    private String status; 
    private Timestamp start_time;
    private Timestamp end_time;
    private int actual_time_minutes;
    private float actual_capacity;
    private String note;
    // --- THÊM DÒNG NÀY VÀO LÀ HẾT LỖI ---
    private float actual_quantity; 

    public ProductionTracking() {}
    
    // Getters and Setters
    public int getTracking_id() { return tracking_id; }
    public void setTracking_id(int tracking_id) { this.tracking_id = tracking_id; }

    public int getOrder_id() { return order_id; }
    public void setOrder_id(int order_id) { this.order_id = order_id; }

    public int getStage_id() { return stage_id; }
    public void setStage_id(int stage_id) { this.stage_id = stage_id; }

    public String getStage_name() { return stage_name; }
    public void setStage_name(String stage_name) { this.stage_name = stage_name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getStart_time() { return start_time; }
    public void setStart_time(Timestamp start_time) { this.start_time = start_time; }

    public Timestamp getEnd_time() { return end_time; }
    public void setEnd_time(Timestamp end_time) { this.end_time = end_time; }

    public int getActual_time_minutes() { return actual_time_minutes; }
    public void setActual_time_minutes(int actual_time_minutes) { this.actual_time_minutes = actual_time_minutes; }

    public float getActual_capacity() { return actual_capacity; }
    public void setActual_capacity(float actual_capacity) { this.actual_capacity = actual_capacity; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    // Getter và Setter cho actual_quantity (đã có biến khai báo ở trên nên giờ sẽ hết báo đỏ)
    public void setActual_quantity(float actual_quantity) {
        this.actual_quantity = actual_quantity;
    }

    public float getActual_quantity() {
        return actual_quantity;
    }
}