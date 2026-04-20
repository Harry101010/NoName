package aptech.proj_NN_group2.model.entity;

public class IngredientExportReceiptDetail {
	private int id;
    private int receiptId;
    private int requestDetailId;
    private int lotId;
    private double issuedQuantity;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(int receiptId) {
		this.receiptId = receiptId;
	}
	public int getRequestDetailId() {
		return requestDetailId;
	}
	public void setRequestDetailId(int requestDetailId) {
		this.requestDetailId = requestDetailId;
	}
	public int getLotId() {
		return lotId;
	}
	public void setLotId(int lotId) {
		this.lotId = lotId;
	}
	public double getIssuedQuantity() {
		return issuedQuantity;
	}
	public void setIssuedQuantity(double issuedQuantity) {
		this.issuedQuantity = issuedQuantity;
	}
}
