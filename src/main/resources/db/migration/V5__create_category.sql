CREATE TABLE categories (
        id BIGSERIAL PRIMARY KEY,
        parent_id BIGINT,
        name VARCHAR(255) NOT NULL,
        slug VARCHAR(100) UNIQUE NOT NULL,
        icon_url VARCHAR(255),
        level INT NOT NULL DEFAULT 1,
        sort_order INT NOT NULL DEFAULT 0,
        is_active BOOLEAN NOT NULL DEFAULT TRUE,
        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_categories_parent
             FOREIGN KEY (parent_id)
                  REFERENCES categories(id)
                    ON DELETE SET NULL
);

-- Mock Data for Categories (3 Levels)
INSERT INTO categories (id, parent_id, name, slug, icon_url, level, sort_order, is_active, created_date, last_modified_date) VALUES
-- Cấp 1
(1, NULL, 'Thiết Bị Điện Tử', 'thiet-bi-dien-tu', 'https://api.dicebear.com/7.x/icons/svg?seed=electronics', 1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, NULL, 'Thời Trang Nam', 'thoi-trang-nam', 'https://api.dicebear.com/7.x/icons/svg?seed=fashion', 1, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, NULL, 'Nhà Cửa & Đời Sống', 'nha-cua-doi-song', 'https://api.dicebear.com/7.x/icons/svg?seed=home', 1, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cấp 2
(4, 1, 'Điện Thoại & Phụ Kiện', 'dien-thoai-phu-kien', NULL, 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 1, 'Máy Tính & Laptop', 'may-tinh-laptop', NULL, 2, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 2, 'Áo Nam', 'ao-nam', NULL, 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 2, 'Quần Nam', 'quan-nam', NULL, 2, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 3, 'Đồ Dùng Bếp', 'do-dung-bep', NULL, 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cấp 3
(9, 4, 'Smartphone / Điện thoại', 'smartphone', NULL, 3, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 4, 'Ốp lưng & Bao da', 'op-lung-bao-da', NULL, 3, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 5, 'Laptop Gaming', 'laptop-gaming', NULL, 3, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 6, 'Áo Thun / T-Shirt', 'ao-thun-t-shirt', NULL, 3, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 6, 'Áo Sơ Mi Nam', 'ao-so-mi-nam', NULL, 3, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 7, 'Quần Jeans Nam', 'quan-jeans-nam', NULL, 3, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
