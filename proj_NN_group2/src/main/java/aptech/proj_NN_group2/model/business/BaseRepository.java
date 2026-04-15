package aptech.proj_NN_group2.model.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import aptech.proj_NN_group2.util.Database;

public abstract class BaseRepository<T> {

    @FunctionalInterface
    public interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    protected abstract T map(ResultSet rs) throws SQLException;

    public T findOne(String sql, Binder binder) {
        try (Connection conn = Database.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (binder != null) binder.bind(ps);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("Query Error: " + e.getMessage());
        }
        return null;
    }

    public List<T> find(String sql, Binder binder) {
        List<T> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (binder != null) binder.bind(ps);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("Query Error: " + e.getMessage());
        }
        return result;
    }

    public boolean executeUpdate(String sql, Binder binder) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (binder != null) binder.bind(ps);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Update/Insert/Delete Error: " + e.getMessage());
        }
        return false;
    }

    protected int count(String sql, Binder binder) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (binder != null) binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Count Error: " + e.getMessage());
        }
        return 0;
    }
}