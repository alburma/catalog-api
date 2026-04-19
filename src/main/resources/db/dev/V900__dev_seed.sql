-- Dev-profile-only seed. Not executed when SPRING_PROFILES_ACTIVE=prod.
-- Loaded only when the application runs with the `dev` profile
-- (see application-dev.yml -> spring.flyway.locations).
--
-- "Warks" is a fictional distributor used as a demo domain for this sample.

INSERT INTO categories (name, slug) VALUES
  ('Wines',        'wines'),
  ('Confectionery','confectionery'),
  ('Snacks',       'snacks'),
  ('Bakery',       'bakery'),
  ('Frozen',       'frozen');

INSERT INTO products (sku, name, description, price_cents, currency, stock, category_id, active) VALUES
  ('WINE-RED-750',  'Red Table Wine 0.75L',      'Dry red wine, 12% ABV',                  21900, 'CZK', 120, 1, TRUE),
  ('WINE-BOX-3L',   'Bag-in-Box Dry White 3L',   'White wine in 3L bag-in-box',            39900, 'CZK', 60,  1, TRUE),
  ('BON-CHOC-1KG',  'Chocolate Bonbons 1kg',     'Assorted chocolate bonbons, 1kg',        23900, 'CZK', 85,  2, TRUE),
  ('BON-CARAM-500', 'Caramel Candies 500g',      'Soft caramel candies, 500g',             11900, 'CZK', 200, 2, TRUE),
  ('SNK-CHIP-150',  'Potato Chips Classic 150g', 'Salted potato chips, 150g',               3490, 'CZK', 400, 3, TRUE),
  ('SNK-SEED-200',  'Sunflower Seeds 200g',      'Roasted sunflower seeds, 200g',           2990, 'CZK', 300, 3, TRUE),
  ('BAK-BISC-300',  'Butter Biscuits 300g',      'Traditional butter biscuits, 300g',       5490, 'CZK', 150, 4, TRUE),
  ('BAK-CAKE-500',  'Honey Cake 500g',           'Layered honey cake, 500g',                8990, 'CZK', 45,  4, TRUE),
  ('FRZ-DUMP-1KG',  'Frozen Dumplings 1kg',      'Potato-and-cheese dumplings, 1kg',       13900, 'CZK', 80,  5, TRUE),
  ('FRZ-BERRY-500', 'Mixed Frozen Berries 500g', 'IQF berry mix, 500g',                     9990, 'CZK', 100, 5, TRUE);

-- No user seed is shipped. Register via POST /api/auth/register.
-- To promote an account to ADMIN, update the `role` column directly in the database.
