ALTER TABLE item_reserva
    DROP FOREIGN KEY fk_item_reserva_reserva,
    ADD CONSTRAINT fk_item_reserva_reserva_cascade
        FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE CASCADE,
    DROP FOREIGN KEY fk_item_reserva_tipo_ingresso,
    ADD CONSTRAINT fk_item_reserva_tipo_ingresso_cascade
        FOREIGN KEY (tipo_ingresso_id) REFERENCES tipo_ingresso (id) ON DELETE CASCADE;

ALTER TABLE pedido
    DROP FOREIGN KEY fk_pedido_reserva,
    ADD CONSTRAINT fk_pedido_reserva_cascade
        FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE CASCADE;

ALTER TABLE item_pedido
    DROP FOREIGN KEY fk_item_pedido_pedido,
    ADD CONSTRAINT fk_item_pedido_pedido_cascade
        FOREIGN KEY (pedido_id) REFERENCES pedido (id) ON DELETE CASCADE,
    DROP FOREIGN KEY fk_item_pedido_tipo_ingresso,
    ADD CONSTRAINT fk_item_pedido_tipo_ingresso_cascade
        FOREIGN KEY (tipo_ingresso_id) REFERENCES tipo_ingresso (id) ON DELETE CASCADE;

ALTER TABLE pagamento
    DROP FOREIGN KEY fk_pagamento_pedido,
    ADD CONSTRAINT fk_pagamento_pedido_cascade
        FOREIGN KEY (pedido_id) REFERENCES pedido (id) ON DELETE CASCADE;

ALTER TABLE reembolso
    DROP FOREIGN KEY fk_reembolso_pagamento,
    ADD CONSTRAINT fk_reembolso_pagamento_cascade
        FOREIGN KEY (pagamento_id) REFERENCES pagamento (id) ON DELETE CASCADE;

ALTER TABLE ingresso
    DROP FOREIGN KEY fk_ingresso_pedido,
    ADD CONSTRAINT fk_ingresso_pedido_cascade
        FOREIGN KEY (pedido_id) REFERENCES pedido (id) ON DELETE CASCADE,
    DROP FOREIGN KEY fk_ingresso_tipo,
    ADD CONSTRAINT fk_ingresso_tipo_cascade
        FOREIGN KEY (tipo_ingresso_id) REFERENCES tipo_ingresso (id) ON DELETE CASCADE;

ALTER TABLE repasse_financeiro
    DROP FOREIGN KEY fk_repasse_evento,
    ADD CONSTRAINT fk_repasse_evento_cascade
        FOREIGN KEY (evento_id) REFERENCES evento (id) ON DELETE CASCADE;
