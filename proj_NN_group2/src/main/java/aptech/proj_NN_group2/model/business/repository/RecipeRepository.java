package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.Recipe;
import aptech.proj_NN_group2.model.entity.RecipeRow;
import aptech.proj_NN_group2.model.interfaces.ICreate;
import aptech.proj_NN_group2.model.interfaces.IDelete;
import aptech.proj_NN_group2.model.interfaces.IFind;
import aptech.proj_NN_group2.model.interfaces.IUpdate;
import aptech.proj_NN_group2.model.mapper.RecipeMapper;

public class RecipeRepository extends BaseRepository<Recipe> 
	implements IFind<Recipe>, ICreate<Recipe>, IUpdate<Recipe>, IDelete<Recipe> {
	
    private final RecipeMapper mapper = new RecipeMapper();
    private final RecipeRowRepository recipeRowRepository = new RecipeRowRepository();
    
    @Override
    public Recipe findById(int id) {
        return findOne("SELECT * FROM recipes WHERE recipe_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Recipe> findAll() {
        return find("SELECT * FROM recipes ORDER BY ice_cream_id, ingredient_id", null);
    }

    public List<RecipeRow> findAllRows() {
    	return recipeRowRepository.findAll();
    }

    public List<RecipeRow> findRowsByIceCreamId(int iceCreamId) {
    	return recipeRowRepository.findRowsByIceCreamId(iceCreamId);
    }

    public Recipe findByIceCreamAndIngredient(int iceCreamId, int ingredientId) {
        return findOne("""
                SELECT * FROM recipes
                WHERE ice_cream_id = ? AND ingredient_id = ?
                """, ps -> {
            ps.setInt(1, iceCreamId);
            ps.setInt(2, ingredientId);
        });
    }

    public boolean create(Recipe r) {
        String sql = """
            INSERT INTO recipes (ice_cream_id, ingredient_id, quantity_per_kg)
            VALUES (?, ?, ?)
        """;
        return executeUpdate(
        	sql,
        	ps -> {
        		ps.setInt(1, r.getIce_cream_id());
        		ps.setInt(2, r.getIngredient_id());
        		ps.setDouble(3, r.getQuantity_per_kg());
        	}
        );
    }

    public boolean update(Recipe r) {
        String sql = """
		    UPDATE recipes
		    SET ice_cream_id = ?, ingredient_id = ?, quantity_per_kg = ?
		    WHERE recipe_id = ?
        """;
        return executeUpdate(
        	sql,
        	ps -> {        		
        		ps.setInt(1, r.getIce_cream_id());
        		ps.setInt(2, r.getIngredient_id());
        		ps.setDouble(3, r.getQuantity_per_kg());
        		ps.setInt(4, r.getRecipe_id());
        	}
        );
    }

    public boolean delete(int id) {
        return executeUpdate("DELETE FROM recipes WHERE recipe_id = ?", ps -> ps.setInt(1, id));
    }

    public boolean deleteByIceCreamId(int iceCreamId) {
        return executeUpdate("DELETE FROM recipes WHERE ice_cream_id = ?", ps -> ps.setInt(1, iceCreamId));
    }

    public boolean deleteByIngredientId(int ingredientId) {
        return executeUpdate("DELETE FROM recipes WHERE ingredient_id = ?", ps -> ps.setInt(1, ingredientId));
    }

    @Override
    protected Recipe map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}