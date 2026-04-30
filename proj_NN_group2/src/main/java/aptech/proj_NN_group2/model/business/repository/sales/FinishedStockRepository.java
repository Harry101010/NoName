package aptech.proj_NN_group2.model.business.repository.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import aptech.proj_NN_group2.model.entity.sales.FinishedStock;
import aptech.proj_NN_group2.util.Database;

public class FinishedStockRepository {

    // Lấy toàn bộ tồn kho để hiển thị lên TableView
	public List<FinishedStock> getAllStock() {
	    List<FinishedStock> list = new ArrayList<>();
	    // 1. Phải có production_order_id trong câu lệnh SELECT
	    String sql = "SELECT production_order_id, product_name, current_quantity, mfg_date, exp_date, storage_location " +
	                 "FROM finished_product_inventory WHERE current_quantity > 0 " +
	                 "ORDER BY mfg_date DESC";
	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        
	        while (rs.next()) {
	            FinishedStock stock = new FinishedStock();
	            // 2. Đọc đúng tên cột từ SQL
	            stock.setOrderId(rs.getInt("production_order_id")); 
	            stock.setProductName(rs.getString("product_name"));
	            stock.setQuantity(rs.getDouble("current_quantity"));
	            stock.setMfgDate(rs.getString("mfg_date"));
	            stock.setExpDate(rs.getString("exp_date"));
	            stock.setLocation(rs.getString("storage_location"));
	            list.add(stock);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

    // HÀM QUAN TRỌNG: Nhập kho đa tầng (Thành phẩm + Lô chi tiết + Request/Receipt)
    public boolean importFinishedStock(int productionOrderId, double quantity) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Tạo Request (Dành cho quy trình thủ kho duyệt)
            String insertRequest = "INSERT INTO finished_stock_requests (production_order_id, requested_quantity, request_status) VALUES (?, ?, N'approved')";
            try (PreparedStatement psReq = conn.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
                psReq.setInt(1, productionOrderId);
                psReq.setDouble(2, quantity);
                psReq.executeUpdate();
                
                ResultSet rsReq = psReq.getGeneratedKeys();
                if (!rsReq.next()) throw new SQLException("Không tạo được request.");
                int requestId = rsReq.getInt(1);

                // 2. Tạo Receipt
                String insertReceipt = "INSERT INTO finished_stock_receipts (finished_stock_request_id, received_quantity) VALUES (?, ?)";
                try (PreparedStatement psRec = conn.prepareStatement(insertReceipt)) {
                    psRec.setInt(1, requestId);
                    psRec.setDouble(2, quantity);
                    psRec.executeUpdate();
                }
            }

            // 3. Cập nhật bảng lô hàng chi tiết (Bảng bạn xem trên giao diện)
            // Lưu trực tiếp production_order_id để hiện số 16, 17
         // SỬA LẠI: Chỉ lấy ID thuần túy, không cộng thêm chữ 'PO-'
            String sqlInsertDetail = "INSERT INTO dbo.finished_product_inventory " +
                                     "(production_order_id, production_po_code, product_name, current_quantity, mfg_date, exp_date, storage_location) " +
                                     "SELECT po.production_order_id, CAST(po.production_order_id AS NVARCHAR), ice.ice_cream_name, ?, GETDATE(), DATEADD(month, 1, GETDATE()), N'Khu vực chờ' " +
                                     "FROM production_orders po JOIN ice_creams ice ON po.ice_cream_id = ice.ice_cream_id WHERE po.production_order_id = ?";try (PreparedStatement psDet = conn.prepareStatement(sqlInsertDetail)) {
                psDet.setDouble(1, quantity);
                psDet.setInt(2, productionOrderId);
                psDet.executeUpdate();
            }

            // 4. Update tổng tồn kho (Merge)
            String updateInventory = "MERGE finished_inventory AS target " +
                                     "USING (SELECT ice_cream_id FROM production_orders WHERE production_order_id = ?) AS source " +
                                     "ON target.ice_cream_id = source.ice_cream_id " +
                                     "WHEN MATCHED THEN UPDATE SET quantity_on_hand = quantity_on_hand + ? " +
                                     "WHEN NOT MATCHED THEN INSERT (ice_cream_id, quantity_on_hand) VALUES (source.ice_cream_id, ?);";
            try (PreparedStatement psInv = conn.prepareStatement(updateInventory)) {
                psInv.setInt(1, productionOrderId);
                psInv.setDouble(2, quantity);
                psInv.setDouble(3, quantity);
                psInv.executeUpdate();
            }

            conn.commit(); // Hoàn tất giao dịch
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public void syncMissingOrders() {
        // Câu lệnh SQL này sẽ tìm những đơn hàng đã 'finished' trong sản xuất 
        // mà chưa tồn tại trong bảng kho thành phẩm.
        String sql = "INSERT INTO dbo.finished_product_inventory " +
                     "(production_order_id, production_po_code, product_name, current_quantity, mfg_date, exp_date, storage_location) " +
                     "SELECT po.production_order_id, CAST(po.production_order_id AS NVARCHAR), ic.ice_cream_name, po.planned_output_kg, GETDATE(), DATEADD(month, 1, GETDATE()), N'Khu vực chờ' " +
                     "FROM production_orders po " +
                     "JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id " +
                     "WHERE po.order_status = 'finished' " + // Đảm bảo đơn hàng ở xưởng đã đánh dấu hoàn tất
                     "AND NOT EXISTS (SELECT 1 FROM finished_product_inventory WHERE production_order_id = po.production_order_id)";
                     
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int rows = ps.executeUpdate();
            System.out.println("Đồng bộ thành công: Đã thêm " + rows + " đơn hàng mới vào kho.");
        } catch (SQLException e) {
            System.err.println("Lỗi đồng bộ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}