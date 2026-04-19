INSERT INTO categories (name, slug) VALUES
  ('Clothing', 'clothing'),
  ('Accessories', 'accessories'),
  ('Footwear', 'footwear');

INSERT INTO products (sku, name, description, price_cents, currency, stock, category_id, active) VALUES
  ('TSHIRT-BLK-M',  'Black T-Shirt M',         'Classic cotton tee',        1990,  'EUR', 50,  1, TRUE),
  ('HOODIE-GRY-L',  'Grey Hoodie L',           'Oversized heavy hoodie',    5490,  'EUR', 20,  1, TRUE),
  ('CAP-NAVY',      'Navy Cap',                '6-panel snapback',          2490,  'EUR', 75,  2, TRUE),
  ('BELT-LTHR-BRN', 'Brown Leather Belt',      'Full-grain leather',        3990,  'EUR', 30,  2, TRUE),
  ('SNK-WHT-42',    'White Sneakers 42',       'Minimal low-top',           8990,  'EUR', 12,  3, TRUE),
  ('BOOT-BLK-43',   'Black Chelsea Boots 43',  'Handmade, Goodyear welted', 15990, 'EUR', 6,   3, TRUE);

-- password = "password123" (bcrypt, strength 10)
INSERT INTO users (username, email, password_hash, role) VALUES
  ('admin', 'admin@example.com', '$2b$10$BKo9uVlzjQGTttVP63M/FuytVP/K./rjYGMYAOHUT.PsXhVn5X45m', 'ADMIN'),
  ('user',  'user@example.com',  '$2b$10$BKo9uVlzjQGTttVP63M/FuytVP/K./rjYGMYAOHUT.PsXhVn5X45m', 'USER');
