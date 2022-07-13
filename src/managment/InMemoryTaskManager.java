package managment;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int idSetter = 1;

    public InMemoryTaskManager(){}

    @Override
    public void makeEpic(Epic epic){
        epic.setId(idSetter);
        idSetter++;

        if(epics == null){
            epics = new HashMap<>();
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void makeTask(Task task){
        task.setId(idSetter);
        idSetter++;

        if(tasks == null){
            tasks = new HashMap<>();
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void makeSubtask(Subtask subtask){
        subtask.setId(idSetter);
        idSetter++;

        if(subtasks == null){
            subtasks = new HashMap<>();
        }
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getParentEpic()).addSubtask(subtask, getAllSubtaskOfEpic(subtask.getParentEpic()));
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
        tasks.clear();
    }

    @Override
    public void deleteAllEpics(){
        for (Epic epic : epics.values()){
            for (int subtask : epic.getSubtasksId()){
                subtasks.remove(subtask);
            }
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks(){
        for (Subtask subtask : subtasks.values()){
            epics.get(subtask.getParentEpic()).removeSubtask(subtask, getAllSubtaskOfEpic(subtask.getParentEpic()));
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasksOfEpics(Epic epic){
        for (int subtask : epic.getSubtasksId()){
            subtasks.remove(subtask);
        }
    }

    @Override
    public Epic getEpic(int id){
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task getTask(int id){
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id){
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
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
        epics.get(subtask.getParentEpic()).changeStatus(getAllSubtaskOfEpic(subtask.getParentEpic()));
    }

    @Override
    public void deleteEpicById(int id){
        Epic epic = epics.get(id);
        epics.remove(id);
        for (int subtask : epic.getSubtasksId()){
            subtasks.remove(subtask);
        }
    }

    @Override
    public void deleteTaskById(int id){
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id){
        (epics.get(subtasks.get(id).getParentEpic())).removeSubtask(subtasks.get(id), getAllSubtaskOfEpic(subtasks.get(id).getParentEpic()));
        subtasks.remove(id);
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }
}