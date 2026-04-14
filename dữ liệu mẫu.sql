USE IceCreamManagement;
GO

IF NOT EXISTS (SELECT 1 FROM roles)
BEGIN
    INSERT INTO roles (role_name)
    VALUES (N'Admin'),
    (N'Trưởng sản xuất'),
    (N'Quản lý kho'),
    (N'Nhân viên kinh doanh');
END

INSERT INTO dbo.users (username, email, password_hash, role_id, is_active)
VALUES 
(N'admin_user',     N'harryvnsg@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 1, 1),
(N'manager_alice',  N'nghvukt3@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 2, 1),
(N'manager_bob',    N'harryvnsg19@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 2, 1),
(N'staff_charlie',  N'nguyenduy23093@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_david',    N'maitue201@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_eve',      N'a@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_frank',    N'b@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_grace',    N'c@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 4, 1),
(N'staff_heidi',    N'd@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 4, 1),
(N'staff_ivan',     N'e@gmail.com',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 0); -- One inactive user for testing
GO

IF NOT EXISTS (SELECT 1 FROM dbo.units)
BEGIN
    INSERT INTO dbo.units (unit_name)
    VALUES (N'kg'), (N'l'), (N'pcs');
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.ice_creams)
BEGIN
    INSERT INTO dbo.ice_creams (ice_cream_name, is_active)
    VALUES
    (N'Kem Vanilla', 1),
    (N'Kem Chocolate', 1),
    (N'Kem Strawberry', 1),
    (N'Kem Matcha', 1);
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.ingredients)
BEGIN
    INSERT INTO dbo.ingredients
    (ingredient_name, origin, storage_condition, unit_id, price_per_unit, is_active)
    VALUES
    (N'Milk', N'Veng soure', N'Keep chilled', 2, 12000, 1),
    (N'Sugar', N'Local', N'Dry place', 1, 25000, 1),
    (N'Cream', N'Imported', N'Keep chilled', 2, 45000, 1),
    (N'Cocoa Powder', N'Imported', N'Dry place', 1, 70000, 1),
    (N'Strawberry', N'Local', N'Keep chilled', 3, 110000, 1);
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.ingredient_lots)
BEGIN
INSERT INTO dbo.ingredient_lots
    (ingredient_id, import_date, expiry_date, received_quantity, remaining_quantity)
SELECT i.ingredient_id, v.import_date, v.expiry_date, v.received_quantity, v.remaining_quantity
FROM (VALUES
    (N'Milk', '2026-03-20 08:00:00', '2026-06-30', 500.000, 420.000),
    (N'Milk', '2026-04-01 08:00:00', '2026-07-30', 300.000, 280.000),
    (N'Sugar','2026-03-25 09:00:00', '2027-01-31',1000.000, 900.000),
    (N'Cream','2026-03-22 10:00:00', '2026-05-15', 400.000, 350.000),
    (N'Cocoa Powder','2026-03-18 11:00:00', '2027-02-28',200.000,180.000),
    (N'Strawberry','2026-03-18 11:00:00', '2027-02-28',10000.000,700.000),
    (N'Bột Matcha','2026-03-18 11:00:00', '2027-02-28',10000.000,700.000)
) v(name, import_date, expiry_date, received_quantity, remaining_quantity)
JOIN dbo.ingredients i ON i.ingredient_name = v.name;
END
GO

INSERT INTO dbo.recipes
    (ice_cream_id, ingredient_id, quantity_per_kg)
SELECT ic.ice_cream_id, ing.ingredient_id, v.qty
FROM (VALUES
    (N'Kem Vanilla',    N'Milk',         0.550),
    (N'Kem Vanilla',    N'Sugar',        0.150),
    (N'Kem Vanilla',    N'Cream',        0.250),

    (N'Kem Chocolate',  N'Milk',         0.500),
    (N'Kem Chocolate',  N'Sugar',        0.160),
    (N'Kem Chocolate',  N'Cream',        0.200),
    (N'Kem Chocolate',  N'Cocoa Powder', 0.080),

    (N'Kem Strawberry', N'Milk',         0.480),
    (N'Kem Strawberry', N'Sugar',        0.170),
    (N'Kem Strawberry', N'Cream',        0.200),
    (N'Kem Strawberry', N'Strawberry',   5),

    (N'Kem Matcha', N'Milk',         0.550),
    (N'Kem Matcha', N'Sugar',        0.170),
    (N'Kem Matcha', N'Cream',        0.200),
    (N'Kem Matcha', N'Bột Matcha',   0.080)

) v(ice_name, ing_name, qty)
JOIN dbo.ice_creams ic 
    ON ic.ice_cream_name = v.ice_name
JOIN dbo.ingredients ing 
    ON ing.ingredient_name = v.ing_name;

INSERT INTO finished_product_inventory (production_po_code, product_name, current_quantity, mfg_date, exp_date, storage_location)
VALUES 
('PO-SX-01', N'Kem Vanilla', 500.0, '2026-04-01', '2026-10-01', N'Kệ A-01'),
('PO-SX-02', N'Kem Chocolate', 300.0, '2026-04-02', '2026-10-02', N'Kệ A-05'),
('PO-SX-03', N'Kem Matcha', 200.0, '2026-04-05', '2026-10-05', N'Kệ C-01');

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

