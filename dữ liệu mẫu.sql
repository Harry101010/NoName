USE IceCreamManagement;
GO

/*

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

*/

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


-- =========================================================
-- DUY NGUYEN NGOC - Mock Data
-- Production Orders, Stages, Ingredient Export Requests/Receipts
-- =========================================================

-- Lấy user_id của Trưởng sản xuất và Quản lý kho
DECLARE @TruongSX INT = (SELECT TOP 1 user_id FROM users WHERE role_id = 2);
DECLARE @QuanLyKho INT = (SELECT TOP 1 user_id FROM users WHERE role_id = 3);

-- Lấy ice_cream_id
DECLARE @Vanilla    INT = (SELECT ice_cream_id FROM ice_creams WHERE ice_cream_name = N'Kem Vanilla');
DECLARE @Chocolate  INT = (SELECT ice_cream_id FROM ice_creams WHERE ice_cream_name = N'Kem Chocolate');
DECLARE @Strawberry INT = (SELECT ice_cream_id FROM ice_creams WHERE ice_cream_name = N'Kem Strawberry');
DECLARE @Matcha     INT = (SELECT ice_cream_id FROM ice_creams WHERE ice_cream_name = N'Kem Matcha');

-- =========================================================
-- 1. PRODUCTION ORDERS (Lệnh sản xuất)
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM dbo.production_orders)
BEGIN
    INSERT INTO dbo.production_orders (ice_cream_id, planned_output_kg, created_by, order_status, note)
    VALUES
    -- Lệnh đã hoàn thành (để xem lịch sử)
    (@Vanilla,    20.000, @TruongSX, N'finished',            N'Lô sản xuất tháng 3'),
    (@Chocolate,  15.000, @TruongSX, N'finished',            N'Lô sản xuất tháng 3'),
    -- Lệnh đang chờ nguyên liệu
    (@Strawberry, 10.000, @TruongSX, N'waiting_ingredient',  N'Lô sản xuất tháng 4'),
    -- Lệnh đang sản xuất
    (@Matcha,     12.000, @TruongSX, N'in_progress',         N'Lô sản xuất tháng 4'),
    -- Lệnh mới tạo (draft) — để test Create Ingredient Issue Request
    (@Vanilla,    25.000, @TruongSX, N'draft',               N'Lô sản xuất tháng 5 - lô 1'),
    (@Chocolate,  18.000, @TruongSX, N'draft',               N'Lô sản xuất tháng 5 - lô 2');
END
GO

-- =========================================================
-- 2. PRODUCTION STAGES (Công đoạn sản xuất)
-- Tạo 8 công đoạn cho lệnh đang in_progress (Matcha)
-- =========================================================
DECLARE @OrderMatcha INT = (
    SELECT TOP 1 production_order_id FROM production_orders
    WHERE order_status = N'in_progress'
    ORDER BY created_at DESC
);

IF @OrderMatcha IS NOT NULL AND NOT EXISTS (
    SELECT 1 FROM production_stages WHERE production_order_id = @OrderMatcha
)
BEGIN
    INSERT INTO production_stages
        (production_order_id, stage_no, stage_name, stage_status, start_time, end_time, actual_duration_min, actual_volume, note)
    VALUES
    (@OrderMatcha, 1, N'Xử lý nguyên liệu và trộn hỗn hợp', N'completed',
        DATEADD(HOUR, -5, SYSDATETIME()), DATEADD(HOUR, -4, SYSDATETIME()),
        60, 12.500, N'[Nhiệt độ trộn: 65°C] [Tỉ lệ trộn: Sữa 55% - Đường 17% - Kem 20% - Matcha 8%]'),
    (@OrderMatcha, 2, N'Đồng hóa',       N'completed',
        DATEADD(HOUR, -4, SYSDATETIME()), DATEADD(MINUTE, -210, SYSDATETIME()),
        30, 12.200, N'Áp suất đồng hóa 150 bar'),
    (@OrderMatcha, 3, N'Thanh trùng',    N'completed',
        DATEADD(MINUTE, -210, SYSDATETIME()), DATEADD(MINUTE, -165, SYSDATETIME()),
        45, 12.000, N'Nhiệt độ 85°C trong 15 giây'),
    (@OrderMatcha, 4, N'Ủ kem',          N'completed',
        DATEADD(MINUTE, -165, SYSDATETIME()), DATEADD(MINUTE, -45, SYSDATETIME()),
        120, 11.800, N'Ủ ở 4°C'),
    (@OrderMatcha, 5, N'Đánh kem',       N'open',
        DATEADD(MINUTE, -45, SYSDATETIME()), NULL,
        NULL, NULL, NULL),
    (@OrderMatcha, 6, N'Chiết rót vào khuôn', N'pending', NULL, NULL, NULL, NULL, NULL),
    (@OrderMatcha, 7, N'Làm cứng',            N'pending', NULL, NULL, NULL, NULL, NULL),
    (@OrderMatcha, 8, N'Bảo quản và đóng gói',N'pending', NULL, NULL, NULL, NULL, NULL);
END
GO

-- Tạo 8 công đoạn đã hoàn thành cho lệnh finished (Vanilla)
DECLARE @OrderVanilla INT = (
    SELECT TOP 1 production_order_id FROM production_orders
    WHERE order_status = N'finished' AND ice_cream_id = (
        SELECT ice_cream_id FROM ice_creams WHERE ice_cream_name = N'Kem Vanilla'
    )
);

IF @OrderVanilla IS NOT NULL AND NOT EXISTS (
    SELECT 1 FROM production_stages WHERE production_order_id = @OrderVanilla
)
BEGIN
    INSERT INTO production_stages
        (production_order_id, stage_no, stage_name, stage_status, start_time, end_time, actual_duration_min, actual_volume, mold_count, note)
    VALUES
    (@OrderVanilla, 1, N'Xử lý nguyên liệu và trộn hỗn hợp', N'completed', DATEADD(DAY,-2,SYSDATETIME()), DATEADD(DAY,-2,DATEADD(HOUR,1,SYSDATETIME())), 60, 20.500, NULL, N'[Nhiệt độ trộn: 63°C] [Tỉ lệ trộn: Sữa 55% - Đường 15% - Kem 25% - Vanilla 5%]'),
    (@OrderVanilla, 2, N'Đồng hóa',        N'completed', DATEADD(DAY,-2,DATEADD(HOUR,1,SYSDATETIME())), DATEADD(DAY,-2,DATEADD(HOUR,2,SYSDATETIME())), 30, 20.200, NULL, NULL),
    (@OrderVanilla, 3, N'Thanh trùng',     N'completed', DATEADD(DAY,-2,DATEADD(HOUR,2,SYSDATETIME())), DATEADD(DAY,-2,DATEADD(HOUR,3,SYSDATETIME())), 45, 20.000, NULL, N'85°C/15s'),
    (@OrderVanilla, 4, N'Ủ kem',           N'completed', DATEADD(DAY,-2,DATEADD(HOUR,3,SYSDATETIME())), DATEADD(DAY,-1,SYSDATETIME()),                 480, 19.800, NULL, N'4°C qua đêm'),
    (@OrderVanilla, 5, N'Đánh kem',        N'completed', DATEADD(DAY,-1,SYSDATETIME()), DATEADD(DAY,-1,DATEADD(HOUR,1,SYSDATETIME())),                 60, 19.500, NULL, NULL),
    (@OrderVanilla, 6, N'Chiết rót vào khuôn', N'completed', DATEADD(DAY,-1,DATEADD(HOUR,1,SYSDATETIME())), DATEADD(DAY,-1,DATEADD(HOUR,2,SYSDATETIME())), 45, 19.200, 192, NULL),
    (@OrderVanilla, 7, N'Làm cứng',        N'completed', DATEADD(DAY,-1,DATEADD(HOUR,2,SYSDATETIME())), DATEADD(DAY,-1,DATEADD(HOUR,6,SYSDATETIME())), 240, 19.000, NULL, N'-18°C'),
    (@OrderVanilla, 8, N'Bảo quản và đóng gói', N'completed', DATEADD(DAY,-1,DATEADD(HOUR,6,SYSDATETIME())), DATEADD(DAY,-1,DATEADD(HOUR,7,SYSDATETIME())), 60, 19.000, NULL, N'Đóng gói hoàn tất');
END
GO

-- =========================================================
-- 3. INGREDIENT EXPORT REQUESTS (Phiếu yêu cầu xuất kho)
-- Cho lệnh waiting_ingredient (Strawberry)
-- =========================================================
DECLARE @OrderStrawberry INT = (
    SELECT TOP 1 production_order_id FROM production_orders
    WHERE order_status = N'waiting_ingredient'
);

IF @OrderStrawberry IS NOT NULL AND NOT EXISTS (
    SELECT 1 FROM ingredient_export_requests WHERE production_order_id = @OrderStrawberry
)
BEGIN
    INSERT INTO ingredient_export_requests
        (production_order_id, requested_by, request_status, note)
    VALUES
        (@OrderStrawberry, @TruongSX, N'approved', N'Yêu cầu xuất kho cho lô Strawberry tháng 4');

    DECLARE @ReqId INT = SCOPE_IDENTITY();

    -- Chi tiết nguyên liệu (tính từ công thức * 10kg)
    INSERT INTO ingredient_export_request_details
        (ingredient_export_request_id, ingredient_id, required_quantity)
    SELECT @ReqId, r.ingredient_id, r.quantity_per_kg * 10
    FROM recipes r
    JOIN production_orders po ON po.ice_cream_id = r.ice_cream_id
    WHERE po.production_order_id = @OrderStrawberry;
END
GO

-- =========================================================
-- 4. INGREDIENT EXPORT RECEIPTS (Phiếu xuất kho)
-- Tạo phiếu xuất kho đã duyệt — để test Confirm Received (3.8)
-- =========================================================
DECLARE @ReqIdApproved INT = (
    SELECT TOP 1 ingredient_export_request_id
    FROM ingredient_export_requests
    WHERE request_status = N'approved'
);

IF @ReqIdApproved IS NOT NULL AND NOT EXISTS (
    SELECT 1 FROM ingredient_export_receipts
    WHERE ingredient_export_request_id = @ReqIdApproved
)
BEGIN
    INSERT INTO ingredient_export_receipts
        (ingredient_export_request_id, approved_by, receipt_status, note)
    VALUES
        (@ReqIdApproved, @QuanLyKho, N'approved', N'Đã kiểm tra đủ nguyên liệu, sẵn sàng xuất kho');
END

) AS v(ingredient_name, origin, storage_condition, unit_id, price_per_unit)

WHERE NOT EXISTS (
    SELECT 1 
    FROM ingredients i 
    WHERE i.ingredient_name = v.ingredient_name
);
SELECT 
    i.ingredient_name,
    il.lot_id,
    il.remaining_quantity,
    il.expiry_date
FROM ingredients i
LEFT JOIN ingredient_lots il ON i.ingredient_id = il.ingredient_id
ORDER BY i.ingredient_name;


/* =========================================================
   1) STANDARDIZE UNITS
   ========================================================= */
-- Clear old units if necessary, then insert standard English units
INSERT INTO dbo.units (unit_name)
SELECT v.unit_name
FROM (VALUES 
    (N'kg'),    -- Solids (Sugar, Milk Powder)
    (N'g'),     -- Fine Powders/Additives (Matcha, Salt)
    (N'l'),     -- Bulk Liquids (Fresh Milk, Cream)
    (N'ml'),    -- Concentrates (Extracts, Flavors)
    ()N'pcs'),   -- Countable items (Eggs, Cones)
    (N'tray'),  -- Production output
    (N'box')    -- Packaging
) AS v(unit_name)
WHERE NOT EXISTS (SELECT 1 FROM units WHERE unit_name = v.unit_name);
GO

/* =========================================================
   2) INGREDIENTS (Master Data)
   ========================================================= */
INSERT INTO ingredients (ingredient_name, origin, storage_condition, unit_id, price_per_unit, is_active)
SELECT v.name, v.origin, v.storage, u.unit_id, v.price, 1
FROM (VALUES 
    (N'Whole Fresh Milk',     N'TH True Milk',   N'Chilled 2-4°C',     N'l',   22000),
    (N'Full Cream Milk Powder', N'New Zealand',   N'Dry & Cool',        N'kg',  145000),
    (N'Whipping Cream',       N'Anchor',         N'Chilled 2-4°C',     N'l',   160000),
    (N'Bourbon Vanilla Extract', N'France',       N'Room Temp',         N'ml',  5000),
    (N'Pure Cocoa Powder',    N'Belgium',        N'Dry & Cool',        N'kg',  280000),
    (N'Refined Sugar',        N'Bien Hoa',       N'Dry & Cool',        N'kg',  24000),
    (N'Glucose Syrup',        N'South Korea',    N'Room Temp',         N'kg',  45000),
    (N'Fresh Eggs',           N'Ba Huan',        N'Chilled',           N'pcs', 3500),
    (N'Frozen Blueberries',   N'Chile',          N'Frozen -18°C',      N'kg',  320000),
    (N'Pistachio Kernels',    N'USA',            N'Dry & Cool',        N'kg',  550000),
    (N'Matcha Powder',        N'Japan',          N'Airtight, Dark',    N'g',   2500),
    (N'Chocolate Chips',      N'Vietnam',        N'Light Chilled',     N'kg',  120000),
    (N'Strawberry Jam',       N'Da Lat',         N'Chilled after open',N'kg',  85000),
    (N'Stabilizer (CMC)',     N'Germany',        N'Dry & Cool',        N'kg',  190000),
    (N'Sea Salt',             N'Vietnam',        N'Dry',               N'kg',  12000),
    (N'Shredded Coconut',     N'Ben Tre',        N'Frozen',            N'kg',  60000),
    (N'Peppermint Essence',   N'India',          N'Cool',              N'ml',  3000),
    (N'Walnut Kernels',       N'USA',            N'Dry & Cool',        N'kg',  480000),
    (N'Unsalted Butter',      N'France',         N'Chilled',           N'kg',  210000),
    (N'Caramel Sauce',        N'Internal',       N'Chilled',           N'l',   90000)
) AS v(name, origin, storage, unit_name, price)
JOIN units u ON u.unit_name = v.unit_name
WHERE NOT EXISTS (SELECT 1 FROM ingredients WHERE ingredient_name = v.name);

/* =========================================================
   3) INGREDIENT LOTS (Stock Data)
   ========================================================= */
INSERT INTO ingredient_lots (ingredient_id, import_date, expiry_date, received_quantity, remaining_quantity, supplier_id)
SELECT i.ingredient_id, v.imp, v.exp, v.qty, v.qty, 1
FROM (VALUES
    (N'Whole Fresh Milk',     '2026-04-10', '2026-05-10', 100.0),
    (N'Whole Fresh Milk',     '2026-04-20', '2026-05-20', 150.0), 
    (N'Full Cream Milk Powder', '2026-03-01', '2027-03-01', 50.0),
    (N'Whipping Cream',       '2026-04-15', '2026-06-15', 40.0),
    (N'Bourbon Vanilla Extract', '2026-01-10', '2027-01-10', 1000.0),
    (N'Pure Cocoa Powder',    '2026-02-15', '2027-02-15', 20.0),
    (N'Refined Sugar',        '2026-04-01', '2027-04-01', 200.0),
    (N'Glucose Syrup',        '2026-03-20', '2027-03-20', 30.0),
    (N'Fresh Eggs',           '2026-04-21', '2026-05-05', 500.0),
    (N'Frozen Blueberries',   '2026-04-05', '2026-10-05', 15.0),
    (N'Pistachio Kernels',    '2026-03-10', '2026-09-10', 10.0),
    (N'Matcha Powder',        '2026-04-12', '2026-10-12', 5000.0),
    (N'Chocolate Chips',      '2026-04-01', '2026-08-01', 25.0),
    (N'Strawberry Jam',       '2026-04-18', '2026-10-18', 12.0),
    (N'Stabilizer (CMC)',     '2026-02-20', '2028-02-20', 5.0),
    (N'Sea Salt',             '2026-01-01', '2028-01-01', 10.0),
    (N'Shredded Coconut',     '2026-04-10', '2026-07-10', 30.0),
    (N'Peppermint Essence',   '2026-03-05', '2027-03-05', 500.0),
    (N'Walnut Kernels',       '2026-04-01', '2026-10-01', 8.0),
    (N'Unsalted Butter',      '2026-04-15', '2026-07-15', 20.0)
) AS v(name, imp, exp, qty)
JOIN ingredients i ON i.ingredient_name = v.name;
GO
