package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import aptech.proj_NN_group2.model.entity.IngredientLot;
import aptech.proj_NN_group2.model.mapper.IngredientLotMapper;
import aptech.proj_NN_group2.util.Database;
import aptech.proj_NN_group2.model.entity.InventorySummary;

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
                SELECT l.*, i.ingredient_name, u.unit_name, s.supplier_name
                FROM ingredient_lots l
                JOIN ingredients i ON l.ingredient_id = i.ingredient_id
                JOIN units u ON i.unit_id = u.unit_id
                LEFT JOIN suppliers s ON l.supplier_id = s.supplier_id
                WHERE l.remaining_quantity > 0
                  AND l.is_deleted = 0
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

    public List<String> getAllIngredientNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT ingredient_name FROM ingredients";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("ingredient_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // NHẬP KHO
    // =========================
    public void importStock(int ingredientId, double quantity, String expiryDate, String supplierName) {

        try (Connection conn = getConnection()) {

            // lấy hoặc tạo supplier
            int supplierId = getOrCreateSupplier(supplierName);

            String sql = """
                    INSERT INTO ingredient_lots
                    (
                        ingredient_id,
                        import_date,
                        expiry_date,
                        received_quantity,
                        remaining_quantity,
                        supplier_id
                    )
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, ingredientId);

            // tự động lấy ngày hiện tại
            ps.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));

            ps.setString(3, expiryDate);
            ps.setDouble(4, quantity);
            ps.setDouble(5, quantity);
            ps.setInt(6, supplierId);

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
                    WHERE ingredient_id = ?
                      AND remaining_quantity > 0
                      AND is_deleted = 0
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

    public int createIngredient(String ingredientName, int unitId) {

        String sql = """
                INSERT INTO ingredients (ingredient_name, unit_id, price_per_unit)
                VALUES (?, ?, 0)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ingredientName);
            ps.setInt(2, unitId);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean exportWithReceipt(int ingredientId, double quantity, int requestDetailId) {

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            String insertReceipt = """
                    INSERT INTO ingredient_export_receipts
                    (ingredient_export_request_id, receipt_status)
                    VALUES (?, N'completed')
                """;

            PreparedStatement psReceipt = conn.prepareStatement(insertReceipt, Statement.RETURN_GENERATED_KEYS);
            psReceipt.setInt(1, 1);
            psReceipt.executeUpdate();

            ResultSet rsKey = psReceipt.getGeneratedKeys();
            rsKey.next();
            int receiptId = rsKey.getInt(1);

            String selectLot = """
                    SELECT * FROM ingredient_lots
                    WHERE ingredient_id = ?
                      AND remaining_quantity > 0
                      AND is_deleted = 0
                    ORDER BY import_date ASC
                """;

            PreparedStatement ps = conn.prepareStatement(selectLot);
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();

            double need = quantity;

            while (rs.next() && need > 0) {

                int lotId = rs.getInt("lot_id");
                double remain = rs.getDouble("remaining_quantity");

                double used = Math.min(remain, need);
                double newRemain = remain - used;

                String insertDetail = """
                        INSERT INTO ingredient_export_receipt_details
                        (ingredient_export_receipt_id, ingredient_export_request_detail_id, lot_id, issued_quantity)
                        VALUES (?, ?, ?, ?)
                    """;

                PreparedStatement psDetail = conn.prepareStatement(insertDetail);
                psDetail.setInt(1, receiptId);
                psDetail.setInt(2, requestDetailId);
                psDetail.setInt(3, lotId);
                psDetail.setDouble(4, used);
                psDetail.executeUpdate();

                String updateLot = """
                        UPDATE ingredient_lots
                        SET remaining_quantity = ?
                        WHERE lot_id = ?
                    """;

                PreparedStatement psUpdate = conn.prepareStatement(updateLot);
                psUpdate.setDouble(1, newRemain);
                psUpdate.setInt(2, lotId);
                psUpdate.executeUpdate();

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

    public int getOrCreateSupplier(String name) {

        String findSql = "SELECT supplier_id FROM suppliers WHERE supplier_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(findSql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("supplier_id");
            }

            // chưa có → tạo mới
            String insertSql = "INSERT INTO suppliers (supplier_name) VALUES (?)";
            PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, name);
            psInsert.executeUpdate();

            ResultSet rsKey = psInsert.getGeneratedKeys();
            rsKey.next();
            return rsKey.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<IngredientLot> findAll() {
        return getAllStock();
    }

    public boolean deleteLot(int lotId) {

        String updateSql = """
                UPDATE ingredient_lots
                SET is_deleted = 1
                WHERE lot_id = ?
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setInt(1, lotId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Không thể xoá nguyên liệu: " + e.getMessage());
        }
    }

    public List<InventorySummary> getSummary() {
        List<InventorySummary> list = new ArrayList<>();

        // FIXED: Sửa thành LEFT JOIN để lấy được cả nguyên liệu có số lượng = 0
        String sql = """
                SELECT 
                    i.ingredient_name,
                    u.unit_name,
                    i.storage_condition,
                    ISNULL(SUM(CASE WHEN l.is_deleted = 0 THEN l.remaining_quantity ELSE 0 END), 0) AS total_stock,
                    MIN(CASE WHEN l.is_deleted = 0 AND l.remaining_quantity > 0 THEN l.expiry_date END) AS nearest_expiry
                FROM ingredients i
                JOIN units u ON i.unit_id = u.unit_id
                LEFT JOIN ingredient_lots l ON i.ingredient_id = l.ingredient_id
                GROUP BY i.ingredient_name, u.unit_name, i.storage_condition
                ORDER BY i.ingredient_name ASC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InventorySummary s = new InventorySummary();
                s.setIngredientName(rs.getString("ingredient_name"));
                s.setUnitName(rs.getString("unit_name"));
                s.setTotalStock(rs.getDouble("total_stock"));
                s.setStorageCondition(rs.getString("storage_condition"));

                if (rs.getDate("nearest_expiry") != null) {
                    s.setNearestExpiry(rs.getDate("nearest_expiry").toLocalDate());
                }

                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<IngredientLot> getLots() {
        List<IngredientLot> list = new ArrayList<>();

        // FIXED: Chuyển điều kiện is_deleted và remaining_quantity vào phần ON của LEFT JOIN
        String sql = """
                SELECT
                    l.lot_id,
                    i.ingredient_name,
                    u.unit_name,
                    s.supplier_name,
                    l.import_date,
                    l.expiry_date,
                    l.remaining_quantity,
                    i.storage_condition
                FROM ingredients i
                JOIN units u ON i.unit_id = u.unit_id
                LEFT JOIN ingredient_lots l ON i.ingredient_id = l.ingredient_id 
                    AND l.is_deleted = 0 
                    AND l.remaining_quantity > 0
                LEFT JOIN suppliers s ON l.supplier_id = s.supplier_id
                ORDER BY l.expiry_date ASC, i.ingredient_name ASC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Chỉ add vào list nếu có lot_id thực sự (tránh hiện dòng trống ở bảng Lots)
                if (rs.getObject("lot_id") == null) continue;

                IngredientLot lot = new IngredientLot();
                lot.setLotId(rs.getInt("lot_id"));
                lot.setIngredientName(rs.getString("ingredient_name"));
                lot.setUnitName(rs.getString("unit_name"));
                lot.setSupplierName(rs.getString("supplier_name"));
                lot.setStorageCondition(rs.getString("storage_condition"));

                if (rs.getDate("import_date") != null) {
                    lot.setImportDate(rs.getDate("import_date").toLocalDate());
                }
                if (rs.getDate("expiry_date") != null) {
                    lot.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                }

                lot.setRemainingQuantity(rs.getDouble("remaining_quantity"));
                list.add(lot);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getAllUnits() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT unit_name FROM units ORDER BY unit_name";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("unit_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int findUnitIdByName(String unitName) {

        String sql = "SELECT unit_id FROM units WHERE unit_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, unitName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("unit_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}