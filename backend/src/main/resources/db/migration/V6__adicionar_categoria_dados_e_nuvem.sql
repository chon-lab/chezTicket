INSERT INTO categoria (nome, slug, ativa) VALUES
    ('Dados e Nuvem', 'dados-e-nuvem', 1)
ON DUPLICATE KEY UPDATE
    nome = VALUES(nome),
    ativa = VALUES(ativa);
