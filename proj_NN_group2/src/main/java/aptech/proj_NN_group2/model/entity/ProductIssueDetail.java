package aptech.proj_NN_group2.model.entity;

public class ProductIssueDetail {
    private int iceCreamId;
    private String productName;
    private double quantity;

    // 1. Constructor mặc định (Rất quan trọng cho một số thư viện và xử lý danh sách)
    public ProductIssueDetail() {}

    // 2. Constructor đầy đủ tham số
    public ProductIssueDetail(int iceCreamId, String productName, double quantity) {
        this.iceCreamId = iceCreamId;
        this.productName = productName;
        this.quantity = quantity;
    }

    // --- Getters ---
    public int getIceCreamId() { return iceCreamId; }
    public String getProductName() { return productName; }
    public double getQuantity() { return quantity; }

    // --- Setters ---
    public void setIceCreamId(int iceCreamId) { this.iceCreamId = iceCreamId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
}