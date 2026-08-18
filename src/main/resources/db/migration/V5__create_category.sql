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
