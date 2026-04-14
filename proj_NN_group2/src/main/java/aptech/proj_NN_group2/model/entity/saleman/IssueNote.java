package aptech.proj_NN_group2.model.entity.saleman;

import java.time.LocalDateTime;

public class IssueNote {
    private int noteId;
    private String customerOrderCode; // Mã đơn khách
    private String salemanName;        // Tên nhân viên
    private String customerName;       // Tên khách hàng
    private double totalQuantity;      // Tổng số lượng
    private LocalDateTime createDate;   // Ngày lập phiếu
    private LocalDateTime deliveryDate; // Ngày phải giao
    private String status;
    // Thêm vào class IssueNote.java
    private String productName;
    private Double quantity;

    // --- Getter và Setter (Đây là phần giúp hết "đèn đỏ") ---

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
    


    // Nhớ tạo Getter và Setter cho 2 trường này (quan trọng để TableView nhận diện)
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
}