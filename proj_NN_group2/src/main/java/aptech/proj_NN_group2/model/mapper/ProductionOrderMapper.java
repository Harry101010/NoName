package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.IMapper;
import aptech.proj_NN_group2.model.entity.ProductionOrder;

public class ProductionOrderMapper implements IMapper<ProductionOrder> {

    @Override
    public ProductionOrder RowMap(ResultSet rs) throws SQLException {
        ProductionOrder o = new ProductionOrder();
        o.setProduction_order_id(rs.getInt("production_order_id"));
        o.setIce_cream_id(rs.getInt("ice_cream_id"));
        o.setPlanned_output_kg(rs.getBigDecimal("planned_output_kg"));
        o.setCreated_by((Integer) rs.getObject("created_by"));
        o.setCreated_at(rs.getTimestamp("created_at"));
        o.setOrder_status(rs.getString("order_status"));
        o.setNote(rs.getString("note"));
        try { o.setIce_cream_name(rs.getString("ice_cream_name")); } catch (SQLException ignored) {}
        return o;
    }
}
