package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.util.Database;

public class ProductionOrderRepository extends BaseRepository<ProductionOrder> {

    @Override
    protected ProductionOrder map(ResultSet rs) throws SQLException {
        ProductionOrder o = new ProductionOrder();
        o.setProduction_order_id(rs.getInt("production_order_id"));
        o.setIce_cream_id(rs.getInt("ice_cream_id"));
        o.setPlanned_output_kg(rs.getBigDecimal("planned_output_kg"));
        o.setCreated_by((Integer) rs.getObject("created_by"));
        o.setCreated_at(rs.getTimestamp("created_at"));
        o.setOrder_status(rs.getString("order_status"));
        o.setNote(rs.getString("note"));
        // optional join field
        try { o.setIce_cream_name(rs.getString("ice_cream_name")); } catch (SQLException ignored) {}
        return o;
    }

    public List<ProductionOrder> findAll() {
        String sql = "SELECT po.*, ic.ice_cream_name FROM production_orders po " +
                     "JOIN ice_creams ic ON po.ice_cream_id = ic.ice_cream_id " +
                     "ORDER BY po.created_at DESC";
        return find(sql, null);
    }

    /** Insert new order, returns generated ID or -1 on failure */
    public int create(ProductionOrder order) {
        String sql = "INSERT INTO production_orders (ice_cream_id, planned_output_kg, created_by, order_status, note) " +
                     "VALUES (?, ?, ?, 'draft', ?)";
//        try (Connection conn = Database.getDataSource().getConnection();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getIce_cream_id());
            ps.setBigDecimal(2, order.getPlanned_output_kg());
            ps.setObject(3, order.getCreated_by());
            ps.setString(4, order.getNote());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Create ProductionOrder Error: " + e.getMessage());
        }
        return -1;
    }
}
