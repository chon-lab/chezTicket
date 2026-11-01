-- =====================================================================
-- chezTicket — suporte ao Módulo de Ingressos (Fase 4 do cronograma).
-- Aditivo apenas: não altera nem remove nada do que já foi migrado em V1-V3.
-- =====================================================================

-- Controle de concorrência otimista no estoque de tipo_ingresso: duas reservas
-- simultâneas para o mesmo tipo de ingresso não derrubam uma a escrita da outra
-- silenciosamente (JPA @Version já falha com HTTP 409 em vez de sobrevender).
ALTER TABLE tipo_ingresso ADD COLUMN versao BIGINT NOT NULL DEFAULT 0;

-- Itens de uma reserva: quais tipos de ingresso e em que quantidade foram retidos
-- temporariamente enquanto o participante conclui a compra (ou a reserva expira).
CREATE TABLE item_reserva (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    reserva_id       BIGINT NOT NULL,
    tipo_ingresso_id BIGINT NOT NULL,
    quantidade       INT NOT NULL,
    CONSTRAINT fk_item_reserva_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id),
    CONSTRAINT fk_item_reserva_tipo_ingresso FOREIGN KEY (tipo_ingresso_id) REFERENCES tipo_ingresso (id)
) ENGINE = InnoDB;
CREATE INDEX ix_item_reserva_reserva ON item_reserva (reserva_id);

-- Participante de demonstração para exercitar o fluxo de reserva pelo Swagger
-- (até existir autenticação de verdade, use o id 4 como participanteId).
INSERT INTO usuario (id, nome, email, senha_hash) VALUES
    (4, 'Participante Demo', 'participante@chezticket.dev', '$2a$10$demoDemoDemoDemoDemoDe');

INSERT INTO participante (usuario_id, cpf, data_nascimento) VALUES
    (4, '000.000.000-00', '2000-01-01');
