package aptech.proj_NN_group2.model.business.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import aptech.proj_NN_group2.model.entity.IssueNote;
import aptech.proj_NN_group2.model.entity.ProductIssueDetail;
import aptech.proj_NN_group2.model.entity.sales.DispatchHistory;
import aptech.proj_NN_group2.model.entity.sales.FinishedProductInventory;
import aptech.proj_NN_group2.util.Database;

public class SalemanRepository {

    // --- PHẦN 1: TẠO PHIẾU YÊU CẦU (SALES) ---

    /**
     * Tạo phiếu yêu cầu đơn lẻ từ Tab 2
     */
    public boolean createIssueRequest(String customer, String product, double qty, String notes) {
        // Sử dụng GETDATE() để DB tự lấy thời gian hiện tại chính xác đến giây
        String sqlReq = "INSERT INTO issue_requests (customer_name, request_date, status, notes) VALUES (?, GETDATE(), 'Pending', ?)";
        String sqlDetail = "INSERT INTO issue_request_details (request_id, product_name, quantity_requested) VALUES (?, ?, ?)";
        
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psReq = conn.prepareStatement(sqlReq, Statement.RETURN_GENERATED_KEYS)) {
                psReq.setString(1, customer);
                psReq.setString(2, notes);
                psReq.executeUpdate();
                
                ResultSet rs = psReq.getGeneratedKeys();
                if (rs.next()) {
                    int reqId = rs.getInt(1);
                    try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                        psDetail.setInt(1, reqId);
                        psDetail.setString(2, product);
                        psDetail.setDouble(3, qty);
                        psDetail.executeUpdate();
                    }
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tạo phiếu yêu cầu nhiều mặt hàng (Dùng Batch Processing)
     */
    public boolean createFullIssueNote(IssueNote note, List<ProductIssueDetail> details) {
        String sqlNote = "INSERT INTO issue_requests (customer_name, request_date, status) VALUES (?, GETDATE(), 'Pending')";
        String sqlDetail = "INSERT INTO issue_request_details (request_id, product_name, quantity_requested) VALUES (?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            int generatedNoteId;
            try (PreparedStatement psNote = conn.prepareStatement(sqlNote, Statement.RETURN_GENERATED_KEYS)) {
                psNote.setString(1, note.getCustomerName());
                psNote.executeUpdate();
                ResultSet rs = psNote.getGeneratedKeys();
                if (rs.next()) {
                    generatedNoteId = rs.getInt(1);
                } else throw new SQLException("ID failed.");
            }

            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                for (ProductIssueDetail item : details) {
                    psDetail.setInt(1, generatedNoteId);
                    psDetail.setString(2, item.getProductName());
                    psDetail.setDouble(3, item.getQuantity());
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    // --- PHẦN 2: LẤY DỮ LIỆU HIỂN THỊ (QUẢN LÝ PHIẾU) ---

    /**
     * Lấy tất cả yêu cầu (Dùng cho bảng lịch sử của Sales ở Tab 2)
     */
    public List<IssueNote> getAllMyRequests() {
        List<IssueNote> list = new ArrayList<>();
        String sql = "SELECT r.request_id, r.customer_name, d.product_name, d.quantity_requested, r.status, r.request_date " +
                "FROM issue_requests r " +
                "JOIN issue_request_details d ON r.request_id = d.request_id " +
                "ORDER BY r.request_date DESC"; // Mới nhất hiện lên đầu
        try (Connection conn = Database.getConnection(); 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                IssueNote n = new IssueNote();
                n.setNoteId(rs.getInt("request_id"));
                n.setCustomerName(rs.getString("customer_name"));
                n.setProductName(rs.getString("product_name"));
                n.setQuantity(rs.getDouble("quantity_requested"));
                n.setStatus(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("request_date");
                if (ts != null) n.setRequestDate(ts.toLocalDateTime());
                list.add(n);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Lấy danh sách phiếu chờ duyệt (Tab 3)
     */
    public List<IssueNote> getPendingRequests() {
        List<IssueNote> list = new ArrayList<>();
        String sql = "SELECT r.request_id, r.customer_name, d.product_name, d.quantity_requested, r.status, r.request_date " +
                     "FROM issue_requests r JOIN issue_request_details d ON r.request_id = d.request_id " +
                     "WHERE r.status = 'Pending' ORDER BY r.request_date ASC";
        try (Connection conn = Database.getConnection(); 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                IssueNote n = new IssueNote();
                n.setNoteId(rs.getInt("request_id"));
                n.setCustomerName(rs.getString("customer_name"));
                n.setProductName(rs.getString("product_name"));
                n.setQuantity(rs.getDouble("quantity_requested"));
                n.setStatus(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("request_date");
                if (ts != null) n.setRequestDate(ts.toLocalDateTime());
                list.add(n);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- PHẦN 3: XỬ LÝ KHO (THỦ KHO & FIFO) ---

    public boolean processApprovalFIFO(int reqId, String quality, String note) throws Exception {
        String sqlGetDetail = "SELECT product_name, quantity_requested, customer_name FROM issue_requests r JOIN issue_request_details d ON r.request_id = d.request_id WHERE r.request_id = ?";
        String sqlGetStock = "SELECT production_order_id, current_quantity, mfg_date, exp_date FROM finished_product_inventory WHERE product_name = ? AND current_quantity > 0 ORDER BY mfg_date ASC";
        
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement psDetail = conn.prepareStatement(sqlGetDetail);
            psDetail.setInt(1, reqId);
            ResultSet rsDetail = psDetail.executeQuery();
            
            if (rsDetail.next()) {
                String product = rsDetail.getString("product_name");
                double remaining = rsDetail.getDouble("quantity_requested");
                String customer = rsDetail.getString("customer_name");
                
                PreparedStatement psStock = conn.prepareStatement(sqlGetStock);
                psStock.setString(1, product);
                ResultSet rsStock = psStock.executeQuery();
                
                while (rsStock.next() && remaining > 0) {
                    int orderId = rsStock.getInt("production_order_id");
                    double available = rsStock.getDouble("current_quantity");
                    double take = Math.min(available, remaining);
                    
                    // Trừ kho
                    PreparedStatement psUp = conn.prepareStatement("UPDATE finished_product_inventory SET current_quantity = current_quantity - ? WHERE production_order_id = ?");
                    psUp.setDouble(1, take); psUp.setInt(2, orderId); psUp.executeUpdate();
                    
                    // Ghi lịch sử (Dùng GETDATE() cho ngày xuất)
                    PreparedStatement psHis = conn.prepareStatement("INSERT INTO dispatch_history (request_id, production_order_id, product_name, quantity_issued, mfg_date, exp_date, dispatch_date, customer_name, quality_status, notes) VALUES (?,?,?,?,?,?,GETDATE(),?,?,?)");
                    psHis.setInt(1, reqId); psHis.setInt(2, orderId); psHis.setString(3, product); psHis.setDouble(4, take);
                    psHis.setTimestamp(5, rsStock.getTimestamp("mfg_date")); psHis.setTimestamp(6, rsStock.getTimestamp("exp_date"));
                    psHis.setString(7, customer); psHis.setString(8, quality); psHis.setString(9, note); 
                    psHis.executeUpdate();
                    
                    remaining -= take;
                }
                if (remaining > 0) { conn.rollback(); throw new Exception("Insufficient stock!"); }
                updateRequestStatus(reqId, "Approved");
                conn.commit(); 
                return true;
            }
        } catch (Exception e) { throw e; }
        return false;
    }

    // --- PHẦN 4: LỊCH SỬ XUẤT KHO (TAB 4) ---

    public List<DispatchHistory> getHistory() {
        List<DispatchHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM dispatch_history ORDER BY dispatch_date DESC";
        try (Connection conn = Database.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                DispatchHistory h = new DispatchHistory();
                h.setOrderId(rs.getInt("production_order_id"));
                h.setProductName(rs.getString("product_name"));
                h.setQuantity(rs.getDouble("quantity_issued"));
                
                Timestamp tsMfg = rs.getTimestamp("mfg_date");
                if (tsMfg != null) h.setMfgDate(tsMfg.toLocalDateTime());
                
                h.setCustomerName(rs.getString("customer_name"));
                h.setQualityStatus(rs.getString("quality_status"));
                h.setNotes(rs.getString("notes"));
                
                Timestamp tsDisp = rs.getTimestamp("dispatch_date");
                if (tsDisp != null) h.setDispatchDate(tsDisp.toLocalDateTime());
                
                list.add(h);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- PHẦN 5: CÁC TIỆN ÍCH KHÁC ---

    public void updateRequestStatus(int id, String status) {
        try (Connection conn = Database.getConnection(); 
             PreparedStatement ps = conn.prepareStatement("UPDATE issue_requests SET status = ? WHERE request_id = ?")) {
            ps.setString(1, status); ps.setInt(2, id); ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<String> getActiveIceCreams() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT ice_cream_name FROM ice_creams WHERE is_active = 1";
        try (Connection conn = Database.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) { list.add(rs.getString("ice_cream_name")); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateIssueNote(IssueNote note) {
        String sqlNote = "UPDATE issue_requests SET customer_name = ? WHERE request_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlNote)) {
            ps.setString(1, note.getCustomerName());
            ps.setInt(2, note.getNoteId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    public boolean approveIssueNote(int id) {
        // Gọi hàm logic FIFO chúng ta đã viết để xuất kho thực tế
        try {
            // Ở đây ta gọi mặc định chất lượng là Qualified khi duyệt nhanh
            return processApprovalFIFO(id, "Qualified", "Approved by Manager");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // (FinishedProductWarehouseController)
    public boolean rejectIssueNote(int id) {
        updateRequestStatus(id, "Rejected");
        return true;
    }

    // (SalemanController)
    public boolean deleteNote(int id) {
        String sqlDetail = "DELETE FROM issue_request_details WHERE request_id = ?";
        String sqlReq = "DELETE FROM issue_requests WHERE request_id = ?";
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psD = conn.prepareStatement(sqlDetail);
                 PreparedStatement psR = conn.prepareStatement(sqlReq)) {
                psD.setInt(1, id); psD.executeUpdate();
                psR.setInt(1, id); psR.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // (Lấy danh sách tất cả các phiếu)
    public List<IssueNote> findAllNotes() {
        return getAllMyRequests(); // Tận dụng hàm đã viết
    }

    // (Lấy tồn kho thành phẩm)
    public List<FinishedProductInventory> getFinishedInventory() {
        List<FinishedProductInventory> list = new ArrayList<>();
        String sql = "SELECT production_order_id, product_name, current_quantity, mfg_date, exp_date FROM finished_product_inventory";
        try (Connection conn = Database.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                FinishedProductInventory i = new FinishedProductInventory();
                i.setInventoryId(rs.getInt("production_order_id"));
                i.setProductName(rs.getString("product_name"));
                i.setQuantity(rs.getDouble("current_quantity"));
                Timestamp mfg = rs.getTimestamp("mfg_date");
                if (mfg != null) i.setMfgDate(mfg.toLocalDateTime());
                list.add(i);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}