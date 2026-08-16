
CREATE TABLE Shops(
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL ,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    logo_url VARCHAR(255),
    banner_url VARCHAR(255),
    rating DOUBLE PRECISION,
    total_products BIGINT,
    total_followers BIGINT,
    is_active BOOLEAN,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)