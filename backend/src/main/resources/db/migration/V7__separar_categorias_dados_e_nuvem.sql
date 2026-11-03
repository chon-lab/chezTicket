-- A categoria composta foi substituída pelas duas categorias independentes.
UPDATE categoria
SET ativa = 0
WHERE slug = 'dados-e-nuvem';

INSERT INTO categoria (nome, slug, ativa) VALUES
    ('Dados', 'dados', 1),
    ('Nuvem', 'nuvem', 1)
ON DUPLICATE KEY UPDATE
    nome = VALUES(nome),
    ativa = VALUES(ativa);
