package aptech.proj_NN_group2.model.entity.saleman;

public class ProductIssueDetail {
    private int iceCreamId;
    private String productName;
    private double quantity;

    public ProductIssueDetail(int iceCreamId, String productName, double quantity) {
        this.iceCreamId = iceCreamId;
        this.productName = productName;
        this.quantity = quantity;
    }

    // Getters
    public int getIceCreamId() { return iceCreamId; }
    public String getProductName() { return productName; }
    public double getQuantity() { return quantity; }

    // Setter quan trọng để phục vụ chức năng Sửa
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}