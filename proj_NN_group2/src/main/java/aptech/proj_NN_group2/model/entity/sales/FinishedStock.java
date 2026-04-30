package aptech.proj_NN_group2.model.entity.sales;

public class FinishedStock {
    private int orderId;
    private String productName;
    private double quantity;
    private String mfgDate;
    private String expDate;
    private String location;

    // Getters và Setters chuẩn hóa
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getMfgDate() { return mfgDate; }
    public void setMfgDate(String mfgDate) { this.mfgDate = mfgDate; }

    public String getExpDate() { return expDate; }
    public void setExpDate(String expDate) { this.expDate = expDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}