package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import aptech.proj_NN_group2.model.IFind;
import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.Ingredient;
import aptech.proj_NN_group2.model.entity.IngredientRow;
import aptech.proj_NN_group2.model.mapper.IngredientMapper;
import aptech.proj_NN_group2.model.mapper.IngredientRowMapper;
import aptech.proj_NN_group2.util.Database;

public class IngredientRepository extends BaseRepository<Ingredient> implements IFind<Ingredient> {
    private final IngredientMapper mapper = new IngredientMapper();
    private final IngredientRowMapper rowMapper = new IngredientRowMapper();

    @Override
    public Ingredient findById(int id) {
        return findOne("SELECT * FROM ingredients WHERE ingredient_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Ingredient> findAll() {
        return find("SELECT * FROM ingredients ORDER BY ingredient_name", null);
    }

    public List<IngredientRow> findAllRows() {
        String sql = """
                SELECT i.ingredient_id, i.ingredient_name, i.origin, i.storage_condition,
                       i.unit_id, u.unit_name, i.price_per_unit, i.is_active
                FROM ingredients i
                JOIN units u ON i.unit_id = u.unit_id
                ORDER BY i.ingredient_name
                """;
        return findRows(sql, null);
    }

    private List<IngredientRow> findRows(String sql, Binder binder) {
        java.util.ArrayList<IngredientRow> list = new java.util.ArrayList<>();
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

    public boolean save(Ingredient i) {
        String sql = """
                INSERT INTO ingredients
                (ingredient_name, origin, storage_condition, unit_id, price_per_unit, is_active)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, i.getIngredient_name());
            ps.setString(2, i.getOrigin());
            ps.setString(3, i.getStorage_condition());
            ps.setInt(4, i.getUnit_id());
            ps.setDouble(5, i.getPrice_per_unit());
            ps.setBoolean(6, i.getIs_active());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) i.setIngredient_id(rs.getInt(1));
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Insert Error: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Ingredient i) {
        String sql = """
                UPDATE ingredients
                SET ingredient_name = ?, origin = ?, storage_condition = ?, unit_id = ?, price_per_unit = ?, is_active = ?
                WHERE ingredient_id = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, i.getIngredient_name());
            ps.setString(2, i.getOrigin());
            ps.setString(3, i.getStorage_condition());
            ps.setInt(4, i.getUnit_id());
            ps.setDouble(5, i.getPrice_per_unit());
            ps.setBoolean(6, i.getIs_active());
            ps.setInt(7, i.getIngredient_id());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        return executeUpdate("DELETE FROM ingredients WHERE ingredient_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    protected Ingredient map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}