package managment;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    public void makeEpic(Epic epic);

    public void makeTask(Task task);

    public void makeSubtask(Subtask subtask);

    public ArrayList<Epic> getAllEpics();

    public ArrayList<Task> getAllTasks();

    public ArrayList<Subtask> getAllSubtasks();

    public ArrayList<Subtask> getAllSubtaskOfEpic(int id);

    public void deleteAllTasks();

    public void deleteAllEpics();

    public void deleteAllSubtasks();

    public void deleteAllSubtasksOfEpics(Epic epic);

    public Epic getEpicById(int id);

    public Task getTaskById(int id);

    public Subtask getSubtaskById(int id);

    public void updateEpics(Epic epic);

    public void updateTask(Task task);

    public void updateSubtask(Subtask subtask);

    public void deleteEpicById(int id);

    public void deleteTaskById(int id);

    public void deleteSubtaskById(int id);
}
