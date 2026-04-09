package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.IMapper;
import aptech.proj_NN_group2.model.entity.RecipeRow;

public class RecipeRowMapper implements IMapper<RecipeRow> {
    @Override
    public RecipeRow RowMap(ResultSet rs) throws SQLException {
        RecipeRow r = new RecipeRow();
        r.setRecipe_id(rs.getInt("recipe_id"));
        r.setIce_cream_id(rs.getInt("ice_cream_id"));
        r.setIce_cream_name(rs.getString("ice_cream_name"));
        r.setIngredient_id(rs.getInt("ingredient_id"));
        r.setIngredient_name(rs.getString("ingredient_name"));
        r.setUnit_name(rs.getString("unit_name"));
        r.setQuantity_per_kg(rs.getDouble("quantity_per_kg"));
        return r;
    }
}