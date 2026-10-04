# chezTicket — Back-end

[![Backend CI](https://github.com/chon-lab/chezTicket/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/chon-lab/chezTicket/actions/workflows/backend-ci.yml)

API da plataforma de ingressos **chezTicket**.

- **Java 17** + **Spring Boot 3.3**
- **Arquitetura hexagonal** (Ports & Adapters)
- **MariaDB 11** com migrações **Flyway**
- **Docker / Docker Compose** para subir tudo com um comando

> Modelagem que orienta o código: [`../docs/modelagem-uml.html`](../docs/modelagem-uml.html)

---

## Subir o ambiente (recomendado — não precisa de Java local)

Pré-requisito: **Docker Desktop** em execução.

```bash
cp .env.example .env          # na raiz do repositório
docker compose up --build
```

| Serviço  | URL                                    | Observação                         |
|----------|----------------------------------------|------------------------------------|
| API      | http://localhost:8081                  | Spring Boot                        |
| Health   | http://localhost:8081/actuator/health  | deve responder `{"status":"UP"}`   |
| Swagger  | http://localhost:8081/swagger-ui.html  | documentação interativa da API     |
| MariaDB  | `localhost:8080`                       | user/senha `chezticket`/`chezticket` |
| Adminer  | http://localhost:8090                  | cliente web do banco (server: `mariadb`) |

Parar: `docker compose down` — apagar também os dados: `docker compose down -v`.

### Validar pelo Swagger

Abra **http://localhost:8081/swagger-ui.html** — todos os endpoints com "Try it out",
enums em dropdown e exemplos preenchidos. O JSON da spec fica em `/v3/api-docs`.

### Testar por linha de comando

```bash
# Categorias (seed da migração V2)
curl http://localhost:8081/api/categorias

# Criar um evento em rascunho (organizador 2 e categoria 1 vêm da migração V3)
curl -X POST http://localhost:8081/api/eventos \
     -H "Content-Type: application/json" \
     -d '{"organizadorId":2,"categoriaId":1,"titulo":"Festival de Inverno 2026","descricao":"Shows e oficinas."}'

# Publicar
curl -X POST http://localhost:8081/api/eventos/1/publicacao

# Listar só os publicados
curl "http://localhost:8081/api/eventos?status=PUBLICADO"

# Categoria: criar, renomear e desativar
curl -X POST http://localhost:8081/api/categorias -H "Content-Type: application/json" -d '{"nome":"Feira de Games"}'
curl -X PUT http://localhost:8081/api/categorias/9 -H "Content-Type: application/json" -d '{"nome":"Feira de Jogos"}'
curl -X POST http://localhost:8081/api/categorias/9/desativacao
```

### Endpoints de Categoria

| Método | Rota | Ação |
|--------|------|------|
| `GET`    | `/api/categorias` | lista (`apenasAtivas=true` oculta as desativadas) |
| `GET`    | `/api/categorias/{id}` | detalha |
| `POST`   | `/api/categorias` | cadastra |
| `PUT`    | `/api/categorias/{id}` | renomeia (slug é recalculado) |
| `DELETE` | `/api/categorias/{id}` | remove → `204`, ou `409` se houver evento vinculado |
| `POST`   | `/api/categorias/{id}/ativacao` | volta a aparecer nas buscas |
| `POST`   | `/api/categorias/{id}/desativacao` | some das buscas com `apenasAtivas=true` |

### Endpoints de Evento

| Método | Rota | Ação |
|--------|------|------|
| `GET`    | `/api/eventos` | lista (filtros: `status`, `categoriaId`, `organizadorId`) |
| `GET`    | `/api/eventos/{id}` | detalha |
| `POST`   | `/api/eventos` | cria rascunho |
| `PUT`    | `/api/eventos/{id}` | atualiza dados (só em RASCUNHO ou PUBLICADO) |
| `DELETE` | `/api/eventos/{id}` | remove: RASCUNHO → qualquer um; publicado/cancelado/encerrado → só ADMIN (ver abaixo) |
| `POST`   | `/api/eventos/{id}/publicacao` | RASCUNHO → PUBLICADO (exige descrição) |
| `POST`   | `/api/eventos/{id}/cancelamento` | qualquer estado não terminal → CANCELADO |
| `POST`   | `/api/eventos/{id}/encerramento` | PUBLICADO/ESGOTADO → ENCERRADO |

Erros seguem RFC 7807: `422` regra de negócio, `404` inexistente, `409` conflito (transição de
status inválida, nome duplicado, ou remoção bloqueada por FK — ex.: categoria com evento
vinculado), `403` sem permissão, `400` payload malformado.

### Regra: só ADMIN remove evento publicado

`DELETE /api/eventos/{id}` deixa qualquer um remover um evento em `RASCUNHO`, mas exige
administrador para remover um evento em qualquer outro status. Como **ainda não existe
autenticação**, quem está chamando é informado por um header temporário:

```bash
curl -X DELETE http://localhost:8081/api/eventos/2
# -> 403, se o evento não estiver em RASCUNHO

curl -X DELETE http://localhost:8081/api/eventos/2 -H "X-Usuario-Id: 3"
# -> 204 (usuário 3 é administrador no seed de demonstração, V3)
```

Implementação (`VerificarPapelPort` / `VerificarPapelAdapter`, em
`evento/adapter/out/identidade/`): consulta a tabela `administrador`. O `X-Usuario-Id` é um
placeholder deliberado — quando a autenticação existir, só o adaptador muda (lê o usuário
autenticado em vez do header); a porta e o `EventoService` continuam os mesmos.

---

## Desenvolvimento local (fora do container)

Só precisa de **JDK 17** instalado — não precisa instalar Maven. O projeto usa o **Maven
Wrapper** (`mvnw`/`mvnw.cmd`), que baixa e usa a versão certa do Maven sozinho:

```bash
docker compose up -d mariadb   # só o banco; a API roda local
cd backend
./mvnw spring-boot:run         # Windows: mvnw.cmd spring-boot:run
```

O `application.yml` já aponta, por padrão, para `jdbc:mariadb://localhost:8080/chezticket`.
Para sobrescrever, use variáveis de ambiente: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

> **Não tem JDK 17 instalado?**
> - **Windows**: `winget install EclipseAdoptium.Temurin.17.JDK`
> - **macOS**: `brew install --cask temurin17`
> - **Linux**: `sdk install java 17.0.13-tem` (via [SDKMAN!](https://sdkman.io)) ou o pacote
>   `openjdk-17-jdk` do seu gerenciador
>
> Depois de instalar, confirme que `JAVA_HOME` aponta para esse JDK (`echo $JAVA_HOME` /
> `echo $env:JAVA_HOME`) — é a causa mais comum de erro "falso" no editor (tudo sublinhado de
> vermelho mesmo com o código certo) quando `JAVA_HOME` está vazio ou aponta para um caminho que
> não existe mais. Se não quiser mexer na máquina local, o caminho via Docker no topo deste
> arquivo não depende de nada disso.

### Testes

```bash
cd backend
./mvnw test     # unitários (domínio + serviço de aplicação, mockado) — rápido, sem Docker
./mvnw verify   # tudo, incluindo os *IT (Testcontainers) — requer Docker em execução
```

| Camada | Classe | O que valida |
|---|---|---|
| Domínio (puro) | `CategoriaTest`, `EventoTest` | regras de formação e transições de status, sem Spring |
| Aplicação (mockada) | `CategoriaServiceTest`, `EventoServiceTest` | orquestração dos casos de uso com as portas de saída mockadas (Mockito) |
| Persistência (`*IT`) | `CategoriaPersistenceAdapterIT`, `EventoPersistenceAdapterIT` | sobe um MariaDB real via **Testcontainers**, roda o Flyway e testa o adaptador — inclusive a query de filtro dinâmico do Evento |

As classes `*IT` rodam só em `mvn verify` (plugin **Failsafe**), nunca em `mvn test` — é a
convenção Maven para separar teste rápido de teste que precisa de infraestrutura.

---

## Como o código está organizado (hexagonal)

Pacote raiz: `com.chezticket`. Uma **fatia vertical por funcionalidade**; dentro dela,
as camadas do hexágono:

```
catalogo/categoria/
├── domain/                     # núcleo puro — sem Spring, sem JPA
│   ├── Categoria.java          #   regras: nome válido, slug derivado, ativar/desativar
│   ├── Slug.java
│   └── exception/
├── application/                # casos de uso (orquestração)
│   ├── port/in/                #   o que o mundo pode PEDIR ao sistema
│   │   ├── CadastrarCategoriaUseCase.java
│   │   ├── ConsultarCategoriasUseCase.java
│   │   ├── AtualizarCategoriaUseCase.java
│   │   ├── GerenciarStatusDaCategoriaUseCase.java  (ativar/desativar)
│   │   └── RemoverCategoriaUseCase.java
│   ├── port/out/               #   o que o sistema PRECISA do mundo
│   │   └── CategoriaRepositorio.java
│   └── CategoriaService.java   #   implementa as portas de entrada
└── adapter/
    ├── in/web/                 # dirige o sistema: REST + DTOs + tradução de erros
    └── out/persistence/        # é dirigido pelo sistema: JPA + MariaDB
        ├── CategoriaJpaEntity.java
        ├── CategoriaJpaRepository.java
        ├── CategoriaPersistenceMapper.java
        └── CategoriaPersistenceAdapter.java   # implementa CategoriaRepositorio

shared/
├── domain/                     # exceções base do domínio (RegraDeNegocio, Conflito, ...)
└── web/ApiExceptionHandler.java# domínio -> HTTP (RFC 7807); também traduz
                                 # DataIntegrityViolationException (violação de FK) em 409,
                                 # sem nenhum módulo precisar conhecer o outro pra isso
```

**Regra da dependência:** as setas apontam sempre para dentro. `adapter` conhece
`application`; `application` conhece `domain`; `domain` não conhece ninguém.
O `CategoriaController` depende da **interface** `CadastrarCategoriaUseCase`, e o
`CategoriaService` depende da **interface** `CategoriaRepositorio` — trocar MariaDB por
outra coisa é escrever um novo adaptador, sem tocar no núcleo.

---

## Banco de dados

O **Flyway é a única fonte da verdade** do schema (`spring.jpa.hibernate.ddl-auto: none`).

```
src/main/resources/db/migration/
├── V1__esquema_inicial.sql       # todas as tabelas do modelo lógico (Figura 5)
├── V2__seed_categorias.sql       # categorias iniciais
└── V3__dados_demonstracao.sql    # organizador e local de exemplo, p/ testar via Swagger
```

Para evoluir o schema, **nunca edite uma migração já aplicada** — crie a próxima:
`V4__descricao_curta.sql`. As migrações rodam sozinhas no start da aplicação.
(Exceção: enquanto `backend/` não for versionado no Git — hoje está no `.gitignore` —,
ainda dá pra ajustar a `V1` direto e recriar o volume com `docker compose down -v`.)

---

## Módulos implementados

| Módulo | Endpoints | Observação |
|--------|-----------|------------|
| `catalogo/categoria` | `/api/categorias` | CRUD completo + ativar/desativar |
| `catalogo/evento` | `/api/eventos` | CRUD completo + ciclo de vida (publicar/cancelar/encerrar) |

O módulo `evento` depende de `categoria` apenas pela porta de saída `VerificarCategoriaPort`,
implementada em `evento/adapter/out/catalogo/` — único ponto de acoplamento entre módulos.

## Próximos passos

1. **Local**, **Sessão** e **TipoIngresso** (fecham o contexto de catálogo).
2. Paginação e ordenação nas listagens.
3. Autenticação de verdade (usuário / organizador / participante) e proteção dos demais
   endpoints — troca o header `X-Usuario-Id` (placeholder) por um usuário autenticado.
4. Reserva de estoque + checkout + integração **Stripe** (Fases 4–5 do cronograma).
