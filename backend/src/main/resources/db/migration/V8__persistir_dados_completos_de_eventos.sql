INSERT INTO usuario (id, nome, email, senha_hash)
SELECT 2, 'Produtora Cultural Exemplo', 'contato@produtora.exemplo', '$2a$10$demoDemoDemoDemoDemoDe'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 2);

INSERT INTO organizador (usuario_id, razao_social, documento, taxa_servico)
SELECT 2, 'Produtora Cultural Exemplo Ltda', '11.111.111/0001-11', 0.1000
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM organizador WHERE usuario_id = 2);

ALTER TABLE evento
    ADD COLUMN imagem_divulgacao VARCHAR(255) NULL;

CREATE TABLE evento_tag (
    evento_id BIGINT NOT NULL,
    tag VARCHAR(80) NOT NULL,
    PRIMARY KEY (evento_id, tag),
    CONSTRAINT fk_evento_tag_evento
        FOREIGN KEY (evento_id) REFERENCES evento (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE evento_rede_social (
    evento_id BIGINT NOT NULL,
    rede VARCHAR(20) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    PRIMARY KEY (evento_id, rede),
    CONSTRAINT fk_evento_rede_social_evento
        FOREIGN KEY (evento_id) REFERENCES evento (id) ON DELETE CASCADE
) ENGINE = InnoDB;

ALTER TABLE sessao
    ADD COLUMN duracao VARCHAR(8) NOT NULL DEFAULT '';

ALTER TABLE tipo_ingresso
    ADD COLUMN inicio_vendas DATE NULL,
    ADD COLUMN fim_vendas DATE NULL;
