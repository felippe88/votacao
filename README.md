# API de Votação de Assembleias

API REST em Java/Spring Boot para gerenciar pautas e sessões de votação de assembleias de
cooperativa: cadastro de pauta, abertura de sessão por tempo determinado, registro de votos
(Sim/Não, um por associado por pauta) e apuração do resultado.

O diferencial da solução é que ela não expõe um JSON "cru" de domínio: as respostas são
**telas** — objetos que descrevem título, campos e botões de ação, prontos para um cliente
mobile genérico montar a interface sem conhecer regra de negócio nenhuma. Esse contrato de
tela está descrito em detalhe mais abaixo.

## Sumário

- [Arquitetura](#arquitetura)
- [Decisões técnicas e por quês](#decisões-técnicas-e-por-quês)
- [Contrato de tela](#contrato-de-tela)
- [Endpoints](#endpoints)
- [Como rodar](#como-rodar)
- [Configuração](#configuração)
- [Verificação de elegibilidade por CPF](#verificação-de-elegibilidade-por-cpf)
- [Simulando o fluxo completo](#simulando-o-fluxo-completo)
- [Testes](#testes)
- [Versionamento da API](#versionamento-da-api)
- [Performance](#performance)
- [Logs](#logs)

## Arquitetura

O projeto segue arquitetura hexagonal (ports & adapters), em um único módulo Maven:

```
com.assembleia.votacao
├── domain
│   ├── model          Pauta, SessaoVotacao, Voto, OpcaoVoto, ResultadoApuracao, ResultadoFinal
│   ├── exception       exceções de negócio (RuntimeException)
│   ├── port
│   │   ├── entrada     interfaces de caso de uso
│   │   └── saida       interfaces de repositório e de integração externa
│   └── servico          implementação dos casos de uso (só depende das portas)
├── adapter
│   ├── entrada.web
│   │   ├── controller   @RestController finos, delegam para caso de uso + fábrica de tela
│   │   ├── dto          records de request com bean validation
│   │   ├── tela         contrato JSON de telas (Tela, TelaFormulario, TelaSelecao, ItemTela...)
│   │   └── excecao      @RestControllerAdvice, converte exceção → HTTP status + Tela de erro
│   └── saida
│       ├── persistencia  entidades JPA, repositórios Spring Data, adapters das portas de saída
│       └── integracao    adapter que consulta o serviço externo de verificação de CPF
└── config              @ConfigurationProperties, OpenAPI, RestClient da integração de CPF
```

Não há camada de "application service" adicional, nem multi-módulo — o volume do domínio não
justifica essa complexidade extra.

## Decisões técnicas e por quês

- **Sessão de votação sem campo de status.** `SessaoVotacao` não guarda um campo "aberta/
  fechada" — o método `estaAberta(Instant referencia)` calcula isso comparando com a data de
  fechamento. Isso evita scheduler/job em background e sobrevive a restart sem estado extra.

- **Relação 1 pauta : no máximo 1 sessão.** Reforçada por uma constraint única
  (`sessao_votacao.pauta_id UNIQUE`). Uma vez criada uma sessão para uma pauta, não é possível
  abrir outra — mesmo que a primeira já tenha expirado. O domínio não prevê múltiplas rodadas
  de votação para a mesma pauta.

- **Voto único garantido por constraint de banco, não por verificação em memória.** A unicidade
  de `(pauta_id, associado_id)` é uma constraint única no PostgreSQL. O adapter de persistência
  captura a violação (`DataIntegrityViolationException`) e relança como `VotoDuplicadoException`
  (`409`). Isso evita condição de corrida entre requisições concorrentes — validado por um
  teste de concorrência com múltiplas threads votando simultaneamente.

- **Apuração via query agregada.** O resultado é calculado com `COUNT` agrupado por opção
  direto no banco (`countByPautaIdAndOpcao`), nunca carregando todos os votos em memória —
  importante para pautas com grande volume de votos.

- **`associadoId` é tratado como CPF** na verificação de elegibilidade (mais detalhes na seção
  específica abaixo). O cadastro de voto usa um único identificador por associado; não há campo
  separado para CPF, então essa equivalência é assumida.

## Contrato de tela

O cliente (não implementado neste repositório — só o backend é avaliado aqui) entende dois
tipos de tela:

**FORMULARIO** — exibe uma coleção de campos e um ou dois botões de ação. Ao tocar num botão, o
cliente faz `POST` para a `url` do botão, com um corpo formado pela junção do `body` declarado
no botão com os valores dos campos de entrada preenchidos pelo usuário.

```json
{
  "tipo": "FORMULARIO",
  "titulo": "Registrar voto",
  "itens": [
    { "tipo": "TEXTO", "texto": "Sessão de votação aberta para a pauta 3. Encerra em ..." },
    { "tipo": "INPUT_TEXTO", "id": "associadoId", "titulo": "Número do associado", "valor": "" }
  ],
  "botaoOk": { "texto": "Votar Sim", "url": ".../votos", "body": { "opcao": "SIM" } },
  "botaoCancelar": { "texto": "Votar Não", "url": ".../votos", "body": { "opcao": "NAO" } }
}
```

**SELECAO** — exibe uma lista de opções; tocar em uma opção dispara um `POST` para a `url`
daquele item.

```json
{
  "tipo": "SELECAO",
  "titulo": "Pautas",
  "itens": [
    { "texto": "Reforma do estatuto", "url": ".../pautas/3/sessoes" }
  ]
}
```

Um detalhe relevante: os itens de uma tela `SELECAO` **não** carregam o identificador da pauta
como campo separado — ele fica embutido na `url`. Isso é proposital: o cliente não precisa (e
não deve) conhecer IDs de domínio, só precisa seguir os links que o backend fornece.

## Endpoints

Prefixo `/api/v1` em todos os endpoints.

| Método | Path | Request | Resposta |
|---|---|---|---|
| GET | `/api/v1/pautas` | — | `TelaSelecao` com uma opção por pauta |
| POST | `/api/v1/pautas` | `{titulo, descricao}` | `TelaFormulario` de confirmação |
| POST | `/api/v1/pautas/{pautaId}/sessoes` | `{duracaoSegundos}` (opcional, default 60s) | `TelaFormulario` de votação |
| POST | `/api/v1/pautas/{pautaId}/votos` | `{associadoId, opcao}` (opcao: `SIM`/`NAO`) | `TelaFormulario` de confirmação |
| GET | `/api/v1/pautas/{pautaId}/resultado` | — | `TelaFormulario` com totais e resultado |

Erros de negócio também respondem no formato `TelaFormulario` (título "Erro", com o texto da
falha), além do HTTP status correspondente:

| Situação | HTTP |
|---|---|
| Pauta ou sessão não encontrada | 404 |
| CPF do associado inválido ou não encontrado | 404 |
| Sessão já existe para a pauta | 409 |
| Associado já votou nessa pauta | 409 |
| Sessão encerrada ou inexistente | 422 |
| Associado não apto a votar | 422 |
| Serviço de verificação de elegibilidade indisponível | 503 |
| Corpo da requisição inválido (ex.: `opcao` diferente de `SIM`/`NAO`) | 400 |

## Como rodar

Pré-requisitos: Docker (para Postgres e/ou build da imagem) e, para rodar fora de container,
Java 21.

**Subindo tudo via Docker Compose** (Postgres + aplicação):
```bash
docker-compose up -d --build
```
A aplicação sobe em `http://localhost:8080`, com Swagger em `/swagger-ui.html`.

**Rodando a aplicação localmente** (fora de container), com só o Postgres em Docker:
```bash
docker-compose up -d postgres
./mvnw spring-boot:run
```

## Configuração

Toda configuração sensível a ambiente é externalizada por variável de ambiente, com valor
padrão para desenvolvimento local:

| Propriedade | Variável de ambiente | Default |
|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/votacao` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `votacao` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `votacao` |
| `app.base-url` | `APP_BASE_URL` | `http://localhost:8080` |
| `app.integracao-cpf.base-url` | `INTEGRACAO_CPF_BASE_URL` | ⚠️ veja abaixo |
| `app.sessao.duracao-padrao-segundos` | — | `60` |

`APP_BASE_URL` é usado para montar as URLs de callback retornadas nas telas (o domínio que o
cliente mobile vai chamar) — importante trocar entre emulador (`http://10.0.2.2:8080`),
dispositivo físico (IP da máquina na rede) e produção, sem precisar recompilar.

`INTEGRACAO_CPF_BASE_URL` tem duas opções válidas (detalhes na próxima seção):

- **Local**: use o profile `dev` em vez de configurar essa variável manualmente — ele já aponta
  para o mock local incluído no projeto, em `http://localhost:3001`:
  ```bash
  SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
  ```
- **Produção/nuvem**: aponte para o mock hospedado, `https://mock-integracao-cpf.onrender.com`.

## Verificação de elegibilidade por CPF

Antes de aceitar um voto, a aplicação consulta um serviço externo (`GET /users/{cpf}`,
tratando `associadoId` como o CPF do associado) para confirmar se ele está apto a votar. A
resposta esperada é `{"status": "ABLE_TO_VOTE"}` ou `{"status": "UNABLE_TO_VOTE"}`; um CPF
inválido faz o serviço responder `404`.

**Limitação conhecida:** o serviço externo usado como referência para essa integração está
permanentemente fora do ar — a plataforma que o hospedava descontinuou hospedagem gratuita de
aplicações em 2022, e hoje só responde com uma página de erro genérica, para qualquer rota.
Isso foi confirmado via `curl`, testando tanto CPFs gerados quanto a raiz do host. Por isso, use
um dos dois mocks documentados abaixo (local ou hospedado) em vez do serviço de referência.

Por isso, o adapter (`VerificadorElegibilidadeAssociadoAdapter`) distingue dois cenários que,
sem essa distinção, ficariam ambíguos:

- Um `404` com corpo em JSON é tratado como resposta legítima da aplicação → CPF inválido.
- Um `404` (ou qualquer outro erro) com corpo que não seja JSON — como a página HTML do
  Heroku — é tratado como **serviço indisponível** (`503`), já que quem respondeu não foi a
  aplicação de verificação.

### Ferramenta de mock para testes manuais

Como o serviço de referência está fora do ar, incluí `ferramentas/MockCpfServer.java`: um
servidor HTTP mínimo, em Java puro (usa só `com.sun.net.httpserver`, já embutido no JDK — não
adiciona nenhuma dependência ao projeto), que simula o mesmo contrato:

- Valida o dígito verificador do CPF de verdade (mesmo algoritmo oficial).
- CPF válido → `200 {"status": "ABLE_TO_VOTE"}`.
- CPF inválido → `404`.

Para rodar (mesmo comando no Windows, Linux ou Mac, graças à execução de arquivo único do
Java 11+):
```bash
java ferramentas/MockCpfServer.java
```
Por padrão sobe em `http://localhost:3001`; a porta pode ser trocada passando um argumento
(`java ferramentas/MockCpfServer.java 3002`).

Para a aplicação usar o mock em vez do serviço real, ative o profile `dev` (que já aponta
`app.integracao-cpf.base-url` para `http://localhost:3001`, ver
`src/main/resources/application-dev.properties`):
```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```
No IntelliJ, o mesmo efeito é obtido preenchendo "Active profiles: dev" na Run Configuration.

### Mock hospedado (para o ambiente em produção)

O mesmo `MockCpfServer` está publicado como um serviço à parte (repositório e deploy próprios,
fora deste projeto), então a instância em produção da API de votação também pode apontar para
um mock de verdade em vez do serviço de referência morto. Para isso, configure a variável de
ambiente do serviço da API de votação (não a do mock):
```
INTEGRACAO_CPF_BASE_URL=https://mock-integracao-cpf.onrender.com
```
Sem essa configuração, todo voto em produção retorna `503`, pelo motivo já explicado acima. Como
esse serviço roda no tier gratuito do Render, ele hiberna após um período de inatividade — a
primeira chamada depois de um tempo parado pode demorar alguns segundos a mais enquanto ele
"acorda".

## Simulando o fluxo completo

Antes de começar, suba o mock de verificação de CPF e rode a aplicação com o profile `dev`
(senão qualquer voto vai retornar `503`, já que o serviço externo de referência está fora do
ar):
```bash
java ferramentas/MockCpfServer.java
# em outro terminal:
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Com os dois no ar, o fluxo completo pode ser simulado assim (via `curl` ou pelo Swagger):

```bash
# 1. Cadastrar uma pauta
curl -X POST http://localhost:8080/api/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Reforma do estatuto","descricao":"Discussão da reforma"}'
# -> 201, anote o id da pauta a partir da url retornada em botaoOk

# 2. Abrir a sessão de votação (usa o default de 60s)
curl -X POST http://localhost:8080/api/v1/pautas/{id}/sessoes \
  -H "Content-Type: application/json" -d '{}'
# -> 201

# 3. Votar (associadoId tratado como CPF - use um CPF com dígito verificador válido)
curl -X POST http://localhost:8080/api/v1/pautas/{id}/votos \
  -H "Content-Type: application/json" \
  -d '{"associadoId":"78791561272","opcao":"SIM"}'
# -> 201

# 4. Tentar votar de novo com o mesmo associado
curl -X POST http://localhost:8080/api/v1/pautas/{id}/votos \
  -H "Content-Type: application/json" \
  -d '{"associadoId":"78791561272","opcao":"NAO"}'
# -> 409 (associado já votou)

# 5. Apurar o resultado
curl http://localhost:8080/api/v1/pautas/{id}/resultado
# -> 200, com os totais e o resultado (APROVADA/REPROVADA/EMPATE)
```

Casos de erro úteis para testar manualmente:
- Votar com um CPF sem dígito verificador válido → `404`.
- Votar com `"opcao"` diferente de `SIM`/`NAO` → `400`.
- Abrir uma segunda sessão para a mesma pauta → `409`.
- Votar após a sessão expirar (`duracaoSegundos` baixo) → `422`.
- Derrubar o mock (ou não ativar o profile `dev`) e tentar votar → `503`.

## Testes

- **Unitários** (`./mvnw test`, sem Docker): serviços de caso de uso (mockando as portas de
  saída) e fábricas de tela (validando o JSON gerado).
- **Integração** (`./mvnw verify`, requer Docker — usa Testcontainers para subir um Postgres
  real): fluxo completo via `MockMvc`, constraint de voto único, e um teste de concorrência com
  múltiplas threads votando simultaneamente para o mesmo associado (espera exatamente 1 sucesso).
- O adapter de verificação de CPF é testado com `MockRestServiceServer`, sem depender de rede.

## Versionamento da API

A versão é exposta na própria URI (`/api/v1`). Para uma mudança incompatível futura, o caminho
mais direto é introduzir `/api/v2` coexistindo com `/api/v1` enquanto os consumidores migram,
depreciando a versão antiga depois. Alternativas descartadas por ora: versionamento por header
customizado (`X-API-Version`) ou por `Accept` com media-type versionado — ambos adicionam
complexidade de roteamento sem necessidade real no volume atual da API.

## Performance

Os pontos mais sensíveis a volume alto de votos já são tratados na modelagem: a apuração usa
`COUNT` agregado no banco (nunca carrega os votos em memória), e há um índice
(`idx_voto_pauta_opcao`) cobrindo exatamente essa consulta. A unicidade do voto é garantida por
constraint de banco, não por uma leitura prévia — evita uma consulta extra por voto e continua
correta sob concorrência.

### Teste de carga com k6

`ferramentas/k6/votacao-performance.js` simula votos concorrentes numa mesma pauta e mede a
latência de `POST /votos` e `GET /resultado`. Os números medidos são sempre de ambiente local
(via `docker-compose`) — não faz sentido medir contra uma instância gratuita no Render, cujo
gargalo seria o tier de infraestrutura, não o código da aplicação.

**Pré-requisitos:** [k6 instalado](https://k6.io/docs/get-started/installation/), a aplicação
rodando com o profile `dev` ativo e o `MockCpfServer` no ar (o voto consulta a verificação de
CPF a cada chamada — sem isso, todo voto falharia com `503`):
```bash
java ferramentas/MockCpfServer.java
# em outro terminal, com o profile dev ativo:
./mvnw spring-boot:run
```

**Cenários disponíveis** (escolhidos via `-e CENARIO=...`; default é `moderado`):

| Cenário | O que simula |
|---|---|
| `moderado` | rampa até 20 usuários simultâneos por ~1 minuto — smoke test de carga leve |
| `pico` | rampa rápida até 200 usuários simultâneos — pico repentino de acesso |
| `sustentado` | 50 usuários simultâneos constantes por 2 minutos |
| `volume` | 100.000 votos no total (100 usuários, sem pausa entre requisições) — o cenário que valida volume alto de fato |

Rodar um cenário e salvar o resumo em arquivo:
```bash
k6 run -e CENARIO=volume --summary-export=ferramentas/k6/resultado-volume.json ferramentas/k6/votacao-performance.js
```

### Como ler o resultado (glossário rápido)

O k6 imprime um resumo no terminal ao final da execução. Os termos mais importantes:

| Termo | O que significa |
|---|---|
| **VU** (Virtual User) | um "usuário simulado" fazendo requisições. Mais VUs = mais carga simultânea. |
| **iteração** | uma execução completa do fluxo de teste (nesse script: gerar um CPF e votar, ocasionalmente consultar o resultado). |
| **`http_req_duration`** | quanto tempo cada requisição HTTP levou, do envio até a resposta completa. É a métrica de latência. |
| **`avg` / `med` / `min` / `max`** | média, mediana, mínimo e máximo desse tempo entre todas as requisições medidas. |
| **`p90` / `p95`** ("percentil 90/95") | o valor abaixo do qual ficaram 90% (ou 95%) das requisições. Ex.: `p95=40ms` quer dizer que 95% das requisições foram mais rápidas que 40ms — só as 5% mais lentas ficaram acima disso. É uma medida mais confiável que a média, porque não é distorcida por poucos casos muito lentos. |
| **`http_req_failed`** | percentual de requisições que deram erro (status HTTP de falha ou problema de rede). |
| **`checks`** | validações definidas no próprio script (ex.: "voto aceito ou associado já votou") — mostra quantas passaram vs. falharam. |
| **`threshold`** | um critério de aprovação/reprovação que definimos no script (ex.: "p95 do voto deve ser menor que 500ms"). O k6 marca com `✓` se passou e `✗` se não passou. |
| **`req/s`** (requisições por segundo) | throughput — quantas requisições o sistema conseguiu processar por segundo durante o teste. |

**Exemplo real** (cenário `moderado`, rodado localmente): 7490 votos processados em ~1 minuto,
com p95 de 40.65ms por voto e 0% de falhas — ou seja, olhando só as 5% de requisições mais
lentas dentre quase 8 mil, mesmo essas ainda levaram menos de 41 milissegundos.

## Logs

INFO em: pauta cadastrada, sessão aberta, voto aceito, resultado apurado (com os totais). WARN
em: rejeições de regra de negócio (sessão já aberta, sessão encerrada, voto duplicado, CPF
inválido, associado não apto, corpo de requisição inválido). ERROR com stacktrace em: serviço de
verificação indisponível e exceções não mapeadas (500).
