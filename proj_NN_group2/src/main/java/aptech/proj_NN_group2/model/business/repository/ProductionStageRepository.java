package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.ProductionStage;
import aptech.proj_NN_group2.util.Database;

import aptech.proj_NN_group2.model.mapper.ProductionStageMapper;

public class ProductionStageRepository extends BaseRepository<ProductionStage> {

    private final ProductionStageMapper mapper = new ProductionStageMapper();

    // 8 tên công đoạn theo đề
    public static final String[] STAGE_NAMES = {
        "Xử lý nguyên liệu và trộn hỗn hợp",
        "Đồng hóa",
        "Thanh trùng",
        "Ủ kem",
        "Đánh kem",
        "Chiết rót vào khuôn",
        "Làm cứng",
        "Bảo quản và đóng gói"
    };

    @Override
    protected ProductionStage map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }

    public List<ProductionStage> findByOrderId(int orderId) {
        return find("SELECT * FROM production_stages WHERE production_order_id = ? ORDER BY stage_no",
                ps -> ps.setInt(1, orderId));
    }

    /** Tạo 8 công đoạn mặc định cho một lệnh sản xuất */
    public void initStages(int orderId) {
        String sql = "INSERT INTO production_stages (production_order_id, stage_no, stage_name, stage_status) VALUES (?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < STAGE_NAMES.length; i++) {
                ps.setInt(1, orderId);
                ps.setInt(2, i + 1);
                ps.setString(3, STAGE_NAMES[i]);
                ps.setString(4, i == 0 ? "open" : "pending");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("initStages Error: " + e.getMessage());
        }
    }

    /** Cập nhật trạng thái + dữ liệu thực tế của một công đoạn */
    public boolean completeStage(ProductionStage stage) {
        String sql = "UPDATE production_stages SET stage_status='completed', actual_duration_min=?, " +
                     "actual_volume=?, mold_count=?, end_time=SYSDATETIME(), recorded_by=?, note=? " +
                     "WHERE production_stage_id=?";
        return executeUpdate(sql, ps -> {
            ps.setObject(1, stage.getActual_duration_min());
            ps.setBigDecimal(2, stage.getActual_volume());
            ps.setObject(3, stage.getMold_count());
            ps.setObject(4, stage.getRecorded_by());
            ps.setString(5, stage.getNote());
            ps.setInt(6, stage.getProduction_stage_id());
        });
    }

    /** Mở công đoạn tiếp theo */
    public boolean unlockNextStage(int orderId, int nextStageNo) {
        return executeUpdate(
            "UPDATE production_stages SET stage_status='open', start_time=SYSDATETIME() " +
            "WHERE production_order_id=? AND stage_no=?",
            ps -> { ps.setInt(1, orderId); ps.setInt(2, nextStageNo); });
    }
}
