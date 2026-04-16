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
}