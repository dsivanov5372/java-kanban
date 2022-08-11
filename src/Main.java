import managment.FileBackedTasksManager;
import managment.InMemoryTaskManager;
import managment.Managers;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        Epic firstEpic = new Epic("Сдать летнюю сессию", "Заботать все предметы, чтобы не вылететь из вуза");
        Epic secondEpic = new Epic("Купить подарок", "Купить подарок для своего одногруппника");
        InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();
        manager.makeEpic(firstEpic);
        manager.makeEpic(secondEpic);

        Task firstTask = new Task("Уборка", "Собрать вещи для переезда в СПб на месяц", Status.IN_PROGRESS);
        Task secondTask = new Task("Собрать вещи", "Заботать линейные операторы, квадрики и коники", Status.NEW);
        manager.makeTask(firstTask);
        manager.makeTask(secondTask);

        Subtask firstSubtask = new Subtask("Заботать линал", "Заботать линейные операторы, квадрики и коники",
                Status.IN_PROGRESS, firstEpic);
        Subtask secondSubtask = new Subtask("Заботать Архитектуру ЭВМ", "Что такое топология звезда?",
                Status.IN_PROGRESS, firstEpic);
        Subtask thirdSubtask = new Subtask("Выбрать бюджет", "Понять, как долго смогу поголодать",
                Status.DONE, firstEpic);
        manager.makeSubtask(firstSubtask);
        manager.makeSubtask(secondSubtask);
        manager.makeSubtask(thirdSubtask);

        System.out.println("Печать всех задач");
        System.out.println(manager.getAllTasks());
        System.out.println("Печать всех эпиков");
        System.out.println(manager.getAllEpics());
        System.out.println("Печать всех подзадач");
        System.out.println(manager.getAllSubtasks());

        System.out.println("Поменяем статус у подзадачи");
        thirdSubtask.setStatus(Status.NEW);
        manager.updateSubtask(thirdSubtask);
        System.out.println(secondEpic);
        System.out.println("Ещё раз поменяем статус у подзадачи");
        thirdSubtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(thirdSubtask);
        System.out.println(secondEpic);

        System.out.println("печать истории");
        System.out.println(manager.getHistory());
        System.out.println("сделаем различные запросы задач");
        manager.getTask(secondTask.getId());
        manager.getSubtask(secondSubtask.getId());
        manager.getEpic(secondEpic.getId());
        manager.getTask(firstTask.getId());
        manager.getSubtask(firstSubtask.getId());
        manager.getEpic(firstEpic.getId());
        manager.getSubtask(thirdSubtask.getId());
        System.out.println(manager.getHistory().size() + "\n" + manager.getHistory());

        System.out.println("Cделаем повторный вызов");
        manager.getTask(firstTask.getId());
        System.out.println(manager.getHistory().size() + "\n" + manager.getHistory());

        System.out.println("удалим эпик с подзадачами");
        manager.deleteEpicById(firstEpic.getId());
        System.out.println(manager.getHistory().size() + "\n" + manager.getHistory());

        System.out.println("удалим задачу");
        manager.deleteTaskById(secondTask.getId());
        System.out.println(manager.getHistory().size() + "\n" + manager.getHistory());

        System.out.println("Удалим всё");
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getHistory());
    }
}
