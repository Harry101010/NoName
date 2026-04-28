package aptech.proj_NN_group2.model.business.repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportReceipt;
import aptech.proj_NN_group2.model.mapper.IngredientExportReceiptMapper;
import aptech.proj_NN_group2.util.Database;

public class IngredientExportReceiptRepository extends BaseRepository<IngredientExportReceipt> {

    private static final String RECEIPT_STATUS_APPROVED = "approved";
    private static final String RECEIPT_STATUS_COMPLETED = "completed";
    private static final String REQUEST_STATUS_COMPLETED = "completed";
    private static final String ORDER_STATUS_IN_PROGRESS = "in_progress";
    private static final String ORDER_STATUS_WAITING_INGREDIENT = "waiting_ingredient";

    private static final String BASE_SELECT = """
        SELECT ier2.*, ier.request_status, po.planned_output_kg, ic.ice_cream_name
        FROM ingredient_export_receipts ier2
        JOIN ingredient_export_requests ier ON ier2.ingredient_export_request_id = ier.ingredient_export_request_id
        JOIN production_orders po ON ier.production_order_id = po.production_order_id
        JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id
        """;
    private final IngredientExportReceiptMapper mapper = new IngredientExportReceiptMapper();

    @Override
    protected IngredientExportReceipt map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
    public List<IngredientExportReceipt> findApproved() {
        String sql = BASE_SELECT + " WHERE ier2.receipt_status = ? ORDER BY ier2.created_at DESC";
        return find(sql, ps -> ps.setString(1, RECEIPT_STATUS_APPROVED));
    }

    /** Tìm phiếu xuất kho đã duyệt theo production_order_id — dùng cho ProductionDashboard */
    public IngredientExportReceipt findApprovedByOrderId(int productionOrderId) {
        String sql = BASE_SELECT
                + " WHERE ier.production_order_id = ? AND ier2.receipt_status = ?"
                + " ORDER BY ier2.created_at DESC";
        return findOne(sql, ps -> {
            ps.setInt(1, productionOrderId);
            ps.setString(2, RECEIPT_STATUS_APPROVED);
        });
    }

    public List<IngredientExportReceipt> findAll() {
        return find(BASE_SELECT + " ORDER BY ier2.created_at DESC", null);
    }

    public boolean confirmReceived(int receiptId, int requestId, int productionOrderId) {
        String sqlReceipt = "UPDATE ingredient_export_receipts SET receipt_status = ? WHERE ingredient_export_receipt_id = ?";
        String sqlRequest = "UPDATE ingredient_export_requests SET request_status = ? WHERE ingredient_export_request_id = ?";
        String sqlOrder = "UPDATE production_orders SET order_status = ? WHERE production_order_id = ? AND order_status = ?";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            int receiptRows;
            try (PreparedStatement ps = conn.prepareStatement(sqlReceipt)) {
                ps.setString(1, RECEIPT_STATUS_COMPLETED);
                ps.setInt(2, receiptId);
                receiptRows = ps.executeUpdate();
            }

            int requestRows;
            try (PreparedStatement ps = conn.prepareStatement(sqlRequest)) {
                ps.setString(1, REQUEST_STATUS_COMPLETED);
                ps.setInt(2, requestId);
                requestRows = ps.executeUpdate();
            }

            int orderRows;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                ps.setString(1, ORDER_STATUS_IN_PROGRESS);
                ps.setInt(2, productionOrderId);
                ps.setString(3, ORDER_STATUS_WAITING_INGREDIENT);
                orderRows = ps.executeUpdate();
            }

            if (receiptRows == 0 || requestRows == 0 || orderRows == 0) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(conn);
        }
    }

    private static void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
    public int createReceipt(int requestId, Integer approvedBy, String note) {
        String sql = """
            INSERT INTO ingredient_export_receipts
            (ingredient_export_request_id, approved_by, receipt_status, note)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, requestId);
            ps.setObject(2, approvedBy);
            ps.setString(3, RECEIPT_STATUS_APPROVED);
            ps.setString(4, note);

            int rows = ps.executeUpdate();
            if (rows == 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public IngredientExportReceipt findByRequestId(int requestId) {
        String sql = BASE_SELECT + " WHERE ier2.ingredient_export_request_id = ?";
        return findOne(sql, ps -> ps.setInt(1, requestId));
    }
    public List<IngredientExportReceipt> findByStatus(String status) {
        String sql = BASE_SELECT + " WHERE ier2.receipt_status = ?";
        return find(sql, ps -> ps.setString(1, status));
    }
    public boolean existsByRequestId(int requestId) {
        String sql = "SELECT COUNT(1) FROM ingredient_export_receipts WHERE ingredient_export_request_id = ?";
        return count(sql, ps -> ps.setInt(1, requestId)) > 0;
    }
    public boolean createReceiptWithFIFO(int requestId, Integer approvedBy, String note) {

        String insertReceipt = """
            INSERT INTO ingredient_export_receipts
            (ingredient_export_request_id, approved_by, receipt_status, note)
            VALUES (?, ?, ?, ?)
        """;

        String getDetails = """
            SELECT ingredient_export_request_detail_id, ingredient_id, required_quantity
            FROM ingredient_export_request_details
            WHERE ingredient_export_request_id = ?
        """;

        String getLotsFIFO = """
            SELECT lot_id, remaining_quantity
            FROM ingredient_lots
            WHERE ingredient_id = ? AND remaining_quantity > 0
            ORDER BY import_date ASC, lot_id ASC
        """;

        String updateLot = """
            UPDATE ingredient_lots
            SET remaining_quantity = ?
            WHERE lot_id = ?
        """;

        String insertDetail = """
            INSERT INTO ingredient_export_receipt_details
            (ingredient_export_receipt_id, ingredient_export_request_detail_id, lot_id, issued_quantity)
            VALUES (?, ?, ?, ?)
        """;

        Connection conn = null;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            int receiptId;

            // 1. tạo receipt
            try (PreparedStatement ps = conn.prepareStatement(insertReceipt, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, requestId);
                ps.setObject(2, approvedBy);
                ps.setString(3, RECEIPT_STATUS_APPROVED);
                ps.setString(4, note);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    receiptId = rs.getInt(1);
                }
            }

            // 2. lấy detail
            try (PreparedStatement psDetail = conn.prepareStatement(getDetails)) {
                psDetail.setInt(1, requestId);

                try (ResultSet rsDetail = psDetail.executeQuery()) {

                    while (rsDetail.next()) {
                        int requestDetailId = rsDetail.getInt("ingredient_export_request_detail_id");
                        int ingredientId = rsDetail.getInt("ingredient_id");
                        double requiredQty = rsDetail.getDouble("required_quantity");

                        // 3. FIFO
                        try (PreparedStatement psLot = conn.prepareStatement(getLotsFIFO)) {
                            psLot.setInt(1, ingredientId);

                            try (ResultSet rsLot = psLot.executeQuery()) {

                                while (rsLot.next() && requiredQty > 0) {
                                    int lotId = rsLot.getInt("lot_id");
                                    double remain = rsLot.getDouble("remaining_quantity");

                                    double used = Math.min(remain, requiredQty);
                                    double newRemain = remain - used;

                                    // update lot
                                    try (PreparedStatement psUpdate = conn.prepareStatement(updateLot)) {
                                        psUpdate.setDouble(1, newRemain);
                                        psUpdate.setInt(2, lotId);
                                        psUpdate.executeUpdate();
                                    }

                                    // insert detail đúng schema
                                    try (PreparedStatement psInsert = conn.prepareStatement(insertDetail)) {
                                        psInsert.setInt(1, receiptId);
                                        psInsert.setInt(2, requestDetailId);
                                        psInsert.setInt(3, lotId);
                                        psInsert.setDouble(4, used);
                                        psInsert.executeUpdate();
                                    }

                                    requiredQty -= used;
                                }
                            }
                        }

                        // thiếu hàng
                        if (requiredQty > 0) {
                            System.err.println("Không đủ nguyên liệu ID = " + ingredientId);
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            rollbackQuietly(conn);
            return false;
        } finally {
            closeQuietly(conn);
        }
    }
}
