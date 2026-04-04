-- SQL Server script: Quản lý bán kem / quản lý sản xuất kem
-- Bản dựng lại theo phân tích yêu cầu và khung triển khai đã chốt.
-- Thiết kế ưu tiên: dễ chạy trong SSMS, rõ quan hệ, bám sát luồng sản xuất - kho - tồn thành phẩm.

IF DB_ID(N'BanKemDB') IS NULL
BEGIN
    CREATE DATABASE BanKemDB;
END
GO

USE BanKemDB;
GO

/*========================================================
  CLEAN UP: cho phép chạy lại nhiều lần
========================================================*/
IF OBJECT_ID(N'dbo.product_inventory_transactions', N'U') IS NOT NULL DROP TABLE dbo.product_inventory_transactions;
IF OBJECT_ID(N'dbo.finished_goods_inventory', N'U') IS NOT NULL DROP TABLE dbo.finished_goods_inventory;
IF OBJECT_ID(N'dbo.warehouse_receive_receipts', N'U') IS NOT NULL DROP TABLE dbo.warehouse_receive_receipts;
IF OBJECT_ID(N'dbo.warehouse_request_receive', N'U') IS NOT NULL DROP TABLE dbo.warehouse_request_receive;
IF OBJECT_ID(N'dbo.warehouse_issue_receipt_details', N'U') IS NOT NULL DROP TABLE dbo.warehouse_issue_receipt_details;
IF OBJECT_ID(N'dbo.warehouse_issue_receipts', N'U') IS NOT NULL DROP TABLE dbo.warehouse_issue_receipts;
IF OBJECT_ID(N'dbo.warehouse_request_issue', N'U') IS NOT NULL DROP TABLE dbo.warehouse_request_issue;
IF OBJECT_ID(N'dbo.production_stages', N'U') IS NOT NULL DROP TABLE dbo.production_stages;
IF OBJECT_ID(N'dbo.production_orders', N'U') IS NOT NULL DROP TABLE dbo.production_orders;
IF OBJECT_ID(N'dbo.recipe_details', N'U') IS NOT NULL DROP TABLE dbo.recipe_details;
IF OBJECT_ID(N'dbo.recipes', N'U') IS NOT NULL DROP TABLE dbo.recipes;
IF OBJECT_ID(N'dbo.ice_cream_products', N'U') IS NOT NULL DROP TABLE dbo.ice_cream_products;
IF OBJECT_ID(N'dbo.ingredient_batches', N'U') IS NOT NULL DROP TABLE dbo.ingredient_batches;
IF OBJECT_ID(N'dbo.ingredients', N'U') IS NOT NULL DROP TABLE dbo.ingredients;
IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL DROP TABLE dbo.users;
IF OBJECT_ID(N'dbo.roles', N'U') IS NOT NULL DROP TABLE dbo.roles;
GO

/*========================================================
  1) ROLES + USERS
========================================================*/
CREATE TABLE dbo.roles (
    role_id     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    role_name   NVARCHAR(100) NOT NULL UNIQUE
);
GO

CREATE TABLE dbo.users (
    user_id         INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    username        NVARCHAR(50)  NOT NULL UNIQUE,
    password_hash   NVARCHAR(255) NOT NULL,
    full_name       NVARCHAR(100) NOT NULL,
    role_id         INT NOT NULL,
    is_active       BIT NOT NULL CONSTRAINT DF_users_is_active DEFAULT (1),
    created_at      DATETIME2 NOT NULL CONSTRAINT DF_users_created_at DEFAULT (SYSDATETIME()),
    CONSTRAINT FK_users_roles
        FOREIGN KEY (role_id) REFERENCES dbo.roles(role_id)
);
GO

/*========================================================
  2) INGREDIENTS + LOTS
========================================================*/
CREATE TABLE dbo.ingredients (
    ingredient_id          INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    ingredient_name        NVARCHAR(150) NOT NULL UNIQUE,
    storage_instructions   NVARCHAR(255) NULL,
    unit_name              NVARCHAR(50) NOT NULL,
    description            NVARCHAR(500) NULL,
    is_active              BIT NOT NULL DEFAULT (1)
);

CREATE TABLE dbo.ingredient_batches (
    batch_id            INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    ingredient_id       INT NOT NULL,
    batch_code          NVARCHAR(50) NOT NULL UNIQUE,
    origin              NVARCHAR(255) NULL,
    unit_cost           DECIMAL(18,2) NOT NULL CHECK (unit_cost >= 0),
    import_date         DATE NOT NULL,
    expiry_date         DATE NOT NULL,
    quantity_on_hand    DECIMAL(18,3) NOT NULL CHECK (quantity_on_hand >= 0),
    status              NVARCHAR(30) NOT NULL DEFAULT (N'AVAILABLE'),
    note                NVARCHAR(255) NULL,

    CONSTRAINT FK_ingredient_batches_ingredients
        FOREIGN KEY (ingredient_id) REFERENCES dbo.ingredients(ingredient_id)
);

-- Index to optimize batch lookup per ingredient
-- Supports FIFO (import_date) and FEFO (expiry_date) retrieval
-- Used when selecting batches for issuing materials in production
CREATE INDEX IX_ingredient_batches_ingredient_import
ON dbo.ingredient_batches (ingredient_id, import_date, expiry_date);
GO

/*========================================================
  3) PRODUCTS + RECIPES
========================================================*/
CREATE TABLE dbo.ice_cream_products (
    product_id      INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_name    NVARCHAR(150) NOT NULL UNIQUE,
    description     NVARCHAR(500) NULL,
    is_active       BIT NOT NULL CONSTRAINT DF_ice_cream_products_is_active DEFAULT (1)
);
GO

CREATE TABLE dbo.recipes (
    recipe_id           INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_id          INT NOT NULL,
    standard_output_kg  DECIMAL(18,3) NOT NULL
        CONSTRAINT CK_recipes_standard_output CHECK (standard_output_kg > 0),
    note                NVARCHAR(255) NULL,

    CONSTRAINT UQ_recipes_product UNIQUE (product_id),
    CONSTRAINT FK_recipes_products
        FOREIGN KEY (product_id) REFERENCES dbo.ice_cream_products(product_id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
GO

CREATE TABLE dbo.recipe_details (
    recipe_detail_id    INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    recipe_id           INT NOT NULL,
    ingredient_id       INT NOT NULL,
    quantity_per_kg     DECIMAL(18,3) NOT NULL
        CONSTRAINT CK_recipe_details_qty CHECK (quantity_per_kg > 0),
    note                NVARCHAR(255) NULL,

    CONSTRAINT FK_recipe_details_recipes
        FOREIGN KEY (recipe_id) REFERENCES dbo.recipes(recipe_id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,

    CONSTRAINT FK_recipe_details_ingredients
        FOREIGN KEY (ingredient_id) REFERENCES dbo.ingredients(ingredient_id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,

    CONSTRAINT UQ_recipe_details_recipe_ingredient UNIQUE (recipe_id, ingredient_id)
);
GO

CREATE INDEX IX_recipe_details_ingredient
ON dbo.recipe_details (ingredient_id);
GO

/*========================================================
  4) PRODUCTION ORDERS + STAGES
========================================================*/
CREATE TABLE dbo.production_orders (
    production_order_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_id            INT NOT NULL,
    expected_output_kg    DECIMAL(18,3) NOT NULL
        CONSTRAINT CK_production_orders_expected CHECK (expected_output_kg > 0),
    actual_output_kg      DECIMAL(18,3) NULL,
    status                NVARCHAR(30) NOT NULL
        CONSTRAINT DF_production_orders_status DEFAULT (N'NEW'),
    created_by            INT NOT NULL,
    created_at            DATETIME2 NOT NULL
        CONSTRAINT DF_production_orders_created_at DEFAULT (SYSDATETIME()),
    completed_at          DATETIME2 NULL,
    note                  NVARCHAR(255) NULL,

    CONSTRAINT FK_production_orders_products
        FOREIGN KEY (product_id) REFERENCES dbo.ice_cream_products(product_id),

    CONSTRAINT FK_production_orders_users
        FOREIGN KEY (created_by) REFERENCES dbo.users(user_id),

    CONSTRAINT CK_production_orders_status
        CHECK (status IN (N'NEW', N'WAITING_ISSUE', N'IN_PROCESS', N'WAITING_RECEIPT', N'COMPLETED', N'CANCELLED')),

    CONSTRAINT CK_production_orders_completed_rule
        CHECK (
            (status = N'COMPLETED'
                AND actual_output_kg IS NOT NULL
                AND actual_output_kg >= 0
                AND completed_at IS NOT NULL)
            OR
            (status <> N'COMPLETED'
                AND actual_output_kg IS NULL
                AND completed_at IS NULL)
        )
);
GO

CREATE INDEX IX_production_orders_product_status
ON dbo.production_orders (product_id, status, created_at DESC);
GO

CREATE TABLE dbo.production_stages (
    stage_id             INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    production_order_id  INT NOT NULL,
    stage_code           NVARCHAR(50) NOT NULL,
    stage_name           NVARCHAR(150) NOT NULL,
    stage_order          INT NOT NULL,
    status               NVARCHAR(30) NOT NULL
        CONSTRAINT DF_production_stages_status DEFAULT (N'LOCKED'),
    started_at           DATETIME2 NULL,
    completed_at         DATETIME2 NULL,
    confirmed_by         INT NULL,
    volume_value         DECIMAL(18,3) NULL,
    time_setting_value   INT NULL,
    number_of_molds      INT NULL,
    note                 NVARCHAR(255) NULL,

    CONSTRAINT FK_production_stages_orders
        FOREIGN KEY (production_order_id) REFERENCES dbo.production_orders(production_order_id),

    CONSTRAINT FK_production_stages_users
        FOREIGN KEY (confirmed_by) REFERENCES dbo.users(user_id),

    CONSTRAINT UQ_production_stages_order_stage
        UNIQUE (production_order_id, stage_order),

    CONSTRAINT UQ_production_stages_order_code
        UNIQUE (production_order_id, stage_code),

    CONSTRAINT CK_production_stages_stage_order
        CHECK (stage_order > 0),

    CONSTRAINT CK_production_stages_status
        CHECK (status IN (N'LOCKED', N'AVAILABLE', N'IN_PROGRESS', N'DONE')),

    CONSTRAINT CK_production_stages_volume
        CHECK (volume_value IS NULL OR volume_value >= 0),

    CONSTRAINT CK_production_stages_time_setting
        CHECK (time_setting_value IS NULL OR time_setting_value >= 0),

    CONSTRAINT CK_production_stages_number_of_molds
        CHECK (number_of_molds IS NULL OR number_of_molds >= 0),

    CONSTRAINT CK_production_stages_status_rule
        CHECK (
            (status IN (N'LOCKED', N'AVAILABLE')
                AND started_at IS NULL
                AND completed_at IS NULL
                AND confirmed_by IS NULL)
            OR
            (status = N'IN_PROGRESS'
                AND started_at IS NOT NULL
                AND completed_at IS NULL
                AND confirmed_by IS NULL)
            OR
            (status = N'DONE'
                AND started_at IS NOT NULL
                AND completed_at IS NOT NULL
                AND confirmed_by IS NOT NULL)
        )
);
GO

CREATE INDEX IX_production_stages_order_status
ON dbo.production_stages (production_order_id, status, stage_order);
GO

/*========================================================
  5) ISSUE REQUEST / ISSUE RECEIPT (xuất nguyên liệu)
========================================================*/
CREATE TABLE dbo.warehouse_request_issue (
    request_issue_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    production_order_id INT NOT NULL,
    requested_by       INT NOT NULL,
    requested_at       DATETIME2 NOT NULL CONSTRAINT DF_request_issue_requested_at DEFAULT (SYSDATETIME()),
    status             NVARCHAR(30) NOT NULL CONSTRAINT DF_request_issue_status DEFAULT (N'PENDING'),
    note               NVARCHAR(255) NULL,
    CONSTRAINT FK_request_issue_orders
        FOREIGN KEY (production_order_id) REFERENCES dbo.production_orders(production_order_id),
    CONSTRAINT FK_request_issue_users
        FOREIGN KEY (requested_by) REFERENCES dbo.users(user_id),
    CONSTRAINT CK_request_issue_status CHECK (status IN (N'PENDING', N'APPROVED', N'REJECTED', N'DONE'))
);
GO

CREATE TABLE dbo.warehouse_issue_receipts (
    issue_receipt_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    request_issue_id   INT NOT NULL,
    issued_by          INT NOT NULL,
    issued_at          DATETIME2 NOT NULL CONSTRAINT DF_issue_receipts_issued_at DEFAULT (SYSDATETIME()),
    status             NVARCHAR(30) NOT NULL CONSTRAINT DF_issue_receipts_status DEFAULT (N'APPROVED'),
    note               NVARCHAR(255) NULL,
    CONSTRAINT FK_issue_receipts_request
        FOREIGN KEY (request_issue_id) REFERENCES dbo.warehouse_request_issue(request_issue_id),
    CONSTRAINT FK_issue_receipts_users
        FOREIGN KEY (issued_by) REFERENCES dbo.users(user_id),
    CONSTRAINT CK_issue_receipts_status CHECK (status IN (N'PENDING', N'APPROVED', N'REJECTED', N'DONE'))
);
GO

CREATE TABLE dbo.warehouse_issue_receipt_details (
    issue_receipt_detail_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    issue_receipt_id        INT NOT NULL,
    ingredient_id           INT NOT NULL,
    batch_id                INT NOT NULL,
    quantity_issued         DECIMAL(18,3) NOT NULL CONSTRAINT CK_issue_receipt_details_qty CHECK (quantity_issued > 0),
    unit_name               NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_issue_receipt_details_receipt
        FOREIGN KEY (issue_receipt_id) REFERENCES dbo.warehouse_issue_receipts(issue_receipt_id),
    CONSTRAINT FK_issue_receipt_details_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES dbo.ingredients(ingredient_id),
    CONSTRAINT FK_issue_receipt_details_batch
        FOREIGN KEY (batch_id) REFERENCES dbo.ingredient_batches(batch_id)
);
GO

CREATE INDEX IX_issue_receipt_details_receipt
ON dbo.warehouse_issue_receipt_details (issue_receipt_id);
GO

/*========================================================
  6) RECEIVE REQUEST / RECEIVE RECEIPT (nhập thành phẩm)
========================================================*/
CREATE TABLE dbo.warehouse_request_receive (
    request_receive_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    production_order_id   INT NOT NULL,
    requested_by         INT NOT NULL,
    requested_at         DATETIME2 NOT NULL CONSTRAINT DF_request_receive_requested_at DEFAULT (SYSDATETIME()),
    requested_quantity   DECIMAL(18,3) NOT NULL CONSTRAINT CK_request_receive_qty CHECK (requested_quantity > 0),
    status               NVARCHAR(30) NOT NULL CONSTRAINT DF_request_receive_status DEFAULT (N'PENDING'),
    note                 NVARCHAR(255) NULL,
    CONSTRAINT FK_request_receive_orders
        FOREIGN KEY (production_order_id) REFERENCES dbo.production_orders(production_order_id),
    CONSTRAINT FK_request_receive_users
        FOREIGN KEY (requested_by) REFERENCES dbo.users(user_id),
    CONSTRAINT CK_request_receive_status CHECK (status IN (N'PENDING', N'APPROVED', N'REJECTED', N'DONE'))
);
GO

CREATE TABLE dbo.warehouse_receive_receipts (
    receive_receipt_id   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    request_receive_id   INT NOT NULL,
    received_by          INT NOT NULL,
    received_at          DATETIME2 NOT NULL CONSTRAINT DF_receive_receipts_received_at DEFAULT (SYSDATETIME()),
    status               NVARCHAR(30) NOT NULL CONSTRAINT DF_receive_receipts_status DEFAULT (N'APPROVED'),
    note                 NVARCHAR(255) NULL,
    CONSTRAINT FK_receive_receipts_request
        FOREIGN KEY (request_receive_id) REFERENCES dbo.warehouse_request_receive(request_receive_id),
    CONSTRAINT FK_receive_receipts_users
        FOREIGN KEY (received_by) REFERENCES dbo.users(user_id),
    CONSTRAINT CK_receive_receipts_status CHECK (status IN (N'PENDING', N'APPROVED', N'REJECTED', N'DONE'))
);
GO

/*========================================================
  7) FINISHED GOODS INVENTORY
========================================================*/
CREATE TABLE dbo.finished_goods_inventory (
    inventory_id       INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_id         INT NOT NULL UNIQUE,
    quantity_on_hand   DECIMAL(18,3) NOT NULL CONSTRAINT DF_finished_goods_qty DEFAULT (0),
    unit_name          NVARCHAR(50) NOT NULL CONSTRAINT DF_finished_goods_unit DEFAULT (N'kg'),
    last_updated_at    DATETIME2 NOT NULL CONSTRAINT DF_finished_goods_last_updated DEFAULT (SYSDATETIME()),
    CONSTRAINT CK_finished_goods_qty CHECK (quantity_on_hand >= 0),
    CONSTRAINT FK_finished_goods_products
        FOREIGN KEY (product_id) REFERENCES dbo.ice_cream_products(product_id)
);
GO

CREATE TABLE dbo.product_inventory_transactions (
    transaction_id    INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_id        INT NOT NULL,
    transaction_type  NVARCHAR(20) NOT NULL,
    quantity_change   DECIMAL(18,3) NOT NULL,
    reference_type    NVARCHAR(50) NULL,
    reference_id      INT NULL,
    created_at        DATETIME2 NOT NULL CONSTRAINT DF_product_inventory_transactions_created_at DEFAULT (SYSDATETIME()),
    created_by        INT NULL,
    note              NVARCHAR(255) NULL,
    CONSTRAINT FK_product_inventory_transactions_products
        FOREIGN KEY (product_id) REFERENCES dbo.ice_cream_products(product_id),
    CONSTRAINT FK_product_inventory_transactions_users
        FOREIGN KEY (created_by) REFERENCES dbo.users(user_id),
    CONSTRAINT CK_product_inventory_transactions_qty CHECK (quantity_change <> 0),
    CONSTRAINT CK_product_inventory_transactions_type CHECK (transaction_type IN (N'IN', N'OUT', N'ADJUST'))
);
GO

CREATE INDEX IX_product_inventory_transactions_product_time
ON dbo.product_inventory_transactions (product_id, created_at DESC);
GO

/*========================================================
  8) SEED TỐI THIỂU
========================================================*/
INSERT INTO dbo.roles (role_name)
VALUES 
       ('Admin'),
       (N'Trưởng sản xuất'),
       (N'Quản lý kho'),
       (N'Nhân viên kinh doanh');
GO

/*========================================================
  9) GHI CHÚ TRIỂN KHAI
  - Chưa seed user / ingredient / product / recipe để tránh khóa cứng dữ liệu demo.
  - Ứng dụng JavaFX có thể tự insert dữ liệu khởi tạo tùy lớp service.
========================================================*/
