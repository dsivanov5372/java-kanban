package managment;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{
    public FileBackedTasksManager(){
        super();
    }

    @Override
    public void addEpic(Epic epic){
        super.makeEpic(epic);
        save();
    }

    @Override
    public void addTask(Task task){
        super.makeTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask){
        super.makeSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics(){
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks(){
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasksOfEpics(Epic epic){
        super.deleteAllSubtasksOfEpics(epic);
        save();
    }

    @Override
    public void updateEpics(Epic epic){
        super.updateEpics(epic);
        save();
    }

    @Override
    public void updateTask(Task task){
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask){
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteEpicById(int id){
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id){
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id){
        super.deleteSubtaskById(id);
        save();
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

    private String toString(Task task){
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getTitle(),
                            task.getStatus().toString(), task.getDetails(), getParentEpicId(task)};
        return String.join(",", toJoin);
    }

    private Task fromString(String value){
        String[] params = value.split(",");

        //как сказано в условии, рассчитано на идеальные условия,
        //что файл не трогали и он в нужном формате, что задачи идут в нужном порядке;
        if (params[1].equals("EPIC")){
            Epic epic = new Epic(params[2], params[4]);
            epic.setId(Integer.parseInt(params[0]));
            epic.setStatus(Status.valueOf(params[3]));
            return epic;
        } else if (params[1].equals("SUBTASK")){
            Subtask subtask = new Subtask(params[2], params[4], Status.valueOf(params[3]),
                                            super.getEpic(Integer.parseInt(params[5])));
            subtask.setId(Integer.parseInt(params[0]));
            return subtask;
        } else {
            Task task = new Task(params[2], params[4], Status.valueOf(params[3]));
            task.setId(Integer.parseInt(params[0]));
            return task;
        }
    }

    private void save(){
        ArrayList<Task> tasks = super.getAllTasks();
        ArrayList<Epic> epics = super.getAllEpics();
        String path = System.getProperty("user.home");
        Path of = Path.of(path, "backup.csv");
        //dir: /Users/dmitry

        try {
            if (Files.exists(of)){
                Files.delete(of);
            }
            Files.createFile(of);
        } catch (IOException e) {
            System.out.println("Не удалось найти файл для записи данных");
        }

        try (FileWriter writer = new FileWriter(new File(String.valueOf(of)), StandardCharsets.UTF_8)){
            String params = "id,type,name,status,description,epic\n";
            writer.write(params);

            for (Task task : tasks){
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : epics){
                writer.write(toString(epic) + "\n");
                ArrayList<Integer> subtaskId = epic.getSubtasksId();
                for (int id : subtaskId){
                    Subtask subtask = super.getSubtask(id);
                    writer.write(toString(subtask) + "\n");
                }
            }

            writer.write("\n");

            List<Task> history = super.getHistory();
            StringBuilder str = new StringBuilder();

            for (Task task : history){
                str.append(task.getId()).append(",");
            }

            if (str.length() != 0) {
                str.deleteCharAt(str.length() - 1);
            }

            writer.write(str.toString());
        } catch (IOException exception){
            System.out.println("Не удалось записать данные в файл");
            return;
        }
    }

    private void readFile(File file) throws IOException {
        FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
        BufferedReader bf = new BufferedReader(reader);

        String line = bf.readLine();

        while (bf.ready()){
            line = bf.readLine();
            if (line.equals("")){
                break;
            }

            Task task = fromString(line);
            if (task instanceof Epic){
                super.addEpic((Epic) task);
            } else if (task instanceof Subtask){
                super.addSubtask((Subtask) task);
            } else {
                super.addTask(task);
            }
        }

        String lineWithHistory = bf.readLine();
        for (int id : historyFromString(lineWithHistory)){
            super.addToHistory(id);
        }
    }
    public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager manager = new FileBackedTasksManager();

        try {
            manager.readFile(file);
        } catch (IOException exception) {
            System.out.println("Не удалось считать данные из файла");
        } finally {
            return manager;
        }
    }

    static String historyToString(HistoryManager manager){
        StringBuilder str = new StringBuilder();

        for (Task task : manager.getHistory()){
            str.append(task.getId()).append(",");
        }
        str.deleteCharAt(str.length() - 1);

        return str.toString();
    }

    static List<Integer> historyFromString(String value){
        String[] id = value.split(",");
        ArrayList<Integer> toReturn = new ArrayList<>();

        for (String number : id){
            toReturn.add(Integer.parseInt(number));
        }

        return toReturn;
    }

    static void main(String[] args){
        FileBackedTasksManager manager = new FileBackedTasksManager();
        Epic firstEpic = new Epic("Сдать летнюю сессию", "Заботать все предметы чтобы не вылететь из вуза");
        Epic secondEpic = new Epic("Купить подарок", "Купить подарок для своего одногруппника");
        manager.addEpic(firstEpic);
        manager.addEpic(secondEpic);

        Task firstTask = new Task("Уборка", "Собрать вещи для переезда в СПб на месяц", Status.IN_PROGRESS);
        Task secondTask = new Task("Собрать вещи", "Заботать линейные операторы квадрики и коники", Status.NEW);
        manager.addTask(firstTask);
        manager.addTask(secondTask);

        Subtask firstSubtask = new Subtask("Заботать линал", "Заботать линейные операторы квадрики и коники",
                Status.IN_PROGRESS, firstEpic);
        Subtask secondSubtask = new Subtask("Заботать Архитектуру ЭВМ", "Что такое топология звезда?",
                Status.IN_PROGRESS, firstEpic);
        Subtask thirdSubtask = new Subtask("Выбрать бюджет", "Понять как долго смогу поголодать",
                Status.DONE, firstEpic);
        manager.addSubtask(firstSubtask);
        manager.addSubtask(secondSubtask);
        manager.addSubtask(thirdSubtask);
        System.out.println(manager.getHistory());

        System.out.println("Проверим что правильно считываем из файла");
        String path = System.getProperty("user.home");
        Path of = Path.of(path, "backup.csv");
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(new File(String.valueOf(of)));
        System.out.println("Эпики");
        System.out.println(newManager.getAllEpics());
        System.out.println("Задачи");
        System.out.println(newManager.getAllTasks());
        System.out.println("Подзадачи");
        System.out.println(newManager.getAllSubtasks());
        System.out.println("История");
        System.out.println(newManager.getHistory());
    }
}
