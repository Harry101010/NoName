package aptech.proj_NN_group2.model.business.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IngredientExportRequest;
import aptech.proj_NN_group2.model.entity.IngredientExportRequestDetail;
import aptech.proj_NN_group2.model.mapper.IngredientExportRequestDetailMapper;
import aptech.proj_NN_group2.model.mapper.IngredientExportRequestMapper;
import aptech.proj_NN_group2.util.Database;

public class IngredientExportRequestRepository extends BaseRepository<IngredientExportRequest> {

    private static final String REQUEST_STATUS_PENDING = "pending";
    private static final String ORDER_STATUS_DRAFT = "draft";
    private static final String ORDER_STATUS_WAITING_INGREDIENT = "waiting_ingredient";

    private static final String BASE_SELECT = """
        SELECT ier.*, po.planned_output_kg, po.order_status, ic.ice_cream_name
        FROM ingredient_export_requests ier
        JOIN production_orders po ON ier.production_order_id = po.production_order_id
        JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id
        """;

    private final IngredientExportRequestMapper mapper = new IngredientExportRequestMapper();
    private final IngredientExportRequestDetailRepository detailRepository = new IngredientExportRequestDetailRepository();

    @Override
    protected IngredientExportRequest map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }


    public List<IngredientExportRequest> findAll() {
        return find(BASE_SELECT + " ORDER BY ier.requested_at DESC", null);
    }

    public IngredientExportRequest findById(int id) {
        return findOne(BASE_SELECT + " WHERE ier.ingredient_export_request_id = ?", ps -> ps.setInt(1, id));
    }
    public boolean existsByOrderId(int productionOrderId) {
        String sql = "SELECT COUNT(1) FROM ingredient_export_requests WHERE production_order_id = ?";
        return count(sql, ps -> ps.setInt(1, productionOrderId)) > 0;
    }
    public int createWithDetails(IngredientExportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        String sqlRequest = """
            INSERT INTO ingredient_export_requests
            (production_order_id, requested_by, request_status, note)
            VALUES (?, ?, ?, ?)
            """;
        String sqlDetails = """
            INSERT INTO ingredient_export_request_details
            (ingredient_export_request_id, ingredient_id, required_quantity)
            SELECT ?, r.ingredient_id, r.quantity_per_kg * ?
            FROM recipes r
            JOIN production_orders po ON po.ice_cream_id = r.ice_cream_id
            WHERE po.production_order_id = ?
            """;
        String sqlUpdateOrder = """
            UPDATE production_orders
            SET order_status = ?
            WHERE production_order_id = ? AND order_status = ?
            """;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            int newId;
            try (PreparedStatement ps = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, request.getProduction_order_id());
                ps.setObject(2, request.getRequested_by());
                ps.setString(3, REQUEST_STATUS_PENDING);
                ps.setString(4, request.getNote());

                int inserted = ps.executeUpdate();
                if (inserted == 0) {
                    conn.rollback();
                    return -1;
                }

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return -1;
                    }
                    newId = keys.getInt(1);
                }
            }

            int detailRows;
            try (PreparedStatement ps = conn.prepareStatement(sqlDetails)) {
                ps.setInt(1, newId);
                ps.setBigDecimal(2, BigDecimal.valueOf(request.getPlanned_output_kg()));
                ps.setInt(3, request.getProduction_order_id());
                detailRows = ps.executeUpdate();
            }

            if (detailRows == 0) {
                conn.rollback();
                return -1;
            }

            int updatedOrderRows;
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateOrder)) {
                ps.setString(1, ORDER_STATUS_WAITING_INGREDIENT);
                ps.setInt(2, request.getProduction_order_id());
                ps.setString(3, ORDER_STATUS_DRAFT);
                updatedOrderRows = ps.executeUpdate();
            }

            if (updatedOrderRows == 0) {
                conn.rollback();
                return -1;
            }

            conn.commit();
            return newId;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            System.err.println("createWithDetails Error: " + e.getMessage());
        } finally {
            closeQuietly(conn);
        }
        return -1;
    }
    public List<IngredientExportRequestDetail> previewDetails(int productionOrderId, BigDecimal plannedKg) {
        String sql = """
            SELECT
                0 AS ingredient_export_request_detail_id,
                0 AS ingredient_export_request_id,
                r.ingredient_id,
                CAST(r.quantity_per_kg * ? AS DECIMAL(18,3)) AS required_quantity,
                i.ingredient_name,
                u.unit_name
            FROM recipes r
            JOIN production_orders po ON po.ice_cream_id = r.ice_cream_id
            JOIN ingredients i ON r.ingredient_id = i.ingredient_id
            JOIN units u ON i.unit_id = u.unit_id
            WHERE po.production_order_id = ?
            ORDER BY i.ingredient_name
            """;
    
        return detailRepository.find(sql, ps -> {
            ps.setBigDecimal(1, plannedKg);
            ps.setInt(2, productionOrderId);
        });
    }

    public int findProductionOrderId(int requestId) {
        String sql = "SELECT production_order_id FROM ingredient_export_requests WHERE ingredient_export_request_id = ?";
        Integer productionOrderId = queryInteger(sql, ps -> ps.setInt(1, requestId));
        return productionOrderId != null ? productionOrderId : -1;
    }
    public List<IngredientExportRequestDetail> findDetailsByRequestId(int requestId) {
        String sql = """
            SELECT d.*, i.ingredient_name, u.unit_name
            FROM ingredient_export_request_details d
            JOIN ingredients i ON d.ingredient_id = i.ingredient_id
            JOIN units u ON i.unit_id = u.unit_id
            WHERE d.ingredient_export_request_id = ?
            ORDER BY i.ingredient_name
            """;
        return detailRepository.find(sql, ps -> ps.setInt(1, requestId));
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

    private static final class IngredientExportRequestDetailRepository extends BaseRepository<IngredientExportRequestDetail> {
        private final IngredientExportRequestDetailMapper mapper = new IngredientExportRequestDetailMapper();

        @Override
        protected IngredientExportRequestDetail map(ResultSet rs) throws SQLException {
            return mapper.RowMap(rs);
        }
    }
    public List<IngredientExportRequest> findPending() {
        String sql = BASE_SELECT + " WHERE ier.request_status = 'pending'";
        return find(sql, null);
    }
    public boolean updateStatus(int requestId, String status) {

        String sql = """
            UPDATE ingredient_export_requests
            SET request_status = ?
            WHERE ingredient_export_request_id = ?
        """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, status);
            ps.setInt(2, requestId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean approveRequestWithFIFO(int requestId) {

        Connection conn = null;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // kiểm tra yêu cầu có tồn tại và còn pending không
            String checkRequestSql = """
                SELECT request_status
                FROM ingredient_export_requests
                WHERE ingredient_export_request_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(checkRequestSql)) {
                ps.setInt(1, requestId);

                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    throw new RuntimeException("Không tìm thấy yêu cầu xuất kho.");
                }

                String status = rs.getString("request_status");

                if (!REQUEST_STATUS_PENDING.equalsIgnoreCase(status)) {
                    throw new RuntimeException(
                            "Yêu cầu này đã được xử lý trước đó. Trạng thái hiện tại: " + status
                    );
                }
            }

            // kiểm tra đã có phiếu xuất chưa
            String checkReceiptSql = """
                SELECT ingredient_export_receipt_id
                FROM ingredient_export_receipts
                WHERE ingredient_export_request_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(checkReceiptSql)) {
                ps.setInt(1, requestId);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    throw new RuntimeException("Yêu cầu này đã được duyệt trước đó.");
                }
            }

            List<IngredientExportRequestDetail> details =
                    findDetailsByRequestId(requestId);

            if (details == null || details.isEmpty()) {
                throw new RuntimeException("Yêu cầu này chưa có nguyên liệu để xuất.");
            }

            // kiểm tra tồn kho trước khi trừ
            for (IngredientExportRequestDetail detail : details) {

                String sqlCheck = """
                    SELECT ISNULL(SUM(remaining_quantity), 0) AS total_quantity
                    FROM ingredient_lots
                    WHERE ingredient_id = ?
                """;

                try (PreparedStatement ps = conn.prepareStatement(sqlCheck)) {

                    ps.setInt(1, detail.getIngredient_id());

                    ResultSet rs = ps.executeQuery();
                    rs.next();

                    BigDecimal available = rs.getBigDecimal("total_quantity");

                    if (available == null) {
                        available = BigDecimal.ZERO;
                    }

                    if (available.compareTo(detail.getRequired_quantity()) < 0) {

                        throw new RuntimeException(
                                "Không đủ tồn kho cho nguyên liệu: "
                                        + detail.getIngredient_name()
                                        + "\nCần: " + detail.getRequired_quantity() + " " + detail.getUnit_name()
                                        + "\nCòn: " + available + " " + detail.getUnit_name()
                        );
                    }
                }
            }

            // tạo phiếu xuất kho
            int receiptId;

            String insertReceiptSql = """
                INSERT INTO ingredient_export_receipts
                (ingredient_export_request_id, receipt_status)
                VALUES (?, 'approved')
            """;

            try (PreparedStatement ps = conn.prepareStatement(
                    insertReceiptSql,
                    Statement.RETURN_GENERATED_KEYS
            )) {

                ps.setInt(1, requestId);

                int inserted = ps.executeUpdate();

                if (inserted == 0) {
                    throw new RuntimeException("Không thể tạo phiếu xuất kho.");
                }

                ResultSet rs = ps.getGeneratedKeys();

                if (!rs.next()) {
                    throw new RuntimeException("Không lấy được mã phiếu xuất.");
                }

                receiptId = rs.getInt(1);
            }

            // trừ kho theo FIFO
            for (IngredientExportRequestDetail detail : details) {

                BigDecimal need = detail.getRequired_quantity();

                String sqlLots = """
                	    SELECT lot_id, remaining_quantity
                	    FROM ingredient_lots
                	    WHERE ingredient_id = ?
                	      AND remaining_quantity > 0
                	    ORDER BY expiry_date ASC, import_date ASC, lot_id ASC
                	""";
                try (PreparedStatement ps = conn.prepareStatement(sqlLots)) {

                    ps.setInt(1, detail.getIngredient_id());

                    ResultSet rs = ps.executeQuery();

                    while (rs.next() && need.compareTo(BigDecimal.ZERO) > 0) {

                        int lotId = rs.getInt("lot_id");
                        BigDecimal remain = rs.getBigDecimal("remaining_quantity");

                        if (remain == null || remain.compareTo(BigDecimal.ZERO) <= 0) {
                            continue;
                        }

                        BigDecimal issueQty =
                                remain.compareTo(need) >= 0 ? need : remain;

                        // cập nhật số lượng còn lại của lô
                        String updateLotSql = """
                            UPDATE ingredient_lots
                            SET remaining_quantity = remaining_quantity - ?
                            WHERE lot_id = ?
                        """;

                        try (PreparedStatement psUpdate = conn.prepareStatement(updateLotSql)) {
                            psUpdate.setBigDecimal(1, issueQty);
                            psUpdate.setInt(2, lotId);
                            psUpdate.executeUpdate();
                        }

                        // lưu chi tiết phiếu xuất
                        String insertDetailSql = """
                            INSERT INTO ingredient_export_receipt_details
                            (
                                ingredient_export_receipt_id,
                                ingredient_export_request_detail_id,
                                lot_id,
                                issued_quantity
                            )
                            VALUES (?, ?, ?, ?)
                        """;

                        try (PreparedStatement psInsert = conn.prepareStatement(insertDetailSql)) {
                            psInsert.setInt(1, receiptId);
                            psInsert.setInt(2, detail.getIngredient_export_request_detail_id());
                            psInsert.setInt(3, lotId);
                            psInsert.setBigDecimal(4, issueQty);
                            psInsert.executeUpdate();
                        }

                        need = need.subtract(issueQty);
                    }
                }

                // nếu sau FIFO mà vẫn chưa đủ
                if (need.compareTo(BigDecimal.ZERO) > 0) {
                    throw new RuntimeException(
                            "Không thể xuất đủ nguyên liệu: " + detail.getIngredient_name()
                    );
                }
            }

            // cập nhật trạng thái yêu cầu
            String updateRequestSql = """
                UPDATE ingredient_export_requests
                SET request_status = 'approved'
                WHERE ingredient_export_request_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(updateRequestSql)) {
                ps.setInt(1, requestId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {

            rollbackQuietly(conn);

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            throw new RuntimeException("Không thể duyệt yêu cầu: " + e.getMessage());

        } finally {
            closeQuietly(conn);
        }
    }
}
