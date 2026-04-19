-- Dev-profile-only seed. Not executed when SPRING_PROFILES_ACTIVE=prod.
-- Loaded only when the application runs with the `dev` profile
-- (see application-dev.yml -> spring.flyway.locations).

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

-- No user seed is shipped. Register via POST /api/auth/register.
-- To promote an account to ADMIN, update the `role` column directly in the database.
