package aptech.proj_NN_group2.model.entity.saleman;
import java.time.LocalDateTime;

public class FinishedProductInventory {
    private int inventoryId;
    private String poCode;
    private String productName;
    private double quantity;
    private LocalDateTime mfgDate;
    private LocalDateTime expDate;
    private String location;

    // Constructors, Getters và Setters (Hãy generate đầy đủ trong IDE)
    public FinishedProductInventory() {}
    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public String getPoCode() { return poCode; }
    public void setPoCode(String poCode) { this.poCode = poCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public LocalDateTime getMfgDate() { return mfgDate; }
    public void setMfgDate(LocalDateTime mfgDate) { this.mfgDate = mfgDate; }
    public LocalDateTime getExpDate() { return expDate; }
    public void setExpDate(LocalDateTime expDate) { this.expDate = expDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}