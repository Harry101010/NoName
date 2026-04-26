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

    // --- LOOKUP ---
    @Override
    public Recipe findById(int id) {
        return findOne("SELECT * FROM recipes WHERE recipe_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Recipe> findAll() {
        return find("SELECT * FROM recipes ORDER BY ice_cream_id, ingredient_id", null);
    }

    /**
     * Returns the joined recipe rows used by the UI tables.
     */
    public List<RecipeRow> findAllRows() {
        return recipeRowRepository.findAll();
    }

    /**
     * Returns the joined recipe rows for one ice cream product.
     */
    public List<RecipeRow> findRowsByIceCreamId(int iceCreamId) {
        return recipeRowRepository.findRowsByIceCreamId(iceCreamId);
    }

    // --- CREATE / UPDATE / DELETE ---
    @Override
    public boolean create(Recipe r) {
        String sql = "INSERT INTO recipes (ice_cream_id, ingredient_id, quantity_per_kg) VALUES (?, ?, ?)";
        return executeUpdate(sql, ps -> {
            ps.setInt(1, r.getIce_cream_id());
            ps.setInt(2, r.getIngredient_id());
            ps.setDouble(3, r.getQuantity_per_kg());
        });
    }

    @Override
    public boolean update(Recipe r) {
        String sql = "UPDATE recipes SET quantity_per_kg = ? WHERE recipe_id = ?";
        return executeUpdate(sql, ps -> {
            ps.setDouble(1, r.getQuantity_per_kg());
            ps.setInt(2, r.getRecipe_id());
        });
    }

    /**
     * UI helper: update by ice cream + ingredient pair.
     */
    public void update(RecipeRow row) {
        String sql = "UPDATE recipes SET quantity_per_kg = ? WHERE ice_cream_id = ? AND ingredient_id = ?";
        executeUpdate(sql, ps -> {
            ps.setDouble(1, row.getQuantity_per_kg());
            ps.setInt(2, row.getIce_cream_id());
            ps.setInt(3, row.getIngredient_id());
        });
    }

    @Override
    public boolean delete(int id) {
        return executeUpdate("DELETE FROM recipes WHERE recipe_id = ?", ps -> ps.setInt(1, id));
    }

    public boolean deleteByIceCreamId(int iceCreamId) {
        return executeUpdate("DELETE FROM recipes WHERE ice_cream_id = ?", ps -> ps.setInt(1, iceCreamId));
    }

    /**
     * Deletes all recipe rows that reference the given ingredient.
     * Needed by RecipeManagementController2 when removing an ingredient.
     */
    public boolean deleteByIngredientId(int ingredientId) {
        return executeUpdate("DELETE FROM recipes WHERE ingredient_id = ?", ps -> ps.setInt(1, ingredientId));
    }

    /**
     * Deletes one joined row by its recipe id.
     */
    public void deleteRow(RecipeRow row) {
        String sql = "DELETE FROM recipes WHERE ice_cream_id = ? AND ingredient_id = ?";

        executeUpdate(sql, ps -> {
            ps.setInt(1, row.getIce_cream_id());
            ps.setInt(2, row.getIngredient_id());
        });
    }

    // --- MAPPING ---
    @Override
    protected Recipe map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}