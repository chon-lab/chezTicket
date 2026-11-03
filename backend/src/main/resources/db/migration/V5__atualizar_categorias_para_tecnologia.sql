-- Mantém as categorias antigas no banco para preservar eventos vinculados,
-- mas remove essas opções do catálogo ativo.
UPDATE categoria
SET ativa = 0
WHERE slug IN (
    'shows-e-musica',
    'teatro-e-espetaculos',
    'esportes',
    'cultura-e-arte',
    'gastronomia'
);

-- Reativa ou cadastra as categorias de tecnologia solicitadas.
INSERT INTO categoria (nome, slug, ativa) VALUES
    ('IA',       'ia',       1),
    ('Robótica', 'robotica', 1),
    ('Games',    'games',    1),
    ('Hackaton', 'hackaton', 1)
ON DUPLICATE KEY UPDATE
    nome = VALUES(nome),
    ativa = VALUES(ativa);
