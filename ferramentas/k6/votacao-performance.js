import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const CENARIO = __ENV.CENARIO || 'moderado';

const cenarios = {
  moderado: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '15s', target: 20 },
      { duration: '30s', target: 20 },
      { duration: '15s', target: 0 },
    ],
  },
  pico: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '10s', target: 200 },
      { duration: '20s', target: 200 },
      { duration: '10s', target: 0 },
    ],
  },
  sustentado: {
    executor: 'constant-vus',
    vus: 50,
    duration: '2m',
  },
  volume: {
    executor: 'shared-iterations',
    vus: 100,
    iterations: 100000,
    maxDuration: '20m',
  },
};

if (!cenarios[CENARIO]) {
  throw new Error(`Cenario desconhecido: ${CENARIO}. Use um de: ${Object.keys(cenarios).join(', ')}`);
}

export const options = {
  scenarios: { votacao: cenarios[CENARIO] },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    'http_req_duration{tipo:votar}': ['p(95)<500'],
    'http_req_duration{tipo:resultado}': ['p(95)<300'],
  },
};

function gerarCpf() {
  const nove = Array.from({ length: 9 }, () => Math.floor(Math.random() * 10));

  const calcularDigito = (base) => {
    let soma = 0;
    let peso = base.length + 1;
    for (const digito of base) {
      soma += digito * peso;
      peso--;
    }
    const resto = soma % 11;
    return resto < 2 ? 0 : 11 - resto;
  };

  const d1 = calcularDigito(nove);
  const d2 = calcularDigito([...nove, d1]);
  return [...nove, d1, d2].join('');
}

export function setup() {
  const headers = { headers: { 'Content-Type': 'application/json' } };

  const cadastro = http.post(
    `${BASE_URL}/api/v1/pautas`,
    JSON.stringify({ titulo: 'Pauta de teste de performance', descricao: `Gerada em ${new Date().toISOString()}` }),
    headers,
  );
  check(cadastro, { 'pauta cadastrada (201)': (r) => r.status === 201 });

  const match = cadastro.body.match(/\/pautas\/(\d+)\/sessoes/);
  if (!match) {
    throw new Error(`Nao foi possivel extrair o id da pauta da resposta: ${cadastro.body}`);
  }
  const pautaId = match[1];

  const sessao = http.post(
    `${BASE_URL}/api/v1/pautas/${pautaId}/sessoes`,
    JSON.stringify({ duracaoSegundos: 1800 }),
    headers,
  );
  check(sessao, { 'sessao aberta (201)': (r) => r.status === 201 });

  return { pautaId };
}

export default function (data) {
  const headers = { headers: { 'Content-Type': 'application/json' } };
  const cpf = gerarCpf();
  const opcao = Math.random() < 0.5 ? 'SIM' : 'NAO';

  const votoRes = http.post(
    `${BASE_URL}/api/v1/pautas/${data.pautaId}/votos`,
    JSON.stringify({ associadoId: cpf, opcao }),
    { ...headers, tags: { tipo: 'votar' } },
  );
  // Com CPF gerado aleatoriamente em volume alto, colisao (mesmo CPF sorteado duas vezes)
  // e esperada ocasionalmente - 409 nesse caso e comportamento correto, nao falha.
  check(votoRes, { 'voto aceito ou associado ja votou (201/409)': (r) => r.status === 201 || r.status === 409 });

  if (Math.random() < 0.05) {
    const resultadoRes = http.get(
      `${BASE_URL}/api/v1/pautas/${data.pautaId}/resultado`,
      { tags: { tipo: 'resultado' } },
    );
    check(resultadoRes, { 'resultado consultado (200)': (r) => r.status === 200 });
  }

  if (CENARIO !== 'volume') {
    sleep(0.1);
  }
}
