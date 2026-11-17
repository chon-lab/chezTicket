ALTER TABLE sessao
    DROP FOREIGN KEY fk_sessao_evento;

ALTER TABLE tipo_ingresso
    DROP FOREIGN KEY fk_tipo_ingresso_sessao;

ALTER TABLE sessao
    ADD CONSTRAINT fk_sessao_evento_cascade FOREIGN KEY (evento_id) REFERENCES evento (id) ON DELETE CASCADE;

ALTER TABLE tipo_ingresso
    ADD CONSTRAINT fk_tipo_ingresso_sessao_cascade FOREIGN KEY (sessao_id) REFERENCES sessao (id) ON DELETE CASCADE;