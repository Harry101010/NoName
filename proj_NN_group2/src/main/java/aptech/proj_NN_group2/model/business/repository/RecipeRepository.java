package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.Recipe;
import aptech.proj_NN_group2.model.entity.RecipeRow;
import aptech.proj_NN_group2.model.interfaces.ICreate;
import aptech.proj_NN_group2.model.interfaces.IDelete;
import aptech.proj_NN_group2.model.interfaces.IFind;
import aptech.proj_NN_group2.model.interfaces.IUpdate;
import aptech.proj_NN_group2.model.mapper.RecipeMapper;
import aptech.proj_NN_group2.util.Database;


public class RecipeRepository extends BaseRepository<Recipe> 
    implements IFind<Recipe>, ICreate<Recipe>, IUpdate<Recipe>, IDelete<Recipe> {
	
    private final RecipeMapper mapper = new RecipeMapper();
    private final RecipeRowRepository recipeRowRepository = new RecipeRowRepository();
    
    // --- CÁC HÀM TÌM KIẾM ---
    @Override
    public Recipe findById(int id) {
        return findOne("SELECT * FROM recipes WHERE recipe_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Recipe> findAll() {
        return find("SELECT * FROM recipes ORDER BY ice_cream_id, ingredient_id", null);
    }

    public List<RecipeRow> findRowsByIceCreamId(int iceCreamId) {
        return recipeRowRepository.findRowsByIceCreamId(iceCreamId);
    }

    // --- CÁC HÀM THÊM/SỬA/XÓA ---
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

    // Hàm update cho UI (cập nhật theo IceCream và Ingredient)
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

    // --- MAPPING ---
    @Override
    protected Recipe map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
    public void deleteRow(RecipeRow row) {
        // SQL này xóa nguyên liệu cụ thể của sản phẩm cụ thể
        String sql = "DELETE FROM recipes WHERE ice_cream_id = ? AND ingredient_id = ?";
        
        executeUpdate(sql, ps -> {
            ps.setInt(1, row.getIce_cream_id());
            ps.setInt(2, row.getIngredient_id());
        });
    }
    public List<Recipe> findAllRows() {
        List<Recipe> list = new ArrayList<>();
        // Câu lệnh SQL lấy tất cả bản ghi
        String sql = "SELECT * FROM recipes"; 

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Recipe recipe = new Recipe();
                
                // Map dữ liệu từ ResultSet vào Object Recipe
                // (Hãy đảm bảo tên cột "..." khớp với trong Database của bạn)
                recipe.setRecipe_id(rs.getInt("recipe_id"));
                recipe.setIce_cream_id(rs.getInt("ice_cream_id"));
                recipe.setIngredient_id(rs.getInt("ingredient_id"));
                recipe.setQuantity(rs.getBigDecimal("quantity"));
                recipe.setUnit(rs.getString("unit"));
                
                list.add(recipe);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách recipes: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public boolean deleteByIngredientId(int ingredientId) {
        String sql = "DELETE FROM recipes WHERE ingredient_id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Thiết lập tham số cho dấu chấm hỏi (?)
            ps.setInt(1, ingredientId);
            
            // executeUpdate trả về số dòng bị ảnh hưởng
            int rowsAffected = ps.executeUpdate();
            
            // Trả về true nếu có dòng được xóa
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa recipe theo ingredient_id: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}