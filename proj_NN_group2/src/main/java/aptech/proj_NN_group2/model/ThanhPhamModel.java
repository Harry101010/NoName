package aptech.proj_NN_group2.model;

public class ThanhPhamModel {
    private int id;
    private String tenKem;
    private int soLuong;
    private String ngaySanXuat;
    private String hanSuDung;

    public ThanhPhamModel(int id, String tenKem, int soLuong, String ngaySanXuat, String hanSuDung) {
        this.id = id;
        this.tenKem = tenKem;
        this.soLuong = soLuong;
        this.ngaySanXuat = ngaySanXuat;
        this.hanSuDung = hanSuDung;
    }

    // Getters (Bắt buộc để TableView hoạt động)
    public int getId() { return id; }
    public String getTenKem() { return tenKem; }
    public int getSoLuong() { return soLuong; }
    public String getNgaySanXuat() { return ngaySanXuat; }
    public String getHanSuDung() { return hanSuDung; }
}