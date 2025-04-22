-- Remove unique constraint for UPC
ALTER TABLE products DROP CONSTRAINT IF EXISTS uk_products_upc;

-- Remove UPC column from products table
ALTER TABLE products DROP COLUMN IF EXISTS upc; 