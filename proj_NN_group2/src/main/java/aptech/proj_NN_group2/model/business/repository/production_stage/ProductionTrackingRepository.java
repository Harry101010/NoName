package aptech.proj_NN_group2.model.business.repository.production_stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import aptech.proj_NN_group2.model.entity.production_stage.ProductionSummary; // Đừng quên import class này
import aptech.proj_NN_group2.model.entity.production_stage.ProductionTracking;
import aptech.proj_NN_group2.util.Database;
import aptech.proj_NN_group2.model.entity.production_stage.ProductionPerformance; // Đảm bảo import đúng entity
import java.util.ArrayList;


public class ProductionTrackingRepository {

    // 1. Dùng để lấy thông tin trạng thái và thời gian
    public ProductionTracking getTrackingData(int orderId, int stageId) {
        // [LƯU Ý]: Kiểm tra tên cột 'production_order_id' trong DB của bạn. 
        // Nếu DB đặt tên là 'order_id' thì phải sửa lại ở đây và tất cả các hàm dưới.
        String sql = "SELECT * FROM production_tracking WHERE production_order_id = ? AND stage_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId); ps.setInt(2, stageId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ProductionTracking pt = new ProductionTracking();
                pt.setStatus(rs.getString("status"));
                pt.setStart_time(rs.getTimestamp("actual_start_time"));
                pt.setEnd_time(rs.getTimestamp("actual_end_time"));
                return pt;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // 2. Dùng để cập nhật trạng thái khi nhấn "Start"
    public void updateStatusByOrderAndStage(int orderId, int stageId, String status) {
        String sql = "UPDATE production_tracking SET status = ?, actual_start_time = GETDATE() WHERE production_order_id = ? AND stage_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.setInt(3, stageId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 3. Dùng để cập nhật khi nhấn "Finish" (Done/Fail)
    public void updateStageStatus(int orderId, int stageId, String quantity, String note, String status) {
        String sql = "UPDATE production_tracking SET status = ?, actual_quantity = ?, note = ?, actual_end_time = GETDATE() " +
                     "WHERE production_order_id = ? AND stage_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setBigDecimal(2, new java.math.BigDecimal(quantity));
            stmt.setString(3, note);
            stmt.setInt(4, orderId);
            stmt.setInt(5, stageId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 4. Lấy status để check khóa công đoạn
    public String getStatusByOrderAndStage(int orderId, int stageId) {
        String sql = "SELECT status FROM production_tracking WHERE production_order_id = ? AND stage_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId); ps.setInt(2, stageId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("status");
        } catch (SQLException e) { e.printStackTrace(); }
        return "pending"; // Mặc định
    }

    // 5. Lấy dữ liệu công đoạn trước (để điền số lượng)
    public ProductionTracking getPreviousStageData(int orderId, int currentStageId) {
        String sql = "SELECT * FROM production_tracking WHERE production_order_id = ? AND stage_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId); ps.setInt(2, currentStageId - 1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ProductionTracking pt = new ProductionTracking();
                pt.setActual_quantity(rs.getFloat("actual_quantity"));
                return pt;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // 6. Lấy công đoạn cuối cùng
    public int getMaxStageNo(int orderId) {
        String sql = "SELECT MAX(stage_id) FROM production_tracking WHERE production_order_id = ?";
        
        // DÒNG NÀY SẼ GIÚP BẠN THẤY CÂU LỆNH MÀ JAVA THỰC SỰ GỬI VÀO DB
        System.out.println("DEBUG SQL: " + sql + " | OrderID: " + orderId);
        
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { 
            System.err.println("Lỗi SQL thực tế: " + e.getMessage()); // Sẽ hiện lỗi cụ thể hơn
            e.printStackTrace(); 
        }
        return 0;
    }
    
    
    public List<ProductionSummary> getProductionSummary() {
        List<ProductionSummary> list = new ArrayList<>();
        String sql = "SELECT * FROM View_ProductionSummary"; // View bạn vừa tạo
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ProductionSummary s = new ProductionSummary();
                s.setOrderId(rs.getInt("production_order_id"));
                s.setProductName(rs.getString("product_name"));
                s.setProgressPercent(rs.getDouble("progress_percent"));
                s.setCurrentStage(rs.getString("current_stage")); // Đọc từ cột mới
                s.setFailedStage(rs.getString("failed_stage"));   // Đọc từ cột mới
                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    public List<ProductionPerformance> getPerformanceReport(int orderId) {
        List<ProductionPerformance> list = new ArrayList<>();
        // [CẦN KIỂM TRA TÊN CỘT]: Đảm bảo câu SQL khớp với Database của bạn
        // Tôi giả định bạn có cột stage_name trong bảng production_stages hoặc từ join
        String sql = "SELECT s.stage_name, t.actual_quantity, t.actual_start_time, t.actual_end_time " +
                     "FROM production_tracking t " +
                     "JOIN production_stages s ON t.stage_id = s.stage_id " +
                     "WHERE t.production_order_id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ProductionPerformance p = new ProductionPerformance();
                p.setStageName(rs.getString("stage_name"));
                p.setActualQuantity(rs.getFloat("actual_quantity"));
                p.setStartTime(rs.getTimestamp("actual_start_time"));
                p.setEndTime(rs.getTimestamp("actual_end_time"));
                
                // Tính toán Duration nếu cần, hoặc để setter tự tính
                list.add(p);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }
    public void initializeTrackingForOrder(int orderId) {
        // 1. Lấy danh sách tất cả các stage cần thực hiện
        List<aptech.proj_NN_group2.model.entity.production_stage.ProductionStageTemplate> allStages = 
            new aptech.proj_NN_group2.model.business.repository.ProductionStageRepository().getAllStages();
        
        // 2. Chèn vào bảng production_tracking với status mặc định là 'pending'
        String sql = "INSERT INTO production_tracking (production_order_id, stage_id, status) VALUES (?, ?, 'pending')";
        
        try (Connection conn = Database.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (var stage : allStages) {
                ps.setInt(1, orderId);
                ps.setInt(2, stage.getStageId());
                ps.executeUpdate();
            }
            System.out.println("DEBUG: Đã tạo tracking cho đơn hàng " + orderId);
        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo tracking: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void completeOrderAndImportStock(int orderId, double quantity) {
        String updateOrderSql = "UPDATE production_orders SET order_status = 'finished' WHERE production_order_id = ?";
        String insertReceiptSql = "INSERT INTO finished_stock_receipts (production_order_id, received_quantity, created_at) VALUES (?, ?, SYSDATETIME())";
        String updateInventorySql = "UPDATE finished_inventory SET quantity_on_hand = quantity_on_hand + ? WHERE ice_cream_id = (SELECT ice_cream_id FROM production_orders WHERE production_order_id = ?)";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            try (PreparedStatement ps1 = conn.prepareStatement(updateOrderSql);
                 PreparedStatement ps2 = conn.prepareStatement(insertReceiptSql);
                 PreparedStatement ps3 = conn.prepareStatement(updateInventorySql)) {
                
                // Thực hiện update order
                ps1.setInt(1, orderId);
                ps1.executeUpdate();

                // Thực hiện nhập kho
                ps2.setInt(1, orderId);
                ps2.setDouble(2, quantity);
                ps2.executeUpdate();

                // Thực hiện update inventory
                ps3.setDouble(1, quantity);
                ps3.setInt(2, orderId);
                ps3.executeUpdate();

                conn.commit(); // Lưu tất cả nếu không có lỗi
            } catch (SQLException e) {
                conn.rollback(); // Quay lại trạng thái cũ nếu có 1 bước bị lỗi
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getMaxStageIdByOrder(int orderId) {
        String sql = "SELECT MAX(stage_id) FROM production_tracking WHERE production_order_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
   
}