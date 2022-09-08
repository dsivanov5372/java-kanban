package managment;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileTaskReader {
    public static void readFile(File file, FileBackedTasksManager manager) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                }

                Task task = manager.fromString(line);
                if (task instanceof Epic epic) {
                    manager.addEpic(epic);
                } else if (task instanceof Subtask subtask) {
                    manager.addSubtask(subtask);
                } else {
                    manager.addTask(task);
                }
            }

            String lineWithHistory = bufferedReader.readLine();
            for (int id : FileBackedTasksManager.historyFromString(lineWithHistory)){
                manager.addToHistory(id);
            }
        } catch (IOException e) {
            System.out.println("Не удалось считать данные из файла.");
        }
    }

    public static void save(FileBackedTasksManager manager){
        ArrayList<Task> tasks = manager.getAllTasks();
        ArrayList<Epic> epics = manager.getAllEpics();
        Path fileToSaveData = Path.of("backup.csv");

        try {
            if (Files.exists(fileToSaveData)){
                Files.delete(fileToSaveData);
            }
            Files.createFile(fileToSaveData);
        } catch (IOException e) {
            System.out.println("Не удалось найти файл для записи данных");
        }

        try (FileWriter writer = new FileWriter(String.valueOf(fileToSaveData), StandardCharsets.UTF_8)){
            String params = "id,type,name,status,description,epic,duration,start_time\n";
            writer.write(params);

            for (Task task : tasks){
                writer.write(manager.toString(task) + "\n");
            }

            for (Epic epic : epics){
                writer.write(manager.toString(epic) + "\n");
                ArrayList<Integer> subtaskId = epic.getSubtasksId();
                for (int id : subtaskId){
                    Subtask subtask = manager.getSubtaskWithoutHistory(id);
                    writer.write(manager.toString(subtask) + "\n");
                }
            }

            writer.write("\n");

            List<Task> history = manager.getHistory();
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
        }
    }
}
