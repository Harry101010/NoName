-- 1. Bảng quản lý nguyên liệu (Ưu tiên lấy cũ dùng trước) [cite: 4, 5]
CREATE TABLE KhoNguyenLieu (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ten_nguyen_lieu VARCHAR(255),
    don_vi_do_luong VARCHAR(50),
    so_luong_ton DOUBLE,
    han_su_dung DATE,
    ngay_nhap_kho DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng theo dõi 8 công đoạn sản xuất [cite: 8 - 27]
CREATE TABLE TienDoSanXuat (
    id_lenh_sx INT PRIMARY KEY AUTO_INCREMENT,
    ten_mon_kem VARCHAR(100),
    buoc_hien_tai VARCHAR(50), -- TRON, DONG_HOA, THANH_TRUNG, U, DANH_KEM, CHIET_ROT, LAM_CUNG, DONG_GOI
    dung_tich_thuc DOUBLE, -- Lưu dung tích sau khi xử lý [cite: 12, 15]
    so_luong_khuon INT, -- Lưu số khuôn ở bước chiết rót [cite: 23]
    thoi_gian_xac_nhan DATETIME
);