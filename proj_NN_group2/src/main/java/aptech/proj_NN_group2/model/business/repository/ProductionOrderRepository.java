package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.model.mapper.ProductionOrderMapper;
import aptech.proj_NN_group2.util.Database;

public class ProductionOrderRepository extends BaseRepository<ProductionOrder> {

    private static final String ORDER_STATUS_DRAFT = "draft";

    private final ProductionOrderMapper mapper = new ProductionOrderMapper();

    @Override
    protected ProductionOrder map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }

    public List<ProductionOrder> findAll() {
        String sql = "SELECT po.*, ic.ice_cream_name FROM production_orders po " +
                     "JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id " +
                     "ORDER BY po.created_at DESC";
        return find(sql, null);
    }

    public List<ProductionOrder> findByStatus(String status) {
        String sql = "SELECT po.*, ic.ice_cream_name FROM production_orders po " +
                     "JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id " +
                     "WHERE po.order_status = ? " +
                     "ORDER BY po.created_at DESC";
        return find(sql, ps -> ps.setString(1, status));
    }

    public int create(ProductionOrder order) {
        String sql = "INSERT INTO production_orders (ice_cream_id, planned_output_kg, created_by, order_status, note) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getIce_cream_id());
            ps.setBigDecimal(2, order.getPlanned_output_kg());
            ps.setObject(3, order.getCreated_by());
            ps.setString(4, ORDER_STATUS_DRAFT);
            ps.setString(5, order.getNote());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                return -1;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Create ProductionOrder Error: " + e.getMessage());
        }
        return -1;
    }
}