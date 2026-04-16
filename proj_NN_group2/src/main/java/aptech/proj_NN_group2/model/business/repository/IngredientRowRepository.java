package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IngredientRow;
import aptech.proj_NN_group2.model.interfaces.IFind;
import aptech.proj_NN_group2.model.mapper.IngredientRowMapper;

class IngredientRowRepository extends BaseRepository<IngredientRow> implements IFind<IngredientRow> {

    private static final String BASE_SELECT = """
        SELECT i.ingredient_id, i.ingredient_name, i.origin, i.storage_condition,
               i.unit_id, u.unit_name, i.price_per_unit, i.is_active
        FROM ingredients i
        JOIN units u ON i.unit_id = u.unit_id
        """;

    private final IngredientRowMapper mapper = new IngredientRowMapper();

    @Override
    public IngredientRow findById(int id) {
        return findOne(BASE_SELECT + " WHERE i.ingredient_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<IngredientRow> findAll() {
        return find(BASE_SELECT + " ORDER BY i.ingredient_name", null);
    }

    @Override
    protected IngredientRow map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}