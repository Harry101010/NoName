USE IceCreamManagement;
GO

-- 1. LÀM SẠCH DỮ LIỆU CŨ ĐỂ ĐỒNG BỘ THEO LUỒNG MỚI
DELETE FROM dbo.product_issue_details;
DELETE FROM dbo.product_issue_notes;
DELETE FROM dbo.finished_product_inventory;
DELETE FROM dbo.ice_creams;

DBCC CHECKIDENT ('dbo.ice_creams', RESEED, 0);
DBCC CHECKIDENT ('dbo.product_issue_notes', RESEED, 0);

-- 2. DANH MỤC SẢN PHẨM (Chỉ giữ tên)
INSERT INTO dbo.ice_creams (ice_cream_name, is_active)
VALUES (N'Kem Vanilla', 1), (N'Kem Chocolate', 1), (N'Kem Matcha', 1);

-- 3. TỒN KHO THÀNH PHẨM (Tên sản phẩm phải khớp với danh mục trên)
INSERT INTO dbo.finished_product_inventory (production_po_code, product_name, current_quantity, mfg_date, exp_date, storage_location)
VALUES 
('PO-SX-01', N'Kem Vanilla', 500.0, '2026-04-01', '2026-10-01', N'Kệ A-01'),
('PO-SX-02', N'Kem Chocolate', 300.0, '2026-04-02', '2026-10-02', N'Kệ A-05'),
('PO-SX-03', N'Kem Matcha', 200.0, '2026-04-05', '2026-10-05', N'Kệ C-01');

-- 4. TRIGGER TRỪ KHO TỰ ĐỘNG (Dựa trên tên sản phẩm đồng bộ)
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_ApproveAndSubtractInventory')
    DROP TRIGGER trg_ApproveAndSubtractInventory;
GO

CREATE TRIGGER trg_ApproveAndSubtractInventory
ON dbo.product_issue_notes
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    -- Chạy khi status chuyển từ 'Chờ duyệt' sang 'Đã duyệt'
    IF UPDATE(status)
    BEGIN
        UPDATE inv
        SET inv.current_quantity = inv.current_quantity - det.quantity
        FROM dbo.finished_product_inventory inv
        INNER JOIN dbo.ice_creams ice ON inv.product_name = ice.ice_cream_name
        INNER JOIN dbo.product_issue_details det ON ice.ice_cream_id = det.ice_cream_id
        INNER JOIN inserted ins ON det.note_id = ins.note_id
        INNER JOIN deleted del ON ins.note_id = del.note_id
        WHERE ins.status = N'Đã duyệt' AND del.status = N'Chờ duyệt';
    END
END
GO

USE IceCreamManagement;
GO

-- 1. XÓA DỮ LIỆU CŨ TRONG PHẦN PHIẾU YÊU CẦU ĐỂ LÀM MỚI
DELETE FROM dbo.product_issue_details;
DELETE FROM dbo.product_issue_notes;

-- Reset ID tự tăng về 1
DBCC CHECKIDENT ('dbo.product_issue_notes', RESEED, 0);
DBCC CHECKIDENT ('dbo.product_issue_details', RESEED, 0);
GO

-- 2. KHAI BÁO BIẾN ĐỂ LẤY ID TỪ CÁC BẢNG GỐC
DECLARE @U_ID INT = (SELECT TOP 1 user_id FROM dbo.users); -- ID nhân viên kinh doanh
DECLARE @IC_Vanilla INT = (SELECT ice_cream_id FROM dbo.ice_creams WHERE ice_cream_name = N'Kem Vanilla');
DECLARE @IC_Choco INT = (SELECT ice_cream_id FROM dbo.ice_creams WHERE ice_cream_name = N'Kem Chocolate');
DECLARE @IC_Matcha INT = (SELECT ice_cream_id FROM dbo.ice_creams WHERE ice_cream_name = N'Kem Matcha');

-- 3. CHÈN 10 PHIẾU YÊU CẦU XUẤT KHO (MASTER)
INSERT INTO dbo.product_issue_notes (saleman_id, customer_name, customer_order_code, delivery_date, status, note)
VALUES 
(@U_ID, N'Siêu thị WinMart', 'WM-001', '2026-04-15', N'Chờ duyệt', N'Giao ca sáng'),
(@U_ID, N'Đại lý kem Miền Tây', 'MT-PO-88', '2026-04-16', N'Chờ duyệt', N'Khách ưu tiên'),
(@U_ID, N'Cửa hàng tiện lợi 7-11', '7E-102', '2026-04-14', N'Đã duyệt', N'Đã xuất kho xong'),
(@U_ID, N'Khách lẻ Anh Tuấn', NULL, '2026-04-15', N'Chờ duyệt', N'Khách tự lấy tại kho'),
(@U_ID, N'Hệ thống Circle K', 'CK-QM-09', '2026-04-17', N'Chờ duyệt', N'Giao kho lạnh quận 1'),
(@U_ID, N'Trường học Quốc tế', 'SCH-99', '2026-04-13', N'Từ chối', N'Hủy đơn do sai số lượng'),
(@U_ID, N'Nhà hàng Sen Tây Hồ', 'SEN-2026', '2026-04-18', N'Chờ duyệt', N'Sự kiện tối thứ 7'),
(@U_ID, N'Đại lý kem Mixue', 'MX-PO-11', '2026-04-19', N'Chờ duyệt', N'Đơn dự phòng'),
(@U_ID, N'Siêu thị Lotte', 'LT-554', '2026-04-20', N'Chờ duyệt', N'Cần kiểm tra hạn sử dụng'),
(@U_ID, N'Canteen Vinfast', 'VF-PRO', '2026-04-15', N'Chờ duyệt', N'Xuất mẫu thử');

-- 4. CHÈN CHI TIẾT SẢN PHẨM CHO 10 PHIẾU TRÊN (DETAILS)
-- Mỗi phiếu sẽ tương ứng với một loại kem và số lượng khác nhau để bạn test
INSERT INTO dbo.product_issue_details (note_id, ice_cream_id, quantity)
VALUES 
(1, @IC_Vanilla, 20.0),  -- Phiếu 1: 20kg Vanilla
(2, @IC_Choco, 15.5),    -- Phiếu 2: 15.5kg Chocolate
(3, @IC_Matcha, 10.0),   -- Phiếu 3: Đã duyệt (Tồn kho đã trừ khi bạn chạy Trigger)
(4, @IC_Vanilla, 5.0),    -- Phiếu 4: 5kg Vanilla
(5, @IC_Choco, 30.0),    -- Phiếu 5: 30kg Chocolate
(6, @IC_Matcha, 50.0),   -- Phiếu 6: Đơn bị từ chối
(7, @IC_Vanilla, 12.0),   -- Phiếu 7: 12kg Vanilla
(8, @IC_Choco, 8.0),     -- Phiếu 8: 8kg Chocolate
(9, @IC_Matcha, 25.0),   -- Phiếu 9: 25kg Matcha
(10, @IC_Vanilla, 2.0);   -- Phiếu 10: 2kg Vanilla
GO