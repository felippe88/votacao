import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockCpfServer {

    private static final Pattern ROTA = Pattern.compile("^/users/(\\d{11})$");
    private static final AtomicLong CONTADOR = new AtomicLong();

    public static void main(String[] args) throws Exception {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : 3001;
        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.setExecutor(Executors.newFixedThreadPool(50));

        servidor.createContext("/users", exchange -> {
            Matcher matcher = ROTA.matcher(exchange.getRequestURI().getPath());
            int status;
            String corpo;

            if (!matcher.matches() || !cpfValido(matcher.group(1))) {
                status = 404;
                corpo = "{\"erro\":\"cpf nao encontrado\"}";
            } else {
                status = 200;
                corpo = "{\"status\":\"ABLE_TO_VOTE\"}";
            }

            long numero = CONTADOR.incrementAndGet();
            if (numero <= 20 || numero % 500 == 0) {
                System.out.printf("[%d] %s %s -> %d %s%n", numero, exchange.getRequestMethod(),
                        exchange.getRequestURI(), status, corpo);
            }

            byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });

        servidor.start();
        System.out.println("Mock rodando em http://localhost:" + porta);
        System.out.println();
        System.out.println("Exemplo de CPF valido:   78791561272");
        System.out.println("Exemplo de CPF invalido: 12345678900");
        System.out.println();
        System.out.println("Tentativas (as primeiras 20 e depois a cada 500):");
    }

    private static boolean cpfValido(String cpf) {
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }
        int[] digitos = cpf.chars().map(c -> c - '0').toArray();
        int d1 = calcularDigitoVerificador(Arrays.copyOfRange(digitos, 0, 9));
        int d2 = calcularDigitoVerificador(Arrays.copyOfRange(digitos, 0, 10));
        return d1 == digitos[9] && d2 == digitos[10];
    }

    private static int calcularDigitoVerificador(int[] base) {
        int soma = 0;
        int peso = base.length + 1;
        for (int digito : base) {
            soma += digito * peso;
            peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
