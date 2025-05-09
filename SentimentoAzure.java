import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SentimentoAzure {
    public static void main(String[] args) throws Exception {
        String endpoint = "https://analisedesentimentos123.cognitiveservices.azure.com/";
        String chave = "99LEATB84Zr3JTlxwBsEstjqJPti4WvjNB2QzEy9EVzuFjuDvcvUJQQJ99BEACYeBjFXJ3w3AAAEACOGfqOv";
        String url = endpoint + "text/analytics/v3.1/sentiment";

        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite um texto para análise de sentimento: ");
        String textoUsuario = scanner.nextLine();
        scanner.close();

        String corpoJson = String.format(
            "{ \"documents\": [ { \"language\": \"pt\", \"id\": \"1\", \"text\": \"%s\" } ] }",
            textoUsuario.replace("\"", "\\\"") // escapa aspas
        );

        String resposta = enviarRequisicao(url, chave, corpoJson);

        String sentimento = extrairSentimento(resposta);

        System.out.println("\n===== Resultado da Análise =====");
        System.out.println("Texto: " + textoUsuario);
        System.out.println("Sentimento identificado: " + sentimento);
        System.out.println("Resposta completa da API:\n" + resposta);
    }

    private static String enviarRequisicao(String urlStr, String chave, String corpo) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("POST");
        conexao.setRequestProperty("Content-Type", "application/json");
        conexao.setRequestProperty("Ocp-Apim-Subscription-Key", chave);
        conexao.setDoOutput(true);

        try (OutputStream os = conexao.getOutputStream()) {
            byte[] input = corpo.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Ler resposta
        StringBuilder resposta = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conexao.getInputStream(), "utf-8"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                resposta.append(linha);
            }
        }

        return resposta.toString();
    }

    private static String extrairSentimento(String json) {
        String chave = "\"sentiment\":\"";
        int indiceInicio = json.indexOf(chave);
        if (indiceInicio == -1) return "Não identificado";
        int inicio = indiceInicio + chave.length();
        int fim = json.indexOf("\"", inicio);
        return json.substring(inicio, fim);
    }
}
