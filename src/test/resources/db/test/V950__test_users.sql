-- Test-scope seed: users for integration tests.
-- Loaded ONLY when spring.flyway.locations includes classpath:db/test
-- (see src/test/resources/application-test.yml).
-- Not shipped in the production jar.

-- password = "password123" (bcrypt strength 10) — test fixture only
INSERT INTO users (username, email, password_hash, role) VALUES
  ('admin', 'admin@test.local', '$2b$10$BKo9uVlzjQGTttVP63M/FuytVP/K./rjYGMYAOHUT.PsXhVn5X45m', 'ADMIN'),
  ('user',  'user@test.local',  '$2b$10$BKo9uVlzjQGTttVP63M/FuytVP/K./rjYGMYAOHUT.PsXhVn5X45m', 'USER');
