package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.entity.IngredientRow;
import aptech.proj_NN_group2.model.interfaces.IMapper;

public class IngredientRowMapper implements IMapper<IngredientRow> {
	@Override
    public IngredientRow RowMap(ResultSet rs) throws SQLException {
        IngredientRow i = new IngredientRow();
        i.setIngredient_id(rs.getInt("ingredient_id"));
        i.setIngredient_name(rs.getString("ingredient_name"));
        i.setOrigin(rs.getString("origin"));
        i.setStorage_condition(rs.getString("storage_condition"));
        i.setUnit_id(rs.getInt("unit_id"));
        i.setUnit_name(rs.getString("unit_name"));
        i.setPrice_per_unit(rs.getDouble("price_per_unit"));
        i.setIs_active(rs.getBoolean("is_active"));
        return i;
    }
}