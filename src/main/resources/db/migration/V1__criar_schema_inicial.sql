CREATE TABLE pauta (
  id BIGSERIAL PRIMARY KEY,
  titulo VARCHAR(200) NOT NULL,
  descricao VARCHAR(2000),
  data_criacao TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sessao_votacao (
  id BIGSERIAL PRIMARY KEY,
  pauta_id BIGINT NOT NULL UNIQUE REFERENCES pauta(id),
  data_abertura TIMESTAMPTZ NOT NULL,
  data_fechamento TIMESTAMPTZ NOT NULL
);

CREATE TABLE voto (
  id BIGSERIAL PRIMARY KEY,
  pauta_id BIGINT NOT NULL REFERENCES pauta(id),
  associado_id VARCHAR(60) NOT NULL,
  opcao VARCHAR(3) NOT NULL CHECK (opcao IN ('SIM','NAO')),
  data_voto TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uk_voto_pauta_associado UNIQUE (pauta_id, associado_id)
);

CREATE INDEX idx_voto_pauta_opcao ON voto (pauta_id, opcao);
