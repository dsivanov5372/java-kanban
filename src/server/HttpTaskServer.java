package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managment.Managers;
import managment.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final TaskManager manager;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(String url) throws IOException, InterruptedException {
        manager = Managers.getDefault(url);
        server = HttpServer.create();

        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
    }

    public void start(){
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public void stop(){
        server.stop(1);
    }

    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String params = httpExchange.getRequestURI().getQuery();
            String[] splitPath = path.split("/");

            try (httpExchange) {
                switch (method) {
                    case "GET" -> get(httpExchange, params, splitPath);
                    case "POST" -> post(httpExchange, splitPath);
                    case "DELETE" -> delete(httpExchange, params, splitPath);
                    default -> httpExchange.sendResponseHeaders(501, 0);
                }
            } catch (IOException exception) {
                System.out.println("Во время обработки запроса что-то пошло не так :(");
            }

        }

        private void delete(HttpExchange httpExchange, String params, String[] splitPath) throws IOException {
            if (splitPath.length > 2){
                if (params != null){
                    int id = Integer.parseInt(params.split("=")[1]);

                    switch (splitPath[2]) {
                        case "task" -> manager.deleteTaskById(id);
                        case "subtask" -> {
                            if (splitPath.length == 4) {
                                if (splitPath[3].equals("epic")) {
                                    Epic epic = manager.getEpic(id);
                                    manager.deleteAllSubtasksOfEpic(epic);
                                } else {
                                    httpExchange.sendResponseHeaders(400, 0);
                                    return;
                                }
                            } else {
                                manager.deleteSubtaskById(id);
                            }
                        }
                        case "epic" -> manager.deleteEpicById(id);
                        default -> {
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                    }

                } else {
                    switch (splitPath[2]) {
                        case "task" -> manager.deleteAllTasks();
                        case "subtask" -> manager.deleteAllSubtasks();
                        case "epic" -> manager.deleteAllEpics();
                        default -> {
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                    }
                }

                httpExchange.sendResponseHeaders(200, 0);
            } else {
                httpExchange.sendResponseHeaders(400, 0);
            }
        }

        private void post(HttpExchange httpExchange, String[] splitPath) throws IOException {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Duration.class, new DurationAdapter());
            builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
            Gson gson = builder.create();
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            inputStream.close();

            switch (splitPath[2]) {
                case "task" -> {
                    Task task = gson.fromJson(body, Task.class);
                    manager.makeTask(task);
                    manager.updateTask(task);
                }
                case "subtask" -> {
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    manager.makeSubtask(subtask);
                    manager.updateSubtask(subtask);
                }
                case "epic" -> {
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic.getStatus() == null){
                        epic.setStatus(Status.NEW);
                    }
                    manager.makeEpic(epic);
                    manager.updateEpic(epic);
                }
                default -> {
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
            }
            httpExchange.sendResponseHeaders(200, 0);
        }

        private void get(HttpExchange httpExchange, String params, String[] splitPath) throws IOException{
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Duration.class, new DurationAdapter());
            builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
            Gson gson = builder.create();
            String response;

            if (splitPath.length == 2) {
                response = gson.toJson(manager.getPrioritizedTasks());
            } else {
                if (params != null){
                    int id = Integer.parseInt(params.split("=")[1]);

                    switch (splitPath[2]) {
                        case "task" -> response = gson.toJson(manager.getTask(id));
                        case "subtask" -> {
                            if (splitPath.length == 4) {
                                if (splitPath[3].equals("epic")) {
                                    response = gson.toJson(manager.getAllSubtaskOfEpic(id));
                                } else {
                                    httpExchange.sendResponseHeaders(400, 0);
                                    return;
                                }
                            } else {
                                response = gson.toJson(manager.getSubtask(id));
                            }
                        }
                        case "epic" -> response = gson.toJson(manager.getEpic(id));
                        default -> {
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                    }
                } else {
                    switch (splitPath[2]){
                        case "task" -> response = gson.toJson(manager.getAllTasks());
                        case "subtask" -> response = gson.toJson(manager.getAllSubtasks());
                        case "epic" -> response = gson.toJson(manager.getAllEpics());
                        case "history" -> response = gson.toJson(manager.getHistory());
                        default -> {
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                    }
                }
            }

            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

}
