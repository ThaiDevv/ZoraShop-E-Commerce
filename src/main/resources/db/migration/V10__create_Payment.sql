CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT NOT NULL UNIQUE,
                          transaction_id VARCHAR(100),
                          method VARCHAR(30) NOT NULL,
                          status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                          amount DECIMAL(15, 2) NOT NULL,
                          provider VARCHAR(50),
                          metadata TEXT,
                          paid_at TIMESTAMP,
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_payments_order
                              FOREIGN KEY (order_id)
                                  REFERENCES orders(id)
                                  ON DELETE CASCADE,

                          CONSTRAINT chk_payments_status
                              CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),

                          CONSTRAINT chk_payments_method
                              CHECK (method IN ('COD', 'BANK_TRANSFER', 'VNPAY', 'MOMO', 'CREDIT_CARD'))
);
