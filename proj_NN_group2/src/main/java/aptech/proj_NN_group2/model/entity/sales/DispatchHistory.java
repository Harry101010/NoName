package aptech.proj_NN_group2.model.entity.sales;

import java.time.LocalDateTime;

public class DispatchHistory {
    private int historyId;
    private int orderId; // Traceability (16, 17...)
    private String productName;
    private double quantity;
    private LocalDateTime mfgDate;
    private String customerName;
    private String qualityStatus;
    private String notes;
    private LocalDateTime dispatchDate;

    // Getters và Setters (Quan trọng để TableView hiển thị)
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public LocalDateTime getMfgDate() { return mfgDate; }
    public void setMfgDate(LocalDateTime mfgDate) { this.mfgDate = mfgDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getQualityStatus() { return qualityStatus; }
    public void setQualityStatus(String qualityStatus) { this.qualityStatus = qualityStatus; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getDispatchDate() { return dispatchDate; }
    public void setDispatchDate(LocalDateTime dispatchDate) { this.dispatchDate = dispatchDate; }
}