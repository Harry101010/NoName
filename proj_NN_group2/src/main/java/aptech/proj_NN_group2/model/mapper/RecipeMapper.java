package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.IMapper;
import aptech.proj_NN_group2.model.entity.Recipe;

public class RecipeMapper implements IMapper<Recipe> {
    
	@Override
    public Recipe RowMap(ResultSet rs) throws SQLException {
        Recipe r = new Recipe();
        r.setRecipe_id(rs.getInt("recipe_id"));
        r.setIce_cream_id(rs.getInt("ice_cream_id"));
        r.setIngredient_id(rs.getInt("ingredient_id"));
        r.setQuantity_per_kg(rs.getDouble("quantity_per_kg"));
        return r;
    }

}