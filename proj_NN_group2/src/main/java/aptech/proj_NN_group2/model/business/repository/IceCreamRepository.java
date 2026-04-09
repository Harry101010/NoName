package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import aptech.proj_NN_group2.model.IFind;
import aptech.proj_NN_group2.model.business.BaseRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.mapper.IceCreamMapper;
import aptech.proj_NN_group2.util.Database;

public class IceCreamRepository extends BaseRepository<IceCream> implements IFind<IceCream> {
    private final IceCreamMapper mapper = new IceCreamMapper();

    @Override
    public IceCream findById(int id) {
        return findOne("SELECT * FROM ice_creams WHERE ice_cream_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<IceCream> findAll() {
        return find("SELECT * FROM ice_creams ORDER BY ice_cream_name", null);
    }

    public boolean save(IceCream i) {
        String sql = "INSERT INTO ice_creams (ice_cream_name, is_active) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, i.getIce_cream_name());
            ps.setBoolean(2, i.getIs_active());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) i.setIce_cream_id(rs.getInt(1));
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Insert Error: " + e.getMessage());
        }
        return false;
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
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, i.getIce_cream_name());
            ps.setBoolean(2, i.getIs_active());
            ps.setInt(3, i.getIce_cream_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        return executeUpdate("DELETE FROM ice_creams WHERE ice_cream_id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    protected IceCream map(ResultSet rs) throws SQLException {
        return mapper.RowMap(rs);
    }
}

