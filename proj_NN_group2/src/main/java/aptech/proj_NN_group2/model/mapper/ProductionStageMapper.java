package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.ProductionStage;
import aptech.proj_NN_group2.model.interfaces.IMapper;

public class ProductionStageMapper implements IMapper<ProductionStage> {
    @Override
    public ProductionStage RowMap(ResultSet rs) throws SQLException {
        ProductionStage s = new ProductionStage();
        s.setProduction_stage_id(rs.getInt("production_stage_id"));
        s.setProduction_order_id(rs.getInt("production_order_id"));
        s.setStage_no(rs.getInt("stage_no"));
        s.setStage_name(rs.getString("stage_name"));
        s.setPlanned_duration_min((Integer) rs.getObject("planned_duration_min"));
        s.setActual_duration_min((Integer) rs.getObject("actual_duration_min"));
        s.setActual_volume(rs.getBigDecimal("actual_volume"));
        s.setMold_count((Integer) rs.getObject("mold_count"));
        s.setStart_time(rs.getTimestamp("start_time"));
        s.setEnd_time(rs.getTimestamp("end_time"));
        s.setStage_status(rs.getString("stage_status"));
        s.setRecorded_by((Integer) rs.getObject("recorded_by"));
        s.setNote(rs.getString("note"));
        return s;
    }
}