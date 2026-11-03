# chezTicket — Back-end

[![Backend CI](https://github.com/chon-lab/chezTicket/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/chon-lab/chezTicket/actions/workflows/backend-ci.yml)

API da plataforma de ingressos **chezTicket**. Java 17 · Spring Boot 3.3 · MariaDB 11 + Flyway · Docker.

> Modelagem: [`../docs/modelagem-uml.html`](../docs/modelagem-uml.html) · Arquitetura e status
> de implementação: [`../docs/implementacao-backend.html`](../docs/implementacao-backend.html)

---

## Subir com Docker (recomendado)

Pré-requisito: **Docker Desktop** em execução.

```bash
cp .env.example .env          # na raiz do repositório
docker compose up --build
```

| Serviço  | URL                                    | Observação                         |
|----------|-----------------------------------------|-------------------------------------|
| API      | http://localhost:8081                  | Spring Boot                        |
| Swagger  | http://localhost:8081/swagger-ui.html  | documentação interativa da API     |
| Health   | http://localhost:8081/actuator/health  | deve responder `{"status":"UP"}`   |
| MariaDB  | `localhost:8080`                       | user/senha `chezticket`/`chezticket` |
| Adminer  | http://localhost:8090                  | cliente web do banco (server: `mariadb`) |

Parar: `docker compose down` — apagar também os dados: `docker compose down -v`.

## Testar a API

Mais fácil: abra **http://localhost:8081/swagger-ui.html** — todos os endpoints com "Try it
out", enums em dropdown e exemplos preenchidos.

Por linha de comando:

```bash
# Categorias vêm do seed (V2) e do ajuste de catálogo (V5)
curl http://localhost:8081/api/categorias

# Criar um evento em rascunho (organizador 2 vem do seed V3; categoria 9 é IA no seed padrão)
curl -X POST http://localhost:8081/api/eventos \
     -H "Content-Type: application/json" \
     -d '{"organizadorId":2,"categoriaId":9,"titulo":"Festival de Inverno 2026","descricao":"Evento de tecnologia."}'

# Publicar e filtrar
curl -X POST http://localhost:8081/api/eventos/1/publicacao
curl "http://localhost:8081/api/eventos?status=PUBLICADO"

# Criar evento, sessão e ingressos juntos; imagemCapa e imagemDivulgacao são opcionais
curl -X POST http://localhost:8081/api/eventos/completo \
     -F 'dados={"organizadorId":2,"categoriaId":9,"titulo":"Encontro de IA","descricao":"Evento de tecnologia.","classificacaoEtaria":"LIVRE","politicaReembolso":"PADRAO","endereco":"Av. Exemplo, 100, Rio de Janeiro, RJ","inicio":"2027-01-20T19:00:00","fim":"2027-01-20T22:00:00","tags":["IA","tecnologia"],"redesSociais":{"instagram":"https://instagram.com/exemplo"},"ingressos":[{"nome":"Inteira","gratuito":false,"preco":100.00,"taxaServico":10.00,"quantidadeTotal":200,"limitePorCompra":5,"inicioVendas":"2026-11-03","fimVendas":"2027-01-20"}],"publicar":false};type=application/json' \
     -F 'imagemCapa=@./capa.jpg;type=image/jpeg'

# Categoria: criar, renomear, desativar
curl -X POST http://localhost:8081/api/categorias -H "Content-Type: application/json" -d '{"nome":"Feira de Games"}'
curl -X PUT http://localhost:8081/api/categorias/16 -H "Content-Type: application/json" -d '{"nome":"Feira de Jogos"}'
curl -X POST http://localhost:8081/api/categorias/16/desativacao

# Fluxo completo do Módulo de Ingressos: sessão (local 1 do seed) -> tipo de ingresso -> reserva
curl -X POST http://localhost:8081/api/sessoes -H "Content-Type: application/json" \
     -d '{"eventoId":1,"localId":1,"inicio":"2026-12-05T19:00:00","fim":"2026-12-05T22:00:00"}'

curl -X POST http://localhost:8081/api/tipos-ingresso -H "Content-Type: application/json" \
     -d '{"sessaoId":1,"nome":"Inteira","gratuito":false,"preco":100.00,"taxaServico":10.00,"quantidadeTotal":200,"limitePorCompra":5}'

# participanteId 4 vem do seed de demonstração (V4)
curl -X POST http://localhost:8081/api/reservas -H "Content-Type: application/json" \
     -d '{"participanteId":4,"itens":[{"tipoIngressoId":1,"quantidade":2}]}'
```

## Endpoints

### Categoria — `/api/categorias`

O catálogo padrão mantém ativas as categorias **IA**, **Robótica**, **Games**, **Hackaton**,
**Dados** e **Nuvem**, além de **Congressos e Palestras**, **Cursos e Workshops** e
**Institucional CEFET/RJ**. A categoria composta **Dados e Nuvem**, Shows e Música, Teatro e
Espetáculos, Esportes, Cultura e Arte e Gastronomia ficam inativas; os registros são preservados
para manter referências de eventos existentes.

| Método | Rota | Ação |
|--------|------|------|
| `GET`    | `/` | lista (`?apenasAtivas=true` oculta as desativadas) |
| `GET`    | `/{id}` | detalha |
| `POST`   | `/` | cadastra |
| `PUT`    | `/{id}` | renomeia (slug é recalculado) |
| `DELETE` | `/{id}` | remove → `204`, ou `409` se houver evento vinculado |
| `POST`   | `/{id}/ativacao` | volta a aparecer nas buscas |
| `POST`   | `/{id}/desativacao` | some das buscas com `apenasAtivas=true` |

### Evento — `/api/eventos`

| Método | Rota | Ação |
|--------|------|------|
| `GET`    | `/` | lista (filtros: `?status=`, `?categoriaId=`, `?organizadorId=`) |
| `GET`    | `/{id}` | detalha |
| `POST`   | `/` | cria rascunho |
| `POST`   | `/completo` | cria evento, sessão e ingressos em uma transação (`multipart/form-data`) |
| `PUT`    | `/{id}` | atualiza dados (só em `RASCUNHO` ou `PUBLICADO`) |
| `DELETE` | `/{id}` | remove — `RASCUNHO`: qualquer um · outro status: só ADMIN, ver abaixo |
| `POST`   | `/{id}/publicacao` | `RASCUNHO` → `PUBLICADO` (exige descrição) |
| `POST`   | `/{id}/cancelamento` | qualquer estado não terminal → `CANCELADO` |
| `POST`   | `/{id}/encerramento` | `PUBLICADO`/`ESGOTADO` → `ENCERRADO` |

`POST /api/eventos/completo` recebe um campo JSON `dados` com organizador, categoria, título,
descrição, classificação etária, política de reembolso, endereço digitado, início/fim, tags,
links sociais, ingressos e a opção `publicar`. O endereço fica associado à sessão sem criar uma
entrada no catálogo global de locais. Os campos `imagemCapa` e
`imagemDivulgacao` aceitam JPG, PNG ou WEBP, até 5 MB cada, e são opcionais. A operação cria
evento, sessão e ingressos atomicamente; `publicar: true` também publica o evento.

As imagens ficam no diretório `uploads/` na raiz do repositório durante o uso local e são servidas
em `/api/uploads/{arquivo}`. No Docker, o diretório é montado em `./uploads` para sobreviver à
recriação do container. O caminho pode ser alterado com `CHEZTICKET_UPLOAD_DIR`.

Erros seguem RFC 7807: `400` payload malformado, `403` sem permissão, `404` inexistente,
`409` conflito, `422` regra de negócio violada.

**Removendo um evento publicado** — exige administrador. Autenticação ainda não existe, então
quem chama se identifica por um header temporário:

```bash
curl -X DELETE http://localhost:8081/api/eventos/2
# -> 403 (evento não está em RASCUNHO)

curl -X DELETE http://localhost:8081/api/eventos/2 -H "X-Usuario-Id: 3"
# -> 204 (usuário 3 é administrador no seed de demonstração)
```

### Local — `/api/locais`

| Método | Rota | Ação |
|--------|------|------|
| `GET`    | `/` | lista |
| `GET`    | `/{id}` | detalha |
| `POST`   | `/` | cadastra |
| `PUT`    | `/{id}` | atualiza dados |
| `DELETE` | `/{id}` | remove → `204`, ou `409` se houver sessão vinculada |

### Sessão — `/api/sessoes`

| Método | Rota | Ação |
|--------|------|------|
| `GET`    | `/` | lista (filtros: `?eventoId=`, `?status=`) |
| `GET`    | `/{id}` | detalha |
| `POST`   | `/` | agenda (evento e local precisam existir) |
| `PUT`    | `/{id}` | reagenda local/horário (só em `AGENDADA`) |
| `DELETE` | `/{id}` | remove (só em `AGENDADA`) |
| `POST`   | `/{id}/inicio` | `AGENDADA` → `EM_ANDAMENTO` |
| `POST`   | `/{id}/encerramento` | `EM_ANDAMENTO` → `ENCERRADA` |
| `POST`   | `/{id}/cancelamento` | `AGENDADA`/`EM_ANDAMENTO` → `CANCELADA` |

### Tipo de ingresso — `/api/tipos-ingresso`

| Método | Rota | Ação |
|--------|------|------|
| `GET`    | `/?sessaoId=` | lista os tipos de uma sessão |
| `GET`    | `/{id}` | detalha (inclui `quantidadeDisponivel`) |
| `POST`   | `/` | cadastra (`gratuito:true` zera preço/taxa) |
| `PUT`    | `/{id}` | atualiza (quantidade total não pode ficar abaixo da vendida) |
| `DELETE` | `/{id}` | remove (só sem nenhuma unidade vendida/reservada) |

### Reserva — `/api/reservas`

| Método | Rota | Ação |
|--------|------|------|
| `GET`    | `/?participanteId=` | lista as reservas de um participante |
| `GET`    | `/{id}` | detalha |
| `POST`   | `/` | reserva um ou mais tipos de ingresso → `409` se faltar estoque |
| `POST`   | `/{id}/cancelamento` | cancela e libera o estoque na hora |

Reserva fica `ATIVA` por 10 minutos (constante no `ReservaService`); depois disso, um
`@Scheduled` interno expira e libera o estoque automaticamente — não precisa chamar nada.

## Rodar sem Docker

Só precisa de **JDK 17** — o projeto usa o **Maven Wrapper**, então não precisa instalar Maven:

```bash
docker compose up -d mariadb   # só o banco; a API roda local
cd backend
./mvnw spring-boot:run         # Windows: mvnw.cmd spring-boot:run
```

Sem JDK 17 instalado: `winget install EclipseAdoptium.Temurin.17.JDK` (Windows) ·
`brew install --cask temurin17` (macOS) · `sdk install java 17.0.13-tem` via
[SDKMAN!](https://sdkman.io) ou `apt install openjdk-17-jdk` (Linux).

Para apontar para outro banco, defina `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e
`SPRING_DATASOURCE_PASSWORD` (por padrão usa `jdbc:mariadb://localhost:8080/chezticket`).

### Testes

```bash
./mvnw test     # unitários (domínio + serviço de aplicação) — rápido, sem Docker
./mvnw verify   # tudo, incluindo os *IT (Testcontainers) — requer Docker em execução
```

### Schema do banco

O **Flyway é a única fonte da verdade** do schema (`src/main/resources/db/migration/`).
Nunca edite uma migração já aplicada — crie a próxima versão disponível (após V7, por exemplo,
`V8__descricao_curta.sql`).
