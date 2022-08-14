package managment;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private int idSetter = 1;

    public InMemoryTaskManager(){
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }
    @Override
    public void makeEpic(Epic epic){
        epic.setId(idSetter);
        idSetter++;
        epics.put(epic.getId(), epic);
    }

    @Override
    public void makeTask(Task task){
        task.setId(idSetter);
        idSetter++;
        tasks.put(task.getId(), task);
    }

    @Override
    public void makeSubtask(Subtask subtask){
        subtask.setId(idSetter);
        idSetter++;
        subtasks.put(subtask.getId(), subtask);
        Epic parentEpic = epics.get(subtask.getParentEpic());
        parentEpic.addSubtask(subtask, getAllSubtaskOfEpic(parentEpic.getId()));
    }

    @Override
    public ArrayList<Epic> getAllEpics(){
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getAllTasks(){
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtaskOfEpic(int id){
        ArrayList<Integer> ids = epics.get(id).getSubtasksId();
        ArrayList<Subtask> result = new ArrayList<>();
        for (int subtaskId : ids){
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    @Override
    public void deleteAllTasks(){
        for(int task : tasks.keySet()){
            historyManager.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics(){
        for (Epic epic : epics.values()){
            deleteAllSubtasksOfEpics(epic);
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks(){
        for (Subtask subtask : subtasks.values()){
            Epic parentEpic = epics.get(subtask.getParentEpic());
            parentEpic.removeSubtask(subtask, getAllSubtaskOfEpic(parentEpic.getId()));
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasksOfEpics(Epic epic){
        for (int subtask : epic.getSubtasksId()){
            subtasks.remove(subtask);
            historyManager.remove(subtask);
        }
    }

    @Override
    public Epic getEpic(int id){
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Task getTask(int id){
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int id){
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void updateEpics(Epic epic){
        ArrayList<Subtask> newSubtasks = new ArrayList<>();
        for (int id : epic.getSubtasksId()){
            newSubtasks.add(subtasks.get(id));
        }
        Epic prevEpic = epics.get(epic.getId());
        for (int subtask : prevEpic.getSubtasksId()){
            subtasks.remove(subtask);
        }
        epics.put(epic.getId(), epic);
        for (Subtask subtask : newSubtasks){
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask){
        subtasks.put(subtask.getId(), subtask);
        Epic parentEpic = epics.get(subtask.getParentEpic());
        parentEpic.changeStatus(getAllSubtaskOfEpic(parentEpic.getId()));
    }

    @Override
    public void deleteEpicById(int id){
        Epic epic = epics.get(id);
        epics.remove(id);
        historyManager.remove(id);
        for (int subtask : epic.getSubtasksId()){
            subtasks.remove(subtask);
            historyManager.remove(subtask);
        }
    }

    @Override
    public void deleteTaskById(int id){
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id){
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            Epic parentEpic = epics.get(subtask.getParentEpic());
            parentEpic.removeSubtask(subtask, getAllSubtaskOfEpic(parentEpic.getId()));
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    protected void addToHistory(int id){
        if (epics.containsKey(id)){
            historyManager.add(epics.get(id));
        } else if (subtasks.containsKey(id)){
            historyManager.add(subtasks.get(id));
        } else {
            historyManager.add(tasks.get(id));
        }
    }

    protected void addEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }

    protected void addSubtask(Subtask subtask){
        subtasks.put(subtask.getId(), subtask);
    }

    protected void addTask(Task task){
        tasks.put(task.getId(), task);
    }

    protected Epic findById(int id){
        return epics.get(id);
    }
}