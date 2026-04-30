package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IngredientRow;
import aptech.proj_NN_group2.model.entity.ingredient.Ingredient;
import aptech.proj_NN_group2.model.interfaces.ICreate;
import aptech.proj_NN_group2.model.interfaces.IDelete;
import aptech.proj_NN_group2.model.interfaces.IFind;
import aptech.proj_NN_group2.model.interfaces.IUpdate;
import aptech.proj_NN_group2.model.mapper.IngredientMapper;

public class IngredientRepository extends BaseRepository<Ingredient> 
    implements IFind<Ingredient>, ICreate<Ingredient>, IUpdate<Ingredient>, IDelete<Ingredient> {
    
    private final IngredientMapper mapper = new IngredientMapper();
    private final IngredientRowRepository ingredientRowRepository = new IngredientRowRepository();

    @Override
    public Ingredient findById(int id) {
        // Cập nhật SQL: Join với bảng units để lấy unit_name
        String sql = """
                     SELECT i.*, u.unit_name 
                     FROM ingredients i 
                     LEFT JOIN units u ON i.unit_id = u.unit_id 
                     WHERE i.ingredient_id = ?
                     """;
        return findOne(sql, ps -> ps.setInt(1, id));
    }

    @Override
    public List<Ingredient> findAll() {
        // Cập nhật SQL: Join với bảng units để lấy unit_name
        String sql = """
                     SELECT i.*, u.unit_name 
                     FROM ingredients i 
                     LEFT JOIN units u ON i.unit_id = u.unit_id 
                     ORDER BY i.ingredient_name
                     """;
        return find(sql, null);
    }

    public List<IngredientRow> findAllRows() {
        return ingredientRowRepository.findAll();
    }

    public boolean create(Ingredient i) {
        String sql = """
                INSERT INTO ingredients
                (ingredient_name, origin, storage_condition, unit_id, price_per_unit, is_active)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        return executeUpdate(sql, ps -> {
            ps.setString(1, i.getIngredient_name());
            ps.setString(2, i.getOrigin());
            ps.setString(3, i.getStorage_condition());
            ps.setInt(4, i.getUnit_id());
            ps.setDouble(5, i.getPrice_per_unit());
            ps.setBoolean(6, i.isIs_active());       
        });
    }

    public boolean update(Ingredient i) {
        String sql = """
            UPDATE ingredients
            SET ingredient_name = ?, origin = ?, storage_condition = ?, unit_id = ?, price_per_unit = ?, is_active = ?
            WHERE ingredient_id = ?
        """;
        return executeUpdate(sql, ps -> {
            ps.setString(1, i.getIngredient_name());
            ps.setString(2, i.getOrigin());
            ps.setString(3, i.getStorage_condition());
            ps.setInt(4, i.getUnit_id());
            ps.setDouble(5, i.getPrice_per_unit());
            ps.setBoolean(6, i.isIs_active());
            ps.setInt(7, i.getIngredient_id());
        });
    }

    public boolean delete(int id) {
        return executeUpdate("DELETE FROM ingredients WHERE ingredient_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    protected Ingredient map(ResultSet rs) throws SQLException {
        // 1. Gọi mapper cũ để map các trường cơ bản của Ingredient
        Ingredient i = mapper.RowMap(rs);
        
        // 2. Gán thêm trường unit_name từ kết quả JOIN
        try {
            i.setUnit_name(rs.getString("unit_name"));
        } catch (SQLException e) {
            // Nếu không tìm thấy cột unit_name (trường hợp gọi ở nơi không JOIN), bỏ qua
            i.setUnit_name("N/A");
        }
        
        return i;
    }
 // Trong IngredientRepository.java
    public Ingredient findByName(String name) {
        String sql = "SELECT * FROM ingredients WHERE ingredient_name = ?";
        List<Ingredient> list = find(sql, ps -> ps.setString(1, name));
        return list.isEmpty() ? null : list.get(0);
    }
}