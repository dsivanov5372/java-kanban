import managment.Managers;
import tasks.*;

public class Main {
    public static void main(String[] args){
        Epic firstEpic = new Epic("Сдать летнюю сессию", "Заботать все предметы, чтобы не вылететь из вуза");
        Epic secondEpic = new Epic("Купить подарок", "Купить подарок для своего одногруппника");
        Managers.getDefault().makeEpic(firstEpic);
        Managers.getDefault().makeEpic(secondEpic);

        Task firstTask = new Task("Уборка", "Собрать вещи для переезда в СПб на месяц", Status.IN_PROGRESS);
        Task secondTask = new Task("Собрать вещи", "Заботать линейные операторы, квадрики и коники", Status.NEW);
        Managers.getDefault().makeTask(firstTask);
        Managers.getDefault().makeTask(secondTask);

        Subtask firstSubtask = new Subtask("Заботать линал", "Заботать линейные операторы, квадрики и коники",
                                            Status.IN_PROGRESS, firstEpic);
        Subtask secondSubtask = new Subtask("Заботать Архитектуру ЭВМ", "Что такое топология звезда?",
                                            Status.IN_PROGRESS, firstEpic);
        Subtask thirdSubtask = new Subtask("Выбрать бюджет", "Понять, как долго смогу поголодать",
                                            Status.DONE, secondEpic);
        Managers.getDefault().makeSubtask(firstSubtask);
        Managers.getDefault().makeSubtask(secondSubtask);
        Managers.getDefault().makeSubtask(thirdSubtask);

        System.out.println("Печать всех задач");
        System.out.println(Managers.getDefault().getAllTasks());
        System.out.println("Печать всех эпиков");
        System.out.println(Managers.getDefault().getAllEpics());
        System.out.println("Печать всех подзадач");
        System.out.println(Managers.getDefault().getAllSubtasks());

        System.out.println("Поменяем статус у подзадачи");
        thirdSubtask.setStatus(Status.NEW);
        Managers.getDefault().updateSubtask(thirdSubtask);
        System.out.println(secondEpic);
        System.out.println("Ещё раз поменяем статус у подзадачи");
        thirdSubtask.setStatus(Status.IN_PROGRESS);
        Managers.getDefault().updateSubtask(thirdSubtask);
        System.out.println(secondEpic);

        System.out.println("Удалим подзадачу у эпика");
        Managers.getDefault().deleteSubtaskById(secondSubtask.getId());
        System.out.println(firstEpic);

        System.out.println("Удалим задачу и один эпик");
        Managers.getDefault().deleteEpicById(secondEpic.getId());
        Managers.getDefault().deleteTaskById(secondTask.getId());
        System.out.println(Managers.getDefault().getAllEpics());
        System.out.println(Managers.getDefault().getAllTasks());

        System.out.println("печать истории");
        System.out.println(Managers.getDefaultHistory().getHistory());
        System.out.println("Сделаем три запроса get и посмотрим содержимое истории");
        Managers.getDefault().getEpic(firstEpic.getId());
        Managers.getDefault().getTask(firstTask.getId());
        Managers.getDefault().getSubtask(firstSubtask.getId());
        System.out.println(Managers.getDefaultHistory().getHistory());
        System.out.println("Сделаем ещё 8 таких запросов и проверим размер истории + её содержимое");
        for (int i = 0; i < 8; i++){
            Managers.getDefault().getEpic(firstEpic.getId());
        }
        System.out.println(Managers.getDefaultHistory().getHistory().size());
        System.out.println(Managers.getDefaultHistory().getHistory());

        System.out.println("Удалим всё");
        Managers.getDefault().deleteAllTasks();
        Managers.getDefault().deleteAllEpics();
        System.out.println(Managers.getDefault().getAllTasks());
        System.out.println(Managers.getDefault().getAllEpics());
        System.out.println(Managers.getDefault().getAllSubtasks());
    }
}
