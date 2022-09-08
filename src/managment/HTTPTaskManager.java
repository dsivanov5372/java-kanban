package managment;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final String defaultKey;
    private final Gson gson;

    public HTTPTaskManager(String url) throws IOException, InterruptedException {
        super();
        client = new KVTaskClient(url);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Duration.class, new DurationAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = builder.create();
        defaultKey = "vulnerability";
    }

    public static HTTPTaskManager loadFromServer(String url, String key)
            throws IOException, InterruptedException {
        HTTPTaskManager manager = new HTTPTaskManager(url);
        String taskMap = manager.client.get(key + "Tasks");
        String subtaskMap = manager.client.get(key + "Subtasks");
        String epicMap = manager.client.get(key + "Epics");
        String history = manager.client.get(key + "History");

        Type typeTask = new TypeToken<HashMap<Integer, Task>>(){}.getType();
        Type typeSubtask = new TypeToken<HashMap<Integer, Subtask>>(){}.getType();
        Type typeEpic = new TypeToken<HashMap<Integer, Epic>>(){}.getType();
        Type typeHistory = new TypeToken<ArrayList<Integer>>(){}.getType();

        manager.tasks = manager.gson.fromJson(taskMap, typeTask);
        manager.epics = manager.gson.fromJson(epicMap, typeEpic);
        manager.subtasks = manager.gson.fromJson(subtaskMap, typeSubtask);
        ArrayList<Integer> arr = manager.gson.fromJson(history, typeHistory);
        for (int id : arr){
            if (manager.epics.containsKey(id)){
                manager.historyManager.add(manager.epics.get(id));
            } else if (manager.tasks.containsKey(id)){
                manager.historyManager.add(manager.tasks.get(id));
            } else if (manager.subtasks.containsKey(id)){
                manager.historyManager.add(manager.subtasks.get(id));
            }
        }
        manager.prioritizedTasksSet.addAll(manager.tasks.values());
        manager.prioritizedTasksSet.addAll(manager.subtasks.values());
        return manager;
    }

    @Override
    public void makeEpic(Epic epic){
        if (epic != null && findIntersect(epic) && epic.getId() == 0) {
            super.makeEpic(epic);
            String id = String.valueOf(epic.getId());
            client.put(id, gson.toJson(epic));

            client.put(defaultKey + "Epics", gson.toJson(epics));
        }
    }

    @Override
    public void makeTask(Task task){
        if (task != null && findIntersect(task) && task.getId() == 0) {
            super.makeTask(task);
            String id = String.valueOf(task.getId());
            client.put(id, gson.toJson(task));

            client.put(defaultKey + "Tasks", gson.toJson(tasks));
        }
    }

    @Override
    public void makeSubtask(Subtask subtask){
        if (subtask != null && subtask.getParentEpic() != -1 && findIntersect(subtask) && subtask.getId() == 0) {
            super.makeSubtask(subtask);
            String id = String.valueOf(subtask.getId());
            client.put(id, gson.toJson(subtask));

            client.put(defaultKey + "Subtasks", gson.toJson(subtasks));
        }
    }

    @Override
    public void deleteAllTasks(){
        for (int key : tasks.keySet()){
            client.remove(String.valueOf(key));
        }
        super.deleteAllTasks();
        client.put(defaultKey + "Tasks", gson.toJson(tasks));
        ArrayList<Integer> toSave = new ArrayList<>();
        historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
        client.put(defaultKey + "History", gson.toJson(toSave));
    }

    @Override
    public void deleteAllEpics(){
        for (Epic epic : epics.values()){
            client.remove(String.valueOf(epic.getId()));
            for (int key : epic.getSubtasksId()){
                client.remove(String.valueOf(key));
            }
        }
        client.put(defaultKey + "Epics", gson.toJson(epics));
        super.deleteAllEpics();
        client.put(defaultKey + "Subtasks", gson.toJson(subtasks));
        ArrayList<Integer> toSave = new ArrayList<>();
        historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
        client.put(defaultKey + "History", gson.toJson(toSave));
    }

    @Override
    public void deleteAllSubtasks(){
        for (int key : epics.keySet()){
            client.remove(String.valueOf(key));
        }
        super.deleteAllSubtasks();
        for (Epic epic : epics.values()){
            client.put(String.valueOf(epic.getId()), gson.toJson(epic));
        }
        client.put(defaultKey + "Epics", gson.toJson(epics));
        client.put(defaultKey + "Subtasks", gson.toJson(subtasks));
        ArrayList<Integer> toSave = new ArrayList<>();
        historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
        client.put(defaultKey + "History", gson.toJson(toSave));
    }

    @Override
    public void deleteAllSubtasksOfEpic(Epic epic){
        if (epic != null && epics.containsKey(epic.getId())) {
            for (int key : epic.getSubtasksId()) {
                client.remove(String.valueOf(key));
            }
            super.deleteAllSubtasksOfEpic(epic);
            client.put(String.valueOf(epic.getId()), gson.toJson(epic));

            client.put(defaultKey + "Epics", gson.toJson(epics));
            client.put(defaultKey + "Subtasks", gson.toJson(subtasks));
            ArrayList<Integer> toSave = new ArrayList<>();
            historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
            client.put(defaultKey + "History", gson.toJson(toSave));
        }
    }

    @Override
    public void updateEpic(Epic epic){
        if (epic != null) {
            super.updateEpic(epic);
            String id = String.valueOf(epic.getId());
            client.put(id, gson.toJson(epic));
        }
        client.put(defaultKey + "Epics", gson.toJson(epics));
    }

    @Override
    public void updateTask(Task task){
        if (task != null) {
            super.updateTask(task);
            String id = String.valueOf(task.getId());
            client.put(id, gson.toJson(task));
            client.put(defaultKey + "Tasks", gson.toJson(tasks));
        }
    }

    @Override
    public void updateSubtask(Subtask subtask){
        if (subtask != null) {
            super.updateSubtask(subtask);
            String id = String.valueOf(subtask.getId());
            client.put(id, gson.toJson(subtask));
            client.put(defaultKey + "Subtasks", gson.toJson(subtasks));
        }
    }

    @Override
    public void deleteEpicById(int id){
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int key : epic.getSubtasksId()){
                client.remove(String.valueOf(key));
            }
        }
        super.deleteEpicById(id);
        client.remove(String.valueOf(id));
        client.put(defaultKey + "Epics", gson.toJson(epics));
        client.put(defaultKey + "Subtasks", gson.toJson(subtasks));
        ArrayList<Integer> toSave = new ArrayList<>();
        historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
        client.put(defaultKey + "History", gson.toJson(toSave));
    }

    @Override
    public void deleteTaskById(int id){
        super.deleteTaskById(id);
        client.remove(String.valueOf(id));
        client.put(defaultKey + "Tasks", gson.toJson(tasks));
        ArrayList<Integer> toSave = new ArrayList<>();
        historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
        client.put(defaultKey + "History", gson.toJson(toSave));
    }

    @Override
    public void deleteSubtaskById(int id){
        if (subtasks.containsKey(id)){
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getParentEpic());
            client.put(String.valueOf(epic.getId()), gson.toJson(epic));
        }
        super.deleteSubtaskById(id);
        client.remove(String.valueOf(id));
        client.put(defaultKey + "Epics", gson.toJson(epics));
        client.put(defaultKey + "Subtasks", gson.toJson(subtasks));
        ArrayList<Integer> toSave = new ArrayList<>();
        historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
        client.put(defaultKey + "History", gson.toJson(toSave));
    }

    @Override
    public Epic getEpic(int id){
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            ArrayList<Integer> toSave = new ArrayList<>();
            historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
            client.put(defaultKey + "History", gson.toJson(toSave));
        }
        return epic;
    }

    @Override
    public Task getTask(int id){
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            ArrayList<Integer> toSave = new ArrayList<>();
            historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
            client.put(defaultKey + "History", gson.toJson(toSave));
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int id){
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            ArrayList<Integer> toSave = new ArrayList<>();
            historyManager.getHistory().forEach(obj -> toSave.add(obj.getId()));
            client.put(defaultKey + "History", gson.toJson(toSave));
        }
        return subtask;
    }
}
