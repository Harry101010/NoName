/* =========================================================
   ICE CREAM MANAGEMENT DATABASE
   SQL Server Management Studio
   ========================================================= */
USE master; -- Chuyển sang master để SQL Server "thả" database kia ra
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'IceCreamManagement')
BEGIN
    DROP DATABASE IceCreamManagement;
END

BEGIN
    CREATE DATABASE IceCreamManagement;
END
GO

USE IceCreamManagement;
GO

/* =========================
   1) ROLES
   ========================= */
IF OBJECT_ID(N'dbo.roles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.roles (
        role_id     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        role_name   NVARCHAR(50) NOT NULL UNIQUE
    );
END
GO

/* =========================
   2) USERS
   ========================= */

/*
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        user_id         INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        username        NVARCHAR(50) NOT NULL UNIQUE,
        email           NVARCHAR(255) NULL UNIQUE,
        password_hash   NVARCHAR(255) NOT NULL,
        role_id         INT NOT NULL,
        is_active       BIT NOT NULL CONSTRAINT DF_users_is_active DEFAULT (1),
        created_at      DATETIME2(0) NOT NULL CONSTRAINT DF_users_created_at DEFAULT (SYSDATETIME()),
        reset_token     NVARCHAR(100) NULL,
	    reset_token_expired_at DATETIME NULL,
        CONSTRAINT FK_users_roles
            FOREIGN KEY (role_id) REFERENCES dbo.roles(role_id)
    );
END
GO */

IF NOT EXISTS (SELECT 1 FROM roles)
BEGIN
    INSERT INTO roles (role_name)
    VALUES (N'Admin'),
    (N'Production Supervisor'),
    (N'Storekeeper'),
	(N'Staff'),
    (N'Saleman');
END

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[users](
	[user_id] [int] IDENTITY(1,1) NOT NULL,
	[username] [nvarchar](50) NOT NULL,
	[password_hash] [nvarchar](255) NOT NULL,
	[role_id] [int] NOT NULL,
	[is_active] [bit] NOT NULL,
	[created_at] [datetime2](0) NOT NULL,
	[email] [nvarchar](100) NULL,
	[reset_token] [nvarchar](100) NULL,
	[reset_token_expired_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET IDENTITY_INSERT [dbo].[users] ON 

INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (1, N'admin_user', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 1, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), NULL, NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (2, N'manager_alice', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 2, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), N'osutadayoshi@gmail.com', NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (3, N'manager_bob', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 2, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), N'huuphuoc29791@gmail.com', NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (4, N'staff_charlie', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), NULL, NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (5, N'staff_david', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), NULL, NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (6, N'staff_eve', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), NULL, NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (7, N'staff_frank', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), NULL, NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (8, N'staff_grace', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), NULL, NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (9, N'staff_heidi', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), NULL, NULL, NULL)
INSERT [dbo].[users] ([user_id], [username], [password_hash], [role_id], [is_active], [created_at], [email], [reset_token], [reset_token_expired_at]) VALUES (10, N'staff_ivan', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 0, CAST(N'2026-04-08T23:38:02.0000000' AS DateTime2), NULL, NULL, NULL)
SET IDENTITY_INSERT [dbo].[users] OFF
GO
SET ANSI_PADDING ON
GO

/****** Object:  Index [UQ__users__F3DBC572EE622957]    Script Date: 11/04/2026 8:44:40 PM ******/
ALTER TABLE [dbo].[users] ADD UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [DF_users_is_active]  DEFAULT ((1)) FOR [is_active]
GO
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [DF_users_created_at]  DEFAULT (sysdatetime()) FOR [created_at]
GO
ALTER TABLE [dbo].[users]  WITH CHECK ADD  CONSTRAINT [FK_users_roles] FOREIGN KEY([role_id])
REFERENCES [dbo].[roles] ([role_id])
GO
ALTER TABLE [dbo].[users] CHECK CONSTRAINT [FK_users_roles]
GO


/* =========================
   3) UNITS
   ========================= */
IF OBJECT_ID(N'dbo.units', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.units (
        unit_id     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        unit_name   NVARCHAR(50) NOT NULL UNIQUE
    );
END
GO

-- Nạp Units nếu chưa có
IF NOT EXISTS (SELECT 1 FROM units)
BEGIN
    INSERT INTO units (unit_name) VALUES ('kg'), ('g'), ('liter'), ('pcs');
END

/* =========================
   4) INGREDIENTS / RAW MATERIALS
   - origin, storage_condition, price_per_unit belong here
   - unit_name is not stored here to avoid duplication
   ========================= */
IF OBJECT_ID(N'dbo.ingredients', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ingredients (
        ingredient_id         INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ingredient_name       NVARCHAR(100) NOT NULL UNIQUE,
        origin              NVARCHAR(200) NULL,
        storage_condition   NVARCHAR(255) NULL,
        unit_id             INT NOT NULL,
        price_per_unit      DECIMAL(18,2) NOT NULL,
        is_active           BIT NOT NULL CONSTRAINT DF_ingredients_is_active DEFAULT (1),
        CONSTRAINT FK_ingredients_units
            FOREIGN KEY (unit_id) REFERENCES dbo.units(unit_id),
        CONSTRAINT CK_ingredients_price_per_unit
            CHECK (price_per_unit >= 0)
    );
END
GO

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
/* =========================
   5) INGREDIENT LOTS / BATCHES
   - expiry_date belongs here, not in ingredients
   - FIFO can be handled by import_date, expiry_date, lot_id
   ========================= */

IF OBJECT_ID(N'dbo.ingredient_lots', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ingredient_lots (
        lot_id              INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ingredient_id         INT NOT NULL,
        import_date         DATETIME2(0) NOT NULL CONSTRAINT DF_ingredient_lots_import_date DEFAULT (SYSDATETIME()),
        expiry_date         DATE NOT NULL,
        received_quantity   DECIMAL(18,3) NOT NULL,
        remaining_quantity  DECIMAL(18,3) NOT NULL,
        CONSTRAINT FK_ingredient_lots_ingredients
            FOREIGN KEY (ingredient_id) REFERENCES dbo.ingredients(ingredient_id),
        CONSTRAINT CK_ingredient_lots_received_quantity
            CHECK (received_quantity > 0),
        CONSTRAINT CK_ingredient_lots_remaining_quantity
            CHECK (remaining_quantity >= 0),
        CONSTRAINT CK_ingredient_lots_remaining_le_received
            CHECK (remaining_quantity <= received_quantity)
    );
END
GO

PRINT '--- Đang nạp số lượng tồn kho ban đầu vào bảng ingredient_lots ---';

INSERT INTO ingredient_lots (ingredient_id, expiry_date, received_quantity, remaining_quantity)
SELECT i.ingredient_id, '2026-12-31', v.initial_qty, v.initial_qty
FROM ingredients i
JOIN (VALUES 
    ('Fresh Milk', 50.0),      -- 50 lít
    ('Sugar', 100.0),          -- 100 kg
    ('Matcha Powder', 10.0),   -- 10 g
    ('Cocoa Powder', 20.0),    -- 20 kg
    ('Strawberry', 30.0),      -- 30 kg
    ('Heavy Cream', 20.0),     -- 20 lít
    ('Vanilla Extract', 5.0),  -- 5 g
    ('Sea Salt', 5.0),         -- 5 kg
    ('Shredded Coconut', 10.0),-- 10 kg
    ('Unsalted Butter', 20.0), -- 20 kg
    ('Almond', 15.0),          -- 15 kg
    ('Chocolate Chips', 25.0), -- 25 kg
    ('Milk Powder', 50.0),     -- 50 kg
    ('Glucose Syrup', 10.0),   -- 10 kg
    ('Eggs', 100.0),           -- 100 quả
    ('Strawberry Jam', 15.0),  -- 15 kg
    ('Peppermint', 2.0),       -- 2 g
    ('Walnut', 10.0),          -- 10 kg
    ('Caramel', 5.0),          -- 5 lít
    ('CMC Stabilizer', 2.0)    -- 2 kg
) AS v(ing_name, initial_qty) ON i.ingredient_name = v.ing_name;

PRINT '--- Đã nạp xong số lượng cho các nguyên liệu ---';
GO

/* =========================
   6) ICE CREAM PRODUCTS
   ========================= */

IF OBJECT_ID(N'dbo.ice_creams', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ice_creams (
        ice_cream_id    INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ice_cream_name  NVARCHAR(100) NOT NULL,
        is_active       BIT NOT NULL CONSTRAINT DF_ice_creams_is_active DEFAULT (1)
    );
END
GO

-- Products (5 types)
INSERT INTO ice_creams (ice_cream_name) VALUES 
('Vanilla Ice Cream'), ('Matcha Ice Cream'), ('Chocolate Ice Cream'), 
('Strawberry Ice Cream'), ('Walnut Ice Cream');


/* =========================
   7) RECIPE / MATERIAL NORM
   - One ice cream uses many ingredients
   - One ingredient can be used in many ice creams
   - quantity_per_kg is the amount needed for 1 kg finished product
   ========================= */
IF OBJECT_ID(N'dbo.recipes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.recipes (
        recipe_id           INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ice_cream_id        INT NOT NULL,
        ingredient_id         INT NOT NULL,
        quantity_per_kg     DECIMAL(18,3) NOT NULL,
        CONSTRAINT FK_recipes_ice_creams
            FOREIGN KEY (ice_cream_id) REFERENCES dbo.ice_creams(ice_cream_id),
        CONSTRAINT FK_recipes_ingredients
            FOREIGN KEY (ingredient_id) REFERENCES dbo.ingredients(ingredient_id),
        CONSTRAINT UQ_recipes_icecream_ingredient
            UNIQUE (ice_cream_id, ingredient_id),
        CONSTRAINT CK_recipes_quantity_per_kg
            CHECK (quantity_per_kg > 0)
    );
END
GO

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

/* =========================
   8) PRODUCTION ORDERS
   - One order = one production batch for one ice cream
   ========================= */

IF OBJECT_ID(N'dbo.production_orders', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.production_orders (
        production_order_id     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ice_cream_id            INT NOT NULL,
        planned_output_kg       DECIMAL(18,3) NOT NULL,
        created_by              INT NULL,
        created_at              DATETIME2(0) NOT NULL CONSTRAINT DF_production_orders_created_at DEFAULT (SYSDATETIME()),
        order_status            NVARCHAR(30) NOT NULL CONSTRAINT DF_production_orders_status DEFAULT (N'draft'),
        note                    NVARCHAR(500) NULL,
        CONSTRAINT FK_production_orders_ice_creams
            FOREIGN KEY (ice_cream_id) REFERENCES dbo.ice_creams(ice_cream_id),
        CONSTRAINT FK_production_orders_users
            FOREIGN KEY (created_by) REFERENCES dbo.users(user_id),
        CONSTRAINT CK_production_orders_planned_output_kg
            CHECK (planned_output_kg > 0),
        CONSTRAINT CK_production_orders_status
            CHECK (order_status IN (N'draft', N'waiting_ingredient', N'in_progress', N'finished', N'cancelled'))
    );
END
GO


--  Nạp Order
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

/* =========================
   9) PRODUCTION STAGES
   - 8 stages in order
   - store timing, actual volume, mold count, duration
   ========================= */

/*

IF OBJECT_ID(N'dbo.production_stages', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.production_stages (
        production_stage_id     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        production_order_id     INT NOT NULL,
        stage_no                TINYINT NOT NULL,
        stage_name              NVARCHAR(100) NOT NULL,
        planned_duration_min    INT NULL,
        actual_duration_min     INT NULL,
        actual_volume           DECIMAL(18,3) NULL,
        mold_count              INT NULL,
        start_time              DATETIME2(0) NULL,
        end_time                DATETIME2(0) NULL,
        stage_status            NVARCHAR(30) NOT NULL CONSTRAINT DF_production_stages_status DEFAULT (N'pending'),
        recorded_by             INT NULL,
        note                    NVARCHAR(500) NULL,
        CONSTRAINT FK_production_stages_orders
            FOREIGN KEY (production_order_id) REFERENCES dbo.production_orders(production_order_id),
        CONSTRAINT FK_production_stages_users
            FOREIGN KEY (recorded_by) REFERENCES dbo.users(user_id),
        CONSTRAINT UQ_production_stages_order_stage_no
            UNIQUE (production_order_id, stage_no),
        CONSTRAINT CK_production_stages_stage_no
            CHECK (stage_no BETWEEN 1 AND 8),
        CONSTRAINT CK_production_stages_status
            CHECK (stage_status IN (N'pending', N'open', N'in_progress', N'completed', N'blocked')),
        CONSTRAINT CK_production_stages_planned_duration
            CHECK (planned_duration_min IS NULL OR planned_duration_min >= 0),
        CONSTRAINT CK_production_stages_actual_duration
            CHECK (actual_duration_min IS NULL OR actual_duration_min >= 0),
        CONSTRAINT CK_production_stages_actual_volume
            CHECK (actual_volume IS NULL OR actual_volume >= 0),
        CONSTRAINT CK_production_stages_mold_count
            CHECK (mold_count IS NULL OR mold_count >= 0)
    );
END
GO
*/

-- Bảng Stage (Định nghĩa công đoạn - Khớp với ProductionStage.java)

CREATE TABLE dbo.production_stages (
    stage_id INT PRIMARY KEY IDENTITY(1,1), 
    stage_name NVARCHAR(100) NOT NULL,
    sequence_order INT NOT NULL, 
    standard_time_minutes DECIMAL(10,2) DEFAULT 0,
    is_proportional BIT DEFAULT 0 
);

--  Nạp Production Stages
INSERT INTO production_stages (stage_name, sequence_order, standard_time_minutes, is_proportional) 
VALUES 
('Mixing', 1, 30, 1), ('Homogenization', 2, 20, 1), ('Pasteurization', 3, 15, 1),
('Aging', 4, 120, 0), ('Whipping', 5, 10, 1), ('Filling', 6, 20, 1),
('Hardening', 7, 240, 0), ('Packaging', 8, 30, 1);


-- Tạo bảng theo doi sản xuất

CREATE TABLE dbo.production_tracking (
    tracking_id INT PRIMARY KEY IDENTITY(1,1),
    production_order_id INT NOT NULL,
    stage_id INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'pending',
    actual_start_time DATETIME NULL,
    actual_end_time DATETIME NULL,
    actual_quantity DECIMAL(18,3) DEFAULT 0,
    CONSTRAINT FK_Tracking_Order FOREIGN KEY (production_order_id) REFERENCES dbo.production_orders(production_order_id),
    CONSTRAINT FK_Tracking_Stage FOREIGN KEY (stage_id) REFERENCES dbo.production_stages(stage_id)
);

/* =========================
   10) MATERIAL EXPORT REQUESTS
   - phiếu yêu cầu xuất kho nguyên liệu
   - one request per production order
   ========================= */
IF OBJECT_ID(N'dbo.ingredient_export_requests', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ingredient_export_requests (
        ingredient_export_request_id  INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        production_order_id         INT NOT NULL,
        requested_by                INT NULL,
        requested_at                DATETIME2(0) NOT NULL CONSTRAINT DF_ingredient_export_requests_requested_at DEFAULT (SYSDATETIME()),
        request_status              NVARCHAR(30) NOT NULL CONSTRAINT DF_ingredient_export_requests_status DEFAULT (N'pending'),
        note                        NVARCHAR(500) NULL,
        CONSTRAINT FK_ingredient_export_requests_orders
            FOREIGN KEY (production_order_id) REFERENCES dbo.production_orders(production_order_id),
        CONSTRAINT FK_ingredient_export_requests_users
            FOREIGN KEY (requested_by) REFERENCES dbo.users(user_id),
        CONSTRAINT UQ_ingredient_export_requests_order
            UNIQUE (production_order_id),
        CONSTRAINT CK_ingredient_export_requests_status
            CHECK (request_status IN (N'pending', N'approved', N'rejected', N'completed'))
    );
END
GO

/* =========================
   11) MATERIAL EXPORT REQUEST DETAILS
   - one request contains multiple ingredients
   - required_quantity is calculated based on planned output kg
   ========================= */
IF OBJECT_ID(N'dbo.ingredient_export_request_details', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ingredient_export_request_details (
        ingredient_export_request_detail_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ingredient_export_request_id          INT NOT NULL,
        ingredient_id                         INT NOT NULL,
        required_quantity                   DECIMAL(18,3) NOT NULL,
        CONSTRAINT FK_ingredient_export_request_details_requests
            FOREIGN KEY (ingredient_export_request_id) REFERENCES dbo.ingredient_export_requests(ingredient_export_request_id),
        CONSTRAINT FK_ingredient_export_request_details_ingredients
            FOREIGN KEY (ingredient_id) REFERENCES dbo.ingredients(ingredient_id),
        CONSTRAINT UQ_ingredient_export_request_details_request_ingredient
            UNIQUE (ingredient_export_request_id, ingredient_id),
        CONSTRAINT CK_ingredient_export_request_details_required_quantity
            CHECK (required_quantity > 0)
    );
END
GO

/* =========================
   12) MATERIAL EXPORT RECEIPTS
   - phiếu xuất kho nguyên liệu
   - one receipt per request
   ========================= */
IF OBJECT_ID(N'dbo.ingredient_export_receipts', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ingredient_export_receipts (
        ingredient_export_receipt_id  INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ingredient_export_request_id   INT NOT NULL,
        approved_by                  INT NULL,
        created_at                   DATETIME2(0) NOT NULL CONSTRAINT DF_ingredient_export_receipts_created_at DEFAULT (SYSDATETIME()),
        receipt_status               NVARCHAR(30) NOT NULL CONSTRAINT DF_ingredient_export_receipts_status DEFAULT (N'pending'),
        note                         NVARCHAR(500) NULL,
        CONSTRAINT FK_ingredient_export_receipts_requests
            FOREIGN KEY (ingredient_export_request_id) REFERENCES dbo.ingredient_export_requests(ingredient_export_request_id),
        CONSTRAINT FK_ingredient_export_receipts_users
            FOREIGN KEY (approved_by) REFERENCES dbo.users(user_id),
        CONSTRAINT UQ_ingredient_export_receipts_request
            UNIQUE (ingredient_export_request_id),
        CONSTRAINT CK_ingredient_export_receipts_status
            CHECK (receipt_status IN (N'pending', N'approved', N'rejected', N'completed'))
    );
END
GO

/* =========================
   13) MATERIAL EXPORT RECEIPT DETAILS
   - track FIFO by lot usage
   ========================= */
IF OBJECT_ID(N'dbo.ingredient_export_receipt_details', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ingredient_export_receipt_details (
        ingredient_export_receipt_detail_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ingredient_export_receipt_id          INT NOT NULL,
        ingredient_export_request_detail_id    INT NOT NULL,
        lot_id                              INT NOT NULL,
        issued_quantity                     DECIMAL(18,3) NOT NULL,
        CONSTRAINT FK_ingredient_export_receipt_details_receipts
            FOREIGN KEY (ingredient_export_receipt_id) REFERENCES dbo.ingredient_export_receipts(ingredient_export_receipt_id),
        CONSTRAINT FK_ingredient_export_receipt_details_request_details
            FOREIGN KEY (ingredient_export_request_detail_id) REFERENCES dbo.ingredient_export_request_details(ingredient_export_request_detail_id),
        CONSTRAINT FK_ingredient_export_receipt_details_lots
            FOREIGN KEY (lot_id) REFERENCES dbo.ingredient_lots(lot_id),
        CONSTRAINT CK_ingredient_export_receipt_details_issued_quantity
            CHECK (issued_quantity > 0)
    );
END
GO

/* =========================
   14) FINISHED STOCK REQUESTS
   - phiếu yêu cầu nhập tồn kho thành phẩm
   - one request per production order
   ========================= */
IF OBJECT_ID(N'dbo.finished_stock_requests', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.finished_stock_requests (
        finished_stock_request_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        production_order_id         INT NOT NULL,
        requested_by                INT NULL,
        requested_quantity          DECIMAL(18,3) NOT NULL,
        requested_at                DATETIME2(0) NOT NULL CONSTRAINT DF_finished_stock_requests_requested_at DEFAULT (SYSDATETIME()),
        request_status              NVARCHAR(30) NOT NULL CONSTRAINT DF_finished_stock_requests_status DEFAULT (N'pending'),
        note                        NVARCHAR(500) NULL,
        CONSTRAINT FK_finished_stock_requests_orders
            FOREIGN KEY (production_order_id) REFERENCES dbo.production_orders(production_order_id),
        CONSTRAINT FK_finished_stock_requests_users
            FOREIGN KEY (requested_by) REFERENCES dbo.users(user_id),
        CONSTRAINT UQ_finished_stock_requests_order
            UNIQUE (production_order_id),
        CONSTRAINT CK_finished_stock_requests_requested_quantity
            CHECK (requested_quantity > 0),
        CONSTRAINT CK_finished_stock_requests_status
            CHECK (request_status IN (N'pending', N'approved', N'rejected', N'completed'))
    );
END
GO

/* =========================
   15) FINISHED STOCK RECEIPTS
   - phiếu nhập tồn kem
   - one receipt per request
   ========================= */
IF OBJECT_ID(N'dbo.finished_stock_receipts', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.finished_stock_receipts (
        finished_stock_receipt_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        finished_stock_request_id    INT NOT NULL,
        approved_by                  INT NULL,
        received_quantity            DECIMAL(18,3) NOT NULL,
        created_at                   DATETIME2(0) NOT NULL CONSTRAINT DF_finished_stock_receipts_created_at DEFAULT (SYSDATETIME()),
        /* receipt_status               NVARCHAR(30) NOT NULL CONSTRAINT DF_finished_stock_receipts_status DEFAULT (N'pending'), */
        note                         NVARCHAR(500) NULL,
        CONSTRAINT FK_finished_stock_receipts_requests
            FOREIGN KEY (finished_stock_request_id) REFERENCES dbo.finished_stock_requests(finished_stock_request_id),
        CONSTRAINT FK_finished_stock_receipts_users
            FOREIGN KEY (approved_by) REFERENCES dbo.users(user_id),
        CONSTRAINT UQ_finished_stock_receipts_request
            UNIQUE (finished_stock_request_id),
        CONSTRAINT CK_finished_stock_receipts_received_quantity
            CHECK (received_quantity > 0),
        /*
        CONSTRAINT CK_finished_stock_receipts_status
            CHECK (receipt_status IN (N'pending', N'approved', N'rejected', N'completed'))
        */
    );
END
GO

/* =========================
   16) FINISHED INVENTORY
   - stock by ice cream type
   ========================= */
IF OBJECT_ID(N'dbo.finished_inventory', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.finished_inventory (
        ice_cream_id        INT NOT NULL PRIMARY KEY,
        quantity_on_hand    DECIMAL(18,3) NOT NULL CONSTRAINT DF_finished_inventory_quantity DEFAULT (0),
        last_updated        DATETIME2(0) NOT NULL CONSTRAINT DF_finished_inventory_last_updated DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_finished_inventory_ice_creams
            FOREIGN KEY (ice_cream_id) REFERENCES dbo.ice_creams(ice_cream_id),
        CONSTRAINT CK_finished_inventory_quantity_on_hand
            CHECK (quantity_on_hand >= 0)
    );
END
GO

-- 17. BẢNG TỒN KHO THÀNH PHẨM (Nơi chứa số lượng hiện thực tế)
IF OBJECT_ID(N'dbo.finished_product_inventory', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.finished_product_inventory (
        inventory_id INT IDENTITY(1,1) PRIMARY KEY,
        production_po_code NVARCHAR(50) NULL,
        product_name NVARCHAR(200) NOT NULL,
        current_quantity DECIMAL(18,3) NOT NULL DEFAULT (0),
        mfg_date DATETIME2(0) NULL,
        exp_date DATETIME2(0) NULL,
        storage_location NVARCHAR(100) NULL
    );
END
GO

-- 18. BẢNG PHIẾU YÊU CẦU XUẤT KHO (Bảng cha - Lưu thông tin chung của đơn)
IF OBJECT_ID(N'dbo.product_issue_notes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.product_issue_notes (
        note_id INT IDENTITY(1,1) PRIMARY KEY,
        saleman_id INT NOT NULL,
        customer_name NVARCHAR(200) NOT NULL,
        customer_order_code NVARCHAR(50) NULL,
        delivery_date DATETIME2(0) NULL,
        status NVARCHAR(50) DEFAULT N'Chờ duyệt',
        note NVARCHAR(MAX) NULL,
        create_date DATETIME2(0) DEFAULT GETDATE(),
        FOREIGN KEY (saleman_id) REFERENCES dbo.users(user_id)
    );
END
GO

-- 19. BẢNG CHI TIẾT PHIẾU YÊU CẦU (Bảng con - Lưu từng món kem trong phiếu)
IF OBJECT_ID(N'dbo.product_issue_details', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.product_issue_details (
        detail_id INT IDENTITY(1,1) PRIMARY KEY,
        note_id INT NOT NULL,
        ice_cream_id INT NOT NULL,
        quantity DECIMAL(18,3) NOT NULL DEFAULT (0),
        FOREIGN KEY (note_id) REFERENCES dbo.product_issue_notes(note_id) ON DELETE CASCADE,
        FOREIGN KEY (ice_cream_id) REFERENCES dbo.ice_creams(ice_cream_id)
    );
END

----------------------------------------------
USE IceCreamManagement;
GO

-- 1. Thêm cột is_deleted vào bảng ingredient_lots để sửa lỗi WarehouseRepository
IF NOT EXISTS (SELECT * FROM sys.columns 
               WHERE object_id = OBJECT_ID('dbo.ingredient_lots') 
               AND name = 'is_deleted')
BEGIN
    ALTER TABLE dbo.ingredient_lots ADD is_deleted BIT NOT NULL DEFAULT 0;
    PRINT 'Đã thêm cột is_deleted vào bảng ingredient_lots.';
END
GO

-- 2. Thêm cột must_change_password vào bảng users để sửa lỗi cảnh báo
IF NOT EXISTS (SELECT * FROM sys.columns 
               WHERE object_id = OBJECT_ID('dbo.users') 
               AND name = 'must_change_password')
BEGIN
    ALTER TABLE dbo.users ADD must_change_password BIT NOT NULL DEFAULT 0;
    PRINT 'Đã thêm cột must_change_password vào bảng users.';
END
GO

--tới dây


-- Bảng nhà cung cấp

GO
CREATE TABLE suppliers (
    supplier_id INT IDENTITY PRIMARY KEY,
    supplier_name NVARCHAR(200) NOT NULL,
    phone NVARCHAR(20),
    email NVARCHAR(100),
    address NVARCHAR(255),
    is_active BIT DEFAULT 1
);
ALTER TABLE ingredient_lots
ADD supplier_id INT;

ALTER TABLE ingredient_lots
ADD CONSTRAINT FK_lot_supplier
FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id);
IF OBJECT_ID(N'dbo.product_issue_details', N'U') IS NOT NULL DROP TABLE dbo.product_issue_details;
IF OBJECT_ID(N'dbo.product_issue_notes', N'U') IS NOT NULL DROP TABLE dbo.product_issue_notes;
IF OBJECT_ID(N'dbo.ingredient_lots', N'U') IS NOT NULL DROP TABLE dbo.ingredient_lots;
IF OBJECT_ID(N'dbo.suppliers', N'U') IS NOT NULL DROP TABLE dbo.suppliers;
CREATE TABLE [dbo].[suppliers](
    [supplier_id] INT IDENTITY(1,1) PRIMARY KEY,
    [supplier_name] NVARCHAR(200) NOT NULL,
    [phone] NVARCHAR(20),
    [email] NVARCHAR(100),
    [address] NVARCHAR(255),
    [is_active] BIT DEFAULT 1
);
CREATE TABLE [dbo].[ingredient_lots](
    [lot_id]             INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [ingredient_id]      INT NOT NULL,
    [import_date]        DATETIME2(0) NOT NULL DEFAULT (SYSDATETIME()),
    [expiry_date]        DATE NOT NULL,
    [received_quantity]  DECIMAL(18, 3) NOT NULL,
    [remaining_quantity] DECIMAL(18, 3) NOT NULL,
    [supplier_id]        INT NULL,
    [is_deleted]         BIT NOT NULL DEFAULT (0),
    CONSTRAINT [FK_ingredient_lots_ingredients] FOREIGN KEY([ingredient_id]) REFERENCES [dbo].[ingredients] ([ingredient_id]),
    CONSTRAINT [FK_lot_supplier] FOREIGN KEY([supplier_id]) REFERENCES [dbo].[suppliers] ([supplier_id])
);
CREATE TABLE [dbo].[product_issue_notes](
    [note_id]             INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [saleman_id]          INT NOT NULL,
    [customer_name]       NVARCHAR(200) NOT NULL,
    [customer_order_code] NVARCHAR(50) NULL,
    [delivery_date]       DATETIME2(0) NULL,
    [status]              NVARCHAR(50) DEFAULT (N'Chờ duyệt'),
    [note]                NVARCHAR(MAX) NULL,
    [create_date]         DATETIME2(0) DEFAULT (GETDATE()),
    CONSTRAINT [FK_product_issue_notes_users] FOREIGN KEY([saleman_id]) REFERENCES [dbo].[users] ([user_id])
);



-- 1. Tạo bảng suppliers nếu chưa có
IF OBJECT_ID(N'dbo.suppliers', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.suppliers(
        [supplier_id] INT IDENTITY(1,1) PRIMARY KEY,
        [supplier_name] NVARCHAR(200) NOT NULL,
        [phone] NVARCHAR(20),
        [email] NVARCHAR(100),
        [address] NVARCHAR(255),
        [is_active] BIT DEFAULT 1
    );
END
GO

-- 2. Chỉ thêm cột supplier_id vào bảng ingredient_lots nếu nó chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.ingredient_lots') AND name = 'supplier_id')
BEGIN
    ALTER TABLE dbo.ingredient_lots ADD supplier_id INT;
END
GO

-- 3. Chỉ thêm khóa ngoại nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_lot_supplier')
BEGIN
    ALTER TABLE dbo.ingredient_lots 
    ADD CONSTRAINT FK_lot_supplier FOREIGN KEY (supplier_id) REFERENCES dbo.suppliers(supplier_id);
END
GO