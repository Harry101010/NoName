package aptech.proj_NN_group2.model.business.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.Unit;
import aptech.proj_NN_group2.model.interfaces.IFind;
import aptech.proj_NN_group2.model.mapper.UnitMapper;

public class UnitRepository extends BaseRepository<Unit> implements IFind<Unit> {
    private final UnitMapper mapper = new UnitMapper();

    @Override
    public Unit findById(int id) {
        return findOne("SELECT * FROM units WHERE unit_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Unit> findAll() {
        return find("SELECT * FROM units ORDER BY unit_name", null);
    }

    @Override
    protected Unit map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}