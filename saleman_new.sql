USE IceCreamManagement;
GO

-- 1. Xóa dữ liệu cũ để đồng bộ
DELETE FROM product_issue_details;
DELETE FROM product_issue_notes;
DELETE FROM finished_product_inventory;
DELETE FROM ice_creams;

DBCC CHECKIDENT ('dbo.ice_creams', RESEED, 0);
DBCC CHECKIDENT ('dbo.product_issue_notes', RESEED, 0);

-- 2. Tạo danh mục kem
INSERT INTO ice_creams (ice_cream_name, is_active)
VALUES (N'Kem Vanilla', 1), (N'Kem Chocolate', 1), (N'Kem Matcha', 1);

-- 3. Tạo tồn kho thành phẩm (Khớp tên 100%)
INSERT INTO finished_product_inventory (production_po_code, product_name, current_quantity, mfg_date, exp_date, storage_location)
VALUES 
('PO-SX-01', N'Kem Vanilla', 500.0, '2026-04-01', '2026-10-01', N'Kệ A-01'),
('PO-SX-02', N'Kem Chocolate', 300.0, '2026-04-02', '2026-10-02', N'Kệ A-05'),
('PO-SX-03', N'Kem Matcha', 200.0, '2026-04-05', '2026-10-05', N'Kệ C-01');

-- 4. Trigger trừ kho tự động
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_ApproveAndSubtractInventory') DROP TRIGGER trg_ApproveAndSubtractInventory;
GO
CREATE TRIGGER trg_ApproveAndSubtractInventory ON dbo.product_issue_notes AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    IF UPDATE(status)
    BEGIN
        UPDATE inv SET inv.current_quantity = inv.current_quantity - det.quantity
        FROM dbo.finished_product_inventory inv
        INNER JOIN dbo.ice_creams ice ON inv.product_name = ice.ice_cream_name
        INNER JOIN dbo.product_issue_details det ON ice.ice_cream_id = det.ice_cream_id
        INNER JOIN inserted ins ON det.note_id = ins.note_id
        INNER JOIN deleted del ON ins.note_id = del.note_id
        WHERE ins.status = N'Đã duyệt' AND del.status = N'Chờ duyệt';
    END
END
GO

-- 5. Nạp 10 dòng dữ liệu lịch sử mẫu
DECLARE @U INT = (SELECT TOP 1 user_id FROM users);
DECLARE @IV INT = (SELECT ice_cream_id FROM ice_creams WHERE ice_cream_name = N'Kem Vanilla');

INSERT INTO product_issue_notes (saleman_id, customer_name, customer_order_code, status, create_date)
VALUES (@U, N'Vinfast Hải Phòng', 'VF-001', N'Chờ duyệt', GETDATE()),
       (@U, N'Hệ thống Circle K', 'CK-002', N'Chờ duyệt', GETDATE()),
       (@U, N'Đại lý Mixue', 'MX-003', N'Từ chối', GETDATE()),
       (@U, N'Siêu thị Coop Mart', 'CP-004', N'Đã duyệt', GETDATE()),
       (@U, N'Cửa hàng 7-Eleven', '7E-005', N'Chờ duyệt', GETDATE()),
       (@U, N'Lotte Cinema', 'LT-006', N'Chờ duyệt', GETDATE()),
       (@U, N'Siêu thị WinMart', 'WM-007', N'Chờ duyệt', GETDATE()),
       (@U, N'Bách Hóa Xanh', 'BX-008', N'Chờ duyệt', GETDATE()),
       (@U, N'Aeon Mall Tân Phú', 'AM-009', N'Chờ duyệt', GETDATE()),
       (@U, N'Canteen Đại học', 'DH-010', N'Chờ duyệt', GETDATE());

INSERT INTO product_issue_details (note_id, ice_cream_id, quantity)
SELECT note_id, @IV, 10 FROM product_issue_notes;
GO