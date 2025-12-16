-- Insert product data (10 products)
-- First 5 use existing flash sale product images, last 5 are new products

INSERT INTO `goods` (`name`, `image_url`, `price`, `description`, `stock`, `status`) VALUES
-- 1. Dyson V15 Vacuum
('Dyson V15 Detect Cordless Vacuum', '/imgs/dysonv15.jpg', 4990.00, 'Dyson V15 Detect cordless vacuum with laser detection technology, 150AW powerful suction, 60-minute runtime, HEPA filtration system, suitable for all floor types.', 100, 1),

-- 2. iPhone 15 Pro
('iPhone 15 Pro 256GB', '/imgs/iphone15pro.jpg', 8999.00, 'iPhone 15 Pro 256GB with A17 Pro chip, titanium design, Pro camera system, Action Button support, USB-C port.', 50, 1),

-- 3. Roomba Robot Vacuum
('iRobot Roomba j7+ Self-Emptying Robot Vacuum', '/imgs/roomba.jpg', 3999.00, 'iRobot Roomba j7+ smart robot vacuum with self-emptying base, AI vision navigation, auto-recharge, app remote control, perfect for large homes.', 80, 1),

-- 4. Sony WH-1000XM5 Headphones
('Sony WH-1000XM5 Wireless Noise Canceling Headphones', '/imgs/sonyxm5.jpg', 2999.00, 'Sony WH-1000XM5 over-ear wireless noise canceling headphones, 30-hour battery life, LDAC high-quality audio, quick charge, comfortable fit, ideal for commuting and travel.', 120, 1),

-- 5. Nintendo Switch OLED
('Nintendo Switch OLED White', '/imgs/switcholed.jpg', 2399.00, 'Nintendo Switch OLED gaming console with 7-inch OLED screen, 64GB storage, supports TV/tabletop/handheld modes, includes Joy-Con controllers.', 150, 1),

-- 6. MacBook Pro 14-inch
('MacBook Pro 14-inch M3 Chip', NULL, 14999.00, 'MacBook Pro 14-inch with M3 chip, 18GB unified memory, 512GB SSD, Liquid Retina XDR display, perfect for professional creative work.', 30, 1),

-- 7. AirPods Pro 2
('AirPods Pro 2nd Gen Active Noise Canceling Earbuds', NULL, 1899.00, 'AirPods Pro 2nd generation with active noise cancellation, spatial audio, MagSafe charging case, up to 30 hours battery life, IPX4 sweat and water resistant.', 200, 1),

-- 8. iPad Air 11-inch
('iPad Air 11-inch M2 Chip', NULL, 4799.00, 'iPad Air 11-inch with M2 chip, 128GB storage, Liquid Retina display, supports Apple Pencil and Magic Keyboard.', 60, 1),

-- 9. Apple Watch Series 9
('Apple Watch Series 9 GPS 45mm', NULL, 2999.00, 'Apple Watch Series 9 with 45mm case, GPS functionality, Always-On Retina display, health monitoring, S9 chip, up to 18 hours battery life.', 100, 1),

-- 10. Xiaomi 14 Pro
('Xiaomi 14 Pro 12GB+256GB', NULL, 4999.00, 'Xiaomi 14 Pro with Snapdragon 8 Gen 3 processor, Leica imaging system, 120W fast charging, 2K curved display, IP68 water resistance, flagship performance.', 80, 1);

