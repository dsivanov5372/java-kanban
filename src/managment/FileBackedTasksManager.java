package managment;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{
    public FileBackedTasksManager(){
        super();
    }

    @Override
    public void makeEpic(Epic epic){
        super.makeEpic(epic);
        FileTaskReader.save(this);
    }

    @Override
    public void makeTask(Task task){
        super.makeTask(task);
        FileTaskReader.save(this);
    }

    @Override
    public void makeSubtask(Subtask subtask){
        super.makeSubtask(subtask);
        FileTaskReader.save(this);
    }

    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        FileTaskReader.save(this);
    }

    @Override
    public void deleteAllEpics(){
        super.deleteAllEpics();
        FileTaskReader.save(this);
    }

    @Override
    public void deleteAllSubtasks(){
        super.deleteAllSubtasks();
        FileTaskReader.save(this);
    }

    @Override
    public void deleteAllSubtasksOfEpic(Epic epic){
        super.deleteAllSubtasksOfEpic(epic);
        FileTaskReader.save(this);
    }

    @Override
    public void updateEpic(Epic epic){
        super.updateEpic(epic);
        FileTaskReader.save(this);
    }

    @Override
    public void updateTask(Task task){
        super.updateTask(task);
        FileTaskReader.save(this);
    }

    @Override
    public void updateSubtask(Subtask subtask){
        super.updateSubtask(subtask);
        FileTaskReader.save(this);
    }

    @Override
    public void deleteEpicById(int id){
        super.deleteEpicById(id);
        FileTaskReader.save(this);
    }

    @Override
    public void deleteTaskById(int id){
        super.deleteTaskById(id);
        FileTaskReader.save(this);
    }

    @Override
    public void deleteSubtaskById(int id){
        super.deleteSubtaskById(id);
        FileTaskReader.save(this);
    }

    public Subtask getSubtaskWithoutHistory(int id){
        if (subtasks.containsKey(id)){
            return subtasks.get(id);
        }
        return null;
    }

    private String getParentEpicId(Task task){
        if (task instanceof Subtask){
            return Integer.toString(((Subtask)task).getParentEpic());
        }
        return "";
    }

    private TaskType getType(Task task){
        if (task instanceof Epic){
            return TaskType.EPIC;
        } else if (task instanceof Subtask){
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    String toString(Task task){
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getTitle(),
                            task.getStatus().toString(), task.getDetails(), getParentEpicId(task),
                            String.valueOf(task.getDuration()), String.valueOf(task.getStartTime())};
        return String.join(",", toJoin);
    }

    Task fromString(String value){
        String[] params = value.split(",");

        Duration duration;
        if (params[6].equals("null")){
            duration = null;
        } else {
            duration = Duration.parse(params[6]);
        }

        LocalDateTime start;
        if (params[7].equals("null")){
            start = null;
        } else {
            start = LocalDateTime.parse(params[7]);
        }
        //как сказано в условии, рассчитано на идеальные условия,
        //что файл не трогали и он в нужном формате, что задачи идут в нужном порядке;
        if (params[1].equals("EPIC")){
            Epic epic = new Epic(params[2], params[4]);
            epic.setId(Integer.parseInt(params[0]));
            epic.setStatus(Status.valueOf(params[3]));
            epic.setDuration(duration);
            epic.setStartTime(start);
            return epic;
        } else if (params[1].equals("SUBTASK")){
            Subtask subtask = new Subtask(params[2], params[4], Status.valueOf(params[3]),
                                            super.findById(Integer.parseInt(params[5])), null, null);
            subtask.setId(Integer.parseInt(params[0]));
            subtask.setDuration(duration);
            subtask.setStartTime(start);
            return subtask;
        } else {
            Task task = new Task(params[2], params[4], Status.valueOf(params[3]), null, null);
            task.setId(Integer.parseInt(params[0]));
            task.setDuration(duration);
            task.setStartTime(start);
            return task;
        }
    }

    public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager manager = new FileBackedTasksManager();
        FileTaskReader.readFile(file, manager);
        return manager;
    }


    static List<Integer> historyFromString(String value){
        ArrayList<Integer> toReturn = new ArrayList<>();
        if (value != null) {
            String[] id = value.split(",");

            for (String number : id) {
                toReturn.add(Integer.parseInt(number));
            }

            return toReturn;
        }
        return toReturn;
    }

    public static void main(String[] args){
        FileBackedTasksManager manager = new FileBackedTasksManager();
        Epic firstEpic = new Epic("Сдать летнюю сессию", "Заботать все предметы чтобы не вылететь из вуза");
        Epic secondEpic = new Epic("Купить подарок", "Купить подарок для своего одногруппника");
        manager.makeEpic(firstEpic);
        manager.makeEpic(secondEpic);
        Task firstTask = new Task("Уборка", "Собрать вещи для переезда в СПб на месяц", Status.IN_PROGRESS, null, null);
        Task secondTask = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask2 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask3 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask4 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask5 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask6 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask7 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask8 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask9 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask10 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask11 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask12 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        Task secondTask13 = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW, null, null);
        manager.makeTask(firstTask);
        manager.makeTask(secondTask);
        manager.makeTask(secondTask2);
        manager.makeTask(secondTask3);
        manager.makeTask(secondTask4);
        manager.makeTask(secondTask5);
        manager.makeTask(secondTask6);
        manager.makeTask(secondTask7);
        manager.makeTask(secondTask8);
        manager.makeTask(secondTask9);
        manager.makeTask(secondTask10);
        manager.makeTask(secondTask11);
        manager.makeTask(secondTask12);
        manager.makeTask(secondTask13);
        Subtask firstSubtask = new Subtask("Заботать линал", "Заботать линейные операторы квадрики и коники",
                Status.IN_PROGRESS, firstEpic, Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 9, 30));
        Subtask secondSubtask = new Subtask("Заботать Архитектуру ЭВМ", "Что такое топология звезда?",
                Status.IN_PROGRESS, firstEpic, null, null);
        Subtask thirdSubtask = new Subtask("Выбрать бюджет", "Понять как долго смогу поголодать",
                Status.DONE, firstEpic, null, null);
        manager.makeSubtask(firstSubtask);
        manager.makeSubtask(secondSubtask);
        manager.makeSubtask(thirdSubtask);
        System.out.println(manager.getHistory());
        System.out.println("Проверим что правильно считываем из файла");
        Path of = Path.of("backup.csv");
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(new File(String.valueOf(of)));
        System.out.println("Эпики");
        System.out.println(newManager.getAllEpics());
        System.out.println("Задачи");
        System.out.println(newManager.getAllTasks());
        System.out.println("Подзадачи");
        System.out.println(newManager.getAllSubtasks());
        System.out.println("История");
        System.out.println(List.of(secondTask2.getId(),secondTask4.getId(),secondTask3.getId(),secondTask5.getId(),secondTask6.getId(),secondTask7.getId()));
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask2.getId());
        manager.getTask(secondTask4.getId());
        manager.getTask(secondTask3.getId());
        manager.getTask(secondTask5.getId());
        manager.getTask(secondTask6.getId());
        manager.getTask(secondTask7.getId());
        manager.deleteSubtaskById(1);
        manager.deleteEpicById(1);
        System.out.println(newManager.getHistory());
        System.out.println("правильная история :)");
        System.out.println(manager.getHistory());
        System.out.println(newManager.getHistory());
        System.out.println("правильная история :)");
        System.out.println(manager.getHistory());
    }
}
