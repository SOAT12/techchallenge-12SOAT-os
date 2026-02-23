ALTER TABLE service_order_stock ADD COLUMN quantity INT NOT NULL DEFAULT 1;
ALTER TABLE service_order_stock ADD COLUMN unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00;