-- Add UPC column to products table with a default value for existing records
ALTER TABLE products ADD COLUMN upc VARCHAR(255);

-- Make UPC column NOT NULL after updating existing records
ALTER TABLE products ALTER COLUMN upc SET NOT NULL;

-- Add unique constraint for UPC
ALTER TABLE products ADD CONSTRAINT uk_products_upc UNIQUE (upc);

