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
}