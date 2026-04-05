package aptech.proj_NN_group2.util;

import aptech.proj_NN_group2.model.NguyenLieuModel; // Quan trọng nhất: Để nó tìm thấy Model
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
public class NguyenLieuDAO {

    // Logic SQL: Thêm mới nguyên liệu
    public boolean insert(String ten, String nguonGoc, String donVi, double soLuong, Date hanSD, double gia) {
        String sql = "INSERT INTO NguyenLieu (ten_nguyen_lieu, nguon_goc, don_vi_tinh, so_luong_ton, han_su_dung, gia_thanh) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = new TestConnection().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, ten);
            ps.setString(2, nguonGoc);
            ps.setString(3, donVi);
            ps.setDouble(4, soLuong);
            ps.setDate(5, hanSD);
            ps.setDouble(6, gia);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Logic SQL: Lấy danh sách theo FIFO (Hàng cũ dùng trước)
    public void getNguyenLieuFIFO() {
        String sql = "SELECT * FROM NguyenLieu ORDER BY han_su_dung ASC, ngay_nhap_kho ASC";
        try (Connection con = new TestConnection().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { 
            
            while (rs.next()) {
                System.out.println("Tên: " + rs.getString("ten_nguyen_lieu") + " - HSD: " + rs.getDate("han_su_dung"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean xuatKhoVetCan(String tenNguyenLieu, double soLuongCan) {
        // 1. Lấy tất cả các lô còn hàng, ưu tiên HSD cũ nhất đứng đầu
        String sqlSelect = "SELECT id, so_luong_ton FROM NguyenLieu " +
                           "WHERE ten_nguyen_lieu = ? AND so_luong_ton > 0 " +
                           "ORDER BY han_su_dung ASC, ngay_nhap_kho ASC";
        
        String sqlUpdate = "UPDATE NguyenLieu SET so_luong_ton = ? WHERE id = ?";

        try (Connection con = new TestConnection().getConnection()) {
            con.setAutoCommit(false); // Bắt đầu Transaction để đảm bảo an toàn
            
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setString(1, tenNguyenLieu);
                ResultSet rs = psSelect.executeQuery();
                
                double conThieu = soLuongCan;

                while (rs.next() && conThieu > 0) {
                    int idLo = rs.getInt("id");
                    double tonHienTai = rs.getDouble("so_luong_ton");

                    if (tonHienTai >= conThieu) {
                        // Lô này đủ cân hết số còn thiếu
                        updateLoHang(con, sqlUpdate, tonHienTai - conThieu, idLo);
                        conThieu = 0; // Đã đủ
                    } else {
                        // Lô này không đủ, vét cạn lô này và trừ tiếp lô sau
                        updateLoHang(con, sqlUpdate, 0, idLo);
                        conThieu -= tonHienTai; // Giảm số lượng cần tìm xuống
                    }
                }

                if (conThieu <= 0) {
                    con.commit(); // Thành công hoàn toàn
                    return true;
                } else {
                    con.rollback(); // Không đủ tổng kho để trừ
                    System.out.println("Lỗi: Tổng kho không đủ để vét!");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm phụ để hỗ trợ update trong vòng lặp
    private void updateLoHang(Connection con, String sql, double soLuongMoi, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, soLuongMoi);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
    public ObservableList<NguyenLieuModel> getAll() {
        ObservableList<NguyenLieuModel> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM NguyenLieu";
        try (Connection con = new TestConnection().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
        	while (rs.next()) {
                list.add(new NguyenLieuModel(
                    rs.getInt("id"),
                    rs.getString("ten_nguyen_lieu"),
                    rs.getString("nguon_goc"),
                    rs.getDouble("so_luong_ton"),
                    rs.getString("don_vi_tinh"),
                    rs.getDate("han_su_dung"),
                    rs.getDouble("gia_thanh"),
                    rs.getString("ngay_nhap_kho") // THÊM DÒNG NÀY (Tham số thứ 8)
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}