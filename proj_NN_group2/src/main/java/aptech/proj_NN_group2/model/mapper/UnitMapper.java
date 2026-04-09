package aptech.proj_NN_group2.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import aptech.proj_NN_group2.model.IMapper;
import aptech.proj_NN_group2.model.entity.Unit;

public class UnitMapper implements IMapper<Unit> {
    @Override
    public Unit RowMap(ResultSet rs) throws SQLException {
        Unit u = new Unit();
        u.setUnit_id(rs.getInt("unit_id"));
        u.setUnit_name(rs.getString("unit_name"));
        return u;
    }
}