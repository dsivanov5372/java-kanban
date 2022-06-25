public class Main {
    public static void main(String[] args){
        Manager manager = new Manager();

        Epic firstEpic = new Epic(null, null, 0);
        Epic secondEpic = new Epic(null, null, 0);
        Task firstTask = new Task(null, null, 0, Status.NEW);
        Task secondTask = new Task(null, null, 0, Status.NEW);
        Subtask firstSubtask = new Subtask(null, null, 0, Status.NEW, firstEpic);
        Subtask secondSubtask = new Subtask(null, null, 0, Status.NEW, firstEpic);
        Subtask thirdSubtask = new Subtask(null, null, 0, Status.NEW, secondEpic);

        manager.makeEpic("Сдать летнюю сессию", "Заботать все предметы, чтобы не вылететь из вуза", firstEpic);
        manager.makeEpic("Купить подарок", "Купить подарок для своего одногруппника", secondEpic);
        manager.makeTask("Уборка", "Прибрать комнату перед отъездом из общежития", Status.IN_PROGRESS, firstTask);
        manager.makeTask("Собрать вещи", "Собрать вещи для переезда в СПб на месяц", Status.NEW, secondTask);
        manager.makeSubtask("Заботать линал", "Заботать линейные операторы, квадрики и коники",
                            Status.IN_PROGRESS, firstSubtask, firstEpic);
        manager.makeSubtask("Заботать Архитектуру ЭВМ", "Что такое топология звезда?",
                            Status.IN_PROGRESS, secondSubtask, firstEpic);
        manager.makeSubtask("Выбрать бюджет", "Понять, как долго смогу поголодать",
                            Status.DONE, thirdSubtask, secondEpic);

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
