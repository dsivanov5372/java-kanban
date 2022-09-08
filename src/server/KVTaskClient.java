package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String API_TOKEN;
    private final String url;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.url = url;

        URI uri = URI.create(url + "register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = client.send(request, handler);
        API_TOKEN = response.body();
    }

    public void remove(String key){
        URI uri = URI.create(url + "delete/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            client.send(request, handler);
        } catch (IOException | InterruptedException exception){
            System.out.println(exception.getMessage());
        }
    }
    public void put(String key, String json)  {
        URI uri = URI.create(url + "save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            client.send(request, handler);
        } catch (IOException | InterruptedException exception){
            System.out.println(exception.getMessage());
        }
    }

    public String get(String key) {
        URI uri = URI.create(url + "load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            return response.body();
        } catch (IOException | InterruptedException exception){
            System.out.println(exception.getMessage());
        }

        return null;
    }
}
