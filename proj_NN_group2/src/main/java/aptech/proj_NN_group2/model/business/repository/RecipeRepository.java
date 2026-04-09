package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.Recipe;
import aptech.proj_NN_group2.model.entity.RecipeRow;
import aptech.proj_NN_group2.model.mapper.RecipeMapper;
import aptech.proj_NN_group2.model.mapper.RecipeRowMapper;
import aptech.proj_NN_group2.util.Database;
import aptech.proj_NN_group2.model.IFind;

public class RecipeRepository extends BaseRepository<Recipe> implements IFind<Recipe> {
    private final RecipeMapper mapper = new RecipeMapper();
    private final RecipeRowMapper rowMapper = new RecipeRowMapper();

    @Override
    public Recipe findById(int id) {
        return findOne("SELECT * FROM recipes WHERE recipe_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Recipe> findAll() {
        return find("SELECT * FROM recipes ORDER BY ice_cream_id, ingredient_id", null);
    }

    public List<RecipeRow> findAllRows() {
        String sql = """
                SELECT r.recipe_id, r.ice_cream_id, ic.ice_cream_name,
                       r.ingredient_id, i.ingredient_name, u.unit_name, r.quantity_per_kg
                FROM recipes r
                JOIN ice_creams ic ON r.ice_cream_id = ic.ice_cream_id
                JOIN ingredients i ON r.ingredient_id = i.ingredient_id
                JOIN units u ON i.unit_id = u.unit_id
                ORDER BY ic.ice_cream_name, i.ingredient_name
                """;
        return findRows(sql, null);
    }

    public List<RecipeRow> findRowsByIceCreamId(int iceCreamId) {
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
        return findRows(sql, ps -> ps.setInt(1, iceCreamId));
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

    public boolean save(Recipe r) {
        String sql = """
                INSERT INTO recipes (ice_cream_id, ingredient_id, quantity_per_kg)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getIce_cream_id());
            ps.setInt(2, r.getIngredient_id());
            ps.setDouble(3, r.getQuantity_per_kg());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) r.setRecipe_id(rs.getInt(1));
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Insert Error: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Recipe r) {
        String sql = """
                UPDATE recipes
                SET ice_cream_id = ?, ingredient_id = ?, quantity_per_kg = ?
                WHERE recipe_id = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, r.getIce_cream_id());
            ps.setInt(2, r.getIngredient_id());
            ps.setDouble(3, r.getQuantity_per_kg());
            ps.setInt(4, r.getRecipe_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Error: " + e.getMessage());
        }
        return false;
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

    private List<RecipeRow> findRows(String sql, Binder binder) {
        List<RecipeRow> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (binder != null) binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rowMapper.RowMap(rs));
            }
        } catch (SQLException e) {
            System.err.println("Query Error: " + e.getMessage());
        }
        return list;
    }

    @Override
    protected Recipe map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}