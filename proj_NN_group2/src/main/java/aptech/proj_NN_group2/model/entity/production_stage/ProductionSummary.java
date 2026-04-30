package aptech.proj_NN_group2.model.entity.production_stage;

public class ProductionSummary {
    private int orderId;
    private String productName;
    private int totalStages;
    private int completedStages;
    private double progressPercent;
    private String currentStage; // Đã thêm
    private String failedStage;  // Đã thêm

    public ProductionSummary() {}

    // Cập nhật Constructor đầy đủ (tùy chọn dùng hoặc không, nhưng setter là quan trọng nhất)
    public ProductionSummary(int orderId, String productName, int totalStages, int completedStages, double progressPercent, String currentStage, String failedStage) {
        this.orderId = orderId;
        this.productName = productName;
        this.totalStages = totalStages;
        this.completedStages = completedStages;
        this.progressPercent = progressPercent;
        this.currentStage = currentStage;
        this.failedStage = failedStage;
    }

    // GETTERS & SETTERS (Đảm bảo có đủ tất cả)
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public double getProgressPercent() { return progressPercent; }
    public void setProgressPercent(double progressPercent) { this.progressPercent = progressPercent; }

    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }

    public String getFailedStage() { return failedStage; }
    public void setFailedStage(String failedStage) { this.failedStage = failedStage; }
}