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

    private final IngredientExportRequestMapper mapper = new IngredientExportRequestMapper();
    private final IngredientExportRequestDetailMapper detailMapper = new IngredientExportRequestDetailMapper();

    @Override
    protected IngredientExportRequest map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }

    /** Lấy tất cả phiếu yêu cầu xuất kho kèm thông tin lệnh sản xuất */
    public List<IngredientExportRequest> findAll() {
        String sql = """
            SELECT ier.*, po.planned_output_kg, po.order_status, ic.ice_cream_name
            FROM ingredient_export_requests ier
            JOIN production_orders po ON ier.production_order_id = po.production_order_id
            JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id
            ORDER BY ier.requested_at DESC
            """;
        return find(sql, null);
    }

    /** Lấy phiếu theo ID */
    public IngredientExportRequest findById(int id) {
        String sql = """
            SELECT ier.*, po.planned_output_kg, po.order_status, ic.ice_cream_name
            FROM ingredient_export_requests ier
            JOIN production_orders po ON ier.production_order_id = po.production_order_id
            JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id
            WHERE ier.ingredient_export_request_id = ?
            """;
        return findOne(sql, ps -> ps.setInt(1, id));
    }

    /** Kiểm tra lệnh sản xuất đã có phiếu yêu cầu chưa — dùng count() từ BaseRepository */
    public boolean existsByOrderId(int productionOrderId) {
        String sql = "SELECT COUNT(1) FROM ingredient_export_requests WHERE production_order_id = ?";
        return count(sql, ps -> ps.setInt(1, productionOrderId)) > 0;
    }

    /**
     * Tạo phiếu yêu cầu xuất kho + tự động tính chi tiết nguyên liệu từ công thức.
     * Dùng transaction nên cần quản lý connection trực tiếp — trường hợp ngoại lệ hợp lý.
     * Trả về ID mới hoặc -1 nếu thất bại.
     */
    public int createWithDetails(IngredientExportRequest request) {
        String sqlRequest = """
            INSERT INTO ingredient_export_requests
            (production_order_id, requested_by, request_status, note)
            VALUES (?, ?, 'pending', ?)
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
            SET order_status = 'waiting_ingredient'
            WHERE production_order_id = ? AND order_status = 'draft'
            """;

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Tạo phiếu yêu cầu
                int newId;
                try (PreparedStatement ps = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, request.getProduction_order_id());
                    ps.setObject(2, request.getRequested_by());
                    ps.setString(3, request.getNote());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) { conn.rollback(); return -1; }
                        newId = keys.getInt(1);
                    }
                }

                // 2. Tạo chi tiết nguyên liệu (tự tính từ công thức * số kg)
                try (PreparedStatement ps = conn.prepareStatement(sqlDetails)) {
                    ps.setInt(1, newId);
                    ps.setBigDecimal(2, BigDecimal.valueOf(request.getPlanned_output_kg()));
                    ps.setInt(3, request.getProduction_order_id());
                    ps.executeUpdate();
                }

                // 3. Cập nhật trạng thái lệnh sản xuất → waiting_ingredient
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateOrder)) {
                    ps.setInt(1, request.getProduction_order_id());
                    ps.executeUpdate();
                }

                conn.commit();
                return newId;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("createWithDetails Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Connection Error: " + e.getMessage());
        }
        return -1;
    }

    /** Preview nguyên liệu cần xuất dựa trên công thức * số kg (chưa lưu DB) */
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
        BaseRepository<IngredientExportRequestDetail> detailRepo = new BaseRepository<>() {
            @Override
            protected IngredientExportRequestDetail map(ResultSet rs) throws SQLException {
                return detailMapper.RowMap(rs);
            }
        };
        return detailRepo.find(sql, ps -> {
            ps.setBigDecimal(1, plannedKg);
            ps.setInt(2, productionOrderId);
        });
    }

    /** Lấy production_order_id từ request_id */
    public int findProductionOrderId(int requestId) {
        String sql = "SELECT production_order_id FROM ingredient_export_requests WHERE ingredient_export_request_id = ?";
        return count(sql, ps -> ps.setInt(1, requestId));
    }

    /** Lấy danh sách chi tiết nguyên liệu của một phiếu */
    public List<IngredientExportRequestDetail> findDetailsByRequestId(int requestId) {
        String sql = """
            SELECT d.*, i.ingredient_name, u.unit_name
            FROM ingredient_export_request_details d
            JOIN ingredients i ON d.ingredient_id = i.ingredient_id
            JOIN units u ON i.unit_id = u.unit_id
            WHERE d.ingredient_export_request_id = ?
            ORDER BY i.ingredient_name
            """;
        BaseRepository<IngredientExportRequestDetail> detailRepo = new BaseRepository<>() {
            @Override
            protected IngredientExportRequestDetail map(ResultSet rs) throws SQLException {
                return detailMapper.RowMap(rs);
            }
        };
        return detailRepo.find(sql, ps -> ps.setInt(1, requestId));
    }
}