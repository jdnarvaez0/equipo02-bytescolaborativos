INSERT INTO users (id, username, email, password, created_at) VALUES
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'admin', 'admin@example.com', '{noop}password', NOW()),
  ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', 'player1', 'player1@example.com', '{noop}password', NOW()),
  ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', 'player2', 'player2@example.com', '{noop}password', NOW()),
  ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d44', 'player3', 'player3@example.com', '{noop}password', NOW());

INSERT INTO user_roles (user_id, role) VALUES
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'ADMIN'),
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'PLAYER'),
  ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', 'PLAYER'),
  ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', 'PLAYER'),
  ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380d44', 'PLAYER');

INSERT INTO products (id, name, description, category, popularity_score, created_at) VALUES
  ('11111111-1111-1111-1111-111111111111', 'Teclado Mecánico RGB', 'Switches azules, retroiluminación personalizable', 'Hardware', 120, NOW()),
  ('22222222-2222-2222-2222-222222222222', 'Mouse Gaming DPI Ajustable', '16000 DPI, 8 botones programables', 'Hardware', 95, NOW()),
  ('33333333-3333-3333-3333-333333333333', 'Curso Avanzado de Java', 'Spring Boot, JPA, Testing', 'Educación', 85, NOW()),
  ('44444444-4444-4444-4444-444444444444', 'Monitor 144Hz 27"', '1ms, IPS, FreeSync', 'Hardware', 110, NOW()),
  ('55555555-5555-5555-5555-555555555555', 'Ebook: Arquitectura de Software', 'Patrones, DDD, Clean Code', 'Educación', 60, NOW()),
  ('66666666-6666-6666-6666-666666666666', 'Juego: CyberRacer 2077', 'Carreras futuristas en ciudades neon', 'Software', 150, NOW());

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

INSERT INTO ratings (id, user_id, product_id, score, created_at) VALUES

  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', '11111111-1111-1111-1111-111111111111', 5, NOW()),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', '22222222-2222-2222-2222-222222222222', 4, NOW()),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', '33333333-3333-3333-3333-333333333333', 5, NOW()),

  ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', '11111111-1111-1111-1111-111111111111', 3, NOW()),
  ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', '44444444-4444-4444-4444-444444444444', 5, NOW()),
  ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c33', '66666666-6666-6666-6666-666666666666', 4, NOW()),

  ('11111111-1111-1111-1111-111111111112', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380d44', '55555555-5555-5555-5555-555555555555', 2, NOW()),
  ('22222222-2222-2222-2222-222222222223', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380d44', '66666666-6666-6666-6666-666666666666', 5, NOW());

INSERT INTO tournaments (id, name, game, start_date, end_date, registration_open_at, registration_close_at, rules, max_participants, status, created_at) VALUES
  ('77777777-7777-7777-7777-777777777777', 'Torneo Valorant LATAM', 'Valorant', '2025-06-01 18:00:00', '2025-06-10 18:00:00', '2025-05-10 00:00:00', '2025-05-30 00:00:00', 'Bo3 eliminatoria directa', 64, 'UPCOMING', NOW()),
  ('88888888-8888-8888-8888-888888888888', 'Liga Clash Royale', 'Clash Royale', '2025-07-15 20:00:00', '2025-07-20 22:00:00', '2025-06-01 00:00:00', '2025-07-10 00:00:00', 'Liga suiza + playoffs', 32, 'OPEN', NOW());