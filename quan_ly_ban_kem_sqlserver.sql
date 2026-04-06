/* =========================================================
   ICE CREAM MANAGEMENT DATABASE
   SQL Server Management Studio
   - Create database if not exists
   - Create tables only if not exists
   - Normalized to avoid duplicate data such as:
     unit_name -> units
     expiry_date -> ingredient_lots
   ========================================================= */

IF DB_ID(N'IceCreamManagement') IS NULL
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
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        user_id         INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        username        NVARCHAR(50) NOT NULL UNIQUE,
        password_hash   NVARCHAR(255) NOT NULL,
        role_id         INT NOT NULL,
        is_active       BIT NOT NULL CONSTRAINT DF_users_is_active DEFAULT (1),
        created_at      DATETIME2(0) NOT NULL CONSTRAINT DF_users_created_at DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_users_roles
            FOREIGN KEY (role_id) REFERENCES dbo.roles(role_id)
    );
END
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

/* =========================
   4) INGREDIENTS / RAW MATERIALS
   - origin, storage_condition, price_per_unit belong here
   - unit_name is not stored here to avoid duplication
   ========================= */
IF OBJECT_ID(N'dbo.ingredients', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ingredients (
        ingredient_id         INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ingredient_name       NVARCHAR(100) NOT NULL,
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

/* =========================
   6) ICE CREAM PRODUCTS
   ========================= */
IF OBJECT_ID(N'dbo.ice_creams', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ice_creams (
        ice_cream_id    INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        ice_cream_name  NVARCHAR(100) NOT NULL UNIQUE,
        is_active       BIT NOT NULL CONSTRAINT DF_ice_creams_is_active DEFAULT (1)
    );
END
GO

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

/* =========================
   9) PRODUCTION STAGES
   - 8 stages in order
   - store timing, actual volume, mold count, duration
   ========================= */
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

/* =========================
   INSERT ROLES (SAFE - NO DUPLICATE)
   ========================= */

-- Admin
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = N'Admin')
BEGIN
    INSERT INTO roles (role_name)
    VALUES (N'Admin');
END

-- Trưởng sản xuất
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = N'Trưởng sản xuất')
BEGIN
    INSERT INTO roles (role_name)
    VALUES (N'Trưởng sản xuất');
END

-- Quản lý kho
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = N'Quản lý kho')
BEGIN
    INSERT INTO roles (role_name)
    VALUES (N'Quản lý kho');
END

-- Nhân viên kinh doanh
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = N'Nhân viên kinh doanh')
BEGIN
    INSERT INTO roles (role_name)
    VALUES (N'Nhân viên kinh doanh');
END

USE IceCreamManagement;
GO

-- 1) Insert sample Roles first
INSERT INTO dbo.roles (role_name) 
VALUES (N'Admin'), (N'Manager'), (N'Staff');
GO

-- 2) Insert 10 Sample Users
-- Note: In a real app, 'password_hash' would be a Bcrypt/Argon2 string.
-- I'm using placeholder strings here for your testing.
INSERT INTO dbo.users (username, password_hash, role_id, is_active)
VALUES 
(N'admin_user',    N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 1, 1),
(N'manager_alice', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 2, 1),
(N'manager_bob',   N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 2, 1),
(N'staff_charlie', N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_david',   N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_eve',     N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_frank',   N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_grace',   N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_heidi',   N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 1),
(N'staff_ivan',    N'$2a$12$ubse3VV12.Cidq36X5tTNeWii7N7yi70tpKPFNOWd5vHbZtvcSV7i', 3, 0); -- One inactive user for testing
GO
