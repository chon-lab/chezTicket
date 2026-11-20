ALTER TABLE cupom
    ADD COLUMN evento_id BIGINT,
    ADD COLUMN valido_ate DATETIME(6),
    ADD CONSTRAINT fk_cupom_evento
        FOREIGN KEY (evento_id) REFERENCES evento (id) ON DELETE CASCADE;

CREATE INDEX ix_cupom_evento ON cupom (evento_id);

ALTER TABLE reserva
    ADD COLUMN cupom_id BIGINT,
    ADD CONSTRAINT fk_reserva_cupom
        FOREIGN KEY (cupom_id) REFERENCES cupom (id) ON DELETE SET NULL;
