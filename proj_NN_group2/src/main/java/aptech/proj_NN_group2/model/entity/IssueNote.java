package aptech.proj_NN_group2.model.entity;

import java.time.LocalDateTime;

public class IssueNote {
    // Các trường của người cũ
    private int noteId;
    private String customerOrderCode; 
    private String salemanName;        
    private String customerName;       
    private double totalQuantity;      
    private LocalDateTime createDate;   
    private LocalDateTime deliveryDate; 
    private String status;

    // Các trường của bạn (Thêm vào để phục vụ Tab 2, 3)
    private String productName;
    private double quantity;
    private LocalDateTime requestDate; // Tương đương với createDate nhưng để đồng bộ code của bạn

    public IssueNote() {}

    // --- Getter và Setter đầy đủ ---

    public int getNoteId() { return noteId; }
    public void setNoteId(int noteId) { this.noteId = noteId; }

    public String getCustomerOrderCode() { return customerOrderCode; }
    public void setCustomerOrderCode(String customerOrderCode) { this.customerOrderCode = customerOrderCode; }

    public String getSalemanName() { return salemanName; }
    public void setSalemanName(String salemanName) { this.salemanName = salemanName; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(double totalQuantity) { this.totalQuantity = totalQuantity; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public LocalDateTime getRequestDate() { 
        return requestDate != null ? requestDate : createDate; 
    }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
}