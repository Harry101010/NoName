
USE IceCreamManagement;
GO

-- 1. Xóa các bảng cũ nếu tồn tại (theo thứ tự ngược để tránh lỗi khóa ngoại)
IF OBJECT_ID('production_tracking', 'U') IS NOT NULL DROP TABLE production_tracking;
IF OBJECT_ID('production_stages', 'U') IS NOT NULL DROP TABLE production_stages;
GO

-- 2. Tạo lại bảng production_stages
CREATE TABLE production_stages (
    stage_id INT PRIMARY KEY IDENTITY(1,1), 
    stage_name NVARCHAR(100),              
    std_time_minutes INT,                  
    std_capacity FLOAT                     
);

-- 3. Tạo lại bảng production_tracking
CREATE TABLE production_tracking (
    tracking_id INT PRIMARY KEY IDENTITY(1,1), 
    order_id INT,
    stage_id INT,
    status NVARCHAR(20) DEFAULT 'pending', 
    start_time DATETIME,
    end_time DATETIME,
    actual_time_minutes INT,
    actual_capacity FLOAT,
    note NVARCHAR(MAX),
    -- Ràng buộc khóa ngoại
    CONSTRAINT FK_Tracking_Order FOREIGN KEY (order_id) REFERENCES production_orders(production_order_id),
    CONSTRAINT FK_Tracking_Stage FOREIGN KEY (stage_id) REFERENCES production_stages(stage_id)
);
GO

-- Dữ liệu mẫu
INSERT INTO production_stages (stage_name, std_time_minutes, std_capacity) VALUES 
('Trộn', 30, 50.0), ('Làm lạnh', 60, 50.0), ('Đóng gói', 20, 50.0);

-- Giả sử bạn có lệnh sản xuất ID = 1
INSERT INTO production_tracking (order_id, stage_id, status) VALUES (1, 1, 'pending');
INSERT INTO production_tracking (order_id, stage_id, status) VALUES (1, 2, 'pending');


-- 1. Xóa bảng con (bảng có chứa khóa ngoại) trước
IF OBJECT_ID('production_tracking', 'U') IS NOT NULL
    DROP TABLE production_tracking;
GO

-- 2. Xóa bảng cha (bảng production_stages)
IF OBJECT_ID('production_stages', 'U') IS NOT NULL
    DROP TABLE production_stages;
GO

-- 3. Tạo lại bảng cha
CREATE TABLE production_stages (
    stage_id INT PRIMARY KEY IDENTITY(1,1), 
    stage_name NVARCHAR(100) NOT NULL,
    sequence_order INT NOT NULL, 
    standard_time_minutes DECIMAL(10,2) DEFAULT 0,
    is_proportional BIT DEFAULT 0 
);
GO

-- 4. Tạo lại bảng con (đảm bảo nó tham chiếu đúng tới stage_id)
CREATE TABLE production_tracking (
    tracking_id INT PRIMARY KEY IDENTITY(1,1),
    production_order_id INT NOT NULL,
    stage_id INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'pending',
    actual_start_time DATETIME NULL,
    actual_end_time DATETIME NULL,
    actual_duration_minutes INT DEFAULT 0,
    actual_quantity DECIMAL(10,2) DEFAULT 0,
    note NVARCHAR(MAX),
    -- Khóa ngoại tham chiếu tới bảng cha
    FOREIGN KEY (stage_id) REFERENCES production_stages(stage_id)
);
GO

-- 5. Chèn lại dữ liệu cho bảng cha
INSERT INTO production_stages (stage_name, sequence_order, standard_time_minutes, is_proportional) VALUES
('Trộn hỗn hợp', 1, 30, 1),
('Đồng hóa', 2, 20, 1),
('Thanh trùng', 3, 15, 1),
('Ủ kem', 4, 120, 0),
('Đánh kem', 5, 10, 1),
('Chiết rót', 6, 20, 1),
('Làm cứng', 7, 240, 0),
('Bảo quản và đóng gói', 8, 30, 1);
GO

UPDATE production_stages SET stage_name = 'Mixing' WHERE stage_id = 1;
UPDATE production_stages SET stage_name = 'Homogenization' WHERE stage_id = 2;
UPDATE production_stages SET stage_name = 'Pasteurization' WHERE stage_id = 3;
UPDATE production_stages SET stage_name = 'Aging' WHERE stage_id = 4;
UPDATE production_stages SET stage_name = 'Whipping' WHERE stage_id = 5;
UPDATE production_stages SET stage_name = 'Filling' WHERE stage_id = 6;
UPDATE production_stages SET stage_name = 'Hardening' WHERE stage_id = 7;
UPDATE production_stages SET stage_name = 'Packaging' WHERE stage_id = 8;
GO

SELECT 
    t.tracking_id, 
    t.production_order_id, 
    s.stage_name, 
    t.status, 
    t.actual_start_time,
    t.actual_end_time -- Thêm cả cột này để kiểm tra luôn cho chắc chắn
FROM production_tracking t
JOIN production_stages s ON t.stage_id = s.stage_id
ORDER BY t.production_order_id, t.stage_id;


-- 1. Xóa sạch dữ liệu cũ theo thứ tự ưu tiên (bảng con trước, bảng cha sau)
DELETE FROM production_tracking;
DELETE FROM production_orders;

-- 2. Reset lại bộ đếm ID về 0 để đơn hàng bắt đầu từ 1
DBCC CHECKIDENT ('production_orders', RESEED, 0);

-- 3. Tạo 10 đơn hàng mẫu (SQL Server sẽ tự tăng ID từ 1 đến 10)
INSERT INTO production_orders (ice_cream_id, planned_output_kg, order_status, created_at, note)
VALUES 
(1, 100, 'in_progress', GETDATE(), 'Test Order 1'),
(2, 150, 'pending', GETDATE(), 'Test Order 2'),
(1, 200, 'pending', GETDATE(), 'Test Order 3'),
(3, 120, 'pending', GETDATE(), 'Test Order 4'),
(4, 300, 'pending', GETDATE(), 'Test Order 5'),
(2, 250, 'pending', GETDATE(), 'Test Order 6'),
(1, 110, 'pending', GETDATE(), 'Test Order 7'),
(3, 400, 'pending', GETDATE(), 'Test Order 8'),
(4, 50,  'pending', GETDATE(), 'Test Order 9'),
(2, 180, 'pending', GETDATE(), 'Test Order 10');

-- 4. Tạo tự động 8 công đoạn 'pending' cho mỗi đơn hàng vừa tạo
INSERT INTO production_tracking (production_order_id, stage_id, status)
SELECT o.production_order_id, s.stage_id, 'pending'
FROM production_orders o
CROSS JOIN production_stages s;

-- Kiểm tra kết quả
SELECT * FROM production_orders;
SELECT * FROM production_tracking ORDER BY production_order_id, stage_id;


UPDATE production_tracking 
SET actual_start_time = GETDATE() 
WHERE status IN ('completed', 'in_progress') AND actual_start_time IS NULL;

SELECT stage_id, actual_start_time, actual_end_time, DATEDIFF(MINUTE, actual_start_time, actual_end_time) as diff
FROM production_tracking 
WHERE production_order_id = 5 AND status = 'completed';

SELECT * FROM users;

INSERT INTO users (username, password_hash, role_id, is_active, email)
VALUES ('admin', 'abc123', 1, 1, 'nghvukt3@gmail.com');

-----------------------------------------------------------------------------------------
-- Bảng Stage (Định nghĩa công đoạn - Khớp với ProductionStage.java)
CREATE TABLE dbo.production_stages (
    stage_id INT PRIMARY KEY IDENTITY(1,1), 
    stage_name NVARCHAR(100) NOT NULL,
    sequence_order INT NOT NULL, 
    standard_time_minutes DECIMAL(10,2) DEFAULT 0,
    is_proportional BIT DEFAULT 0 
);

-- Bảng Order
CREATE TABLE dbo.production_orders (
    production_order_id INT IDENTITY(1,1) PRIMARY KEY,
    ice_cream_id INT NOT NULL,
    planned_output_kg DECIMAL(18,3) NOT NULL,
    order_status NVARCHAR(30) NOT NULL,
    note NVARCHAR(500) NULL,
    created_at DATETIME2(0) DEFAULT SYSDATETIME()
);

-- Bảng Tracking (Theo dõi - Khớp với ProductionTracking.java)
CREATE TABLE dbo.production_tracking (
    tracking_id INT PRIMARY KEY IDENTITY(1,1),
    production_order_id INT NOT NULL,
    stage_id INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'pending',
    actual_start_time DATETIME NULL,
    actual_end_time DATETIME NULL,
    actual_time_minutes INT DEFAULT 0,
    actual_quantity DECIMAL(18,3) DEFAULT 0,
    actual_capacity FLOAT DEFAULT 0,
    note NVARCHAR(MAX),
    FOREIGN KEY (production_order_id) REFERENCES dbo.production_orders(production_order_id),
    FOREIGN KEY (stage_id) REFERENCES dbo.production_stages(stage_id)
);

-- 3. NẠP DỮ LIỆU MẪU
PRINT '3. Đang nạp dữ liệu mẫu...';

-- Nạp 8 công đoạn chuẩn (Khớp tên cột mới tạo)
INSERT INTO production_stages (stage_name, sequence_order, standard_time_minutes, is_proportional) 
VALUES 
('Mixing', 1, 30, 1),
('Homogenization', 2, 20, 1),
('Pasteurization', 3, 15, 1),
('Aging', 4, 120, 0),
('Whipping', 5, 10, 1),
('Filling', 6, 20, 1),
('Hardening', 7, 240, 0),
('Packaging', 8, 30, 1);

-- Nạp đơn hàng mẫu (Giả sử ice_cream_id từ 1 đến 5)
INSERT INTO production_orders (ice_cream_id, planned_output_kg, order_status, note)
VALUES 
(1, 100, 'in_progress', 'Order 01'),
(2, 150, 'pending', 'Order 02'),
(1, 200, 'pending', 'Order 03'),
(3, 120, 'pending', 'Order 04'),
(4, 300, 'pending', 'Order 05');

-- Nạp tracking tự động
INSERT INTO production_tracking (production_order_id, stage_id, status)
SELECT o.production_order_id, s.stage_id, 'pending'
FROM production_orders o
CROSS JOIN production_stages s;

PRINT '--- HOÀN TẤT: DATABASE ĐÃ ĐỒNG BỘ VỚI CODE JAVA ---';
GO