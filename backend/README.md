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
# Categorias já vêm com seed (V2)
curl http://localhost:8081/api/categorias

# Criar um evento em rascunho (organizador 2 e categoria 1 vêm do seed de demonstração, V3)
curl -X POST http://localhost:8081/api/eventos \
     -H "Content-Type: application/json" \
     -d '{"organizadorId":2,"categoriaId":1,"titulo":"Festival de Inverno 2026","descricao":"Shows e oficinas."}'

# Publicar e filtrar
curl -X POST http://localhost:8081/api/eventos/1/publicacao
curl "http://localhost:8081/api/eventos?status=PUBLICADO"

# Categoria: criar, renomear, desativar
curl -X POST http://localhost:8081/api/categorias -H "Content-Type: application/json" -d '{"nome":"Feira de Games"}'
curl -X PUT http://localhost:8081/api/categorias/9 -H "Content-Type: application/json" -d '{"nome":"Feira de Jogos"}'
curl -X POST http://localhost:8081/api/categorias/9/desativacao
```

## Endpoints

### Categoria — `/api/categorias`

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
| `PUT`    | `/{id}` | atualiza dados (só em `RASCUNHO` ou `PUBLICADO`) |
| `DELETE` | `/{id}` | remove — `RASCUNHO`: qualquer um · outro status: só ADMIN, ver abaixo |
| `POST`   | `/{id}/publicacao` | `RASCUNHO` → `PUBLICADO` (exige descrição) |
| `POST`   | `/{id}/cancelamento` | qualquer estado não terminal → `CANCELADO` |
| `POST`   | `/{id}/encerramento` | `PUBLICADO`/`ESGOTADO` → `ENCERRADO` |

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
Nunca edite uma migração já aplicada — crie a próxima (`V4__descricao_curta.sql`).
