package aptech.proj_NN_group2.model;
import java.sql.Date;

public class NguyenLieuModel {
    private int id;
    private String ten, nguonGoc, donVi, ngayNhap;
    private double soLuong, giaThanh;
    private Date hanSuDung;

    public NguyenLieuModel(int id, String ten, String nguonGoc, double soLuong, String donVi, Date hanSuDung, double giaThanh, String ngayNhap) {
        this.id = id;
        this.ten = ten;
        this.nguonGoc = nguonGoc;
        this.soLuong = soLuong;
        this.donVi = donVi;
        this.hanSuDung = hanSuDung;
        this.giaThanh = giaThanh;
        this.ngayNhap = ngayNhap;
    }

    public int getId() { return id; }
    public String getTen() { return ten; }
    public String getNguonGoc() { return nguonGoc; }
    public double getSoLuong() { return soLuong; }
    public String getDonVi() { return donVi; }
    public Date getHanSuDung() { return hanSuDung; }
    public double getGiaThanh() { return giaThanh; }
    public String getNgayNhap() { return ngayNhap; }
}