-- Insert flash sale goods data for the first 5 products with images
-- Flash sale prices are set at 20-30% discount from original prices

INSERT INTO `flash_sale_goods` (`goods_id`, `flash_sale_price`, `flash_sale_stock`, `start_time`, `end_time`, `is_active`) VALUES
-- 1. Dyson V15 Detect Cordless Vacuum (Original: ¥4,990)
(11, 3499.00, 50, '2024-12-08 00:00:00', '2024-12-31 23:59:59', 1),

-- 2. iPhone 15 Pro 256GB (Original: ¥8,999)
(12, 6999.00, 30, '2024-12-08 00:00:00', '2024-12-31 23:59:59', 1),

-- 3. iRobot Roomba j7+ Self-Emptying Robot Vacuum (Original: ¥3,999)
(13, 2799.00, 40, '2024-12-08 00:00:00', '2024-12-31 23:59:59', 1),

-- 4. Sony WH-1000XM5 Wireless Noise Canceling Headphones (Original: ¥2,999)
(14, 1999.00, 60, '2024-12-08 00:00:00', '2024-12-31 23:59:59', 1),

-- 5. Nintendo Switch OLED White (Original: ¥2,399)
(15, 1699.00, 80, '2024-12-08 00:00:00', '2024-12-31 23:59:59', 1);

