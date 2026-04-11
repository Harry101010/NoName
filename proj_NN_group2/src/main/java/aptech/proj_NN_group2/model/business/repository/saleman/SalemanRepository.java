package aptech.proj_NN_group2.model.business.repository.saleman;

import aptech.proj_NN_group2.model.entity.saleman.IssueNote;
import aptech.proj_NN_group2.util.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalemanRepository {

    // Lưu phiếu yêu cầu xuất hàng mới
    public boolean createIssueNote(IssueNote note) {
        String sql = "INSERT INTO product_issue_notes (customer_name, status, create_date) VALUES (?, ?, GETDATE())";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, note.getCustomerName());
            ps.setString(2, "Chờ duyệt");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách phiếu đã tạo (để hiển thị lên bảng nếu cần)
    public List<IssueNote> findAllNotes() {
        List<IssueNote> list = new ArrayList<>();
        String sql = "SELECT * FROM product_issue_notes";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                IssueNote n = new IssueNote();
                n.setNoteId(rs.getInt("note_id"));
                n.setCustomerName(rs.getString("customer_name"));
                n.setStatus(rs.getString("status"));
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}