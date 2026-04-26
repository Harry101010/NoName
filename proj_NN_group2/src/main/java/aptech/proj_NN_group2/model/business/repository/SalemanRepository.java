package aptech.proj_NN_group2.model.business.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import aptech.proj_NN_group2.model.entity.FinishedProductInventory;
import aptech.proj_NN_group2.model.entity.IssueNote;
import aptech.proj_NN_group2.model.entity.ProductIssueDetail;
import aptech.proj_NN_group2.util.CurrentUser;
import aptech.proj_NN_group2.util.Database;

public class SalemanRepository {

    private static final String NOTE_STATUS_PENDING = "Chờ duyệt";
    private static final String NOTE_STATUS_APPROVED = "Đã duyệt";
    private static final String NOTE_STATUS_REJECTED = "Từ chối";

    public boolean createFullIssueNote(IssueNote note, List<ProductIssueDetail> details) {
        if (note == null) {
            throw new IllegalArgumentException("note must not be null");
        }
        if (note.getDeliveryDate() == null) {
            throw new IllegalArgumentException("deliveryDate must not be null");
        }
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("details must not be empty");
        }

        String sqlNote = """
            INSERT INTO product_issue_notes
            (saleman_id, customer_name, customer_order_code, delivery_date, status, create_date)
            VALUES (?, ?, ?, ?, ?, GETDATE())
            """;
        String sqlDetail = """
            INSERT INTO product_issue_details (note_id, ice_cream_id, quantity)
            VALUES (?, ?, ?)
            """;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            int salemanId = CurrentUser.requireUser().getUserId();
            int generatedNoteId;

            try (PreparedStatement psNote = conn.prepareStatement(sqlNote, Statement.RETURN_GENERATED_KEYS)) {
                psNote.setInt(1, salemanId);
                psNote.setString(2, note.getCustomerName());
                psNote.setString(3, note.getCustomerOrderCode());
                psNote.setTimestamp(4, Timestamp.valueOf(note.getDeliveryDate()));
                psNote.setString(5, NOTE_STATUS_PENDING);

                int inserted = psNote.executeUpdate();
                if (inserted == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet rs = psNote.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    generatedNoteId = rs.getInt(1);
                }
            }

            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                for (ProductIssueDetail item : details) {
                    psDetail.setInt(1, generatedNoteId);
                    psDetail.setInt(2, item.getIceCreamId());
                    psDetail.setDouble(3, item.getQuantity());
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException | IllegalStateException e) {
            rollbackQuietly(conn);
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(conn);
        }
    }

    public List<IssueNote> findAllNotes() {
        List<IssueNote> list = new ArrayList<>();
        String sql = """
            SELECT n.note_id, n.customer_order_code, n.customer_name, n.status,
                   n.create_date, n.delivery_date, u.username AS saleman_name,
                   i.ice_cream_name AS product_name, d.quantity
            FROM product_issue_notes n
            JOIN users u ON n.saleman_id = u.user_id
            JOIN product_issue_details d ON n.note_id = d.note_id
            JOIN ice_creams i ON d.ice_cream_id = i.ice_cream_id
            ORDER BY n.create_date DESC, n.note_id DESC
            """;

        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                IssueNote n = new IssueNote();
                n.setNoteId(rs.getInt("note_id"));
                n.setCustomerOrderCode(rs.getString("customer_order_code"));
                n.setCustomerName(rs.getString("customer_name"));
                n.setSalemanName(rs.getString("saleman_name"));
                n.setStatus(rs.getString("status"));
                n.setProductName(rs.getString("product_name"));
                n.setQuantity(rs.getDouble("quantity"));

                Timestamp created = rs.getTimestamp("create_date");
                Timestamp delivery = rs.getTimestamp("delivery_date");
                if (created != null) {
                    n.setCreateDate(created.toLocalDateTime());
                }
                if (delivery != null) {
                    n.setDeliveryDate(delivery.toLocalDateTime());
                }

                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<FinishedProductInventory> getFinishedInventory() {
        List<FinishedProductInventory> list = new ArrayList<>();
        String sql = """
            SELECT inventory_id, production_po_code, product_name, current_quantity,
                   mfg_date, exp_date, storage_location
            FROM finished_product_inventory
            ORDER BY exp_date ASC
            """;

        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                FinishedProductInventory item = new FinishedProductInventory();
                item.setInventoryId(rs.getInt("inventory_id"));
                item.setPoCode(rs.getString("production_po_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getDouble("current_quantity"));

                Timestamp mfg = rs.getTimestamp("mfg_date");
                Timestamp exp = rs.getTimestamp("exp_date");
                if (mfg != null) {
                    item.setMfgDate(mfg.toLocalDateTime());
                }
                if (exp != null) {
                    item.setExpDate(exp.toLocalDateTime());
                }

                item.setLocation(rs.getString("storage_location"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean approveIssueNote(int noteId) {
        return executeStatusUpdate("UPDATE product_issue_notes SET status = ? WHERE note_id = ?",
                noteId, NOTE_STATUS_APPROVED);
    }

    public boolean rejectIssueNote(int noteId) {
        return executeStatusUpdate("UPDATE product_issue_notes SET status = ? WHERE note_id = ?",
                noteId, NOTE_STATUS_REJECTED);
    }

    public List<String> getActiveIceCreams() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT ice_cream_id, ice_cream_name FROM ice_creams WHERE is_active = 1";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getInt("ice_cream_id") + " - " + rs.getString("ice_cream_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteNote(int noteId) {
        String sqlDetail = "DELETE FROM product_issue_details WHERE note_id = ?";
        String sqlNote = "DELETE FROM product_issue_notes WHERE note_id = ?";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psD = conn.prepareStatement(sqlDetail);
                 PreparedStatement psN = conn.prepareStatement(sqlNote)) {
                psD.setInt(1, noteId);
                int detailRows = psD.executeUpdate();

                psN.setInt(1, noteId);
                int noteRows = psN.executeUpdate();

                if (detailRows == 0 || noteRows == 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                rollbackQuietly(conn);
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(conn);
        }
    }

    public boolean updateIssueNote(IssueNote note) {
        if (note == null) {
            throw new IllegalArgumentException("note must not be null");
        }
        if (note.getDeliveryDate() == null) {
            throw new IllegalArgumentException("deliveryDate must not be null");
        }

        String sqlNote = "UPDATE product_issue_notes SET customer_name = ?, customer_order_code = ?, delivery_date = ? WHERE note_id = ?";
        String sqlDetail = "UPDATE product_issue_details SET quantity = ? WHERE note_id = ? AND ice_cream_id = (SELECT ice_cream_id FROM ice_creams WHERE ice_cream_name = ?)";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            int noteRows;
            try (PreparedStatement psNote = conn.prepareStatement(sqlNote)) {
                psNote.setString(1, note.getCustomerName());
                psNote.setString(2, note.getCustomerOrderCode());
                psNote.setTimestamp(3, Timestamp.valueOf(note.getDeliveryDate()));
                psNote.setInt(4, note.getNoteId());
                noteRows = psNote.executeUpdate();
            }

            int detailRows;
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                psDetail.setDouble(1, note.getQuantity() != null ? note.getQuantity() : 0d);
                psDetail.setInt(2, note.getNoteId());
                psDetail.setString(3, note.getProductName());
                detailRows = psDetail.executeUpdate();
            }

            if (noteRows == 0 || detailRows == 0) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(conn);
        }
    }

    private boolean executeStatusUpdate(String sql, int noteId, String status) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, noteId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}