package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.ICreate;
import aptech.proj_NN_group2.model.IDelete;
import aptech.proj_NN_group2.model.IFind;
import aptech.proj_NN_group2.model.IUpdate;
import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.Ingredient;
import aptech.proj_NN_group2.model.entity.IngredientRow;
import aptech.proj_NN_group2.model.mapper.IngredientMapper;

public class IngredientRepository extends BaseRepository<Ingredient> 
	implements IFind<Ingredient>, ICreate<Ingredient>, IUpdate<Ingredient>, IDelete<Ingredient> {
    
	private final IngredientMapper mapper = new IngredientMapper();
    private final IngredientRowRepository ingredientRowRepository = new IngredientRowRepository();

    @Override
    public Ingredient findById(int id) {
        return findOne("SELECT * FROM ingredients WHERE ingredient_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Ingredient> findAll() {
        return find("SELECT * FROM ingredients ORDER BY ingredient_name", null);
    }

    public List<IngredientRow> findAllRows() {
        return ingredientRowRepository.findAll();
    }

    public boolean save(Ingredient i) {
        String sql = """
                INSERT INTO ingredients
                (ingredient_name, origin, storage_condition, unit_id, price_per_unit, is_active)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        return executeUpdate(
        	sql,
        	ps -> {
        		ps.setString(1, i.getIngredient_name());
        		ps.setString(2, i.getOrigin());
        		ps.setString(3, i.getStorage_condition());
        		ps.setInt(4, i.getUnit_id());
        		ps.setDouble(5, i.getPrice_per_unit());
        		ps.setBoolean(6, i.getIs_active());        		
        	}
        );
    }

    public boolean update(Ingredient i) {
        String sql = """
	        UPDATE ingredients
	        SET ingredient_name = ?, origin = ?, storage_condition = ?, unit_id = ?, price_per_unit = ?, is_active = ?
	        WHERE ingredient_id = ?
        """;
        return executeUpdate(
        	sql, 
        	ps -> {
        		ps.setString(1, i.getIngredient_name());
        		ps.setString(2, i.getOrigin());
        		ps.setString(3, i.getStorage_condition());
        		ps.setInt(4, i.getUnit_id());
        		ps.setDouble(5, i.getPrice_per_unit());
        		ps.setBoolean(6, i.getIs_active());
        		ps.setInt(7, i.getIngredient_id());
        	}
        );
    }

    public boolean delete(int id) {
        return executeUpdate("DELETE FROM ingredients WHERE ingredient_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    protected Ingredient map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}