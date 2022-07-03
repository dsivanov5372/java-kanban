package managment;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int idSetter = 1;

    public Manager(){}

    public void makeEpic(Epic epic){
        epic.setId(idSetter);
        idSetter++;

        if(epics == null){
            epics = new HashMap<>();
        }
        epics.put(epic.getId(), epic);
    }

    public void makeTask(Task task){
        task.setId(idSetter);
        idSetter++;

        if(tasks == null){
            tasks = new HashMap<>();
        }
        tasks.put(task.getId(), task);
    }

    public void makeSubtask(Subtask subtask){
        subtask.setId(idSetter);
        idSetter++;

        if(subtasks == null){
            subtasks = new HashMap<>();
        }
        subtasks.put(subtask.getId(), subtask);
    }

    public ArrayList<Epic> getAllEpics(){
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Task> getAllTasks(){
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getAllSubtaskOfEpic(int id){
        return epics.get(id).getSubtasks();
    }

    public void deleteAllTasks(){
        tasks.clear();
    }

    public void deleteAllEpics(){
        for (Epic epic : epics.values()){
            for (Subtask subtask : epic.getSubtasks()){
                subtasks.remove(subtask.getId());
            }
        }
        epics.clear();
    }

    public void deleteAllSubtasks(){
        for (Subtask subtask : subtasks.values()){
            epics.get(subtask.getParentEpic().getId()).removeSubtask(subtask);
        }
        subtasks.clear();
    }

    public void deleteAllSubtasksOfEpics(Epic epic){
        for (Subtask subtask : epic.getSubtasks()){
            subtasks.remove(subtask.getId());
        }
    }

    public Epic getEpicById(int id){
        return epics.get(id);
    }

    public Task getTaskById(int id){
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id){
        return subtasks.get(id);
    }

    public void updateEpics(Epic epic){
        Epic prevEpic = epics.get(epic.getId());
        for (Subtask subtask : prevEpic.getSubtasks()){
            subtasks.remove(subtask.getId());
        }
        epics.put(epic.getId(), epic);
        for (Subtask subtask : epic.getSubtasks()){
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask){
        subtasks.put(subtask.getId(), subtask);
        (subtask.getParentEpic()).changeStatus();
    }

    public void deleteEpicById(int id){
        Epic epic = epics.get(id);
        epics.remove(id);
        for (Subtask subtask : epic.getSubtasks()){
            subtasks.remove(subtask.getId());
        }
    }

    public void deleteTaskById(int id){
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id){
        (epics.get(subtasks.get(id).getParentEpic().getId())).removeSubtask(subtasks.get(id));
        subtasks.remove(id);
    }
}