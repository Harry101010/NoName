package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.interfaces.ICreate;
import aptech.proj_NN_group2.model.interfaces.IDelete;
import aptech.proj_NN_group2.model.interfaces.IFind;
import aptech.proj_NN_group2.model.interfaces.IUpdate;
import aptech.proj_NN_group2.model.mapper.IceCreamMapper;

public class IceCreamRepository extends BaseRepository<IceCream> 
	implements IFind<IceCream>, ICreate<IceCream>, IUpdate<IceCream>, IDelete<IceCream> {
	
	private final IceCreamMapper mapper = new IceCreamMapper();

    @Override
    public IceCream findById(int id) {
        return findOne("SELECT * FROM ice_creams WHERE ice_cream_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<IceCream> findAll() {
        return find("SELECT * FROM ice_creams ORDER BY ice_cream_name", null);
    }

    public boolean create(IceCream i) {
    	return executeUpdate(
			"INSERT INTO ice_creams (ice_cream_name, is_active) VALUES (?, ?)",
			ps -> {
				ps.setString(1, i.getIce_cream_name());
	            ps.setBoolean(2, i.getIs_active());
			}
    	);
    }

    public List<IceCream> findAllActive() {
    	return find("SELECT * FROM ice_creams WHERE is_active = 1 ORDER BY ice_cream_name", null);
    }

    public boolean update(IceCream i) {
        String sql = """
                UPDATE ice_creams
                SET ice_cream_name = ?, is_active = ?
                WHERE ice_cream_id = ?
        """;

    	return executeUpdate(
			sql,
			ps -> {
				ps.setString(1, i.getIce_cream_name());
	            ps.setBoolean(2, i.getIs_active());
	            ps.setInt(3, i.getIce_cream_id());
			}
    	);
    }

    public boolean delete(int id) {
        return executeUpdate("DELETE FROM ice_creams WHERE ice_cream_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    protected IceCream map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}