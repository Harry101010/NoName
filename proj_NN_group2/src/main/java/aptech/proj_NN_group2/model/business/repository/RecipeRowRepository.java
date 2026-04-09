package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.IFind;
import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.RecipeRow;
import aptech.proj_NN_group2.model.mapper.RecipeRowMapper;

class RecipeRowRepository extends BaseRepository<RecipeRow> implements IFind<RecipeRow> {
	private final RecipeRowMapper mapper = new RecipeRowMapper();
	
	@Override
	public RecipeRow findById(int id) {
		return null;
	}

	@Override
	public List<RecipeRow> findAll() {
		String sql = """
	        SELECT r.recipe_id, r.ice_cream_id, ic.ice_cream_name,
	               r.ingredient_id, i.ingredient_name, u.unit_name, r.quantity_per_kg
	        FROM recipes r
	        JOIN ice_creams ic ON r.ice_cream_id = ic.ice_cream_id
	        JOIN ingredients i ON r.ingredient_id = i.ingredient_id
	        JOIN units u ON i.unit_id = u.unit_id
	        ORDER BY ic.ice_cream_name, i.ingredient_name
        """;
        return find(sql, null);
	}
	
	List<RecipeRow> findRowsByIceCreamId(int iceCreamId) {
        String sql = """
		    SELECT r.recipe_id, r.ice_cream_id, ic.ice_cream_name,
		           r.ingredient_id, i.ingredient_name, u.unit_name, r.quantity_per_kg
		    FROM recipes r
		    JOIN ice_creams ic ON r.ice_cream_id = ic.ice_cream_id
		    JOIN ingredients i ON r.ingredient_id = i.ingredient_id
		    JOIN units u ON i.unit_id = u.unit_id
		    WHERE r.ice_cream_id = ?
		    ORDER BY i.ingredient_name
	    """;
        return find(sql, ps -> ps.setInt(1, iceCreamId));
    }

	@Override
	protected RecipeRow map(ResultSet rs) throws SQLException {
		return mapper.RowMap(rs);
	}

}