USE IceCreamManagement;
GO

PRINT '=========================================================';
PRINT 'STARTING DATABASE REFRESH (TRANSACTIONAL ONLY)';
PRINT '=========================================================';

-- =========================================================
-- PHẦN 1: DỌN DẸP DỮ LIỆU GIAO DỊCH (GIỮ NGUYÊN MASTER DATA)
-- =========================================================
PRINT 'Cleaning transaction data...';

-- Tắt kiểm tra khóa ngoại để xóa được bảng con trước
ALTER TABLE production_tracking NOCHECK CONSTRAINT all;
ALTER TABLE ingredient_export_receipt_details NOCHECK CONSTRAINT all;
ALTER TABLE ingredient_export_receipts NOCHECK CONSTRAINT all;
ALTER TABLE ingredient_export_request_details NOCHECK CONSTRAINT all;
ALTER TABLE ingredient_export_requests NOCHECK CONSTRAINT all;
ALTER TABLE production_orders NOCHECK CONSTRAINT all;
ALTER TABLE production_stages NOCHECK CONSTRAINT all; -- Lưu ý: Nếu muốn reset stages thì xóa bảng này

-- Xóa dữ liệu các bảng giao dịch (Transaction Tables)
DELETE FROM production_tracking;
DELETE FROM production_stages;
DELETE FROM ingredient_export_receipt_details;
DELETE FROM ingredient_export_receipts;
DELETE FROM ingredient_export_request_details;
DELETE FROM ingredient_export_requests;
DELETE FROM production_orders;

-- Bật lại kiểm tra khóa ngoại
ALTER TABLE production_tracking CHECK CONSTRAINT all;
ALTER TABLE ingredient_export_receipt_details CHECK CONSTRAINT all;
ALTER TABLE ingredient_export_receipts CHECK CONSTRAINT all;
ALTER TABLE ingredient_export_request_details CHECK CONSTRAINT all;
ALTER TABLE ingredient_export_requests CHECK CONSTRAINT all;
ALTER TABLE production_orders CHECK CONSTRAINT all;
ALTER TABLE production_stages CHECK CONSTRAINT all;

-- Reset bộ đếm ID cho các bảng quan trọng
DBCC CHECKIDENT ('production_orders', RESEED, 0);
DBCC CHECKIDENT ('production_stages', RESEED, 0);
DBCC CHECKIDENT ('production_tracking', RESEED, 0);

PRINT 'Transaction data cleared. Master Data (Users, Ingredients, Recipes) preserved.';

-- =========================================================
-- PHẦN 2: ĐẢM BẢO MASTER DATA (Nạp nếu chưa có)
-- =========================================================
PRINT 'Ensuring Master Data exists...';

-- Nạp Units nếu chưa có
IF NOT EXISTS (SELECT 1 FROM units)
BEGIN
    INSERT INTO units (unit_name) VALUES ('kg'), ('g'), ('liter'), ('pcs');
END

-- Nạp Ingredients nếu chưa có (Tương tự cho các bảng khác)
IF NOT EXISTS (SELECT 1 FROM ingredients)
BEGIN
    INSERT INTO ingredients (ingredient_name, origin, storage_condition, unit_id, price_per_unit)
SELECT v.name, v.origin, v.storage, u.unit_id, v.price
FROM (VALUES 
    ('Fresh Milk', 'Dalat', 'Chilled', 'liter', 25000), 
    ('Sugar', 'Bien Hoa', 'Dry', 'kg', 20000),
    ('Matcha Powder', 'Japan', 'Dark', 'g', 500000), 
    ('Cocoa Powder', 'Belgium', 'Dry', 'kg', 300000),
    ('Strawberry', 'Dalat', 'Chilled', 'kg', 150000), 
    ('Heavy Cream', 'Anchor', 'Chilled', 'liter', 160000),
    ('Vanilla Extract', 'France', 'Dry', 'g', 50000), 
    ('Sea Salt', 'VN', 'Dry', 'kg', 10000),
    ('Shredded Coconut', 'Ben Tre', 'Frozen', 'kg', 60000), 
    ('Unsalted Butter', 'France', 'Chilled', 'kg', 200000),
    ('Almond', 'USA', 'Dry', 'kg', 400000), 
    ('Chocolate Chips', 'VN', 'Chilled', 'kg', 120000),
    ('Milk Powder', 'New Zealand', 'Dry', 'kg', 140000), 
    ('Glucose Syrup', 'Korea', 'Dry', 'kg', 45000),
    ('Eggs', 'VN', 'Chilled', 'pcs', 3500), 
    ('Strawberry Jam', 'Dalat', 'Chilled', 'kg', 85000),
    ('Peppermint', 'India', 'Cool', 'g', 30000), 
    ('Walnut', 'USA', 'Dry', 'kg', 480000),
    ('Caramel', 'Internal', 'Chilled', 'liter', 90000), 
    ('CMC Stabilizer', 'Germany', 'Dry', 'kg', 190000)
) AS v(name, origin, storage, unit_name, price)
JOIN units u ON u.unit_name = v.unit_name;
END

-- Products (5 types)
INSERT INTO ice_creams (ice_cream_name) VALUES 
('Vanilla Ice Cream'), ('Matcha Ice Cream'), ('Chocolate Ice Cream'), 
('Strawberry Ice Cream'), ('Walnut Ice Cream');

PRINT 'Seeding Recipes using names (safer)...';

INSERT INTO recipes (ice_cream_id, ingredient_id, quantity_per_kg)
SELECT ice.ice_cream_id, ing.ingredient_id, v.qty
FROM (VALUES 
    -- Vanilla Ice Cream (ID 1)
    ('Vanilla Ice Cream', 'Fresh Milk', 0.5), ('Vanilla Ice Cream', 'Sugar', 0.1), 
    ('Vanilla Ice Cream', 'Heavy Cream', 0.2), ('Vanilla Ice Cream', 'Vanilla Extract', 0.05), 
    ('Vanilla Ice Cream', 'Milk Powder', 0.15),
    
    -- Matcha Ice Cream (ID 2)
    ('Matcha Ice Cream', 'Fresh Milk', 0.4), ('Matcha Ice Cream', 'Sugar', 0.1), 
    ('Matcha Ice Cream', 'Matcha Powder', 0.1), ('Matcha Ice Cream', 'Heavy Cream', 0.2), 
    ('Matcha Ice Cream', 'Milk Powder', 0.1), ('Matcha Ice Cream', 'Caramel', 0.1),
    
    -- Chocolate Ice Cream (ID 3)
    ('Chocolate Ice Cream', 'Fresh Milk', 0.4), ('Chocolate Ice Cream', 'Sugar', 0.1), 
    ('Chocolate Ice Cream', 'Cocoa Powder', 0.1), ('Chocolate Ice Cream', 'Heavy Cream', 0.2), 
    ('Chocolate Ice Cream', 'Chocolate Chips', 0.1), ('Chocolate Ice Cream', 'Milk Powder', 0.1),
    
    -- Strawberry Ice Cream (ID 4)
    ('Strawberry Ice Cream', 'Fresh Milk', 0.4), ('Strawberry Ice Cream', 'Sugar', 0.1), 
    ('Strawberry Ice Cream', 'Strawberry', 0.2), ('Strawberry Ice Cream', 'Heavy Cream', 0.2), 
    ('Strawberry Ice Cream', 'Strawberry Jam', 0.1),
    
    -- Walnut Ice Cream (ID 5)
    ('Walnut Ice Cream', 'Fresh Milk', 0.4), ('Walnut Ice Cream', 'Sugar', 0.1), 
    ('Walnut Ice Cream', 'Heavy Cream', 0.2), ('Walnut Ice Cream', 'Almond', 0.1), 
    ('Walnut Ice Cream', 'Walnut', 0.1)
) AS v(ice_name, ing_name, qty)
JOIN ice_creams ice ON v.ice_name = ice.ice_cream_name
JOIN ingredients ing ON v.ing_name = ing.ingredient_name;

-- =========================================================
-- PHẦN 3: TẠO DỮ LIỆU GIAO DỊCH MỚI (ORDERS & TRACKING)
-- =========================================================
PRINT 'Seeding Production Orders & Tracking...';

-- 3.1. Nạp Order

INSERT INTO production_orders (ice_cream_id, planned_output_kg, order_status, note)
SELECT ice.ice_cream_id, v.output, v.status, v.note
FROM (VALUES
    ('Vanilla Ice Cream', 100, 'finished', 'Order 01'),
    ('Matcha Ice Cream', 120, 'finished', 'Order 02'),
    ('Chocolate Ice Cream', 80, 'in_progress', 'Order 03'),
    ('Strawberry Ice Cream', 90, 'in_progress', 'Order 04'),
    ('Walnut Ice Cream', 50, 'waiting_ingredient', 'Order 05'),
    ('Vanilla Ice Cream', 150, 'draft', 'Order 06'),
    ('Matcha Ice Cream', 200, 'draft', 'Order 07'),
    ('Chocolate Ice Cream', 70, 'draft', 'Order 08'),
    ('Strawberry Ice Cream', 110, 'draft', 'Order 09'),
    ('Walnut Ice Cream', 60, 'draft', 'Order 10')
) AS v(ice_name, output, status, note)
JOIN ice_creams ice ON v.ice_name = ice.ice_cream_name;

-- 3.2. Nạp Production Stages
INSERT INTO production_stages (stage_name, sequence_order, standard_time_minutes, is_proportional) 
VALUES 
('Mixing', 1, 30, 1), ('Homogenization', 2, 20, 1), ('Pasteurization', 3, 15, 1),
('Aging', 4, 120, 0), ('Whipping', 5, 10, 1), ('Filling', 6, 20, 1),
('Hardening', 7, 240, 0), ('Packaging', 8, 30, 1);

-- 3.3. Nạp Tracking (Sử dụng ID thực tế vừa được tạo ra)
INSERT INTO production_tracking (production_order_id, stage_id, status)
SELECT o.production_order_id, s.production_stage_id, 'pending'
FROM production_orders o
CROSS JOIN production_stages s;

PRINT '=========================================================';
PRINT 'REFRESH COMPLETED SUCCESSFULLY!';
PRINT '=========================================================';