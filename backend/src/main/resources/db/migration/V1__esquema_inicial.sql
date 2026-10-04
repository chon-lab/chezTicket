-- =====================================================================
-- chezTicket — esquema inicial (MariaDB 11)
-- Deriva do modelo lógico da Figura 5 (docs/modelagem-uml.html).
-- Convenções: nomes em snake_case, dinheiro em DECIMAL, datas em DATETIME(6),
-- status como ENUM para autodocumentar os estados do domínio.
-- =====================================================================
SET NAMES utf8mb4;

-- ------------------------------------------------------------------
-- Identidade e perfis (herança por tabela: um usuário é participante,
-- organizador e/ou administrador)
-- ------------------------------------------------------------------
CREATE TABLE usuario (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    email         VARCHAR(180) NOT NULL,
    senha_hash    VARCHAR(255) NOT NULL,
    telefone      VARCHAR(20),
    status        ENUM('ATIVO', 'INATIVO', 'BLOQUEADO') NOT NULL DEFAULT 'ATIVO',
    criado_em     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_usuario_email UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE participante (
    usuario_id      BIGINT PRIMARY KEY,
    cpf             VARCHAR(14),
    data_nascimento DATE,
    CONSTRAINT fk_participante_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT uk_participante_cpf UNIQUE (cpf)
) ENGINE = InnoDB;

CREATE TABLE organizador (
    usuario_id    BIGINT PRIMARY KEY,
    razao_social  VARCHAR(160) NOT NULL,
    documento     VARCHAR(18) NOT NULL,
    dados_repasse VARCHAR(255),
    taxa_servico  DECIMAL(5, 4) NOT NULL DEFAULT 0.1000,
    CONSTRAINT fk_organizador_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT uk_organizador_documento UNIQUE (documento)
) ENGINE = InnoDB;

CREATE TABLE administrador (
    usuario_id BIGINT PRIMARY KEY,
    cargo      VARCHAR(80),
    CONSTRAINT fk_administrador_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
) ENGINE = InnoDB;

-- ------------------------------------------------------------------
-- Catálogo de eventos
-- ------------------------------------------------------------------
CREATE TABLE categoria (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(80) NOT NULL,
    slug      VARCHAR(80) NOT NULL,
    ativa     TINYINT(1) NOT NULL DEFAULT 1,
    criada_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_categoria_nome UNIQUE (nome),
    CONSTRAINT uk_categoria_slug UNIQUE (slug)
) ENGINE = InnoDB;

CREATE TABLE local (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome             VARCHAR(160) NOT NULL,
    endereco         VARCHAR(255) NOT NULL,
    cidade           VARCHAR(120) NOT NULL,
    uf               CHAR(2) NOT NULL,
    capacidade_total INT NOT NULL,
    criado_em        DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE = InnoDB;

CREATE TABLE evento (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    organizador_id       BIGINT NOT NULL,
    categoria_id         BIGINT NOT NULL,
    titulo               VARCHAR(160) NOT NULL,
    descricao            TEXT,
    classificacao_etaria ENUM('LIVRE', 'DEZ', 'DOZE', 'QUATORZE', 'DEZESSEIS', 'DEZOITO') NOT NULL DEFAULT 'LIVRE',
    imagem_capa          VARCHAR(255),
    status               ENUM('RASCUNHO', 'PUBLICADO', 'ESGOTADO', 'CANCELADO', 'ENCERRADO') NOT NULL DEFAULT 'RASCUNHO',
    politica_reembolso   ENUM('FLEXIVEL', 'PADRAO', 'SEM_REEMBOLSO') NOT NULL DEFAULT 'PADRAO',
    data_publicacao      DATETIME(6),
    criado_em            DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em        DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_evento_organizador FOREIGN KEY (organizador_id) REFERENCES organizador (usuario_id),
    CONSTRAINT fk_evento_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
) ENGINE = InnoDB;
CREATE INDEX ix_evento_status ON evento (status);
CREATE INDEX ix_evento_categoria ON evento (categoria_id);

CREATE TABLE sessao (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    local_id  BIGINT NOT NULL,
    inicio    DATETIME(6) NOT NULL,
    fim       DATETIME(6) NOT NULL,
    status    ENUM('AGENDADA', 'EM_ANDAMENTO', 'ENCERRADA', 'CANCELADA') NOT NULL DEFAULT 'AGENDADA',
    CONSTRAINT fk_sessao_evento FOREIGN KEY (evento_id) REFERENCES evento (id),
    CONSTRAINT fk_sessao_local FOREIGN KEY (local_id) REFERENCES local (id)
) ENGINE = InnoDB;
CREATE INDEX ix_sessao_evento ON sessao (evento_id);
CREATE INDEX ix_sessao_inicio ON sessao (inicio);

CREATE TABLE tipo_ingresso (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    sessao_id          BIGINT NOT NULL,
    nome               VARCHAR(80) NOT NULL,
    gratuito           TINYINT(1) NOT NULL DEFAULT 0,
    preco              DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    taxa_servico       DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    quantidade_total   INT NOT NULL,
    quantidade_vendida INT NOT NULL DEFAULT 0,
    limite_por_compra  INT NOT NULL DEFAULT 5,
    CONSTRAINT fk_tipo_ingresso_sessao FOREIGN KEY (sessao_id) REFERENCES sessao (id),
    CONSTRAINT ck_tipo_ingresso_quantidade CHECK (quantidade_vendida <= quantidade_total)
) ENGINE = InnoDB;
CREATE INDEX ix_tipo_ingresso_sessao ON tipo_ingresso (sessao_id);

-- ------------------------------------------------------------------
-- Descontos
-- ------------------------------------------------------------------
CREATE TABLE cupom (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(40) NOT NULL,
    tipo        ENUM('PERCENTUAL', 'VALOR_FIXO') NOT NULL,
    valor       DECIMAL(10, 2) NOT NULL,
    validade    DATE,
    limite_usos INT NOT NULL DEFAULT 0,
    usos        INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_cupom_codigo UNIQUE (codigo)
) ENGINE = InnoDB;

-- ------------------------------------------------------------------
-- Compra, pagamento e emissão
-- ------------------------------------------------------------------
CREATE TABLE reserva (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    participante_id BIGINT NOT NULL,
    expira_em       DATETIME(6) NOT NULL,
    status          ENUM('ATIVA', 'CONVERTIDA', 'EXPIRADA', 'CANCELADA') NOT NULL DEFAULT 'ATIVA',
    criada_em       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_reserva_participante FOREIGN KEY (participante_id) REFERENCES participante (usuario_id)
) ENGINE = InnoDB;
CREATE INDEX ix_reserva_status_expira ON reserva (status, expira_em);

CREATE TABLE pedido (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    participante_id BIGINT NOT NULL,
    reserva_id      BIGINT,
    cupom_id        BIGINT,
    status          ENUM('AGUARDANDO_PAGAMENTO', 'PAGO', 'RECUSADO', 'EXPIRADO', 'REEMBOLSADO', 'CONCLUIDO')
                        NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    subtotal        DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    valor_taxa      DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    valor_total     DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    criado_em       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_pedido_participante FOREIGN KEY (participante_id) REFERENCES participante (usuario_id),
    CONSTRAINT fk_pedido_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id),
    CONSTRAINT fk_pedido_cupom FOREIGN KEY (cupom_id) REFERENCES cupom (id)
) ENGINE = InnoDB;
CREATE INDEX ix_pedido_status ON pedido (status);
CREATE INDEX ix_pedido_participante ON pedido (participante_id);

CREATE TABLE item_pedido (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id        BIGINT NOT NULL,
    tipo_ingresso_id BIGINT NOT NULL,
    quantidade       INT NOT NULL,
    preco_unitario   DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT fk_item_pedido_tipo_ingresso FOREIGN KEY (tipo_ingresso_id) REFERENCES tipo_ingresso (id)
) ENGINE = InnoDB;
CREATE INDEX ix_item_pedido_pedido ON item_pedido (pedido_id);

CREATE TABLE pagamento (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id    BIGINT NOT NULL,
    gateway      VARCHAR(40) NOT NULL DEFAULT 'STRIPE',
    id_transacao VARCHAR(120),
    metodo       ENUM('PIX', 'CARTAO_CREDITO', 'CARTAO_DEBITO') NOT NULL,
    parcelas     INT NOT NULL DEFAULT 1,
    valor        DECIMAL(10, 2) NOT NULL,
    status       ENUM('PENDENTE', 'APROVADO', 'RECUSADO', 'ESTORNADO') NOT NULL DEFAULT 'PENDENTE',
    criado_em    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_pagamento_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT uk_pagamento_pedido UNIQUE (pedido_id),
    CONSTRAINT uk_pagamento_transacao UNIQUE (id_transacao)
) ENGINE = InnoDB;

CREATE TABLE reembolso (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    pagamento_id  BIGINT NOT NULL,
    motivo        ENUM('CANCELAMENTO_ORGANIZADOR', 'REMARCACAO', 'DESISTENCIA', 'CHARGEBACK', 'PAGAMENTO_RECUSADO') NOT NULL,
    tipo          ENUM('TOTAL', 'PARCIAL') NOT NULL,
    valor         DECIMAL(10, 2) NOT NULL,
    status        ENUM('PENDENTE', 'PROCESSADO', 'EM_ANALISE', 'NEGADO') NOT NULL DEFAULT 'PENDENTE',
    solicitado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    processado_em DATETIME(6),
    CONSTRAINT fk_reembolso_pagamento FOREIGN KEY (pagamento_id) REFERENCES pagamento (id)
) ENGINE = InnoDB;

CREATE TABLE ingresso (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id        BIGINT NOT NULL,
    tipo_ingresso_id BIGINT NOT NULL,
    codigo_qr        VARCHAR(64) NOT NULL,
    status           ENUM('RESERVADO', 'EMITIDO', 'TRANSFERIDO', 'UTILIZADO', 'CANCELADO') NOT NULL DEFAULT 'RESERVADO',
    titular          VARCHAR(160),
    data_emissao     DATETIME(6),
    data_checkin     DATETIME(6),
    portao_checkin   VARCHAR(40),
    operador_checkin VARCHAR(120),
    CONSTRAINT fk_ingresso_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT fk_ingresso_tipo FOREIGN KEY (tipo_ingresso_id) REFERENCES tipo_ingresso (id),
    CONSTRAINT uk_ingresso_codigo_qr UNIQUE (codigo_qr)
) ENGINE = InnoDB;
CREATE INDEX ix_ingresso_pedido ON ingresso (pedido_id);
CREATE INDEX ix_ingresso_status ON ingresso (status);

CREATE TABLE repasse_financeiro (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id     BIGINT NOT NULL,
    valor_bruto   DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    valor_taxa    DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    valor_liquido DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    status        ENUM('PREVISTO', 'LIBERADO', 'PAGO') NOT NULL DEFAULT 'PREVISTO',
    data_prevista DATE,
    CONSTRAINT fk_repasse_evento FOREIGN KEY (evento_id) REFERENCES evento (id)
) ENGINE = InnoDB;
