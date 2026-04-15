package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportReceipt;
import aptech.proj_NN_group2.model.mapper.IngredientExportReceiptMapper;

public class IngredientExportReceiptRepository extends BaseRepository<IngredientExportReceipt> {

    private final IngredientExportReceiptMapper mapper = new IngredientExportReceiptMapper();

    @Override
    protected IngredientExportReceipt map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }

    /**
     * Lấy danh sách phiếu xuất kho đã được duyệt (approved)
     * mà Trưởng SX chưa xác nhận nhận hàng (receipt_status = 'approved')
     */
    public List<IngredientExportReceipt> findApproved() {
        String sql = """
            SELECT ier2.*, ier.request_status, po.planned_output_kg, ic.ice_cream_name
            FROM ingredient_export_receipts ier2
            JOIN ingredient_export_requests ier ON ier2.ingredient_export_request_id = ier.ingredient_export_request_id
            JOIN production_orders po ON ier.production_order_id = po.production_order_id
            JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id
            WHERE ier2.receipt_status = 'approved'
            ORDER BY ier2.created_at DESC
            """;
        return find(sql, null);
    }

    /** Lấy tất cả phiếu xuất kho (để hiển thị lịch sử) */
    public List<IngredientExportReceipt> findAll() {
        String sql = """
            SELECT ier2.*, ier.request_status, po.planned_output_kg, ic.ice_cream_name
            FROM ingredient_export_receipts ier2
            JOIN ingredient_export_requests ier ON ier2.ingredient_export_request_id = ier.ingredient_export_request_id
            JOIN production_orders po ON ier.production_order_id = po.production_order_id
            JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id
            ORDER BY ier2.created_at DESC
            """;
        return find(sql, null);
    }

    /**
     * Trưởng SX xác nhận đã nhận đủ nguyên liệu:
     * - Cập nhật receipt_status → 'completed'
     * - Cập nhật request_status → 'completed'
     * - Cập nhật production_order status → 'in_progress'
     */
    public boolean confirmReceived(int receiptId, int requestId, int productionOrderId) {
        boolean r1 = executeUpdate(
            "UPDATE ingredient_export_receipts SET receipt_status = 'completed' WHERE ingredient_export_receipt_id = ?",
            ps -> ps.setInt(1, receiptId));

        boolean r2 = executeUpdate(
            "UPDATE ingredient_export_requests SET request_status = 'completed' WHERE ingredient_export_request_id = ?",
            ps -> ps.setInt(1, requestId));

        boolean r3 = executeUpdate(
            "UPDATE production_orders SET order_status = 'in_progress' WHERE production_order_id = ? AND order_status = 'waiting_ingredient'",
            ps -> ps.setInt(1, productionOrderId));

        return r1 && r2 && r3;
    }
}