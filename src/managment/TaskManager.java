package managment;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void makeEpic(Epic epic);

    void makeTask(Task task);

    void makeSubtask(Subtask subtask);

    ArrayList<Epic> getAllEpics();

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getAllSubtaskOfEpic(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteAllSubtasksOfEpics(Epic epic);

    Epic getEpic(int id);

    Task getTask(int id);

    Subtask getSubtask(int id);

    void updateEpics(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void deleteEpicById(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    List<Task> getHistory();
}
