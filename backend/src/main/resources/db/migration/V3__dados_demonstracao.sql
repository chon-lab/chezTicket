-- =====================================================================
-- Dados de demonstração para o projeto acadêmico.
-- Dão um organizador e um local prontos para exercitar o CRUD de eventos
-- pelo Swagger. IDs fixos para deixar as requisições previsíveis.
-- =====================================================================

INSERT INTO usuario (id, nome, email, senha_hash) VALUES
    (1, 'Coordenação de Eventos CEFET/RJ', 'eventos@cefet-rj.br',      '$2a$10$demoDemoDemoDemoDemoDe'),
    (2, 'Produtora Cultural Exemplo',      'contato@produtora.exemplo', '$2a$10$demoDemoDemoDemoDemoDe'),
    (3, 'Administrador chezTicket',        'admin@chezticket.dev',     '$2a$10$demoDemoDemoDemoDemoDe');

INSERT INTO organizador (usuario_id, razao_social, documento, taxa_servico) VALUES
    (1, 'CEFET/RJ',                        '00.000.000/0001-00', 0.0000),
    (2, 'Produtora Cultural Exemplo Ltda', '11.111.111/0001-11', 0.1000);

-- Usuário de teste com papel de administrador (até existir autenticação de verdade,
-- use o id 3 no header X-Usuario-Id para testar ações restritas a ADMIN no Swagger).
INSERT INTO administrador (usuario_id, cargo) VALUES
    (3, 'Administrador da plataforma');

INSERT INTO local (id, nome, endereco, cidade, uf, capacidade_total) VALUES
    (1, 'Auditório Principal — CEFET/RJ Maracanã', 'Av. Maracanã, 229', 'Rio de Janeiro', 'RJ', 400),
    (2, 'Teatro Municipal Exemplo',                'Praça Central, 100', 'Rio de Janeiro', 'RJ', 1200);
