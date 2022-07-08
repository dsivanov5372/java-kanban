import managment.InMemoryTaskManager;
import tasks.*;

public class Main {
    public static void main(String[] args){
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic firstEpic = new Epic("Сдать летнюю сессию", "Заботать все предметы, чтобы не вылететь из вуза");
        Epic secondEpic = new Epic("Купить подарок", "Купить подарок для своего одногруппника");
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
                                            Status.DONE, secondEpic);
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

        System.out.println("Удалим подзадачу у эпика");
        manager.deleteSubtaskById(secondSubtask.getId());
        System.out.println(firstEpic);

        System.out.println("Удалим задачу и один эпик");
        manager.deleteEpicById(secondEpic.getId());
        manager.deleteTaskById(secondTask.getId());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllTasks());

        System.out.println("Удалим всё");
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

    }
}
