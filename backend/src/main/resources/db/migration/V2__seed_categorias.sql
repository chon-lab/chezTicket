-- Categorias iniciais do catálogo. Idempotente para o ambiente de desenvolvimento.
INSERT INTO categoria (nome, slug, ativa) VALUES
    ('Shows e Música',          'shows-e-musica',           1),
    ('Teatro e Espetáculos',    'teatro-e-espetaculos',     1),
    ('Esportes',                'esportes',                 1),
    ('Congressos e Palestras',  'congressos-e-palestras',   1),
    ('Cursos e Workshops',      'cursos-e-workshops',       1),
    ('Cultura e Arte',          'cultura-e-arte',           1),
    ('Gastronomia',             'gastronomia',              1),
    ('Institucional CEFET/RJ',  'institucional-cefet-rj',   1);
