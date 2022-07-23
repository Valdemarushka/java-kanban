package network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static network.KVUrl.*;

public class KVTaskClient {
    private final String API_TOKEN;
    private final URI uri;
    private final HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(URI uri) throws IOException, InterruptedException {
        this.uri = uri;
        API_TOKEN = register(uri);
    }

    private String register(URI uri) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + REGISTER_URL))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(httpRequest, handler);
        if (response.statusCode() != 200) {
            throw new RuntimeException("Не удается обработать запрос " + REGISTER_URL);
        }
        return response.body();

    }

    public String getApiToken() {
        return API_TOKEN;
    }

    public void put(String key, String valueInJson) {
        try {
            HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.ofString(valueInJson);
            URI uriPut = URI.create(uri + SAVE_URL + "/" + key + "?API_TOKEN=" + API_TOKEN);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(requestBody)
                    .uri(uriPut)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {

        URI uriGet = URI.create(uri + LOAD_URL + "/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriGet)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
