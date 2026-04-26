package aptech.proj_NN_group2.model.business.repository;

import java.sql.*;
import aptech.proj_NN_group2.util.Database;

public class FinishedStockRepository {

    private Connection getConnection() throws Exception {
        return Database.getConnection();
    }

    // =========================
    // NHẬP KHO THÀNH PHẨM
    // =========================
    public boolean importFinishedStock(int productionOrderId, double quantity) {

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            // 1. Tạo request
            String insertRequest = """
                INSERT INTO finished_stock_requests
                (production_order_id, requested_quantity, request_status)
                VALUES (?, ?, N'approved')
            """;

            PreparedStatement psReq = conn.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS);
            psReq.setInt(1, productionOrderId);
            psReq.setDouble(2, quantity);
            psReq.executeUpdate();

            ResultSet rsReq = psReq.getGeneratedKeys();
            rsReq.next();
            int requestId = rsReq.getInt(1);

            // 2. Tạo receipt
            String insertReceipt = """
                INSERT INTO finished_stock_receipts
                (finished_stock_request_id, received_quantity)
                VALUES (?, ?)
            """;

            PreparedStatement psRec = conn.prepareStatement(insertReceipt);
            psRec.setInt(1, requestId);
            psRec.setDouble(2, quantity);
            psRec.executeUpdate();

            // 3. Lấy ice_cream_id từ production_order
            String getIceCream = """
                SELECT ice_cream_id FROM production_orders
                WHERE production_order_id = ?
            """;

            PreparedStatement psGet = conn.prepareStatement(getIceCream);
            psGet.setInt(1, productionOrderId);
            ResultSet rs = psGet.executeQuery();

            int iceCreamId = 0;
            if (rs.next()) {
                iceCreamId = rs.getInt("ice_cream_id");
            }

            // 4. Update tồn kho
            String updateInventory = """
                MERGE finished_inventory AS target
                USING (SELECT ? AS ice_cream_id) AS source
                ON target.ice_cream_id = source.ice_cream_id
                WHEN MATCHED THEN
                    UPDATE SET quantity_on_hand = quantity_on_hand + ?
                WHEN NOT MATCHED THEN
                    INSERT (ice_cream_id, quantity_on_hand)
                    VALUES (?, ?);
            """;

            PreparedStatement psInv = conn.prepareStatement(updateInventory);
            psInv.setInt(1, iceCreamId);
            psInv.setDouble(2, quantity);
            psInv.setInt(3, iceCreamId);
            psInv.setDouble(4, quantity);
            psInv.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}