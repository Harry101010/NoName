package aptech.proj_NN_group2.util;

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
    public boolean xuatKhoFIFO(String tenNguyenLieu, double soLuongCan) {
        // Tìm lô hàng cũ nhất (hạn dùng gần nhất) còn tồn kho
        String sqlSelect = "SELECT TOP 1 id, so_luong_ton FROM NguyenLieu " +
                           "WHERE ten_nguyen_lieu = ? AND so_luong_ton > 0 " +
                           "ORDER BY han_su_dung ASC, ngay_nhap_kho ASC";
        
        try (Connection con = new TestConnection().getConnection()) {
            con.setAutoCommit(false); // Bật giao dịch để đảm bảo an toàn dữ liệu
            
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setString(1, tenNguyenLieu);
                ResultSet rs = psSelect.executeQuery();
                
                if (rs.next()) {
                    int idLoHang = rs.getInt("id");
                    double tonKho = rs.getDouble("so_luong_ton");
                    
                    if (tonKho >= soLuongCan) {
                        // Cập nhật trừ kho
                        String sqlUpdate = "UPDATE NguyenLieu SET so_luong_ton = so_luong_ton - ? WHERE id = ?";
                        try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                            psUpdate.setDouble(1, soLuongCan);
                            psUpdate.setInt(2, idLoHang);
                            psUpdate.executeUpdate();
                        }
                        con.commit();
                        return true;
                    }
                }
            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}