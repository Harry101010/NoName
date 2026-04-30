package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
 // Trong ProductionOrderRepository.java
    public List<ProductionOrder> findActiveOrders() {
        List<ProductionOrder> list = new ArrayList<>();
        // Dùng JOIN để lấy tên kem (ice_cream_name) từ bảng ice_creams
        String sql = "SELECT o.*, i.ice_cream_name " +
                     "FROM production_orders o " +
                     "JOIN ice_creams i ON o.ice_cream_id = i.ice_cream_id " +
                     "WHERE o.order_status = 'in_progress'"; // Chỉ lấy lệnh đang chạy

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ProductionOrder order = new ProductionOrder();
                order.setProduction_order_id(rs.getInt("production_order_id"));
                order.setIce_cream_name(rs.getString("ice_cream_name")); // Quan trọng!
                order.setPlanned_output_kg(rs.getBigDecimal("planned_output_kg"));
                order.setOrder_status(rs.getString("order_status"));
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public ProductionOrder getOrderById(int orderId) {
        // [CẦN KIỂM TRA]: Hãy thay tên bảng và tên cột khớp 100% với SQL Server của bạn
        String sql = "SELECT * FROM production_orders WHERE production_order_id = ?"; 
        
        try (Connection conn = Database.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ProductionOrder order = new ProductionOrder();
                order.setProduction_order_id(rs.getInt("production_order_id"));
                // Lấy BigDecimal từ DB
                order.setPlanned_output_kg(rs.getBigDecimal("planned_output_kg")); 
                return order;
            }
        } catch (SQLException e) { 
            System.err.println("Lỗi lấy thông tin đơn hàng: " + e.getMessage());
            e.printStackTrace(); 
        }
        return null;
    }
}