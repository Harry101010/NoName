package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.ProductionStage;
import aptech.proj_NN_group2.model.entity.ProductionStageDetail;
import aptech.proj_NN_group2.model.mapper.ProductionStageMapper;
import aptech.proj_NN_group2.util.Database;

public class ProductionStageRepository extends BaseRepository<ProductionStage> {

    private static final String STATUS_OPEN = "open";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_COMPLETED = "completed";

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

    private final ProductionStageMapper mapper = new ProductionStageMapper();

    @Override
    protected ProductionStage map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }

    public List<ProductionStage> findByOrderId(int orderId) {
        return find("SELECT * FROM production_stages WHERE production_order_id = ? ORDER BY stage_no",
                ps -> ps.setInt(1, orderId));
    }

    public void initStages(int orderId) {
        String sql = "INSERT INTO production_stages (production_order_id, stage_no, stage_name, stage_status) VALUES (?,?,?,?)";
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < STAGE_NAMES.length; i++) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, i + 1);
                    ps.setString(3, STAGE_NAMES[i]);
                    ps.setString(4, i == 0 ? STATUS_OPEN : STATUS_PENDING);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            System.err.println("initStages Error: " + e.getMessage());
        } finally {
            closeQuietly(conn);
        }
    }

    public ProductionStage findById(int stageId) {
        return findOne("SELECT * FROM production_stages WHERE production_stage_id = ?",
                ps -> ps.setInt(1, stageId));
    }

    public boolean completeStage(ProductionStage stage) {
        String sql = "UPDATE production_stages SET stage_status=?, actual_duration_min=?, " +
                     "actual_volume=?, mold_count=?, end_time=SYSDATETIME(), recorded_by=?, note=? " +
                     "WHERE production_stage_id=?";
        return executeUpdate(sql, ps -> {
            ps.setString(1, STATUS_COMPLETED);
            ps.setObject(2, stage.getActual_duration_min());
            ps.setBigDecimal(3, stage.getActual_volume());
            ps.setObject(4, stage.getMold_count());
            ps.setObject(5, stage.getRecorded_by());
            ps.setString(6, stage.getNote());
            ps.setInt(7, stage.getProduction_stage_id());
        });
    }

    public boolean completeStageWithDetail(ProductionStageDetail detail) {
        String finalNote = detail.getNote();
        if (detail.isMixingStage()) {
            StringBuilder sb = new StringBuilder();
            if (detail.getMixing_temperature_c() != null) {
                sb.append("[Nhiệt độ trộn: ").append(detail.getMixing_temperature_c()).append("°C] ");
            }
            if (detail.getMixing_ratio_note() != null && !detail.getMixing_ratio_note().isBlank()) {
                sb.append("[Tỉ lệ trộn: ").append(detail.getMixing_ratio_note()).append("] ");
            }
            if (detail.getNote() != null && !detail.getNote().isBlank()) {
                sb.append(detail.getNote());
            }
            finalNote = sb.toString().trim();
        }

        String sql = "UPDATE production_stages SET stage_status=?, actual_duration_min=?, " +
                     "actual_volume=?, mold_count=?, end_time=SYSDATETIME(), note=? " +
                     "WHERE production_stage_id=?";
        String noteFinal = finalNote;
        return executeUpdate(sql, ps -> {
            ps.setString(1, STATUS_COMPLETED);
            ps.setObject(2, detail.getActual_duration_min());
            ps.setBigDecimal(3, detail.getActual_volume());
            ps.setObject(4, detail.getMold_count());
            ps.setString(5, noteFinal);
            ps.setInt(6, detail.getProduction_stage_id());
        });
    }

    public boolean unlockNextStage(int orderId, int nextStageNo) {
        return executeUpdate(
            "UPDATE production_stages SET stage_status=?, start_time=SYSDATETIME() " +
            "WHERE production_order_id=? AND stage_no=?",
            ps -> {
                ps.setString(1, STATUS_OPEN);
                ps.setInt(2, orderId);
                ps.setInt(3, nextStageNo);
            });
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