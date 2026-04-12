USE IceCreamManagement;
GO

/* =========================================================================
   ĐÂY LÀ FILE CHỨA CÁC LỆNH TẠO BẢNG (CREATE TABLE) TỪ ĐẦU
   Chỉ chạy file này nếu máy bạn bị mất bảng hoặc muốn khởi tạo lại Database
   ========================================================================= */

-- 1. BẢNG NGƯỜI DÙNG / NHÂN VIÊN (Dùng để lấy saleman_id)
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        user_id INT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(50) NOT NULL,
        password NVARCHAR(255) NOT NULL,
        role NVARCHAR(50) NOT NULL,
        is_active BIT DEFAULT 1
    );
    -- Chèn 1 user mặc định để test nếu bảng trống
    INSERT INTO dbo.users (username, password, role) VALUES (N'nv_kinhdoanh_01', '123456', 'Saleman');
END
GO

-- 2. BẢNG DANH MỤC KEM (Đã bỏ cột price theo yêu cầu mới nhất)
IF OBJECT_ID(N'dbo.ice_creams', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ice_creams (
        ice_cream_id INT IDENTITY(1,1) PRIMARY KEY,
        ice_cream_name NVARCHAR(200) NOT NULL,
        is_active BIT DEFAULT 1
    );
END
GO

-- 3. BẢNG TỒN KHO THÀNH PHẨM (Nơi chứa số lượng hiện thực tế)
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

-- 4. BẢNG PHIẾU YÊU CẦU XUẤT KHO (Bảng cha - Lưu thông tin chung của đơn)
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

-- 5. BẢNG CHI TIẾT PHIẾU YÊU CẦU (Bảng con - Lưu từng món kem trong phiếu)
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
GO