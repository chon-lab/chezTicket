-- =====================================================================
-- chezTicket — suporte ao front-end real (autenticação, cupom por
-- evento, endereço livre em sessão, janela de vendas de ingresso,
-- divulgação de evento). Aditivo: não altera o que já foi migrado.
-- =====================================================================

-- Evento: tags, redes sociais e imagem de divulgação coletadas no
-- assistente de criação (passo "Divulgações"). Guardadas como JSON em
-- texto — não há consulta estruturada sobre esses campos, só exibição.
ALTER TABLE evento
    ADD COLUMN tags TEXT NULL,
    ADD COLUMN redes_sociais TEXT NULL,
    ADD COLUMN imagem_divulgacao VARCHAR(255) NULL;

-- Sessão: o assistente de criação só coleta um endereço em texto livre,
-- sem local estruturado. local_id continua existindo para quem usa o
-- CRUD de Local (Fase 4); endereco é a alternativa para o fluxo rápido.
ALTER TABLE sessao
    MODIFY COLUMN local_id BIGINT NULL,
    ADD COLUMN endereco VARCHAR(255) NULL;

-- Tipo de ingresso: janela de vendas opcional (quando ausente, vende-se
-- a qualquer momento enquanto houver estoque).
ALTER TABLE tipo_ingresso
    ADD COLUMN inicio_vendas DATETIME(6) NULL,
    ADD COLUMN fim_vendas DATETIME(6) NULL;

-- Cupom: no modelo simplificado usado pelo front, cada cupom pertence a
-- um único evento e é criado junto com ele.
ALTER TABLE cupom
    ADD COLUMN evento_id BIGINT NULL,
    ADD CONSTRAINT fk_cupom_evento FOREIGN KEY (evento_id) REFERENCES evento (id);
CREATE INDEX ix_cupom_evento ON cupom (evento_id);
