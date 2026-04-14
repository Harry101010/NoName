package aptech.proj_NN_group2.model.business.repository.saleman;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import aptech.proj_NN_group2.model.entity.saleman.FinishedProductInventory;
import aptech.proj_NN_group2.model.entity.saleman.IssueNote;
import aptech.proj_NN_group2.model.entity.saleman.ProductIssueDetail;
import aptech.proj_NN_group2.util.Database;

public class SalemanRepository {

    public boolean createFullIssueNote(IssueNote note, List<ProductIssueDetail> details) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            String sqlNote = "INSERT INTO product_issue_notes (saleman_id, customer_name, customer_order_code, delivery_date, status, create_date) " +
                             "VALUES (?, ?, ?, ?, N'Chờ duyệt', GETDATE())";
            
            PreparedStatement psNote = conn.prepareStatement(sqlNote, Statement.RETURN_GENERATED_KEYS);
            psNote.setInt(1, 1); // Giả định ID user là 1
            psNote.setString(2, note.getCustomerName());
            psNote.setString(3, note.getCustomerOrderCode());
            psNote.setTimestamp(4, Timestamp.valueOf(note.getDeliveryDate()));
            
            psNote.executeUpdate();

            ResultSet rs = psNote.getGeneratedKeys();
            int generatedNoteId = 0;
            if (rs.next()) {
                generatedNoteId = rs.getInt(1);
            }

            if (generatedNoteId > 0) {
                String sqlDetail = "INSERT INTO product_issue_details (note_id, ice_cream_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                
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
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public List<IssueNote> findAllNotes() {
        List<IssueNote> list = new ArrayList<>();
        String sql = "SELECT n.note_id, n.customer_order_code, n.customer_name, n.status, " +
                     "n.create_date, n.delivery_date, u.username as saleman_name, " +
                     "i.ice_cream_name as product_name, d.quantity " +
                     "FROM product_issue_notes n " +
                     "JOIN users u ON n.saleman_id = u.user_id " +
                     "JOIN product_issue_details d ON n.note_id = d.note_id " +
                     "JOIN ice_creams i ON d.ice_cream_id = i.ice_cream_id " +
                     "ORDER BY n.create_date DESC, n.note_id DESC";
        
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
                
                if (rs.getTimestamp("create_date") != null) n.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
                if (rs.getTimestamp("delivery_date") != null) n.setDeliveryDate(rs.getTimestamp("delivery_date").toLocalDateTime());
                list.add(n);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<FinishedProductInventory> getFinishedInventory() {
        List<FinishedProductInventory> list = new ArrayList<>();
        String sql = "SELECT inventory_id, production_po_code, product_name, current_quantity, mfg_date, exp_date, storage_location " +
                     "FROM finished_product_inventory ORDER BY exp_date ASC";

        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                FinishedProductInventory item = new FinishedProductInventory();
                item.setInventoryId(rs.getInt("inventory_id"));
                item.setPoCode(rs.getString("production_po_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getDouble("current_quantity"));
                
                if (rs.getTimestamp("mfg_date") != null) item.setMfgDate(rs.getTimestamp("mfg_date").toLocalDateTime());
                if (rs.getTimestamp("exp_date") != null) item.setExpDate(rs.getTimestamp("exp_date").toLocalDateTime());
                
                item.setLocation(rs.getString("storage_location"));
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean approveIssueNote(int noteId) {
        String sql = "UPDATE product_issue_notes SET status = N'Đã duyệt' WHERE note_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean rejectIssueNote(int noteId) {
        String sql = "UPDATE product_issue_notes SET status = N'Từ chối' WHERE note_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
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
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteNote(int noteId) {
        String sqlDetail = "DELETE FROM product_issue_details WHERE note_id = ?";
        String sqlNote = "DELETE FROM product_issue_notes WHERE note_id = ?";
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psD = conn.prepareStatement(sqlDetail);
                 PreparedStatement psN = conn.prepareStatement(sqlNote)) {
                psD.setInt(1, noteId);
                psD.executeUpdate();
                psN.setInt(1, noteId);
                int res = psN.executeUpdate();
                conn.commit();
                return res > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // =========================================================
    // HÀM ĐÃ ĐƯỢC SỬA: Cập nhật thông tin trên cả 2 bảng
    // =========================================================
    public boolean updateIssueNote(IssueNote note) {
        String sqlNote = "UPDATE product_issue_notes SET customer_name = ?, customer_order_code = ?, delivery_date = ? WHERE note_id = ?";
        String sqlDetail = "UPDATE product_issue_details SET quantity = ? WHERE note_id = ? AND ice_cream_id = (SELECT ice_cream_id FROM ice_creams WHERE ice_cream_name = ?)";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật thông tin phiếu (khách hàng, ngày tháng...)
            try (PreparedStatement psNote = conn.prepareStatement(sqlNote)) {
                psNote.setString(1, note.getCustomerName());
                psNote.setString(2, note.getCustomerOrderCode());
                psNote.setTimestamp(3, Timestamp.valueOf(note.getDeliveryDate()));
                psNote.setInt(4, note.getNoteId());
                psNote.executeUpdate();
            }

            // 2. Cập nhật số lượng sản phẩm
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                psDetail.setDouble(1, note.getQuantity());
                psDetail.setInt(2, note.getNoteId());
                psDetail.setString(3, note.getProductName()); // Truy vấn ngược lại id dựa theo tên
                psDetail.executeUpdate();
            }

            conn.commit(); // Thành công cả 2 lệnh thì lưu
            return true;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }
}