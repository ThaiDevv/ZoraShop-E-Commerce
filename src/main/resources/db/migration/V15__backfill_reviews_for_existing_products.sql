
INSERT INTO reviews (product_id)
SELECT p.id
FROM products p
WHERE NOT EXISTS (
    SELECT 1 FROM reviews r WHERE r.product_id = p.id
);
