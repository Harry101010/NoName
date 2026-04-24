package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.IngredientExportRequestDetail;
import aptech.proj_NN_group2.model.interfaces.IMapper;

public class IngredientExportRequestDetailMapper implements IMapper<IngredientExportRequestDetail> {
    @Override
    public IngredientExportRequestDetail RowMap(ResultSet rs) throws SQLException {
        IngredientExportRequestDetail d = new IngredientExportRequestDetail();
        d.setIngredient_export_request_detail_id(rs.getInt("ingredient_export_request_detail_id"));
        d.setIngredient_export_request_id(rs.getInt("ingredient_export_request_id"));
        d.setIngredient_id(rs.getInt("ingredient_id"));
        d.setRequired_quantity(rs.getBigDecimal("required_quantity"));
        try { d.setIngredient_name(rs.getString("ingredient_name")); } catch (SQLException ignored) {}
        try { d.setUnit_name(rs.getString("unit_name")); } catch (SQLException ignored) {}
        return d;
    }
}
