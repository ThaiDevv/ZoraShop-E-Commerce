CREATE TABLE orders (
        id BIGSERIAL PRIMARY KEY,
        order_number VARCHAR(50) NOT NULL UNIQUE,
        user_id BIGINT NOT NULL,
        shop_id BIGINT NOT NULL,
        address_id BIGINT NOT NULL,
        voucher_id BIGINT,
        subtotal DECIMAL(15, 2) NOT NULL,
        shipping_fee DECIMAL(15, 2) NOT NULL DEFAULT 0,
        discount_amount DECIMAL(15, 2) NOT NULL DEFAULT 0,
        total_amount DECIMAL(15, 2) NOT NULL,
        status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN (
               'PENDING',
               'CONFIRMED',
               'SHIPPING',
               'DELIVERED',
               'CANCELLED',
               'REFUNDED'
        )),
        note VARCHAR(500),
        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_orders_user
            FOREIGN KEY (user_id)
                REFERENCES users(id),
        CONSTRAINT fk_orders_shop
            FOREIGN KEY (shop_id)
                REFERENCES Shops(id),
        CONSTRAINT fk_orders_address
            FOREIGN KEY (address_id)
                REFERENCES address(id)
--         CONSTRAINT fk_orders_voucher
--              FOREIGN KEY (voucher_id)
--                 REFERENCES vouchers(id)
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    variant_name VARCHAR(255),
    price DECIMAL(15, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(15, 2) NOT NULL,
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_order_items_variant
        FOREIGN KEY (variant_id)
            REFERENCES product_variants(id),
    CONSTRAINT chk_order_items_quantity
        CHECK (quantity > 0),
    CONSTRAINT chk_order_items_price
        CHECK (price >= 0),
    CONSTRAINT chk_order_items_subtotal
        CHECK (subtotal >= 0)
);