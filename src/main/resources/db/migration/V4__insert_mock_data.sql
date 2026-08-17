-- Mock Data for ZoraShop Mini Shopee E-Commerce Platform
-- Note: All mock accounts have password: Password123@

-- 1. INSERT USERS (1 Admin, 3 Sellers, 10 Buyers)
INSERT INTO users (id, email, password, full_name, phone, avatar_url, role, is_active, created_at, updated_at) VALUES
(1, 'admin@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'System Admin', '0900000001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(2, 'seller1@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Nguyen Van Shop One', '0900000002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller1', 'SELLER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'seller2@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Tran Thi Fashion', '0900000003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller2', 'SELLER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'seller3@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Le Van Electro', '0900000004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller3', 'SELLER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(5, 'buyer1@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Pham Hoang Buyer 1', '0911000001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer1', 'BUYER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'buyer2@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Vo Thi Buyer 2', '0911000002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer2', 'BUYER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'buyer3@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Dang Van Buyer 3', '0911000003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer3', 'BUYER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'buyer4@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Bui Thi Buyer 4', '0911000004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer4', 'BUYER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'buyer5@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Do Minh Buyer 5', '0911000005', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer5', 'BUYER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'buyer6@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Ngo Thanh Buyer 6', '0911000006', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer6', 'BUYER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'buyer7@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Duong Quoc Buyer 7', '0911000007', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer7', 'BUYER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'buyer8@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Ly Khanh Buyer 8', '0911000008', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer8', 'BUYER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'buyer9@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Trinh Duc Buyer 9', '0911000009', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer9', 'BUYER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'buyer10@zorashop.com', '$2a$10$78g9o.WqX02Y45K1wZ7Ose42/Xy7Kk1y9Y5x0WqX02Y45K1wZ7Ose', 'Hoang Gia Buyer 10', '0911000010', 'https://api.dicebear.com/7.x/avataaars/svg?seed=buyer10', 'BUYER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- 2. INSERT SHOPS (for Sellers)
INSERT INTO shops (id, seller_id, name, description, logo_url, banner_url, rating, total_products, total_followers, is_active, created_date, last_modified_date) VALUES
(1, 2, 'Zora Official Store', 'Cửa hàng công nghệ chính hãng Zora', 'https://picsum.photos/200/200?random=1', 'https://picsum.photos/800/300?random=1', 4.9, 120, 5500, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, 'Shopee Fashion World', 'Thời trang nam nữ đón đầu xu hướng', 'https://picsum.photos/200/200?random=2', 'https://picsum.photos/800/300?random=2', 4.8, 85, 3200, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 4, 'Electro Mini Superstore', 'Đồ điện tử gia dụng cao cấp giá rẻ', 'https://picsum.photos/200/200?random=3', 'https://picsum.photos/800/300?random=3', 4.7, 45, 1200, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval('shops_id_seq', (SELECT MAX(id) FROM shops));

-- 3. INSERT ADDRESSES
INSERT INTO address (id, user_id, full_name, phone, street, ward, district, city, is_default, created_date, last_modified_date) VALUES
(1, 5, 'Pham Hoang Buyer 1', '0911000001', '123 Đường Nguyễn Huệ', 'Phường Bến Nghé', 'Quận 1', 'TP. Hồ Chí Minh', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 5, 'Pham Hoang (Cơ Quan)', '0911000001', '456 Đường Lê Duẩn', 'Phường Bến Nghé', 'Quận 1', 'TP. Hồ Chí Minh', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 6, 'Vo Thi Buyer 2', '0911000002', '789 Đường Cầu Giấy', 'Phường Dịch Vọng', 'Quận Cầu Giấy', 'Hà Nội', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 7, 'Dang Van Buyer 3', '0911000003', '12 Đường Trần Phú', 'Phường Hải Châu 1', 'Quận Hải Châu', 'Đà Nẵng', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 8, 'Bui Thi Buyer 4', '0911000004', '55 Đường Nguyễn Văn Linh', 'Phường Tân Thuận Tây', 'Quận 7', 'TP. Hồ Chí Minh', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval('address_id_seq', (SELECT MAX(id) FROM address));
