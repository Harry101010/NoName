package aptech.proj_NN_group2.model.business.repository.warehouse;

import java.sql.*;
import java.util.*;
import aptech.proj_NN_group2.model.mapper.IngredientLotMapper;
import aptech.proj_NN_group2.model.entity.warehouse.IngredientLot;
import aptech.proj_NN_group2.util.Database;

public class WarehouseRepository {

    private Connection getConnection() throws Exception {
        return Database.getConnection();
    }

    // =========================
    // LẤY TỒN KHO
    // =========================
    public List<IngredientLot> getAllStock() {
        List<IngredientLot> list = new ArrayList<>();

        String sql = """
            SELECT l.*, i.ingredient_name, u.unit_name
            FROM ingredient_lots l
            JOIN ingredients i ON l.ingredient_id = i.ingredient_id
            JOIN units u ON i.unit_id = u.unit_id
            WHERE l.remaining_quantity > 0
            ORDER BY l.import_date ASC
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(IngredientLotMapper.map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // NHẬP KHO
    // =========================
    public void importStock(int ingredientId, double quantity, String expiryDate) {

        String sql = """
            INSERT INTO ingredient_lots
            (ingredient_id, expiry_date, received_quantity, remaining_quantity)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ps.setString(2, expiryDate);
            ps.setDouble(3, quantity);
            ps.setDouble(4, quantity);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // FIFO XUẤT KHO
    // =========================
    public boolean exportFIFO(int ingredientId, double quantity) {

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            String selectSql = """
                SELECT * FROM ingredient_lots
                WHERE ingredient_id = ? AND remaining_quantity > 0
                ORDER BY import_date ASC
            """;

            PreparedStatement ps = conn.prepareStatement(selectSql);
            ps.setInt(1, ingredientId);

            ResultSet rs = ps.executeQuery();

            double need = quantity;

            while (rs.next() && need > 0) {

                int lotId = rs.getInt("lot_id");
                double remain = rs.getDouble("remaining_quantity");

                double used = Math.min(remain, need);
                double newRemain = remain - used;

                String updateSql = """
                    UPDATE ingredient_lots
                    SET remaining_quantity = ?
                    WHERE lot_id = ?
                """;

                PreparedStatement ups = conn.prepareStatement(updateSql);
                ups.setDouble(1, newRemain);
                ups.setInt(2, lotId);
                ups.executeUpdate();

                need -= used;
            }

            if (need > 0) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // TÌM ID NGUYÊN LIỆU
    // =========================
    public int findIngredientIdByName(String name) {
        String sql = "SELECT ingredient_id FROM ingredients WHERE ingredient_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("ingredient_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}