-- HASH DE "password": $2a$10$RVXEIOGi6ACFOtbK6cQBUeB1qbm1L5ay1DbrKFCaJPmJJiWML7Ou.

-- USERS
-- Incluye todos los campos necesarios de UserEntity: enabled, account_non_expired, account_non_locked, credentials_non_expired
INSERT INTO users (id, username, email, password, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at) VALUES
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'admin', 'admin@example.com', '$2a$10$RVXEIOGi6ACFOtbK6cQBUeB1qbm1L5ay1DbrKFCaJPmJJiWML7Ou.', true, true, true, true, CURRENT_TIMESTAMP),
  ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380e55', 'admin2', 'admin2@example.com', '$2a$10$RVXEIOGi6ACFOtbK6cQBUeB1qbm1L5ay1DbrKFCaJPmJJiWML7Ou.', true, true, true, true, CURRENT_TIMESTAMP),
  ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', 'player1', 'player1@example.com', '$2a$10$RVXEIOGi6ACFOtbK6cQBUeB1qbm1L5ay1DbrKFCaJPmJJiWML7Ou.', true, true, true, true, CURRENT_TIMESTAMP),
  ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', 'player2', 'player2@example.com', '$2a$10$RVXEIOGi6ACFOtbK6cQBUeB1qbm1L5ay1DbrKFCaJPmJJiWML7Ou.', true, true, true, true, CURRENT_TIMESTAMP),
  ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d44', 'player3', 'player3@example.com', '$2a$10$RVXEIOGi6ACFOtbK6cQBUeB1qbm1L5ay1DbrKFCaJPmJJiWML7Ou.', true, true, true, true, CURRENT_TIMESTAMP);

-- ROLES
INSERT INTO user_roles (user_id, role) VALUES
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'ADMIN'),
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'PLAYER'),
  ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380e55', 'ADMIN'),
  ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', 'PLAYER'),
  ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', 'PLAYER'),
  ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d44', 'PLAYER');

-- PRODUCTS
INSERT INTO products (id, name, description, category, popularity_score, created_at) VALUES
  ('11111111-1111-1111-1111-111111111111', 'Teclado Mecánico RGB', 'Switches azules, retroiluminación personalizable', 'Hardware', 120, CURRENT_TIMESTAMP),
  ('22222222-2222-2222-2222-222222222222', 'Mouse Gaming DPI Ajustable', '16000 DPI, 8 botones programables', 'Hardware', 95, CURRENT_TIMESTAMP),
  ('33333333-3333-3333-3333-333333333333', 'Curso Avanzado de Java', 'Spring Boot, JPA, Testing', 'Educación', 85, CURRENT_TIMESTAMP),
  ('44444444-4444-4444-4444-444444444444', 'Monitor 144Hz 27"', '1ms, IPS, FreeSync', 'Hardware', 110, CURRENT_TIMESTAMP),
  ('55555555-5555-5555-5555-555555555555', 'Ebook: Arquitectura de Software', 'Patrones, DDD, Clean Code', 'Educación', 60, CURRENT_TIMESTAMP),
  ('66666666-6666-6666-6666-666666666666', 'Juego: CyberRacer 2077', 'Carreras futuristas en ciudades neon', 'Software', 150, CURRENT_TIMESTAMP);

-- TAGS
INSERT INTO product_tags (product_id, tag) VALUES
  ('11111111-1111-1111-1111-111111111111', 'gaming'),
  ('11111111-1111-1111-1111-111111111111', 'teclado'),
  ('11111111-1111-1111-1111-111111111111', 'rgb'),
  ('22222222-2222-2222-2222-222222222222', 'gaming'),
  ('22222222-2222-2222-2222-222222222222', 'mouse'),
  ('22222222-2222-2222-2222-222222222222', 'dpi'),
  ('33333333-3333-3333-3333-333333333333', 'java'),
  ('33333333-3333-3333-3333-333333333333', 'spring'),
  ('33333333-3333-3333-3333-333333333333', 'curso'),
  ('44444444-4444-4444-4444-444444444444', 'gaming'),
  ('44444444-4444-4444-4444-444444444444', 'monitor'),
  ('44444444-4444-4444-4444-444444444444', '144hz'),
  ('55555555-5555-5555-5555-555555555555', 'arquitectura'),
  ('55555555-5555-5555-5555-555555555555', 'ebook'),
  ('55555555-5555-5555-5555-555555555555', 'software'),
  ('66666666-6666-6666-6666-666666666666', 'juego'),
  ('66666666-6666-6666-6666-666666666666', 'carreras'),
  ('66666666-6666-6666-6666-666666666666', 'gaming');

-- RATINGS
INSERT INTO ratings (id, user_id, product_id, score, created_at) VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', '11111111-1111-1111-1111-111111111111', 5, CURRENT_TIMESTAMP),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', '22222222-2222-2222-2222-222222222222', 4, CURRENT_TIMESTAMP),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', '33333333-3333-3333-3333-333333333333', 5, CURRENT_TIMESTAMP),
  ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', '11111111-1111-1111-1111-111111111111', 3, CURRENT_TIMESTAMP),
  ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', '44444444-4444-4444-4444-444444444444', 5, CURRENT_TIMESTAMP),
  ('11111111-1111-1111-1111-111111111112', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380d44', '55555555-5555-5555-5555-555555555555', 2, CURRENT_TIMESTAMP);

-- TOURNAMENTS
-- Usando TIMESTAMP para compatibilidad con Instant en Java
INSERT INTO tournaments (id, name, game, start_date, end_date, registration_open_at, registration_close_at, rules, max_participants, status, created_at) VALUES
  ('77777777-7777-7777-7777-777777777777', 'Torneo Valorant LATAM', 'Valorant', TIMESTAMP '2025-06-01 18:00:00', TIMESTAMP '2025-06-10 18:00:00', TIMESTAMP '2025-05-10 00:00:00', TIMESTAMP '2025-05-30 00:00:00', 'Bo3 eliminatoria directa', 64, 'UPCOMING', CURRENT_TIMESTAMP),
  ('88888888-8888-8888-8888-888888888888', 'Liga Clash Royale', 'Clash Royale', TIMESTAMP '2025-07-15 20:00:00', TIMESTAMP '2025-07-20 22:00:00', TIMESTAMP '2025-06-01 00:00:00', TIMESTAMP '2025-07-10 00:00:00', 'Liga suiza + playoffs', 32, 'OPEN', CURRENT_TIMESTAMP),
  ('10101010-1010-1010-1010-123456789000', 'Torneo de Programación', 'CodeBytes', TIMESTAMP '2025-11-01 18:00:00', TIMESTAMP '2025-12-31 18:00:00', TIMESTAMP '2025-11-02 00:00:00', TIMESTAMP '2025-11-30 00:00:00', 'Maraton de Programación por eliminación', 24, 'OPEN', CURRENT_TIMESTAMP);


-- TOURNAMENT REGISTRATIONS
INSERT INTO tournament_registrations (id, tournament_id, user_id, nickname, status, registered_at) VALUES
  ('99999999-9999-9999-9999-999999999999', '77777777-7777-7777-7777-777777777777', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', 'PlayerOne', 'REGISTERED', CURRENT_TIMESTAMP),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab', '77777777-7777-7777-7777-777777777777', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', 'GamerPro', 'REGISTERED', CURRENT_TIMESTAMP),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbc', '88888888-8888-8888-8888-888888888888', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380d44', 'Clasher99', 'CONFIRMED', CURRENT_TIMESTAMP);