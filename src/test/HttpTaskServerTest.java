package test;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private static HttpTaskServer httpTaskServer;
    private static KVServer server;
    private static Gson gson;

    @BeforeAll
    static void makeGsonParser(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Duration.class, new DurationAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = builder.create();
    }

    @BeforeEach
    public void createNewManagerWithTasks() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        httpTaskServer = new HttpTaskServer("http://localhost:8078/");
        httpTaskServer.start();

        URI task = URI.create("http://localhost:8080/tasks/task/");
        URI epic = URI.create("http://localhost:8080/tasks/epic/");
        URI subtask = URI.create("http://localhost:8080/tasks/subtask/");

        Epic task1 = new Epic("test", "test");
        Task task2 = new Task("test", "test", Status.NEW, null, null);
        HttpRequest send1 = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).uri(epic).build();
        HttpRequest send2 = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).uri(task).build();
        task1.setId(1);
        Subtask task3 = new Subtask("test", "test", Status.NEW, task1, null, null);
        HttpRequest send3 = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3))).uri(subtask).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        client.send(send1, handler);
        client.send(send2, handler);
        client.send(send3, handler);
    }

    @AfterEach
    public void stopServers(){
        server.stop();
        httpTaskServer.stop();
    }

    @Test
    public void checkAllMapsAndArrays() throws IOException, InterruptedException {
        URI task = URI.create("http://localhost:8080/tasks/task/");
        URI epic = URI.create("http://localhost:8080/tasks/epic/");
        URI subtask = URI.create("http://localhost:8080/tasks/subtask/");
        URI history = URI.create("http://localhost:8080/tasks/history/");
        URI set = URI.create("http://localhost:8080/tasks/");

        HttpRequest taskRequest = HttpRequest.newBuilder().GET().uri(task).build();
        HttpRequest epicRequest = HttpRequest.newBuilder().GET().uri(epic).build();
        HttpRequest subtaskRequest = HttpRequest.newBuilder().GET().uri(subtask).build();
        HttpRequest historyRequest = HttpRequest.newBuilder().GET().uri(history).build();
        HttpRequest setRequest = HttpRequest.newBuilder().GET().uri(set).build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response1 = client.send(taskRequest, handler);
        HttpResponse<String> response2 = client.send(epicRequest, handler);
        HttpResponse<String> response3 = client.send(subtaskRequest, handler);
        HttpResponse<String> response4 = client.send(historyRequest, handler);
        HttpResponse<String> response5 = client.send(setRequest, handler);
        String[] arr = {"[{\"title\":\"test\",\"details\":\"test\",\"id\":2,\"status\":\"NEW\"}]",
                "[{\"subtasksId\":[3],\"title\":\"test\",\"details\":\"test\",\"id\":1,\"status\":\"NEW\"}]",
                "[{\"parentEpic\":1,\"title\":\"test\",\"details\":\"test\",\"id\":3,\"status\":\"NEW\"}]",
                "[]", "[{\"parentEpic\":1,\"title\":\"test\",\"details\":\"test\",\"id\":3,\"status\":\"NEW\"}," +
                "{\"parentEpic\":1,\"title\":\"test\",\"details\":\"test\",\"id\":3,\"status\":\"NEW\"}," +
                "{\"title\":\"test\",\"details\":\"test\",\"id\":2,\"status\":\"NEW\"}," +
                "{\"title\":\"test\",\"details\":\"test\",\"id\":2,\"status\":\"NEW\"}]"};

        assertEquals(arr[0], response1.body());
        assertEquals(arr[1], response2.body());
        assertEquals(arr[2], response3.body());
        assertEquals(arr[3], response4.body());
        assertEquals(arr[4], response5.body());
    }

    @Test
    public void shouldReturnTasksWhichGotWithTheirId() throws IOException, InterruptedException {
        URI task = URI.create("http://localhost:8080/tasks/task/?id=2");
        URI epic = URI.create("http://localhost:8080/tasks/epic/?id=1");
        URI subtask = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        URI subtasksOfEpic = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");

        HttpRequest taskRequest = HttpRequest.newBuilder().GET().uri(task).build();
        HttpRequest epicRequest = HttpRequest.newBuilder().GET().uri(epic).build();
        HttpRequest subtaskRequest = HttpRequest.newBuilder().GET().uri(subtask).build();
        HttpRequest arrRequest = HttpRequest.newBuilder().GET().uri(subtasksOfEpic).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response1 = client.send(taskRequest, handler);
        HttpResponse<String> response2 = client.send(epicRequest, handler);
        HttpResponse<String> response3 = client.send(subtaskRequest, handler);
        HttpResponse<String> response4 = client.send(arrRequest, handler);

        String[] arr = {"{\"title\":\"test\",\"details\":\"test\",\"id\":2,\"status\":\"NEW\"}",
                "{\"subtasksId\":[3],\"title\":\"test\",\"details\":\"test\",\"id\":1,\"status\":\"NEW\"}",
                "{\"parentEpic\":1,\"title\":\"test\",\"details\":\"test\",\"id\":3,\"status\":\"NEW\"}"};
        assertEquals(arr[0], response1.body());
        assertEquals(arr[1], response2.body());
        assertEquals(arr[2], response3.body());
        assertEquals("[" + arr[2] + "]", response4.body());
    }

    @Test
    public void shouldReturnEmptysArrayIfManagerIsEmpty() throws IOException, InterruptedException {
        URI task = URI.create("http://localhost:8080/tasks/task/");
        URI epic = URI.create("http://localhost:8080/tasks/epic/");
        URI subtask = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest taskRequest = HttpRequest.newBuilder().DELETE().uri(task).build();
        HttpRequest epicRequest = HttpRequest.newBuilder().DELETE().uri(epic).build();
        HttpRequest subtaskRequest = HttpRequest.newBuilder().DELETE().uri(subtask).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        client.send(taskRequest, handler);
        client.send(subtaskRequest, handler);

        HttpRequest getTasks = HttpRequest.newBuilder().GET().uri(task).build();
        HttpRequest getSubtasks = HttpRequest.newBuilder().GET().uri(subtask).build();
        HttpResponse<String> response1 = client.send(getTasks, handler);
        HttpResponse<String> response2 = client.send(getSubtasks, handler);
        assertEquals("[]", response1.body());
        assertEquals("[]", response2.body());

        client.send(epicRequest, handler);
        HttpRequest getEpics = HttpRequest.newBuilder().GET().uri(epic).build();
        HttpResponse<String> response3 = client.send(getEpics, handler);
        assertEquals("[]", response3.body());
    }

    @Test
    public void shouldReturnEmptyArraysIfTasksDeletedById() throws IOException, InterruptedException {
        URI task = URI.create("http://localhost:8080/tasks/task/?id=2");
        URI epic = URI.create("http://localhost:8080/tasks/epic/?id=1");
        URI subtask = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest taskRequest = HttpRequest.newBuilder().DELETE().uri(task).build();
        HttpRequest epicRequest = HttpRequest.newBuilder().DELETE().uri(epic).build();
        HttpRequest subtaskRequest = HttpRequest.newBuilder().DELETE().uri(subtask).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        client.send(taskRequest, handler);
        client.send(subtaskRequest, handler);

        HttpRequest getTask = HttpRequest.newBuilder().GET().uri(task).build();
        HttpRequest getSubtask = HttpRequest.newBuilder().GET().uri(subtask).build();
        HttpResponse<String> response1 = client.send(getTask, handler);
        HttpResponse<String> response2 = client.send(getSubtask, handler);
        assertEquals("null", response1.body());
        assertEquals("null", response2.body());

        client.send(epicRequest, handler);
        HttpRequest getEpic = HttpRequest.newBuilder().GET().uri(epic).build();
        HttpResponse<String> response3 = client.send(getEpic, handler);
        assertEquals("null", response3.body());
    }
}
